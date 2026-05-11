package com.kjersti.astryx.common.util;

import reactor.util.annotation.Nullable;

public class ActionObject {
    private String action;
    private int pos;
    private int totalPos;

    public ActionObject(String action, int pos, int totalPos) {
        this.action = action;
        this.pos = pos;
        this.totalPos = totalPos;
    }

    public ActionObject(String action, int accumulator) {
        this.action = action;
        this.pos = accumulator;
        this.totalPos = -1;
    }

    public ActionObject(String action) {
        this.action = action;
        this.pos = -1;
        this.totalPos = -1;
    }

    public String getAction() {
        return action;
    }

    public int getPos() {
        return pos;
    }

    public int getTotalPos() {
        return totalPos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void incrementPos() {
        this.pos++;
    }
}
