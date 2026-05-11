package com.kjersti.astryx.api.object;

import com.kjersti.astryx.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class FaceitStatsObject {
    private final String playerId;
    private final String playerNickname;
    private final String playerRole;
    private final int playerEliminations;
    private final int playerAssists;
    private final int playerDeaths;
    private final int playerDamageDealt;
    private final int playerHealingDone;
    private final int playerDamageMitigated;

    public FaceitStatsObject(Map<String, Object> playerMap) {
        this.playerId = (String) playerMap.get("player_id");
        this.playerNickname = (String) playerMap.get("nickname");

        Map<String, Object> statsMap = (Map<String, Object>) playerMap.get("player_stats");
        this.playerRole = (String) statsMap.get("Role");
        this.playerEliminations = Integer.parseInt((String) statsMap.get("Eliminations"));
        this.playerAssists = Integer.parseInt((String) statsMap.get("Assists"));
        this.playerDeaths = Integer.parseInt((String) statsMap.get("Deaths"));;
        this.playerDamageDealt = Integer.parseInt((String) statsMap.get("Damage Dealt"));
        this.playerHealingDone = Integer.parseInt((String) statsMap.get("Healing Done"));
        this.playerDamageMitigated = Integer.parseInt((String) statsMap.get("Damage Mitigated"));
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public String getPlayerRole() {
        return playerRole;
    }

    public int getPlayerEliminations() {
        return playerEliminations;
    }

    public int getPlayerAssists() {
        return playerAssists;
    }

    public int getPlayerDeaths() {
        return playerDeaths;
    }

    public int getPlayerDamageDealt() {
        return playerDamageDealt;
    }

    public int getPlayerHealingDone() {
        return playerHealingDone;
    }

    public int getPlayerDamageMitigated() {
        return playerDamageMitigated;
    }

    @Override
    public String toString() {
        return "FaceitStatsObject{" +
                "playerId='" + playerId + '\'' +
                ", playerNickname='" + playerNickname + '\'' +
                ", playerRole='" + playerRole + '\'' +
                ", playerEliminations=" + playerEliminations +
                ", playerAssists=" + playerAssists +
                ", playerDeaths=" + playerDeaths +
                ", playerDamageDealt=" + playerDamageDealt +
                ", playerHealingDone=" + playerHealingDone +
                ", playerDamageMitigated=" + playerDamageMitigated +
                '}';
    }
}
