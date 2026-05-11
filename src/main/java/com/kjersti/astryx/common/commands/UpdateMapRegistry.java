package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.MapRegistry;

@ProgramCommand(
        id="update_map_registry",
        desc= "Updates the map registry"
)
public class UpdateMapRegistry implements AstryxCommand {
    @Override
    public void run(String[] args) {
        MapRegistry.updateRegistry();
    }
}
