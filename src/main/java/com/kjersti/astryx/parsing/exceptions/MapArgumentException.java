//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.parsing.exceptions;

public class MapArgumentException extends IllegalArgumentException {
    private final String line;
    private final int lineNumber;

    public MapArgumentException(String cause, String line, int lineNumber) {
        super(cause);
        this.line = line;
        this.lineNumber = lineNumber;
    }

    public String getLine() {
        return this.line;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }
}
