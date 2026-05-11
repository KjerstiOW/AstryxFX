//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.lang;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.Maven;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LanguageLoader {
    private static Properties properties;
    public static void loadProperties() {
        properties = new Properties();

        try {
            FileInputStream input = new FileInputStream("src/main/resources/com/kjersti/astryx/lang/en_us.properties");

            properties.load(input);
            Astryx.LOGGER.info("Successfully loaded language file");

            input.close();
        } catch (IOException ex) {
            Astryx.LOGGER.warn("Could not load language file");
        }
    }

    public static String get(String key) {
        Maven mavenData = new Maven();

        return properties.getProperty(key, key)
                .replace("{maven_name}", mavenData.getBotName())
                .replace("{maven_version}", mavenData.getBotVersion())
                .replace("{maven_developer}", mavenData.getDeveloperId())
                .replace("{times}", "•");
    }
}
