package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.PatchRegistry;
import org.apache.logging.log4j.Logger;

@ProgramCommand(
        id="get_patch",
        desc="Gets the current patch of Overwatch"
)
public class GetPatchCommand implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("patch");

    public void run(String[] args) {
        String patch = PatchRegistry.getPatch();

        LOGGER.info("Current patch: {}", patch);
    }
}
