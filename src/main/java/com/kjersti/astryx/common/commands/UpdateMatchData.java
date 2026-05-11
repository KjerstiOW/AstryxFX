package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.api.handlers.MatchDataHandler;
import com.kjersti.astryx.common.annotations.ProgramCommand;

@ProgramCommand(
        id="update_match_database",
        desc="Updates the championship database"
)
public class UpdateMatchData implements AstryxCommand {
    @Override
    public void run(String[] args) {
        for (String arg: args) {
            switch (arg) {
                case "status":
                    getStatus();
                    return;
            }
        }

        MatchDataHandler.updateMatchDatabase();
    }

    private void getStatus() {
        boolean isRunning = MatchDataHandler.isRunning();

        if (isRunning) {
            MatchDataHandler.LOGGER.info("Status: running");
        } else {
            MatchDataHandler.LOGGER.info("Status: idle");
        }
    }
}