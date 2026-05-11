package com.kjersti.astryx.api.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kjersti.astryx.api.ApiHandler;
import com.kjersti.astryx.api.object.H2HVetoObject;
import com.kjersti.astryx.api.object.VetoObject;
import com.kjersti.astryx.api.object.VetoPrecursorObject;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.util.Duration;
import com.kjersti.astryx.common.util.JsonManager;
import com.kjersti.astryx.sql.SqlBuilder;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class VetoDataHandler {
    public static final Logger LOGGER = AstryxLogManager.getLogger("veto");

    public static final String VETO_LINK = "https://api.faceit.com/democracy/v1/match/";

    private static boolean running = false;

    public static void updateVetoDatabase() {
        if (!running) {
            running = true;

            Thread thread = new Thread(VetoDataHandler::run);
            thread.start();
        } else {
            LOGGER.warn("Already running.");
        }
    }

    public static void updateVetoDatabase(boolean useNewThread) {
        if (!running) {
            running = true;

            if (useNewThread) {
                Thread thread = new Thread(VetoDataHandler::run);
                thread.start();
            } else {
                VetoDataHandler.run();
            }
        } else {
            LOGGER.warn("Already running.");
        }
    }

    private static void run() {
        long startTime = System.nanoTime();
        LOGGER.info("Retrieving vetoes from API");

        List<VetoObject> allObjects = sendAPIRequestConcurrently();

        addToDatabase(allObjects);
        allObjects.stream()
                .map(VetoObject::getMatchId)
                .distinct()
                .forEach(VetoDataHandler::archiveMatch);

        long endTime = System.nanoTime();

        Duration duration = new Duration(startTime, endTime);
        int min = duration.getMinutes();
        int sec = duration.getSeconds();

        LOGGER.info("Retrieved {} vetoes from API. Execution time {} minutes, {} seconds",
                allObjects.size(), min, sec);

        running = false;
    }

    private static List<VetoObject> sendAPIRequestConcurrently() {
        int threadPool = SettingRegistry.getConcurrentThreadCount();

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        List<VetoObject> matches = new ArrayList<>();
        List<VetoPrecursorObject> matchIds = getMatchIds();

        List<Callable<List<VetoObject>>> tasks = new ArrayList<>();

        for (VetoPrecursorObject precursor: matchIds) {
            tasks.add(() -> fetchVetoes(precursor));
        }

        try {
            List<Future<List<VetoObject>>> results = executorService.invokeAll(tasks);

            for (Future<List<VetoObject>> result : results) {
                List<VetoObject> list = result.get();

                if (list == null) continue;

                matches.addAll(list);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error executing tasks", e);
        }

        executorService.shutdown();

        return matches;
    }
    private static List<VetoObject> fetchVetoes(VetoPrecursorObject precursorObject) {
        String matchId = precursorObject.getMatchId();

        String requestData = getVetoData(matchId);

        Map<String, Object> map;

        try {
            map = JsonManager.readJson(requestData);
        } catch (JsonProcessingException ignored) {
            return new ArrayList<>();
        }

        Map<String, Object> vetoData = (Map<String, Object>) map.get("payload");

        return getObjectsFromMap(vetoData, precursorObject);
    }

    private static List<VetoObject> getObjectsFromMap(Map<String, Object> vetoList, VetoPrecursorObject precursor) {
        if (vetoList == null || vetoList.isEmpty()) return new ArrayList<>();

        List<VetoObject> vetoes = new ArrayList<>();
        List<Map<String, Object>> tickets = (List<Map<String, Object>>) vetoList.get("tickets");
        if (tickets.isEmpty()) return new ArrayList<>();
        List<Map<String, Object>> entities = getEntitiesFromTickets(tickets);

        if (entities.isEmpty()) return new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            Map<String, Object> veto = entities.get(i);
            VetoObject vetoObject = new VetoObject(veto, precursor, i);

            vetoes.add(vetoObject);
        }

        return vetoes;
    }

    private static List<Map<String, Object>> getEntitiesFromTickets(List<Map<String, Object>> tickets) {
        List<Map<String, Object>> entities = new ArrayList<>();

        for (Map<String, Object> ticket: tickets) {
            if (ticket.get("entity_type").toString().equals("map") && ticket.get("vote_type").toString().equals("drop_pick")) {
                List<Map<String, Object>> currentEntities = (List<Map<String, Object>>) ticket.get("entities");

                if (currentEntities.size() < 2) continue;

                entities.addAll(currentEntities);
            }
        }

        return entities;
    }

    private static String getVetoData(String matchId) {
        return ApiHandler.getRequest(VETO_LINK + matchId + "/history", null, null, true);
    }

    public static List<VetoPrecursorObject> getMatchIds() {
        String db = SettingRegistry.getApiDatabase();
        String table = SettingRegistry.getMatchDataTable();
        SqlObject obj = SqlHandler.executeQuery(db, "SELECT `match_id`, `team1_id`, `team2_id`, `finished_at` FROM " + table + " WHERE `archived`=0;");

        List<VetoPrecursorObject> matches = new ArrayList<>();

        for (String[] row: obj.getDataWithoutColumnNames()) {
            VetoPrecursorObject precursorObject = new VetoPrecursorObject(row);

            matches.add(precursorObject);
        }

        return matches;
    }

    private static void addToDatabase(List<VetoObject> objects) {
        if (objects.isEmpty()) return;

        int batchSize = SettingRegistry.getSqlBatchLimit();
        int totalSize = objects.size();

        int numberOfBatches = objects.size()/batchSize + 1;

        if (numberOfBatches > 1) {
            LOGGER.info("Object count more than maximum (" + batchSize + "). Using "
                    + numberOfBatches + " batches (" + totalSize + " total)");
        }

        for (int start = 0; start < totalSize; start += batchSize) {
            int end = Math.min(start + batchSize, totalSize);
            List<VetoObject> batch = objects.subList(start, end);
            executeBatch(batch);
        }
    }

    private static void executeBatch(List<VetoObject> batch) {
        String command = SqlBuilder.addRawMatchVetos(batch);

        SqlHandler.executeApiUpdate(command);
    }

    public static void archiveMatch(String matchId) {
        String table = SettingRegistry.getMatchDataTable();
        String query = "SELECT `status` FROM " + table + " WHERE `match_id`='" + matchId + "';";
        SqlObject statusObj = SqlHandler.executeApiQuery(query);

        String status = statusObj.getDataWithoutColumnNames()[0][0];

        if (status.equals("FINISHED")) {
            query = "UPDATE `" + table + "` SET `archived`=1 WHERE `match_id`='" + matchId + "';";

            SqlHandler.executeApiUpdate(query);
        }
    }

    public static boolean isRunning() {
        return running;
    }
}
