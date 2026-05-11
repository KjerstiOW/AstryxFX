package com.kjersti.astryx.api.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjersti.astryx.api.handlers.MatchDataHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchObject {
    private final String competitionId;
    private final String competitionName;
    private final String organizerId;
    private final String matchId;
    private final String region;
    private final String competitionType;
    private final String team1Name;
    private final String team2Name;
    private final String winnerName;
    private final String loserName;
    private final int team1MatchScore;
    private final int team2MatchScore;
    private final int round;
    private final int group;
    private final int bestOf;
    private final String team1Id;
    private final String team2Id;
    private final String winnerId;
    private final String loserId;
    private final List<FaceitPlayerObject> team1Roster;
    private final List<FaceitPlayerObject> team2Roster;
    private final long startedAt;
    private final long finishedAt;
    private final String status;

    public MatchObject(Map<String, Object> matchData) {
        if (matchData.get("started_at") == null) {
            this.startedAt = 0;
        } else {
            this.startedAt = Long.parseLong(String.valueOf(matchData.get("started_at")));
        }

        if (matchData.get("finished_at") == null) {
            this.finishedAt = 0;
        } else {
            this.finishedAt = Long.parseLong(String.valueOf(matchData.get("finished_at")));
        }

        Map<String, Map<String, Object>> teams = (Map<String, Map<String, Object>>) matchData.get("teams");
        Map<String, Object> team1Data = teams.get("faction1");
        Map<String, Object> team2Data = teams.get("faction2");
        Map<String, Object> resultData = (Map<String, Object>) matchData.get("results");
        Map<String, Integer> scoreData = (Map<String, Integer>) resultData.get("score");

        this.competitionId = (String) matchData.get("competition_id");
        this.competitionName = (String) matchData.get("competition_name");
        this.organizerId = (String) matchData.get("organizer_id");
        this.matchId = (String) matchData.get("match_id");
        this.region = (String) matchData.get("region");
        this.competitionType = (String) matchData.get("competition_type");
        this.team1Name = (String) team1Data.get("name");
        this.team2Name = (String) team2Data.get("name");
        this.team1Id = (String) team1Data.get("faction_id");
        this.team2Id = (String) team2Data.get("faction_id");
        this.winnerId = getWinnerId(resultData, team1Id, team2Id);
        this.loserId = getWinnerId(resultData, team2Id, team1Id);
        this.winnerName = getWinnerId(resultData, team1Name, team2Name);
        this.loserName = getWinnerId(resultData, team2Name, team1Name);
        this.team1MatchScore = scoreData.get("faction1");
        this.team2MatchScore = scoreData.get("faction2");
        this.group = (int) matchData.get("group");
        this.round = (int) matchData.get("round");
        this.bestOf = (int) matchData.get("best_of");
        this.status = (String) matchData.get("status");
        this.team1Roster = getRosterFromRaw(team1Data);
        this.team2Roster = getRosterFromRaw(team2Data);
    }

    public String getWinnerId(Map<String, Object> resultData, String team1Id, String team2Id) {
        String winnerString = (String) resultData.get("winner");

        if (winnerString.equalsIgnoreCase("faction1")) {
            return team1Id;
        }

        return team2Id;
    }

    public List<FaceitPlayerObject> getRosterFromRaw(Map<String, Object> teamData) {
        List<Map<String, Object>> playerList = (List<Map<String, Object>>) teamData.get("roster");
        List<FaceitPlayerObject> roster = new ArrayList<>();

        if (playerList == null) {
            return new ArrayList<>();
        }

        for (Map<String, Object> playerMap: playerList) {
            FaceitPlayerObject player = new FaceitPlayerObject(playerMap);

            roster.add(player);
        }

        return roster;
    }

    public String getTeam1RosterJson() {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> dictionariedRoster = team1Roster.stream()
                .map(FaceitPlayerObject::getPlayerDict)
                .toList();

        try {
            return mapper.writeValueAsString(dictionariedRoster);
        } catch (JsonProcessingException e) {
            MatchDataHandler.LOGGER.error("Could not process team 1 roster to JSON");

            return null;
        }
    }

    public String getTeam2RosterJson() {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> dictionariedRoster = team2Roster.stream()
                .map(FaceitPlayerObject::getPlayerDict)
                .toList();

        try {
            return mapper.writeValueAsString(dictionariedRoster);
        } catch (JsonProcessingException e) {
            MatchDataHandler.LOGGER.error("Could not process team 2 roster to JSON");

            return null;
        }
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getRegion() {
        return region;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getLoserName() {
        return loserName;
    }

    public int getTeam1MatchScore() {
        return team1MatchScore;
    }

    public int getTeam2MatchScore() {
        return team2MatchScore;
    }

    public int getRound() {
        return round;
    }

    public int getGroup() {
        return group;
    }

    public int getBestOf() {
        return bestOf;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public String getLoserId() {
        return loserId;
    }

    public List<FaceitPlayerObject> getTeam1Roster() {
        return team1Roster;
    }

    public List<FaceitPlayerObject> getTeam2Roster() {
        return team2Roster;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "MatchObject{" +
                "competitionId='" + competitionId + '\'' +
                ", competitionName='" + competitionName + '\'' +
                ", organizerId='" + organizerId + '\'' +
                ", matchId='" + matchId + '\'' +
                ", region='" + region + '\'' +
                ", competitionType='" + competitionType + '\'' +
                ", team1Name='" + team1Name + '\'' +
                ", team2Name='" + team2Name + '\'' +
                ", winnerName='" + winnerName + '\'' +
                ", loserName='" + loserName + '\'' +
                ", team1MatchScore=" + team1MatchScore +
                ", team2MatchScore=" + team2MatchScore +
                ", round=" + round +
                ", group=" + group +
                ", bestOf=" + bestOf +
                ", team1Id='" + team1Id + '\'' +
                ", team2Id='" + team2Id + '\'' +
                ", winnerId='" + winnerId + '\'' +
                ", loserId='" + loserId + '\'' +
                ", team1Roster=" + team1Roster +
                ", team2Roster=" + team2Roster +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", status='" + status + '\'' +
                '}';
    }
}
