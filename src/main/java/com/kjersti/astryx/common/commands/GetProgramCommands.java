package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import org.apache.logging.log4j.Logger;

@ProgramCommand(
        id="get_program_commands",
        desc="Shows all valid program commands."
)
public class GetProgramCommands implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("help");

    public void run(String[] args) {
        HelpCommand command = new HelpCommand();

        command.run(new String[0]);
    }
}
