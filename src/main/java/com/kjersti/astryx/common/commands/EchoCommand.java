package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import org.apache.logging.log4j.Logger;


@ProgramCommand(
        id="echo",
        desc="Echoes the given input"
)
public class EchoCommand implements AstryxCommand {
    public static final Logger LOGGER = AstryxLogManager.getLogger("echo");

    @Override
    public void run(String[] args) {
        StringBuilder builder = new StringBuilder();

        for (String arg: args) {
            builder.append(arg).append(" ");
        }

        LOGGER.info(builder.toString().trim());
    }
}
