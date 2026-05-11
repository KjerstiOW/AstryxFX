package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import org.apache.logging.log4j.Logger;

@ProgramCommand(
        id="gay",
        desc="",
        visible = false
)
public class EasterEgg implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("gay");

    public void run(String[] args) {
        LOGGER.error("I do not like.. gay people");
    }
}
