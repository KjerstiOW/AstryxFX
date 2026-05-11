package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.lang.LanguageLoader;

@ProgramCommand(
        id="update_language_cache",
        desc= "Updates the language cache"
)
public class UpdateLangRegistry implements AstryxCommand {
    @Override
    public void run(String[] args) {
        LanguageLoader.loadProperties();
    }
}
