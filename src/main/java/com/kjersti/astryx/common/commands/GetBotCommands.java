package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.bot.registry.BotCommandRegistry;
import com.kjersti.astryx.common.Maven;
import com.kjersti.astryx.common.annotations.BotCommandWrapper;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.kjersti.astryx.common.util.StringUtil.*;

@ProgramCommand(
        id="get_bot_commands",
        desc="Shows all valid bot commands."
)
public class GetBotCommands implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("botcmd");

    public void run(String[] args) {
        Maven mavenData = new Maven();

        String output = ("\nList of " + mavenData.getBotName() + " bot commands:");

        List<String> commandIds = BotCommandRegistry.getRegistry()
                .stream()
                .map(BotCommandWrapper::getId)
                .toList();
        List<String> commandDesc = BotCommandRegistry.getRegistry()
                .stream()
                .map(BotCommandWrapper::getDesc)
                .toList();

        String enumeratedList = getEnumeratedList(commandIds, commandDesc);

        LOGGER.info("{}\n{}", output, enumeratedList);
    }
}
