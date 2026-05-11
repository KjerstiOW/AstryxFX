package com.kjersti.astryx.api.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjersti.astryx.api.handlers.MatchDataHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VetoPrecursorObject {
    private final String matchId;
    private final String team1Id;
    private final String team2Id;
    private final long finishedAt;

    public VetoPrecursorObject(String[] row) {
        matchId = row[0];
        team1Id = row[1];
        team2Id = row[2];
        finishedAt = Long.parseLong(row[3]);
    }

    public String getMatchId() {
        return matchId;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public long getFinishedAt() {
        return finishedAt;
    }
}
