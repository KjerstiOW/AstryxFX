//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.util;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TomlReader {
    public static String getString(String key) {
        File file = new File("config/tokens.toml");
        Toml toml = (new Toml()).read(file);

        return toml.getString(key);
    }

    public static Toml readSettingFile() {
        File file = new File("config/settings.toml");

        return (new Toml()).read(file);
    }

    public static Toml readTokenFile() {
        File file = new File("config/tokens.toml");

        return (new Toml()).read(file);
    }

    public static Map<String, Object> getFlattenedSettings() {
        Toml settings = readSettingFile();

        return flattenToml(settings);
    }

    public static Map<String, Object> getFlattenedTokens() {
        Toml tokens = readTokenFile();

        return flattenToml(tokens);
    }

    public static Map<String, Object> flattenToml(Toml toml) {
        Map<String, Object> unflattenedSettings = toml.toMap();

        return flattenMap(unflattenedSettings);
    }

    public static Map<String, Object> flattenMap(Map<String, Object> map) {
        Map<String, Object> flattenedMap = new HashMap<>();

        for (Map.Entry<String, Object> entry: map.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();

            if (obj instanceof HashMap) {
                Map<String, Object> embeddedMap = flattenMap((Map<String, Object>) obj);

                flattenedMap.putAll(embeddedMap);
            } else {
                flattenedMap.put(key, obj);
            }
        }

        return flattenedMap;
    }
}
