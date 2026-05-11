package com.kjersti.astryx.api.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kjersti.astryx.api.ApiHandler;
import com.kjersti.astryx.api.object.MatchObject;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.registry.TokenRegistry;
import com.kjersti.astryx.common.util.Duration;
import com.kjersti.astryx.common.util.JsonManager;
import com.kjersti.astryx.sql.SqlBuilder;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class MatchDataHandler {
    public static final Logger LOGGER = AstryxLogManager.getLogger("match");

    public static final String CHAMPIONSHIP_LINK = "https://open.faceit.com/data/v4/championships/";
    public static final String MATCH_LINK = "https://open.faceit.com/data/v4/matches/";

    private static final int THREAD_POOL = 4;
    private static final int LIMIT = 100;

    private static boolean running = false;

    private static Map<String, String> allMapIds = new HashMap<>();

    public static void updateMatchDatabase() {
        if (!running) {
            running = true;

            Thread thread = new Thread(MatchDataHandler::run);
            thread.start();
        } else {
            LOGGER.warn("Already running.");
        }
    }

    public static void updateMatchDatabase(boolean useNewThread) {
        if (!running) {
            running = true;

            if (useNewThread) {
                Thread thread = new Thread(MatchDataHandler::run);
                thread.start();
            } else {
                MatchDataHandler.run();
            }
        } else {
            LOGGER.warn("Already running.");
        }
    }

    private static void run() {
        allMapIds = new HashMap<>();
        long startTime = System.nanoTime();
        LOGGER.info("Retrieving matches from API");

        List<String> manualMatchIds = getManualMatchIds();
        List<MatchObject> manualMatchObjects = getObjectFromIdList(manualMatchIds);

        LOGGER.info("Retrieved {} manual matches from API", manualMatchObjects.size());

        List<MatchObject> tournamentMatchObjects = sendAPIRequestConcurrently();
        List<MatchObject> allObjects = new ArrayList<>();

        allObjects.addAll(manualMatchObjects);
        allObjects.addAll(tournamentMatchObjects);

        addToDatabase(allObjects);
        addMapsToDatabase(allMapIds);

        List<String> toArchive = getFinishedChampionships();
        manualMatchObjects.forEach(MatchDataHandler::archiveManualMatch);
        toArchive.forEach(MatchDataHandler::archiveChampionship);

        long endTime = System.nanoTime();

        Duration duration = new Duration(startTime, endTime);
        int min = duration.getMinutes();
        int sec = duration.getSeconds();

        LOGGER.info("Retrieved {} matches from API, ({} manual, {} automatic). Execution time {} minutes, {} seconds",
                allObjects.size(), manualMatchObjects.size(), tournamentMatchObjects.size(), min, sec);

        running = false;
    }

    public static List<String> getManualMatchIds() {
        List<String> matchIds = new ArrayList<>();
        String table = SettingRegistry.getManualMatchTable();

        SqlObject obj = SqlHandler.executeApiQuery("SELECT * FROM " + table + " WHERE `archived`=0;");

        for (String[] row: obj.getDataWithoutColumnNames()) {
            matchIds.add(row[0]);
        }

        return matchIds;
    }

    public static List<String> getFinishedChampionships() {
        String query = SqlBuilder.getFinishedChampionships();

        SqlObject data = SqlHandler.executeApiQuery(query);
        List<String> ids = new ArrayList<>();

        for (String[] row: data.getDataWithoutColumnNames()) {
            ids.add(row[0]);
        }

        return ids;
    }

    private static List<MatchObject> getObjectFromIdList(List<String> matchIds) {
        List<MatchObject> objs = new ArrayList<>();

        for (String matchId: matchIds) {
            List<MatchObject> obj = getSingularMatchFromId(matchId);

            if (obj == null) continue;

            objs.addAll(obj);
        }

        return objs;
    }

    private static List<MatchObject> sendAPIRequestConcurrently() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL);
        List<MatchObject> matches = new ArrayList<>();
        List<String> tournamentIds = getTournamentIds();
        List<String> manualIds = ChampionshipDataHandler.getManualChampionshipIds();

        tournamentIds.addAll(manualIds);

        List<Callable<List<MatchObject>>> tasks = new ArrayList<>();

        for (String id: tournamentIds) {
            tasks.add(() -> fetchMatches(id));
        }

        try {
            List<Future<List<MatchObject>>> results = executorService.invokeAll(tasks);

            for (Future<List<MatchObject>> result : results) {
                matches.addAll(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error executing tasks", e);
        }

        executorService.shutdown();

        return matches;
    }
    private static List<MatchObject> fetchMatches(String tournamentId) {
        List<MatchObject> matches = new ArrayList<>();
        int offset = 0;

        while (true) {
            String requestData = getMatchData(tournamentId, offset, LIMIT);
            Map<String, Object> map;

            try {
                map = JsonManager.readJson(requestData);
            } catch (JsonProcessingException ignored) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> matchList = (List<Map<String, Object>>) map.get("items");

            if (matchList.isEmpty()) {
                break;
            }

            Map<String, String> mapIds = getMapIds(matchList);

            List<MatchObject> list = getObjectsFromMap(matchList);
            matches.addAll(list);

            offset += LIMIT;
        }

        return matches;
    }

    public static List<MatchObject> getSingularMatchFromId(String matchId) {
        String token = TokenRegistry.getFaceitToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        String requestData = ApiHandler.getRequest(MATCH_LINK + matchId, headers, null);
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

        return getObjectsFromMap(singularList);
    }

    private static Map<String, String> getMapIds(List<Map<String, Object>> matches) {
        Map<String, String> mapIds = new HashMap<>();

        for (Map<String, Object> match: matches) {
            Map<String, Object> votingData = (Map<String, Object>) match.get("voting");
            if (votingData == null) continue;
            Map<String, Object> mapVotingData = (Map<String, Object>) votingData.get("map");
            if (mapVotingData == null) continue;

            List<Map<String, Object>> entities = (List<Map<String, Object>>) mapVotingData.get("entities");

            for (Map<String, Object> entity: entities) {
                String gameMapId = (String) entity.get("game_map_id");
                String guid = (String) entity.get("guid");
                String name = (String) entity.get("name");

                mapIds.put(gameMapId, name);
                mapIds.put(guid, name);
            }
        }

        allMapIds.putAll(mapIds);

        return mapIds;
    }

    private static List<MatchObject> getObjectsFromMap(List<Map<String, Object>> matchList) {
        List<MatchObject> list = new ArrayList<>();

        for (Map<String, Object> tournamentData: matchList) {
            String status = (String) tournamentData.get("status");
            Object result = tournamentData.get("results");

            if (status.equals("CANCELLED") || status.equals("PAUSED") || result == null) {
                continue;
            }

            MatchObject obj = new MatchObject(tournamentData);

            list.add(obj);
        }

        return list;
    }

    private static String getMatchData(String tournamentId, int offset, int limit) {
        String token = TokenRegistry.getFaceitToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("offset", String.valueOf(offset));
        parameters.put("limit", String.valueOf(limit));

        return ApiHandler.getRequest(CHAMPIONSHIP_LINK + tournamentId + "/matches", headers, parameters);
    }
    public static List<String> getTournamentIds() {
        String db = SettingRegistry.getApiDatabase();
        String table = SettingRegistry.getChampionshipDataTable();
        SqlObject obj = SqlHandler.executeQuery(db, "SELECT `id` FROM " + table + " WHERE `archived`=0");

        List<String> ids = new ArrayList<>();

        for (String[] row: obj.getDataWithoutColumnNames()) {
            String id = row[0];

            ids.add(id);
        }

        return ids;
    }

    private static void addToDatabase(List<MatchObject> objects) {
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
            List<MatchObject> batch = objects.subList(start, end);
            executeBatch(batch);
        }
    }

    private static void addMapsToDatabase(Map<String, String> objects){
        if (objects.isEmpty()) return;

        int batchSize = 100;
        int totalSize = objects.size();

        for (int start = 0; start < totalSize; start += batchSize) {
            int end = Math.min(start + batchSize, totalSize);
            List<Map.Entry<String, String>> batch = new ArrayList<>(objects.entrySet()).subList(start, end);

            StringBuilder command = new StringBuilder(
                    "INSERT INTO `faceit_maps` (`guid`, `map_name`) VALUES "
            );

            for (Map.Entry<String, String> entry : batch) {
                command.append("(\"")
                        .append(entry.getKey())
                        .append("\", \"")
                        .append(entry.getValue())
                        .append("\"), ");
            }

            command.setLength(command.length() - 2);

            command.append(" ON DUPLICATE KEY UPDATE `map_name` = VALUES(`map_name`)");

            String sql = command.toString();
            SqlHandler.executeApiUpdate(sql);
        }
    }

    private static void executeBatch(List<MatchObject> batch) {
        String matchDataTable = SettingRegistry.getMatchDataTable();

        StringBuilder command = new StringBuilder(
                "INSERT INTO `" +
                        matchDataTable +
                        "` (`competition_id`, `competition_name`, `organizer_id`, `match_id`, `region`, " +
                        "`competition_type`, `team1_name`, `team2_name`, `winner_name`, `loser_name`, " +
                        "`team1_match_score`, `team2_match_score`, `round`, `match_group`, `best_of`, " +
                        "`team1_id`, `team2_id`, `winner_id`, `loser_id`, `team1_roster`, `team2_roster`, " +
                        "`started_at`, `finished_at`, `status`, `archived`, `archived_maps`) VALUES "
        );

        for (int i = 0; i < batch.size(); i++) {
            MatchObject obj = batch.get(i);

            command.append("(")
                    .append("\"").append(obj.getCompetitionId()).append("\", ")
                    .append("\"").append(obj.getCompetitionName()).append("\", ")
                    .append("\"").append(obj.getOrganizerId()).append("\", ")
                    .append("\"").append(obj.getMatchId()).append("\", ")
                    .append("\"").append(obj.getRegion()).append("\", ")
                    .append("\"").append(obj.getCompetitionType()).append("\", ")
                    .append("\"").append(obj.getTeam1Name()).append("\", ")
                    .append("\"").append(obj.getTeam2Name()).append("\", ")
                    .append("\"").append(obj.getWinnerName()).append("\", ")
                    .append("\"").append(obj.getLoserName()).append("\", ")
                    .append(obj.getTeam1MatchScore()).append(", ")
                    .append(obj.getTeam2MatchScore()).append(", ")
                    .append(obj.getRound()).append(", ")
                    .append(obj.getGroup()).append(", ")
                    .append(obj.getBestOf()).append(", ")
                    .append("\"").append(obj.getTeam1Id()).append("\", ")
                    .append("\"").append(obj.getTeam2Id()).append("\", ")
                    .append("\"").append(obj.getWinnerId()).append("\", ")
                    .append("\"").append(obj.getLoserId()).append("\", ")
                    .append("'").append(obj.getTeam1RosterJson()).append("', ")
                    .append("'").append(obj.getTeam2RosterJson()).append("', ")
                    .append(obj.getStartedAt()).append(", ")
                    .append(obj.getFinishedAt()).append(", ")
                    .append("\"").append(obj.getStatus()).append("\"").append(", ")
                    .append(0).append(", ")
                    .append(0)
                    .append(")");

            if (i < batch.size() - 1) {
                command.append(", ");
            }
        }

        command.append("ON DUPLICATE KEY UPDATE" +
                " competition_id = VALUES(competition_id)," +
                " competition_name = VALUES(competition_name)," +
                " organizer_id = VALUES(organizer_id)," +
                " match_id = VALUES(match_id)," +
                " region = VALUES(region)," +
                " competition_type = VALUES(competition_type)," +
                " team1_name = VALUES(team1_name)," +
                " team2_name = VALUES(team2_name)," +
                " winner_name = VALUES(winner_name)," +
                " loser_name = VALUES(loser_name)," +
                " team1_match_score = VALUES(team1_match_score)," +
                " team2_match_score = VALUES(team2_match_score)," +
                " round = VALUES(round)," +
                " match_group = VALUES(match_group)," +
                " best_of = VALUES(best_of)," +
                " team1_id = VALUES(team1_id)," +
                " team2_id = VALUES(team2_id)," +
                " winner_id = VALUES(winner_id)," +
                " loser_id = VALUES(loser_id)," +
                " team1_roster = VALUES(team1_roster)," +
                " team2_roster = VALUES(team2_roster)," +
                " started_at = VALUES(started_at)," +
                " finished_at = VALUES(finished_at)," +
                " status = VALUES(status)," +
                " archived = VALUES(archived);");


        String sql = command.toString();
        SqlHandler.executeApiUpdate(sql);
    }

    private static void archiveManualMatch(MatchObject match) {
        if (!match.getStatus().equals("FINISHED")) {
            return;
        }

        String matchId = match.getMatchId();
        String table = SettingRegistry.getManualMatchTable();
        String query = "UPDATE " + table + " SET `archived`=1 WHERE `match_id`='" + matchId + "';";

        SqlHandler.executeApiUpdate(query);
    }

    private static void archiveChampionship(String id) {
        String table = SettingRegistry.getChampionshipDataTable();
        String query = "UPDATE " + table + " SET `archived`=1 WHERE `id`='" + id + "';";

        SqlHandler.executeApiUpdate(query);
    }

    public static boolean isRunning() {
        return running;
    }
}
