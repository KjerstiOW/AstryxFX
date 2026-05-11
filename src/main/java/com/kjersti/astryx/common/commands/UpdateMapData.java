package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.api.handlers.MapDataHandler;
import com.kjersti.astryx.api.handlers.MatchDataHandler;
import com.kjersti.astryx.common.annotations.ProgramCommand;

@ProgramCommand(
        id="update_map_database",
        desc="Updates the map database"
)
public class UpdateMapData implements AstryxCommand {
    @Override
    public void run(String[] args) {
        for (String arg: args) {
            switch (arg) {
                case "status":
                    getStatus();
                    return;
            }
        }

        MapDataHandler.updateMapDatabase(true);
    }

    private void getStatus() {
        boolean isRunning = MapDataHandler.isRunning();

        if (isRunning) {
            MatchDataHandler.LOGGER.info("Status: running");
        } else {
            MatchDataHandler.LOGGER.info("Status: idle");
        }
    }
}