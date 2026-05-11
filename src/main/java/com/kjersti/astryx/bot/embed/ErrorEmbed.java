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

public class ErrorEmbed {
    public static final String EMBED_LANGUAGE_LOCATION = "bot.embed.";
    private final EmbedCreateSpec.Builder builder;
    private final Exception e;

    public ErrorEmbed(Exception e) {
        String footer = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "footer");
        this.e = e;
        this.builder = EmbedCreateSpec.builder()
                .color(Color.RED)
                .title(this.getEmbedTitle())
                .timestamp(Instant.now())
                .footer(footer, null);
        this.addDescription();
    }

    private String getEmbedTitle() {
        return this.e.getClass().getSimpleName();
    }

    private void addDescription() {
        MapArgumentException ex;

        if (this.e.getClass() == InvalidMapException.class) {
            ex = (MapArgumentException) this.e;
            this.builder.addField(this.e.getMessage() + " at line " + ex.getLineNumber(), "```" + ex.getLine().replace("`", "'") + "```", true);
        } else if (this.e.getClass() == MapArgumentException.class) {
            ex = (MapArgumentException) this.e;
            this.builder.addField(this.e.getMessage() + " at line " + ex.getLineNumber(), "```" + ex.getLine().replace("`", "'") + "```", true);
        } else {
            this.builder.addField(this.e.getMessage(), "", true);
        }
    }

    public EmbedCreateSpec getEmbed() {
        return this.builder.build();
    }
}
