//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.parsing;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.registry.PatchRegistry;
import com.kjersti.astryx.parsing.exceptions.InvalidMapException;
import com.kjersti.astryx.parsing.object.OverwatchMap;
import com.kjersti.astryx.sql.SqlHandler;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class MessageParseManager {
    private final MessageParser messageParser;

    public MessageParseManager(String message) {
        this.messageParser = new MessageParser(message);
    }

    public List<OverwatchMap> parse() throws InvalidMapException {
        List<OverwatchMap> rawMaps = this.messageParser.parse();
        if (rawMaps == null) {
            return null;
        } else {
            MapParser parser = new MapParser(rawMaps);
            rawMaps = parser.parseMaps();

            if (rawMaps == null) {
                OverwatchMap invalidMap = parser.findFirstInvalidMap();
                throw new InvalidMapException("Could not verify map name `" + invalidMap.getMapName() + "`", invalidMap);
            } else {
                return rawMaps;
            }
        }
    }

    public void addMapsToDatabase(List<OverwatchMap> overwatchMaps, String sqlLocation) {
        overwatchMaps.forEach((obj) -> {
            this.addToDatabase(obj, sqlLocation);
        });
        Logger var10000 = Astryx.LOGGER;
        int var10001 = overwatchMaps.size();
        var10000.info("Added " + var10001 + " map(s) to " + sqlLocation);
    }

    public void addToDatabase(OverwatchMap map, String sqlLocation) {
        String header = map.getHeader();
        String date = this.getDate();
        String patch = PatchRegistry.getPatch();
        String type = "Scrim";
        String opponent = map.getOpponent();
        String mapId = map.getMapId();
        String mapMode = map.getMapType();
        String vod = map.getVodCode();
        int friendlyScore = map.getFriendlyScore();
        int enemyScore = map.getEnemyScore();
        String query = "INSERT INTO " + sqlLocation + "(`header`, `date`, `patch`, `type`, `opponent`, `map`, `mode`, `friendly_score`, `opponent_score`, `vod`) VALUES ('" + header + "', '" + date + "', '" + patch + "', '" + type + "', '" + opponent + "', '" + mapId + "', '" + mapMode + "'," + friendlyScore + ", " + enemyScore + ",'" + vod + "');";
        SqlHandler.executeMapUpdate(query);
    }

    public String getDate() {
        return LocalDate.now().toString();
    }
}
