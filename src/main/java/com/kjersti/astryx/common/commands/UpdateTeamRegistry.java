package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.TeamRegistry;

@ProgramCommand(
        id="update_team_registry",
        desc="Updates the team registry"
)
public class UpdateTeamRegistry implements AstryxCommand {
    @Override
    public void run(String[] args) {
        TeamRegistry.updateRegistry();
    }
}
