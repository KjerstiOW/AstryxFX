package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.api.handlers.ChampionshipDataHandler;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.util.StringUtil;

@ProgramCommand(
        id="update_championship_database",
        desc="Updates the championship database"
)
public class UpdateChampionshipData implements AstryxCommand {
    @Override
    public void run(String[] args) {
        if (args.length > 0) {
            String arg = args[0].trim();

            if (arg.equals("status")) {
                getStatus();
                return;
            } else if (StringUtil.canBeCastedToInt(arg)) {
                int offset = Integer.parseInt(arg);

                ChampionshipDataHandler.updateChampionshipDatabase(true, offset);
                return;
            }
        }

        ChampionshipDataHandler.updateChampionshipDatabase(true);
    }

    private void getStatus() {
        String action = ChampionshipDataHandler.getActionString();

        AstryxCommandRegistry.LOGGER.info(action);
    }
}