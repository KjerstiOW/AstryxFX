//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.embed;

import com.kjersti.astryx.common.lang.LanguageLoader;
import com.kjersti.astryx.parsing.exceptions.InvalidMapException;
import com.kjersti.astryx.parsing.exceptions.MapArgumentException;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.Instant;

public class InvalidNameEmbed {
    public static final String EMBED_LANGUAGE_LOCATION = "bot.embed.";
    private final EmbedCreateSpec.Builder builder;
    private final String teamId;

    public InvalidNameEmbed(String teamId) {
        String footer = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "footer");
        this.teamId = teamId;
        this.builder = EmbedCreateSpec.builder()
                .color(Color.RED)
                .title(this.getEmbedTitle(teamId))
                .timestamp(Instant.now())
                .footer(footer, null);
        this.addDescription(teamId);
    }

    private String getEmbedTitle(String teamId) {
        return "Invalid Team Id";
    }

    private void addDescription(String teamId) {
        this.builder.addField("Team Id", teamId, true);
    }

    public EmbedCreateSpec getEmbed() {
        return this.builder.build();
    }
}
