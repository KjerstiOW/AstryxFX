//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.embed;

import com.kjersti.astryx.common.lang.LanguageLoader;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.Instant;

public class LackOfPermsEmbed {
    public static final String EMBED_LANGUAGE_LOCATION = "bot.embed.";
    private final EmbedCreateSpec.Builder builder;

    public LackOfPermsEmbed() {
        String footer = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "footer");
        String title = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "lack_of_perms.title");
        String desc = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "lack_of_perms.desc");

        this.builder = EmbedCreateSpec.builder()
                .color(Color.RED)
                .title(title)
                .description(desc)
                .timestamp(Instant.now())
                .footer(footer, null);
    }

    public EmbedCreateSpec getEmbed() {
        return this.builder.build();
    }
}
