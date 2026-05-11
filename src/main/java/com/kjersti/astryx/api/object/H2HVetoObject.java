package com.kjersti.astryx.api.object;

import com.kjersti.astryx.common.util.StringUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class H2HVetoObject {
    private final String date;
    private final long finishedAt;
    private final String matchId;
    private final String team1Id;
    private final String team2Id;
    private final String pickedGuid;
    private final String droppedGuid;
    private final boolean random;
    private final int round;
    private final String selectedBy;

    public H2HVetoObject(VetoObject pickedVeto, VetoObject droppedVeto) {
        finishedAt = pickedVeto.getFinishedAt();
        date = pickedVeto.getDate();
        matchId = pickedVeto.getMatchId();
        team1Id = pickedVeto.getTeam1Id();
        team2Id = pickedVeto.getTeam2Id();
        pickedGuid = pickedVeto.getGuid();
        droppedGuid = droppedVeto.getGuid();
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

    public String getPickedGuid() {
        return pickedGuid;
    }

    public String getDroppedGuid() {
        return droppedGuid;
    }

    public boolean isRandom() {
        return random;
    }

    public int getRound() {
        return round;
    }

    public String getSelectedBy() {
        return selectedBy;
    }

    @Override
    public String toString() {
        return "H2HVetoObject{" +
                "date='" + date + '\'' +
                ", finishedAt=" + finishedAt +
                ", matchId='" + matchId + '\'' +
                ", team1Id='" + team1Id + '\'' +
                ", team2Id='" + team2Id + '\'' +
                ", pickedGuid='" + pickedGuid + '\'' +
                ", droppedGuid='" + droppedGuid + '\'' +
                ", random=" + random +
                ", round=" + round +
                ", selectedBy='" + selectedBy + '\'' +
                '}';
    }
}
