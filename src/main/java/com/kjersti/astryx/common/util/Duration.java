package com.kjersti.astryx.common.util;

public class Duration {
    private long startUnix;
    private long endUnix;
    private int minutes;
    private int seconds;

    public Duration(long startUnix, long endUnix) {
        this.startUnix = startUnix;
        this.endUnix = endUnix;

        long[] durationData = processDuration();
        this.minutes = (int) durationData[0];
        this.seconds = (int) durationData[1];
    }

    public long[] processDuration() {
        long duration = endUnix - startUnix;

        long seconds = duration / 1_000_000_000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return new long[] {minutes, seconds};
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return "Duration{" + minutes + ":" + seconds + "}";
    }
}
