package com.kjersti.astryx.common.registry;

import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.util.TomlReader;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingRegistry {
    public static final Logger LOGGER = AstryxLogManager.getLogger("settings");

    private static Map<String, Object> registry;

    public static void updateSettings() {
        registry = TomlReader.getFlattenedSettings();

        LOGGER.info("Successfully loaded " + registry.size() + " setting(s) from toml");

    }

    public static void updateSetting(String key, Object value) {
        Object currentValue = registry.get(key);

        if (currentValue != null && value != null) {
            try {
                if (currentValue.getClass().isInstance(value)) {
                    registry.put(key, value);

                    LOGGER.info("Changed " + key + ", " + currentValue + "->" + value);
                } else {
                    throw new ClassCastException();
                }
            } catch (ClassCastException e) {
                String currentClass = currentValue.getClass().getSimpleName();
                String newClass = value.getClass().getSimpleName();

                LOGGER.error("Could not change " + newClass + " setting into " + currentClass + ", setting_id=" + key);
            }
        } else {
            LOGGER.error("Current value or new value is null, setting_id=" + key);
        }
    }

    public static Map<String, Object> getRegistry() {return registry;}

    public static String getString(String key) {return String.valueOf(registry.getOrDefault(key, null));}

    public static int getInteger(String key) {
        return Integer.parseInt(String.valueOf(registry.getOrDefault(key, null)));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(String.valueOf(registry.getOrDefault(key, null)));
    }

    public static List<?> getList(String key) {
        return (ArrayList<?>) registry.getOrDefault(key, null);
    }

    public static List<String> getStringList(String key) {
        return (List<String>) getList(key);
    }

    public static boolean useDebugLog() {
        return getBoolean("debug_log");
    }

    public static String getTerminalPrefix() {
        return getString("terminal_command_prefix");
    }

    public static String getDefaultMapType() {
        return getString("default_map_type");
    }

    public static boolean usePatchOverride() {
        return getBoolean("use_patch_override");
    }

    public static String getPatchOverride() {
        return getString("patch_override");
    }

    public static boolean useDateOverride() {
        return getBoolean("use_date_override");
    }

    public static String getDateOverride() {
        return getString("date_override");
    }

    public static String getSqlHost() {
        return getString("host");
    }

    public static String getSqlUser() {
        return getString("username");
    }

    public static String getSqlPass() {
        return getString("password");
    }

    public static String getRegistryDatabase() {
        return getString("registry_database_id");
    }

    public static String getMapDatabase() {
        return getString("map_database_id");
    }

    public static String getApiDatabase() {
        return getString("api_database_id");
    }

    public static String getMapRegistryTable() {
        return getString("map_registry_table");
    }

    public static String getTeamRegistryTable() {
        return getString("team_registry_table");
    }

    public static String getChampionshipDataTable() {
        return getString("championship_data_table");
    }

    public static String getAttackingFirstDataTable() {
        return getString("attacking_first_table");
    }

    public static String getMatchDataTable() {
        return getString("match_data_table");
    }

    public static String getMapDataTable() {
        return getString("map_data_table");
    }

    public static String getVetoDataTable() {
        return getString("veto_data_table");
    }

    public static String getFaceitMapTable() {
        return getString("faceit_map_table");
    }

    public static String getManualMatchTable() {
        return getString("manual_matches_table");
    }

    public static String getManualChampionshipTable() {
        return getString("manual_championships_table");
    }

    public static String getFaceitMapDataTable() {
        return getString("maps_data_table");
    }
    public static String getRawVetoTable() {
        return getString("raw_veto_data_table");
    }

    public static String getHeadToHeadTable() {
        return getString("head_to_head_veto_table");
    }
    public static boolean runAfterClose() {
        return getBoolean("run_after_close");
    }
    public static int getSqlBatchLimit() {
        return getInteger("sql_batch_limit");
    }
    public static String getGameId() {
        return getString("game_id");
    }
    public static int getConcurrentThreadCount() {
        return getInteger("concurrent_thread_count");
    }
    public static int getMaxChampionshipCount() {
        return getInteger("championship_api_limit");
    }

    public static int getWindowWidth() {
        return getInteger("window_width");
    }
    public static int getWindowHeight() {
        return getInteger("window_height");
    }
    public static boolean enableBot() {
        return getBoolean("enable_bot");
    }

    public static boolean useTestDummyDiscord() {
        return getBoolean("use_test_dummy");
    }
    public static boolean showTerminal() {
        return getBoolean("show_terminal");
    }

    public static int getUpdateDowntime() {
        return getInteger("update_mode_downtime_ms");
    }
}
