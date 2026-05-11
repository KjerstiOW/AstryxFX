package com.kjersti.astryx.common.commands;

import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.sql.data.ProcessH2HData;
import com.kjersti.astryx.sql.data.ProcessRawVetoData;

@ProgramCommand(
        id="process_veto_data",
        desc="Processes raw deto data"
)
public class ProcessVetoDataCommand implements AstryxCommand {
    @Override
    public void run(String[] args) {
        Thread thread = new Thread(this::run);
        thread.start();
    }

    private void run() {
        ProcessRawVetoData.processRawVetoData();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}

        ProcessH2HData.processH2HData();
    }
}