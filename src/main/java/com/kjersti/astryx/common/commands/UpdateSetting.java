package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import com.kjersti.astryx.common.registry.SettingRegistry;

@ProgramCommand(
        id="update_setting",
        desc= "Updates a specific setting"
)
public class UpdateSetting implements AstryxCommand {
    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Not enough arguments given. Required: update_setting {setting_id} {new_value}");
            return;
        } else if (args.length > 2) {
            AstryxCommandRegistry.LOGGER.warn(
                    "Too many arguments given. Required: update_setting {setting_id} {new_value}");
            return;
        }

        String settingId = args[0];
        String newValue = args[1];

        if (SettingRegistry.getString(settingId) == null) return;

        SettingRegistry.updateSetting(settingId, newValue);
    }
}
