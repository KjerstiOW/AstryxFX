package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import org.apache.logging.log4j.Logger;


@ProgramCommand(
        id="ping",
        desc="pong!",
        visible=false
)
public class PingCommand implements AstryxCommand {
    public static final Logger LOGGER = AstryxLogManager.getLogger("ping");

    public void run(String[] args) {
        LOGGER.info("pong!");
    }
}
