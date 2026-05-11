//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.parsing.object;

import com.kjersti.astryx.common.registry.MapRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OverwatchMap {
    private final String header;
    private final String opponent;
    private final String mapName;
    private final int friendlyScore;
    private final int enemyScore;
    private final String vodCode;
    private List<OverwatchMap> submaps;
    private String mapId;
    private String mapType;
    private final String line;
    private final int lineNumber;

    public OverwatchMap(String line, int lineNumber, String header, String opponent, String mapName, int friendlyScore, int enemyScore, String vodCode) {
        this.line = line;
        this.lineNumber = lineNumber;
        this.header = header;
        this.opponent = opponent;
        this.mapName = mapName;
        this.friendlyScore = friendlyScore;
        this.enemyScore = enemyScore;
        this.vodCode = vodCode;
        this.submaps = new ArrayList<>();
    }

    public OverwatchMap(String line, int lineNumber, OverwatchMap parentMap, int friendlyScore, int enemyScore, String submapId) {
        this.line = line;
        this.lineNumber = lineNumber;
        this.header = parentMap.getHeader();
        this.opponent = parentMap.getOpponent();
        this.mapName = "";
        this.friendlyScore = friendlyScore;
        this.enemyScore = enemyScore;
        this.vodCode = parentMap.getVodCode();
        this.mapId = submapId;
        this.mapType = MapRegistry.getTypeFromId(submapId);
        this.submaps = new ArrayList<>();
    }

    public String getHeader() {
        return this.header;
    }

    public String getOpponent() {
        return this.opponent;
    }

    public String getMapName() {
        return this.mapName;
    }

    public int getFriendlyScore() {
        return this.friendlyScore;
    }

    public int getEnemyScore() {
        return this.enemyScore;
    }

    public String getVodCode() {
        return this.vodCode;
    }

    public void addSubmap(OverwatchMap submap) {
        this.submaps.add(submap);
    }

    public List<OverwatchMap> getSubmaps() {
        return this.submaps.stream().toList();
    }

    public void setSubmaps(List<OverwatchMap> submaps) {
        this.submaps = submaps;
    }

    public void addImplicitSubmaps() {
        if (!this.hasSubmaps()) {
            String[] submapIds = MapRegistry.getSubmapsFromParentId(this.mapId);
            if (this.mapType.equals("control")) {
                if (this.friendlyScore == 3 && this.enemyScore == 0) {
                    this.addImplicitSubmaps(submapIds, 1, 0);
                } else if (this.friendlyScore == 0 && this.enemyScore == 3) {
                    this.addImplicitSubmaps(submapIds, 0, 1);
                }
            } else if (this.mapType.equals("flashpoint")) {
                if (this.friendlyScore == 5 && this.enemyScore == 0) {
                    this.addImplicitSubmaps(submapIds, 1, 0);
                } else if (this.friendlyScore == 0 && this.enemyScore == 5) {
                    this.addImplicitSubmaps(submapIds, 0, 1);
                }
            }
        }

    }

    private void addImplicitSubmaps(String[] submapIds, int friendlySubmapScore, int enemySubmapScore) {
        for (String submapId : submapIds) {
            OverwatchMap implicitSubmap = new OverwatchMap("", -1, this, friendlySubmapScore, enemySubmapScore, submapId);
            this.submaps.add(implicitSubmap);
        }
    }

    public boolean hasSubmaps() {
        return this.submaps.size() > 0;
    }

    public void removeSubmaps() {
        this.submaps.clear();
    }

    public boolean hasMapId() {
        return this.mapId != null;
    }

    public String getMapId() {
        return this.mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getMapType() {
        return this.mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getLine() {
        return this.line;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setMetadata() {
        String mapId = MapRegistry.getMapIdFromName(this.getMapName());

        String mapType = MapRegistry.getTypeFromId(mapId);

        this.getSubmaps().forEach(OverwatchMap::setMetadata);

        this.setMapId(mapId);
        this.setMapType(mapType);
    }

    public boolean verifyMap() {
        boolean verifiedSubmaps = this.getSubmaps().stream().allMatch(OverwatchMap::hasMapId);
        return this.hasMapId() && verifiedSubmaps;
    }

    public String toString() {
        return "OverwatchMap{id='" + this.mapId + "', header='" + this.header + "', opponent='" + this.opponent + "', mapName='" + this.mapName + "', friendlyScore=" + this.friendlyScore + ", enemyScore=" + this.enemyScore + ", vodCode='" + this.vodCode + "', submaps=" + this.submaps + "}";
    }
}
