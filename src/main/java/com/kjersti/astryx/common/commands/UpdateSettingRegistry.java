package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.SettingRegistry;

@ProgramCommand(
        id="update_settings",
        desc= "Updates the setting registry"
)
public class UpdateSettingRegistry implements AstryxCommand {
    @Override
    public void run(String[] args) {
        SettingRegistry.updateSettings();
    }
}
