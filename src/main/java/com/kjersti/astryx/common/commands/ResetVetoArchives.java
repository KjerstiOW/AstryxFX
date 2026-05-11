package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.sql.SqlHandler;

@ProgramCommand(
        id="reset_veto_archives",
        desc="Resets the veto archives"
)
public class ResetVetoArchives implements AstryxCommand {
    @Override
    public void run(String[] args) {
        if (args.length != 1) {
            AstryxCommandRegistry.LOGGER.warn("Are you sure you want to execute the command 'reset_veto_archives'? Type \"Y\" to execute, \"N\" to block");
        }

        Thread thread = new Thread(() -> {
            String command = Astryx.getTerminalController().getNextInput();

            if (command.equalsIgnoreCase("y") || command.equalsIgnoreCase("yes")) {
                this.run();
            } else if (command.equalsIgnoreCase("n") || command.equalsIgnoreCase("no")) {
                AstryxCommandRegistry.LOGGER.info("Blocked execution of command 'reset_veto_archives'");
            } else {
                AstryxCommandRegistry.LOGGER.warn("Unknown input. Type \"Y\" to execute 'reset_veto_archives'. Type \"N\" to block execution");
                this.run(new String[]{"penis"});
            }
        });

        thread.start();
    }

    private void run() {
        String table = SettingRegistry.getMatchDataTable();
        String rawVetoTable = SettingRegistry.getRawVetoTable();
        String vetoTable = SettingRegistry.getVetoDataTable();
        String query = "UPDATE " + table + " SET `archived` = 0 WHERE 1=1";
        String deleteRawQuery = "DELETE FROM " + rawVetoTable + " WHERE 1=1";
        String deleteQuery = "DELETE FROM " + vetoTable + " WHERE 1=1";

        SqlHandler.executeApiUpdate(query);
        SqlHandler.executeApiUpdate(deleteRawQuery);
        SqlHandler.executeApiUpdate(deleteQuery);

        AstryxCommandRegistry.LOGGER.warn("Successfully reset veto archive data");
    }
}
