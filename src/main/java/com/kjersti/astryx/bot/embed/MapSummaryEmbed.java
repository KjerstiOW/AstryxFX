//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.embed;

import com.kjersti.astryx.common.lang.LanguageLoader;
import com.kjersti.astryx.parsing.object.OverwatchMap;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.Instant;
import java.util.List;

public class MapSummaryEmbed {
    public static final String EMBED_LANGUAGE_LOCATION = "bot.embed.";
    public static final String MAP_LANGUAGE_LOCATION = "maps.med.";
    private final EmbedCreateSpec.Builder builder;
    private final List<OverwatchMap> maps;

    public MapSummaryEmbed(List<OverwatchMap> maps, String sqlLocation) {
        String footer = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "footer");
        String title = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "map_embed_summary.title");
        String desc = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "map_embed_summary.desc");
        desc = desc.replace("{sql_location}", sqlLocation);

        this.maps = maps;
        this.builder = EmbedCreateSpec.builder()
                .color(Color.MAGENTA)
                .title(title)
                .description(desc).
                timestamp(Instant.now())
                .footer(footer, null);
        this.addFields();
    }

    private void addFields() {
        this.builder.addField(this.getFieldTitle(), this.getFieldDesc(), false);
    }

    private String getFieldTitle() {
        return (this.maps.get(0)).getHeader() + " vs " + (this.maps.get(0)).getOpponent();
    }

    private String getFieldDesc() {
        StringBuilder mapData = new StringBuilder();

        for (OverwatchMap map : maps) {
            String displayName = LanguageLoader.get(MAP_LANGUAGE_LOCATION + map.getMapId());
            if (map.getMapType().contains("submap")) {
                String winner;
                if (map.getFriendlyScore() == 1 && map.getEnemyScore() == 0) {
                    winner = "W";
                } else if (map.getFriendlyScore() == 0 && map.getEnemyScore() == 1) {
                    winner = "L";
                } else {
                    int var10000 = map.getFriendlyScore();
                    winner = "" + var10000 + "-" + map.getEnemyScore();
                }

                mapData.append("> ").append(displayName).append(" ").append(winner).append("\n");
            } else {
                mapData.append(displayName).append(" ").append(map.getFriendlyScore()).append("-").append(map.getEnemyScore()).append(" `").append(map.getVodCode()).append("`\n");
            }
        }

        return mapData.substring(0, mapData.length() - 1);
    }

    public EmbedCreateSpec getEmbed() {
        return this.builder.build();
    }
}
