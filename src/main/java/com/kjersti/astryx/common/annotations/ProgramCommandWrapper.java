package com.kjersti.astryx.common.annotations;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.commands.AstryxCommand;

public class ProgramCommandWrapper {
    private final Class<AstryxCommand> clazz;
    private final String id;
    private final String desc;
    private final boolean visible;
    private final boolean enabled;

    public ProgramCommandWrapper(Class<AstryxCommand> clazz, ProgramCommand annotation) {
        this.clazz = clazz;
        this.id = annotation.id();
        this.desc = annotation.desc();
        this.visible = annotation.visible();
        this.enabled = annotation.enabled();
    }

    public void run(String[] args) {
        try {
            AstryxCommand instance = clazz.getDeclaredConstructor().newInstance();

            instance.run(args);
        } catch (Exception e) {
            Astryx.LOGGER.error(e);
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
