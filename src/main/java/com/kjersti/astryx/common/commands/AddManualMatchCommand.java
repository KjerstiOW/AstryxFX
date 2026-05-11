package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.sql.SqlHandler;


@ProgramCommand(
        id="add_match",
        desc="Manually adds a match to the database"
)
public class AddManualMatchCommand implements AstryxCommand {
    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Not enough arguments given. Required: add_match {match_id}");
            return;
        } else if (args.length > 1) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Too many arguments given. Required: add_match {match_id}");
            return;
        }

        String matchId = args[0].trim();

        if (!matchId.startsWith("1-")) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Invalid match id. No signature 1- on id '" + matchId + "'");
            return;
        }

        addMatchIdToTable(matchId);
        AstryxCommandRegistry.LOGGER.info("Added match id '" + matchId + "' to the manual match table");
    }

    private static void addMatchIdToTable(String matchId) {
        String table = SettingRegistry.getManualMatchTable();

        String query = "INSERT INTO `" + table + "` " +
                "(`match_id`, `archived`) VALUES " +
                "('" + matchId + "', 0) " +
                "ON DUPLICATE KEY UPDATE `archived` = VALUES(`archived`);";

        SqlHandler.executeApiUpdate(query);
    }
}
