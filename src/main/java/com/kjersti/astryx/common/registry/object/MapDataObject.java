//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry.object;

public class MapDataObject {
    private final String header;
    private final String date;
    private final String patch;
    private final String type;
    private final String opponent;
    private final String mapId;
    private final String mapModeId;
    private final int friendlyScore;
    private final int opponentScore;
    private final String result;
    private final String vodCode;

    public MapDataObject(String header, String date, String patch, String type, String opponent, String mapId, String mapModeId, int friendlyScore, int opponentScore, String result, String vodCode) {
        this.header = header;
        this.date = date;
        this.patch = patch;
        this.type = type;
        this.opponent = opponent;
        this.mapId = mapId;
        this.mapModeId = mapModeId;
        this.friendlyScore = friendlyScore;
        this.opponentScore = opponentScore;
        this.result = result;
        this.vodCode = vodCode;
    }

    public MapDataObject(String[] row) {
        this.header = row[0];
        this.date = row[1];
        this.patch = row[2];
        this.type = row[3];
        this.opponent = row[4];
        this.mapId = row[5];
        this.mapModeId = row[6];
        this.friendlyScore = Integer.parseInt(row[7]);
        this.opponentScore = Integer.parseInt(row[8]);
        this.result = row[9];
        this.vodCode = row[10];
    }

    public String getHeader() {
        return this.header;
    }

    public String getDate() {
        return this.date;
    }

    public String getPatch() {
        return this.patch;
    }

    public String getType() {
        return this.type;
    }

    public String getOpponent() {
        return this.opponent;
    }

    public String getMapId() {
        return this.mapId;
    }

    public String getMapModeId() {
        return this.mapModeId;
    }

    public int getFriendlyScore() {
        return this.friendlyScore;
    }

    public int getOpponentScore() {
        return this.opponentScore;
    }

    public String getResult() {
        return this.result;
    }

    public String getVodCode() {
        return this.vodCode;
    }

    public String toString() {
        return "MapDataObject{header='" + this.header + "', date='" + this.date + "', patch='" + this.patch + "', type='" + this.type + "', opponent='" + this.opponent + "', mapId='" + this.mapId + "', mapModeId='" + this.mapModeId + "', friendlyScore=" + this.friendlyScore + ", opponentScore=" + this.opponentScore + ", result='" + this.result + "', vodCode='" + this.vodCode + "'}";
    }
}
