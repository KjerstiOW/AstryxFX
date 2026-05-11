//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.parsing;

import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.parsing.exceptions.MapArgumentException;
import com.kjersti.astryx.parsing.object.OverwatchMap;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {
    public static final Logger LOGGER = AstryxLogManager.getLogger("parser");
    private static final Pattern SCORE_REGEX = Pattern.compile("\\b\\d+-\\d+\\b");
    private static final Pattern VOD_REGEX = Pattern.compile("`\\w+`");
    private static final Pattern WHITESPACE_REGEX = Pattern.compile("^\\\\s*$");
    private final String[] lines;
    private String header;
    private String enemyTeam;

    public MessageParser(String message) {
        this.lines = message.split("\n");
    }

    public List<OverwatchMap> parse() throws IllegalArgumentException {
        List<OverwatchMap> allMaps = new ArrayList<>();
        this.replaceRawScore();
        String[] headerLineData = this.getHeaderLineData(this.lines[0]);
        this.header = headerLineData[0].trim();
        this.enemyTeam = headerLineData[1].trim();
        OverwatchMap previousMap = null;

        for(int lineNumber = 1; lineNumber < this.lines.length; ++lineNumber) {
            if (!this.lines[lineNumber].equals("")) {
                OverwatchMap submap;
                if (this.lines[lineNumber].startsWith(">")) {
                    submap = this.parseSubmap(this.lines[lineNumber], lineNumber, previousMap);
                    previousMap.addSubmap(submap);
                } else {
                    submap = this.parseLine(lineNumber, this.lines[lineNumber]);
                    allMaps.add(submap);
                    previousMap = submap;
                }
            }
        }

        if (allMaps.size() == 0) {
            LOGGER.info("Message that contains identifier 'vs' contains no map data, skipping");
            return null;
        } else {
            return allMaps;
        }
    }

    private OverwatchMap parseLine(int lineNumber, String line) {
        String mapName = this.getMapName(line, lineNumber);
        int[] score = this.getScore(line, lineNumber);
        int friendlyScore = score[0];
        int enemyScore = score[1];
        String vodCode = this.getVodCode(line, lineNumber);
        return new OverwatchMap(line, lineNumber + 1, this.header, this.enemyTeam, mapName, friendlyScore, enemyScore, vodCode);
    }

    private OverwatchMap parseSubmap(String line, int lineNumber, OverwatchMap previousMap) throws MapArgumentException {
        if (previousMap == null) {
            throw new MapArgumentException("No previous map", line, lineNumber + 1);
        } else {
            line = line.substring(1);
            String mapName = this.getMapName(line, lineNumber);
            int[] scores = this.getScore(line, lineNumber);
            int friendlyScore = scores[0];
            int enemyScore = scores[1];
            return new OverwatchMap(line, lineNumber + 1, this.header, this.enemyTeam, mapName, friendlyScore, enemyScore, previousMap.getVodCode());
        }
    }

    private void replaceRawScore() {
        for(int lineNumber = 0; lineNumber < this.lines.length; ++lineNumber) {
            if (lines[lineNumber].contains("d W")) {
                continue;
            }

            this.lines[lineNumber] = this.lines[lineNumber].replace(" W", " 1-0");
            this.lines[lineNumber] = this.lines[lineNumber].replace(" L", "  0-1");
        }
    }

    private String[] getHeaderLineData(String line) throws IllegalArgumentException {
        String[] lineData = line.split("vs");
        if (lineData.length == 0) {
            throw new IllegalArgumentException("Invalid header line");
        } else if (lineData.length > 2) {
            throw new IllegalArgumentException("Too many verses in header line");
        } else {
            return lineData;
        }
    }

    private int[] getScore(String line, int lineNumber) throws MapArgumentException {
        Matcher scoreMatcher = SCORE_REGEX.matcher(line);
        if (!scoreMatcher.find()) {
            throw new MapArgumentException("No score regex matches found", line, lineNumber + 1);
        } else {
            String scoreData = scoreMatcher.group(0);
            int friendlyScore = Integer.parseInt(scoreData.split("-")[0]);
            int enemyScore = Integer.parseInt(scoreData.split("-")[1]);
            return new int[]{friendlyScore, enemyScore};
        }
    }

    private String getMapName(String line, int lineNumber) throws MapArgumentException {
        Matcher scoreMatcher = SCORE_REGEX.matcher(line);

        if (!scoreMatcher.find()) {
            throw new MapArgumentException("No valid score found", line, lineNumber + 1);
        } else {
            int scorePosition = scoreMatcher.start();

            String mapName = line.substring(0, scorePosition);
            return mapName.trim();
        }
    }

    private String getVodCode(String line, int lineNumber) throws MapArgumentException {
        Matcher vodMatcher = VOD_REGEX.matcher(line);
        if (!vodMatcher.find()) {
            throw new MapArgumentException("No valid vod code found", line, lineNumber + 1);
        } else {
            String vodData = vodMatcher.group(0);
            return vodData.substring(1, vodData.length() - 1);
        }
    }
}
