package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.api.handlers.MatchDataHandler;
import com.kjersti.astryx.api.object.MatchObject;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.sql.SqlHandler;

import java.util.Objects;


@ProgramCommand(
        id="add_championship",
        desc="Manually adds a championship to the database"
)
public class AddManualChampionshipCommand implements AstryxCommand {
    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Not enough arguments given. Required: add_championship {match_id}");
            return;
        } else if (args.length > 1) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Too many arguments given. Required: add_championship {match_id}");
            return;
        }

        String id = args[0].trim();

        Thread thread = new Thread(() -> AddManualChampionshipCommand.runCommand(id));
        thread.start();
    }

    private static void runCommand(String id) {
        if (id.startsWith("1-")) {
            String championshipId = getChampionshipId(id);

            addMatchIdToTable(championshipId);
            AstryxCommandRegistry.LOGGER.info("Added championship id '" +
                    championshipId + "' to the manual championship table (implied from match_id {})", id);
            return;
        }

        addMatchIdToTable(id);
        AstryxCommandRegistry.LOGGER.info("Added championship id '" + id + "' to the manual championship table");
    }

    private static void addMatchIdToTable(String championshipId) {
        String table = SettingRegistry.getManualChampionshipTable();

        String query = "INSERT INTO `" + table + "` " +
                "(`championship_id`, `archived`) VALUES " +
                "('" + championshipId + "', 0) " +
                "ON DUPLICATE KEY UPDATE `archived` = VALUES(`archived`);";

        SqlHandler.executeApiUpdate(query);
    }

    private static String getChampionshipId(String matchId) {
        MatchObject match;

        try {
            match = Objects.requireNonNull(MatchDataHandler.getSingularMatchFromId(matchId)).get(0);

            return match.getCompetitionId();
        } catch (NullPointerException e) {
            AstryxCommandRegistry.LOGGER.error("Could not get championship from match id {}", matchId);
        }

        return null;
    }
}
