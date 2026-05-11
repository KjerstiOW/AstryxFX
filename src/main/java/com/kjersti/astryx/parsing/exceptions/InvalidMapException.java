//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.parsing.exceptions;

import com.kjersti.astryx.parsing.object.OverwatchMap;

public class InvalidMapException extends MapArgumentException {
    private final OverwatchMap map;

    public InvalidMapException(String cause, OverwatchMap map) {
        super(cause, map.getLine(), map.getLineNumber());
        this.map = map;
    }

    public OverwatchMap getMap() {
        return this.map;
    }
}
