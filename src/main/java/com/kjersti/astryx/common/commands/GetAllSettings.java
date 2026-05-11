package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.util.StringUtil;
import org.apache.logging.log4j.Logger;

import java.util.*;

@ProgramCommand(
        id="get_settings",
        desc="Shows all setting pairs."
)
public class GetAllSettings implements AstryxCommand {
    private static final Logger LOGGER = AstryxLogManager.getLogger("botcmd");

    public void run(String[] args) {
        String output = ("\nList of all settings:\n");

        Map<String, Object> settings = SettingRegistry.getRegistry();
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry: settings.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue().toString());
        }

        String enumeratedList = StringUtil.getEnumeratedList(keys, values);

        SettingRegistry.LOGGER.info(output + enumeratedList);
    }
}
