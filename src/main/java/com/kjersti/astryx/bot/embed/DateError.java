//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.embed;

import com.kjersti.astryx.common.lang.LanguageLoader;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

public class DateError {
    public static final String EMBED_LANGUAGE_LOCATION = "bot.embed.";
    private final EmbedCreateSpec.Builder builder;

    public DateError(String invalidDate, String invalidDateType) {
        String footer = LanguageLoader.get(EMBED_LANGUAGE_LOCATION + "footer");
        this.builder = EmbedCreateSpec.builder()
                .color(Color.RED)
                .title(this.getEmbedTitle())
                .timestamp(Instant.now())
                .footer(footer, null);
        this.addDescription(invalidDate, invalidDateType);
    }

    private String getEmbedTitle() {
        return "Invalid Date";
    }

    private void addDescription(String invalidDate, String invalidType) {
        this.builder.addField(invalidType + " Date", invalidDate, false);
    }

    private String getArgumentsFromPath(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Could not get arguments from path `" + path + "`";
    }

    private String getStacktraceFromPath(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder stacktrace = new StringBuilder();
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                stacktrace.append(line).append("\n");
            }

            return stacktrace.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Could not get";
    }

    public EmbedCreateSpec getEmbed() {
        return this.builder.build();
    }
}
