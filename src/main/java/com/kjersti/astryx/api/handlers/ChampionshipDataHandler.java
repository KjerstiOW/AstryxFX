package com.kjersti.astryx.api.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kjersti.astryx.api.ApiHandler;
import com.kjersti.astryx.api.object.MatchObject;
import com.kjersti.astryx.common.registry.TokenRegistry;
import com.kjersti.astryx.common.util.ActionObject;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.api.object.ChampionshipObject;
import com.kjersti.astryx.common.util.Duration;
import com.kjersti.astryx.common.util.JsonManager;
import com.kjersti.astryx.common.util.StringUtil;
import com.kjersti.astryx.sql.SqlBuilder;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ChampionshipDataHandler {
    public static final Logger LOGGER = AstryxLogManager.getLogger("api");

    public static final String CHAMPIONSHIP_LINK = "https://open.faceit.com/data/v4/championships";

    private static ActionObject primaryAction = null;

    public static void updateChampionshipDatabase(boolean useNewThread, int startingOffset) {
        boolean isRunning = false; //ApiHandler.hasUser();

        if (!isRunning) {
            LOGGER.info("Retrieving championships from API");

            if (useNewThread) {
                Thread thread = new Thread(() -> ChampionshipDataHandler.run(startingOffset));
                thread.start();
            } else {
                run(startingOffset);
            }
        } else {
            //LOGGER.warn("Concurrent requests are already being sent by " + user);
        }
    }

    public static void updateChampionshipDatabase(boolean useNewThread) {
        int initialOffset = getStartingOffset();

        LOGGER.info("No offset given. Implied offset: {}", initialOffset);

        updateChampionshipDatabase(useNewThread, initialOffset);
    }
    private static void run(int initialOffset) {
        long startTime = System.nanoTime();

        List<ChampionshipObject> manualChampionshipObjects = getManualObjects();

        LOGGER.info("Retrieving automatic championships from API");

        List<ChampionshipObject> allObjects = sendAPIRequestConcurrently(initialOffset);
        List<ChampionshipObject> filteredObjects = filterByName(allObjects);

        addToDatabase(filteredObjects);

        long endTime = System.nanoTime();

        finishRequest(startTime, endTime, filteredObjects.size(), manualChampionshipObjects.size(), allObjects.size());
    }

    public static int getStartingOffset() {
        String command = SqlBuilder.getStartingChampionshipOffset();

        SqlObject sqlObj = SqlHandler.executeApiQuery(command);

        if (sqlObj.getDataWithoutColumnNames().length == 0) {
            command = SqlBuilder.getFinalChampionshipOffset();

            sqlObj = SqlHandler.executeApiQuery(command);
        }

        if (sqlObj.getDataWithoutColumnNames().length == 0) {
            return 0;
        }

        String offsetStr = sqlObj.getDataWithoutColumnNames()[0][0];

        if (StringUtil.canBeCastedToInt(offsetStr)) {
            return Integer.parseInt(offsetStr);
        }

        return 0;
    }

    public static List<String> getManualChampionshipIds() {
        List<String> matchIds = new ArrayList<>();
        String table = SettingRegistry.getManualChampionshipTable();

        SqlObject obj = SqlHandler.executeApiQuery("SELECT * FROM " + table);

        for (String[] row: obj.getDataWithoutColumnNames()) {
            matchIds.add(row[0]);
        }

        return matchIds;
    }

    private static List<ChampionshipObject> getManualObjects() {
        List<String> manualChampionshipIds = getManualChampionshipIds();
        List<ChampionshipObject> manualChampionshipObjects = getObjectFromIdList(manualChampionshipIds);

        LOGGER.info("Retrieved {} manual championships from API", manualChampionshipObjects.size());

        return manualChampionshipObjects;
    }

    private static List<ChampionshipObject> getObjectFromIdList(List<String> championshipIds) {
        List<ChampionshipObject> objs = new ArrayList<>();

        for (String championshipId: championshipIds) {
            List<ChampionshipObject> obj = getSingularChampionshipFromId(championshipId);

            if (obj == null) continue;

            objs.addAll(obj);
        }

        return objs;
    }

    public static List<ChampionshipObject> getSingularChampionshipFromId(String championshipId) {
        String token = TokenRegistry.getFaceitToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        String requestData = ApiHandler.getRequest(CHAMPIONSHIP_LINK + "/" + championshipId,
                headers, null, true);
        Map<String, Object> map;

        try {
            map = JsonManager.readJson(requestData);
        } catch (JsonProcessingException ignored) {
            return null;
        }

        if (map.get("results") == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> singularList = new ArrayList<>();
        singularList.add(map);

        return getObjectsFromMap(singularList, -1);
    }

    private static List<ChampionshipObject> sendAPIRequestConcurrently(int initialOffset) {
        primaryAction = new ActionObject("Sending API requests concurrently", 0);

        int threadPool = SettingRegistry.getConcurrentThreadCount();
        int championshipLimit = SettingRegistry.getMaxChampionshipCount();

        List<ChampionshipObject> championships = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        List<Future<List<ChampionshipObject>>> futures = new ArrayList<>();

        for (int i = 0; i < threadPool; i++) {
            int finalOffset = initialOffset + (i * championshipLimit);

            Callable<List<ChampionshipObject>> task = () -> fetchChampionships(finalOffset);
            futures.add(executorService.submit(task));

            primaryAction.setPos(finalOffset);
        }

        for (Future<List<ChampionshipObject>> future : futures) {
            try {
                championships.addAll(future.get());
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }

        executorService.shutdown();
        return championships;
    }

    private static List<ChampionshipObject> fetchChampionships(int offset) {
        List<ChampionshipObject> championships = new ArrayList<>();

        int threadPool = SettingRegistry.getConcurrentThreadCount();
        int championshipLimit = SettingRegistry.getMaxChampionshipCount();

        while (true) {
            String requestData = getTournamentData(offset, championshipLimit);

            Map<String, Object> map;

            try {
                map = JsonManager.readJson(requestData);
            } catch (JsonProcessingException ignored) {
                return new ArrayList<>();
            }
            List<Map<String, Object>> tournamentList = (List<Map<String, Object>>) map.get("items");

            if (tournamentList.isEmpty()) {
                break;
            }

            List<ChampionshipObject> list = getObjectsFromMap(tournamentList, offset);
            championships.addAll(list);

            offset += championshipLimit * threadPool;

        }
        return championships;
    }

    private static String getTournamentData(int offset, int limit) {
        String token = TokenRegistry.getFaceitToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        String gameId = SettingRegistry.getGameId();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("game", gameId);
        parameters.put("offset", String.valueOf(offset));
        parameters.put("limit", String.valueOf(limit));

        return ApiHandler.getRequest(CHAMPIONSHIP_LINK, headers, parameters);
    }

    private static List<ChampionshipObject> getObjectsFromMap(List<Map<String, Object>> tournamentList, int offset) {
        List<ChampionshipObject> list = new ArrayList<>();

        for (Map<String, Object> tournamentData: tournamentList) {
            ChampionshipObject obj = new ChampionshipObject(tournamentData, offset);

            if (obj.getStatus().equals("cancelled")) {
                continue;
            }

            list.add(obj);
        }

        return list;
    }

    private static List<ChampionshipObject> filterByName(List<ChampionshipObject> toFilter) {
        List<String> nameBlacklist = SettingRegistry.getStringList("championship_blacklist");

        return toFilter.stream()
                .filter(obj -> nameBlacklist.stream()
                        .noneMatch(str -> obj.getName().contains(str)))
                .toList();
    }

    private static void addToDatabase(List<ChampionshipObject> objects) {
        primaryAction = new ActionObject("Adding to database", objects.size());

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

            List<ChampionshipObject> batch = objects.subList(start, end);
            executeBatch(batch);
        }
    }

    private static void executeBatch(List<ChampionshipObject> batch) {
        String command = SqlBuilder.buildAddChampionshipCommand(batch);

        SqlHandler.executeApiUpdate(command);
    }

    public static void deleteAll() {
        String championshipDataTable = SettingRegistry.getChampionshipDataTable();
        String command = "DELETE FROM " + championshipDataTable + " WHERE 1=1";

        SqlHandler.executeApiUpdate(command);

        String apiDatabase = SettingRegistry.getApiDatabase();

        LOGGER.warn("Deleted all data from " + StringUtil.getSQLLocation(apiDatabase, championshipDataTable));
    }

    public static void finishRequest(long startTime, long endTime, int filteredCount, int manualCount, int totalCount) {
        Duration duration = new Duration(startTime, endTime);
        int min = duration.getMinutes();
        int sec = duration.getSeconds();

        LOGGER.info("Retrieved {} championships from API, ({} manual, {} automatic, {} removed). Execution time {} minutes, {} seconds",
                filteredCount, manualCount, totalCount, totalCount-filteredCount, min, sec);

        ApiHandler.resetUser();
        ChampionshipDataHandler.primaryAction = null;
    }

    public static String getActionString() {
        if (primaryAction == null) {
            return "Not performing any action";
        }

        if (primaryAction.getPos() == -1) {
            return primaryAction.getAction();
        }

        if (primaryAction.getTotalPos() == -1) {
            return primaryAction.getAction() +
                    ", current amount: " + primaryAction.getPos();
        }

        return primaryAction.getAction() +
                ", current amount: " + primaryAction.getPos() +
                "/" + primaryAction.getTotalPos();
    }
}
