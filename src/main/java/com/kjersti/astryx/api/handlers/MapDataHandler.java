package com.kjersti.astryx.api.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kjersti.astryx.api.ApiHandler;
import com.kjersti.astryx.api.object.*;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.registry.TokenRegistry;
import com.kjersti.astryx.common.util.Duration;
import com.kjersti.astryx.common.util.JsonManager;
import com.kjersti.astryx.sql.SqlBuilder;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MapDataHandler {
    public static final Logger LOGGER = AstryxLogManager.getLogger("veto");
    public static final String MATCH_LINK = "https://open.faceit.com/data/v4/matches/";

    private static boolean running = false;

    public static void updateMapDatabase() {
        if (!running) {
            running = true;

            Thread thread = new Thread(MapDataHandler::run);
            thread.start();
        } else {
            LOGGER.warn("Already running.");
        }
    }

    public static void updateMapDatabase(boolean useNewThread) {
        if (!running) {
            running = true;

            if (useNewThread) {
                Thread thread = new Thread(MapDataHandler::run);
                thread.start();
            } else {
                MapDataHandler.run();
            }
        } else {
            LOGGER.warn("Already running.");
        }
    }

    private static void run() {
        long startTime = System.nanoTime();
        LOGGER.info("Retrieving map statistics from API");

        List<MapObject> allObjects = sendAPIRequestConcurrently();

        addToDatabase(allObjects);;

        long endTime = System.nanoTime();

        Duration duration = new Duration(startTime, endTime);
        int min = duration.getMinutes();
        int sec = duration.getSeconds();

        LOGGER.info("Retrieved {} map statistics from API. Execution time {} minutes, {} seconds",
                allObjects.size(), min, sec);

        running = false;
    }

    private static List<MapObject> sendAPIRequestConcurrently() {
        int threadPool = SettingRegistry.getConcurrentThreadCount();

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        List<MapObject> matches = new ArrayList<>();
        List<MapPrecursorObject> matchIds = getMatchIds();

        List<Callable<List<MapObject>>> tasks = new ArrayList<>();

        for (MapPrecursorObject precursorObject: matchIds) {
            tasks.add(() -> fetchMaps(precursorObject));
        }

        try {
            List<Future<List<MapObject>>> results = executorService.invokeAll(tasks);

            for (Future<List<MapObject>> result : results) {
                List<MapObject> list = result.get();

                if (list == null) continue;

                matches.addAll(list);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error executing tasks", e);
        }

        executorService.shutdown();

        return matches;
    }

    private static List<MapObject> fetchMaps(MapPrecursorObject precursorObject) {
        String requestData = getMapData(precursorObject);

        Map<String, Object> map;

        try {
            map = JsonManager.readJson(requestData);
        } catch (JsonProcessingException ignored) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> mapData = (List<Map<String, Object>>) map.get("rounds");

        return getObjectsFromMap(mapData, precursorObject);
    }

    private static List<MapObject> getObjectsFromMap(List<Map<String, Object>> mapData, MapPrecursorObject precursorObject) {
        if (mapData == null || mapData.isEmpty()) return new ArrayList<>();

        List<MapObject> allMapObjects = new ArrayList<>();

        for (Map<String, Object> roundData: mapData) {
            List<Map<String, Object>> teams = (List<Map<String, Object>>) roundData.get("teams");
            List<FaceitStatsObject> allPlayerStats = new ArrayList<>();

            if (teams == null) continue;

            for (Map<String, Object> team: teams) {
                List<Map<String, Object>> players = (List<Map<String, Object>>) team.get("players");

                if (players == null) continue;

                for (Map<String, Object> player: players) {
                    FaceitStatsObject playerStats = new FaceitStatsObject(player);

                    allPlayerStats.add(playerStats);
                }
            }

            if (allPlayerStats.size() != 10) {
                continue;
            }

            MapObject data = new MapObject(roundData, allPlayerStats, precursorObject);
            allMapObjects.add(data);
        }

        return allMapObjects;
    }

    private static String getMapData(MapPrecursorObject precursorObject) {
        String token = TokenRegistry.getFaceitToken();
        String matchId = precursorObject.getMatchId();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return ApiHandler.getRequest(MATCH_LINK + matchId + "/stats", headers, null, true);
    }

    public static List<MapPrecursorObject> getMatchIds() {
        String db = SettingRegistry.getApiDatabase();
        String table = SettingRegistry.getMatchDataTable();
        SqlObject obj = SqlHandler.executeQuery(db, "SELECT `match_id`, `finished_at` FROM " + table +
                " WHERE `archived_maps` = 0 AND `started_at` != 0 AND `team1_id` != 'bye' AND `team2_id` != 'bye';");

        List<MapPrecursorObject> matches = new ArrayList<>();

        for (String[] row: obj.getDataWithoutColumnNames()) {
            MapPrecursorObject mapPrecursorObject = new MapPrecursorObject(row);

            matches.add(mapPrecursorObject);
        }

        return matches;
    }

    private static void addToDatabase(List<MapObject> objects) {
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
            List<MapObject> batch = objects.subList(start, end);
            executeBatch(batch);
        }
    }

    private static void executeBatch(List<MapObject> batch) {
        String query = SqlBuilder.addToMapDataTable(batch);

        SqlHandler.executeApiUpdate(query);
    }

    public static void archiveMatch(String matchId) {
        String table = SettingRegistry.getMatchDataTable();
        String query = "SELECT `status` FROM " + table + " WHERE `match_id`='" + matchId + "';";
        SqlObject statusObj = SqlHandler.executeApiQuery(query);

        String status = statusObj.getDataWithoutColumnNames()[0][0];

        if (status.equals("FINISHED")) {
            query = "UPDATE `" + table + "` SET `archived_maps`=1 WHERE `match_id`='" + matchId + "';";

            SqlHandler.executeApiUpdate(query);
        }
    }

    public static boolean isRunning() {
        return running;
    }
}
