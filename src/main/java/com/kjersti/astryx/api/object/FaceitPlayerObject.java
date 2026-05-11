package com.kjersti.astryx.api.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceitPlayerObject {
    private final String playerId;
    private final String playerNick;
    private final String playerGameName;

    public FaceitPlayerObject(Map<String, Object> playerMap) {
        this.playerId = (String) playerMap.get("player_id");
        this.playerNick = (String) playerMap.get("nickname");
        this.playerGameName = (String) playerMap.get("game_player_name");
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerNick() {
        return playerNick;
    }

    public String getPlayerGameName() {
        return playerGameName;
    }

    public Map<String, String> getPlayerDict() {
        Map<String, String> playerDict = new HashMap<>();

        playerDict.put("player_id", playerId);
        playerDict.put("player_nick", playerNick);
        playerDict.put("player_game_name", playerGameName);

        return playerDict;
    }

    @Override
    public String toString() {
        return "FaceitPlayerObject{" +
                "playerId='" + playerId + '\'' +
                ", playerNick='" + playerNick + '\'' +
                ", playerGameName='" + playerGameName + '\'' +
                '}';
    }
}
