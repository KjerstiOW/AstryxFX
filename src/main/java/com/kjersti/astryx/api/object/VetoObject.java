package com.kjersti.astryx.api.object;

import com.kjersti.astryx.common.util.StringUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class VetoObject {
    private final String date;
    private final long finishedAt;
    private final String matchId;
    private final String team1Id;
    private final String team2Id;
    private final String guid;
    private final String status;
    private final boolean random;
    private final int round;
    private final int vetoIndex;
    private final String selectedBy;

    public VetoObject(Map<String, Object> vetoData, VetoPrecursorObject precursorObject, int vetoIndex) {
        finishedAt = precursorObject.getFinishedAt();
        this.vetoIndex = vetoIndex;
        date = StringUtil.formatUnix(finishedAt);
        matchId = precursorObject.getMatchId();
        team1Id = precursorObject.getTeam1Id();
        team2Id = precursorObject.getTeam2Id();
        guid = (String) vetoData.get("guid");
        status = (String) vetoData.get("status");
        random = (Boolean) vetoData.get("random");
        round = (Integer) vetoData.get("round");

        if ((vetoData.get("selected_by")).equals("faction1")) {
            selectedBy = team1Id;
        } else {
            selectedBy = team2Id;
        }
    }

    public VetoObject(String[] row) {
        finishedAt = -1;
        date = row[0];
        vetoIndex = Integer.parseInt(row[1]);
        matchId = row[2];
        team1Id = row[3];
        team2Id = row[4];
        guid = row[5];
        status = row[6];
        random = StringUtil.parseBooleanFromBinaryString(row[7]);
        round = Integer.parseInt(row[8]);
        selectedBy = row[9];
    }

    public VetoObject(VetoObject pickedVeto, VetoObject droppedVeto) {
        date = pickedVeto.getDate();
        finishedAt = pickedVeto.getFinishedAt();
        vetoIndex = pickedVeto.getVetoIndex();
        matchId = pickedVeto.getMatchId();
        team1Id = pickedVeto.getTeam1Id();
        team2Id = pickedVeto.getTeam2Id();
        guid = droppedVeto.getGuid();
        status = "drop";
        random = pickedVeto.isRandom();
        round = pickedVeto.getRound();
        selectedBy = pickedVeto.getSelectedBy();
    }

    public String getDate() {
        return date;
    }

    public long getFinishedAt() {
        return finishedAt;
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

    public String getGuid() {
        return guid;
    }

    public String getStatus() {
        return status;
    }

    public boolean isRandom() {
        return random;
    }

    public int getRound() {
        return round;
    }

    public int getVetoIndex() {
        return vetoIndex;
    }

    public String getSelectedBy() {
        return selectedBy;
    }

    @Override
    public String toString() {
        return "VetoObject{" +
                "date='" + date + '\'' +
                ", finishedAt=" + finishedAt +
                ", matchId='" + matchId + '\'' +
                ", team1Id='" + team1Id + '\'' +
                ", team2Id='" + team2Id + '\'' +
                ", guid='" + guid + '\'' +
                ", status='" + status + '\'' +
                ", random=" + random +
                ", round=" + round +
                ", vetoIndex=" + vetoIndex +
                ", selectedBy='" + selectedBy + '\'' +
                '}';
    }
}
