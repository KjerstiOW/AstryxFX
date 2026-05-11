//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.embed;

import com.kjersti.astryx.common.lang.LanguageLoader;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.Instant;

public class VetoWaitingResponseEmbed {
    public static final String EMBED_LANGUAGE_LOCATION = "bot.embed.";
    private final EmbedCreateSpec.Builder builder;

    public VetoWaitingResponseEmbed(String teamId, String teamName) {
        String footer = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "footer");
        String title = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "veto_fetching_response.title")
                .replace("{team_id}", teamId)
                .replace("{team_name}", teamName);
        String desc = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "veto_fetching_response.desc")
                .replace("{team_id}", teamId)
                .replace("{team_name}", teamName);

        this.builder = EmbedCreateSpec.builder()
                .color(Color.MAGENTA)
                .title(title)
                .description(desc)
                .timestamp(Instant.now())
                .footer(footer, null);
    }

    public EmbedCreateSpec getEmbed() {
        return this.builder.build();
    }
}
