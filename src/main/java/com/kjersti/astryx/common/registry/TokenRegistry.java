package com.kjersti.astryx.common.registry;

import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.util.TomlReader;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TokenRegistry {
    public static final Logger LOGGER = AstryxLogManager.getLogger("settings");

    private static Map<String, Object> registry;

    public static void updateTokens() {
        registry = TomlReader.getFlattenedTokens();

        LOGGER.info("Successfully loaded " + registry.size() + " token(s) from toml");
    }

    public static Map<String, Object> getRegistry() {return registry;}

    public static String getToken(String key) {return String.valueOf(registry.getOrDefault(key, null));}

    public static String getFaceitToken() {
        return getToken("faceit_token");
    }

    public static String getDiscordToken() {
        return getToken("discord_token");
    }

    public static String getTestDummyDiscordToken() {
        return getToken("test_dummy_discord_token");
    }
}
