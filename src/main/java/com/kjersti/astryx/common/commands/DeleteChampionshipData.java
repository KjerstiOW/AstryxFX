package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.api.handlers.ChampionshipDataHandler;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;

@ProgramCommand(
        id="delete_championship_data",
        desc= "Deletes all data in the championship database"
)
public class DeleteChampionshipData implements AstryxCommand {
    @Override
    public void run(String[] args) {
        if (args.length != 1) {
            AstryxCommandRegistry.LOGGER.warn("Are you sure you want to execute the command 'delete_championship_data'? Type \"Y\" to execute, \"N\" to block");
        }

        Thread thread = new Thread(() -> {
            String command = Astryx.getTerminalController().getNextInput();

            if (command.equalsIgnoreCase("y") || command.equalsIgnoreCase("yes")) {
                ChampionshipDataHandler.deleteAll();
            } else if (command.equalsIgnoreCase("n") || command.equalsIgnoreCase("no")) {
                AstryxCommandRegistry.LOGGER.info("Blocked execution of command 'delete_championship_data'");
            } else {
                AstryxCommandRegistry.LOGGER.warn("Unknown input. Type \"Y\" to execute 'delete_championship_data'. Type \"N\" to block execution");
                this.run(new String[]{"penis"});
            }
        });

        thread.start();
    }
}