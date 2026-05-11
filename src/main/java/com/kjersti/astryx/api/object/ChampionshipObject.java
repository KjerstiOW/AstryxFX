//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.api.object;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.util.JsonManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChampionshipObject {
    private String id;
    private String name;
    private String organizerId;
    private String type;
    private String status;
    private String region;
    private Long checkinStart;
    private Integer slots;
    private String joinPolicy;
    private Integer totalRounds;
    private Integer totalGroups;
    private Integer offset;

    public ChampionshipObject(String id, String name, String organizerId, String type, String status, String region, Long checkinStart, Integer slots, String joinPolicy, Integer totalRounds, Integer totalGroups, Integer offset) {
        this.id = id;
        this.name = name;
        this.organizerId = organizerId;
        this.type = type;
        this.status = status;
        this.region = region;
        this.checkinStart = checkinStart;
        this.slots = slots;
        this.joinPolicy = joinPolicy;
        this.totalRounds = totalRounds;
        this.totalGroups = totalGroups;
        this.offset = offset;
    }

    public ChampionshipObject(Map<String, Object> tournamentData, int offset) {
        try {
            Map<String, Object> joinChecks = (Map<String, Object>) tournamentData.get("join_checks");

            this.id = (String) tournamentData.get("id");
            this.name = (String) tournamentData.get("name");
            this.organizerId = (String) tournamentData.get("organizer_id");
            this.type = (String) tournamentData.get("type");
            this.status = (String) tournamentData.get("status");
            this.region = (String) tournamentData.get("region");
            this.checkinStart = (Long) tournamentData.get("checkin_start");
            this.slots = (Integer) tournamentData.get("slots");
            this.joinPolicy = (String) joinChecks.get("join_policy");
            this.totalRounds = (Integer) tournamentData.get("total_rounds");
            this.totalGroups = (Integer) tournamentData.get("total_groups");
            this.offset = offset;
        } catch (Exception e) {
            this.id = null;
            this.name = null;
            this.organizerId = null;
            this.type = null;
            this.status = null;
            this.region = null;
            this.checkinStart = null;
            this.slots = null;
            this.joinPolicy = null;
            this.totalRounds = null;
            this.totalGroups = null;
            this.offset = null;

            Astryx.LOGGER.info("Error creating " + this.getClass().getSimpleName() + " object from Json. Error: " + e);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getRegion() {
        return region;
    }

    public Long getCheckinStart() {
        return checkinStart;
    }

    public Integer getSlots() {
        return slots;
    }

    public String getJoinPolicy() {
        return joinPolicy;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public Integer getTotalGroups() {
        return totalGroups;
    }

    public Integer getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "ChampionshipObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", organizerId='" + organizerId + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", region='" + region + '\'' +
                ", checkinStart=" + checkinStart +
                ", slots=" + slots +
                ", joinPolicy='" + joinPolicy + '\'' +
                ", totalRounds=" + totalRounds +
                ", totalGroups=" + totalGroups +
                '}';
    }
}
