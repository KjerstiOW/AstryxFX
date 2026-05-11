package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;

import java.util.List;


public interface AstryxCommand {
    void run(String[] args);
}