package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.TeamRegistry;
import com.kjersti.astryx.common.registry.object.TeamRegistryObject;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.kjersti.astryx.common.util.StringUtil.getEnumeratedList;

@ProgramCommand(
        id="get_teams",
        desc="Shows all registered teams."
)
public class GetTeamsCommand implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("botcmd");

    public void run(String[] args) {
        String output = ("\nList of registered teams:");

        List<String> teamIds = TeamRegistry.getRegistry()
                .stream()
                .map(TeamRegistryObject::getTeamId)
                .toList();

        String enumeratedList = getEnumeratedList(teamIds);

        LOGGER.info("{}\n{}", output, enumeratedList);
    }
}
