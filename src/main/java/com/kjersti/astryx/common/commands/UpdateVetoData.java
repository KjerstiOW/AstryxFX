package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.api.handlers.VetoDataHandler;
import com.kjersti.astryx.common.annotations.ProgramCommand;

@ProgramCommand(
        id="update_veto_database",
        desc="Updates the veto database"
)
public class UpdateVetoData implements AstryxCommand {
    @Override
    public void run(String[] args) {
        for (String arg: args) {
            switch (arg) {
                case "status":
                    getStatus();
                    return;
            }
        }

        VetoDataHandler.updateVetoDatabase();
    }

    private void getStatus() {
        boolean isRunning = VetoDataHandler.isRunning();

        if (isRunning) {
            VetoDataHandler.LOGGER.info("Status: running");
        } else {
            VetoDataHandler.LOGGER.info("Status: idle");
        }
    }
}