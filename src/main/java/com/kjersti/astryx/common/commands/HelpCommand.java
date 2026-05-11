package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.Maven;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.annotations.ProgramCommandWrapper;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.util.StringUtil;
import org.apache.logging.log4j.Logger;

import java.util.List;

@ProgramCommand(
        id="help",
        desc="Shows all valid commands."
)
public class HelpCommand implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("help");

    public void run(String[] args) {
        Maven mavenData = new Maven();

        String output = "\nList of " + mavenData.getBotName() + " commands:";

        List<String> commandIds = AstryxCommandRegistry.visibleCommands.stream()
                .map(ProgramCommandWrapper::getId)
                .toList();

        List<String> commandDesc = AstryxCommandRegistry.visibleCommands.stream()
                .map(ProgramCommandWrapper::getDesc)
                .toList();

        String enumeratedList = StringUtil.getEnumeratedList(commandIds, commandDesc);

        LOGGER.info("{}\n{}", output, enumeratedList);
    }
}
