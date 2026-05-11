package com.kjersti.astryx.api.object;

public class MapPrecursorObject {
    private final String matchId;
    private final long finishedAt;

    public MapPrecursorObject(String[] row) {
        matchId = row[0];
        finishedAt = Long.parseLong(row[1]);
    }

    public String getMatchId() {
        return matchId;
    }

    public long getFinishedAt() {
        return finishedAt;
    }
}
