//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.python;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.util.ProcessUtil;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PythonRunner {
    public static final Logger LOGGER = AstryxLogManager.getLogger("python");
    public static String FRIENDLY_CHART_PATH = "chart/friendly_wins.png";
    public static String MAP_VETO_CHART_PATH = "chart/vetoes.png";
    public static String MAP_WINS_CHART_PATH = "chart/wins.png";

    public static String FRIENDLY_LOG_PATH = "logs/python.log";
    public static String VETOES_LOG_PATH = "logs/vetoes.log";
    public static String WINS_LOG_PATH = "logs/wins.log";

    public static InputStream createMapWinPythonChart(String pythonFilePath, String teamId) {
        List<String> commands = new ArrayList<>();

        String sqlHost = SettingRegistry.getSqlHost();
        String sqlUser = SettingRegistry.getSqlUser();
        String sqlPass = SettingRegistry.getSqlPass();
        String mapDatabase = SettingRegistry.getMapDatabase();
        String registryDatabase = SettingRegistry.getRegistryDatabase();
        String mapRegistryTable = SettingRegistry.getMapRegistryTable();

        commands.add("python");
        commands.add(pythonFilePath);
        commands.add(FRIENDLY_CHART_PATH);

        commands.add(sqlHost);
        commands.add(sqlUser);
        commands.add(sqlPass);

        commands.add(mapDatabase);
        commands.add(registryDatabase);

        commands.add(mapRegistryTable);

        commands.add(teamId);

        LOGGER.info("Running python file with args " + commands);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.warn("Error occurred while running Python. ");
            }

            File chartFile = new File(FRIENDLY_CHART_PATH);

            try {
                return new FileInputStream(chartFile);
            } catch(FileNotFoundException e) {
                return null;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream createMapVetoPythonChart(String teamId, String teamName,
                                                       String startDate, String endDate) {
        List<String> commands = new ArrayList<>();

        String sqlHost = SettingRegistry.getSqlHost();
        String sqlUser = SettingRegistry.getSqlUser();
        String sqlPass = SettingRegistry.getSqlPass();
        String vetoDatabase = SettingRegistry.getApiDatabase();
        String vetoTable = SettingRegistry.getVetoDataTable();
        String registryDatabase = SettingRegistry.getRegistryDatabase();
        String registryTable = SettingRegistry.getMapRegistryTable();
        String apiDatabase = SettingRegistry.getApiDatabase();
        String faceitTable = SettingRegistry.getFaceitMapTable();

        commands.add("python");
        commands.add("src/main/python/TeamVetoes.py");
        commands.add(MAP_VETO_CHART_PATH);

        commands.add(sqlHost);
        commands.add(sqlUser);
        commands.add(sqlPass);

        commands.add(vetoDatabase);
        commands.add(vetoTable);
        commands.add(registryDatabase);
        commands.add(registryTable);
        commands.add(apiDatabase);
        commands.add(faceitTable);

        commands.add(teamId);
        commands.add(teamName);

        commands.add(startDate);
        commands.add(endDate);

        LOGGER.info("Running python file with args " + commands);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.warn("Error occurred while running Python. ");
            }

            File chartFile = new File(MAP_VETO_CHART_PATH);

            try {
                return new FileInputStream(chartFile);
            } catch(FileNotFoundException e) {
                return null;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream createMapWinChart(String teamId, String teamName,
                                                       String startDate, String endDate) {
        List<String> commands = new ArrayList<>();

        String sqlHost = SettingRegistry.getSqlHost();
        String sqlUser = SettingRegistry.getSqlUser();
        String sqlPass = SettingRegistry.getSqlPass();
        String mapDatabase = SettingRegistry.getApiDatabase();
        String mapTable = SettingRegistry.getFaceitMapDataTable();
        String registryDatabase = SettingRegistry.getRegistryDatabase();
        String registryTable = SettingRegistry.getMapRegistryTable();
        String apiDatabase = SettingRegistry.getApiDatabase();
        String faceitTable = SettingRegistry.getFaceitMapTable();

        commands.add("python");
        commands.add("src/main/python/TeamWins.py");
        commands.add(MAP_WINS_CHART_PATH);

        commands.add(sqlHost);
        commands.add(sqlUser);
        commands.add(sqlPass);

        commands.add(mapDatabase);
        commands.add(mapTable);
        commands.add(registryDatabase);
        commands.add(registryTable);
        commands.add(apiDatabase);
        commands.add(faceitTable);

        commands.add(teamId);
        commands.add(teamName);

        commands.add(startDate);
        commands.add(endDate);

        LOGGER.info("Running python file with args " + commands);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.warn("Error occurred while running Python. ");
            }

            File chartFile = new File(MAP_WINS_CHART_PATH);

            try {
                return new FileInputStream(chartFile);
            } catch(FileNotFoundException e) {
                return null;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteChart(String pathStr) {
        Path path = Paths.get(pathStr);

        try {
            Files.delete(path);
            System.out.println("File deleted successfully.");
        } catch (NoSuchFileException e) {
            System.out.println("File not found: " + e.getFile());
        } catch (IOException e) {
            String processName = ProcessUtil.getProcessName(pathStr);

            Astryx.LOGGER.error("Could not delete '" + pathStr + "', file is open in " + processName);
        }
    }
}
