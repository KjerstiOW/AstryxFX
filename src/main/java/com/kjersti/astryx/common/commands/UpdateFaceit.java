package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.api.handlers.ChampionshipDataHandler;
import com.kjersti.astryx.api.handlers.MapDataHandler;
import com.kjersti.astryx.api.handlers.MatchDataHandler;
import com.kjersti.astryx.api.handlers.VetoDataHandler;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.util.Duration;
import com.kjersti.astryx.common.util.StringUtil;
import com.kjersti.astryx.sql.data.ProcessH2HData;
import com.kjersti.astryx.sql.data.ProcessRawVetoData;

@ProgramCommand(
        id="update_faceit",
        desc= "Updates Faceit API data"
)
public class UpdateFaceit implements AstryxCommand {
    private static boolean running = false;

    @Override
    public void run(String[] args) {
        boolean useNewThread = true;
        if (running) return;

        if (args.length > 0 && StringUtil.canBeCastedToBoolean(args[0])) {
            useNewThread = Boolean.parseBoolean(args[0]);
        }

        running = true;
        AstryxCommandRegistry.LOGGER.info("Getting all Faceit API data");

        if (useNewThread) {
            Thread thread = new Thread(this::toDo);
            thread.start();
        } else {
            toDo();
        }
    }

    private void toDo() {
        long startTime = System.nanoTime();

        ChampionshipDataHandler.updateChampionshipDatabase(false);
        MatchDataHandler.updateMatchDatabase(false);
        VetoDataHandler.updateVetoDatabase(false);
        ProcessRawVetoData.processRawVetoData();
        ProcessH2HData.processH2HData();
        MapDataHandler.updateMapDatabase(false);

        long endTime = System.nanoTime();

        Duration duration = new Duration(startTime, endTime);
        int min = duration.getMinutes();
        int sec = duration.getSeconds();

        AstryxCommandRegistry.LOGGER.info("Updated all Faceit data from API, time elapsed {} minutes, {} seconds",
                min, sec);
    }
}
