package com.kjersti.astryx.api.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjersti.astryx.api.handlers.MatchDataHandler;
import com.kjersti.astryx.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapObject {
    private final long finishedAt;
    private final String date;
    private final String matchId;
    private final int matchRound;
    private final String mapMode;
    private final String mapId;
    private final String team1Id;
    private final int team1Score;
    private final String team1Name;
    private final String team2Id;
    private final int team2Score;
    private final String team2Name;
    private final String winnerId;
    private final String winnerName;
    private final String loserId;
    private final String loserName;
    private final List<FaceitStatsObject> stats;

    public MapObject(Map<String, Object> matchData, List<FaceitStatsObject> players, MapPrecursorObject precursorObject) {
        this.finishedAt = precursorObject.getFinishedAt();
        this.date = StringUtil.formatUnix(precursorObject.getFinishedAt());
        this.matchId = (String) matchData.get("match_id");
        this.matchRound = Integer.parseInt((String) matchData.get("match_round"));

        Map<String, Object> roundStats = (Map<String, Object>) matchData.get("round_stats");
        this.mapMode = ((String) roundStats.get("OW2 Mode")).toLowerCase().replace(" ", "_");
        this.mapId = (String) roundStats.get("Map");

        List<Map<String, Object>> teamList = (List<Map<String, Object>>) matchData.get("teams");
        Map<String, Object> team1List = teamList.get(0);
        Map<String, Object> team2List = teamList.get(1);

        this.team1Id = (String) team1List.get("team_id");
        this.team2Id = (String) team2List.get("team_id");

        Map<String, Object> team1Stats = (Map<String, Object>) team1List.get("team_stats");
        Map<String, Object> team2Stats = (Map<String, Object>) team2List.get("team_stats");

        this.team1Score = Integer.parseInt((String) team1Stats.get("Team Score"));
        this.team2Score = Integer.parseInt((String) team2Stats.get("Team Score"));
        this.team1Name = (String) team1Stats.get("Team");
        this.team2Name = (String) team2Stats.get("Team");

        if (team1Score > team2Score) {
            winnerId = team1Id;
            winnerName = team1Name;

            loserId = team2Id;
            loserName = team2Name;
        } else if (team2Score > team1Score) {
            winnerId = team2Id;
            winnerName = team2Name;

            loserId = team1Id;
            loserName = team1Name;
        } else {
            winnerId = "Draw";
            winnerName = "Draw";
            loserId = "Draw";
            loserName = "Draw";
        }

        this.stats = players;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getMatchRound() {
        return matchRound;
    }

    public String getMapMode() {
        return mapMode;
    }

    public String getMapId() {
        return mapId;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getLoserId() {
        return loserId;
    }

    public String getLoserName() {
        return loserName;
    }

    public List<FaceitStatsObject> getStats() {
        return stats;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public String getDate() {
        return date;
    }

    public String getSqlInsertString() {
        StringBuilder command = new StringBuilder();

        command.append("(")
                .append("\"").append(this.getDate()).append("\", ")
                .append("\"").append(this.getMatchId()).append("\", ")
                .append(this.getMatchRound()).append(", ")
                .append("\"").append(this.getMapMode()).append("\", ")
                .append("\"").append(this.getMapId()).append("\", ")
                .append("\"").append(this.getTeam1Id()).append("\", ")
                .append(this.getTeam1Score()).append(", ")
                .append("\"").append(this.getTeam1Name()).append("\", ")
                .append("\"").append(this.getTeam2Id()).append("\", ")
                .append(this.getTeam2Score()).append(", ")
                .append("\"").append(this.getTeam2Name()).append("\", ")
                .append("\"").append(this.getWinnerId()).append("\", ")
                .append("\"").append(this.getWinnerName()).append("\", ")
                .append("\"").append(this.getLoserId()).append("\", ")
                .append("\"").append(this.getLoserName()).append("\", ");

        for (FaceitStatsObject player: stats) {
            command.append("\"").append(player.getPlayerId()).append("\", ")
                .append("\"").append(player.getPlayerNickname()).append("\", ")
                .append("\"").append(player.getPlayerRole()).append("\", ")
                .append(player.getPlayerEliminations()).append(", ")
                .append(player.getPlayerAssists()).append(", ")
                .append(player.getPlayerDeaths()).append(", ")
                .append(player.getPlayerDamageDealt()).append(", ")
                .append(player.getPlayerHealingDone()).append(", ")
                .append(player.getPlayerDamageMitigated()).append(", ");
        }

        return command.substring(0, command.length()-2) + ")";
    }

    @Override
    public String toString() {
        return "MapObject{" +
                "matchId='" + matchId + '\'' +
                ", matchRound=" + matchRound +
                ", mapMode='" + mapMode + '\'' +
                ", mapId='" + mapId + '\'' +
                ", team1Id='" + team1Id + '\'' +
                ", team1Score=" + team1Score +
                ", team1Name='" + team1Name + '\'' +
                ", team2Id='" + team2Id + '\'' +
                ", team2Score=" + team2Score +
                ", team2Name='" + team2Name + '\'' +
                ", winnerId='" + winnerId + '\'' +
                ", winnerName='" + winnerName + '\'' +
                ", loserId='" + loserId + '\'' +
                ", loserName='" + loserName + '\'' +
                ", stats=" + stats +
                '}';
    }
}
