package com.kjersti.astryx;

import com.kjersti.astryx.common.commands.UpdateFaceit;
import com.kjersti.astryx.common.lang.LanguageLoader;
import com.kjersti.astryx.common.registry.*;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.bot.Bot;
import com.kjersti.astryx.common.Maven;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.ui.terminal.TerminalController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class Astryx extends Application {
    public static final Logger LOGGER = AstryxLogManager.getLogger("main");
    private static TerminalController terminalController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        onStartup();
        showUI(primaryStage);
        onRuntime();
    }

    public void showUI(Stage primaryStage) throws IOException {
        File menuFile = new File("src/main/resources/com/kjersti/astryx/ui/menu.fxml");
        File terminalFile = new File("src/main/resources/com/kjersti/astryx/ui/terminal.fxml");

        //Parent menuLoader = FXMLLoader.load(menuFile.toURI().toURL());
        Parent terminalLoader = FXMLLoader.load(terminalFile.toURI().toURL());

        LOGGER.info("Attempting astryx.ui startup");

        int width = SettingRegistry.getWindowWidth();
        int height = SettingRegistry.getWindowHeight();

        primaryStage.setTitle("Astryx Control Panel");
        primaryStage.setResizable(false);
        primaryStage.setHeight(height);
        primaryStage.setWidth(width);

        StackPane root = new StackPane();
        //root.getChildren().addAll(terminalLoader, menuLoader);

        primaryStage.setScene(new Scene(terminalLoader, 800, 600));

        primaryStage.setOnCloseRequest(event -> {
            Platform.runLater(this::onExit);
        });

        if (SettingRegistry.showTerminal()) {
            primaryStage.show();
        } else {
            LOGGER.warn("Terminal disabled in settings");
        }
    }

    public void onStartup() {
        Maven mavenData = new Maven();

        String botName = mavenData.getBotName();
        String botId = mavenData.getBotId();
        String botVersion = mavenData.getBotVersion();
        String devId = mavenData.getDevId();

        LOGGER.info("Starting {} ({}-{})", botName, botId, botVersion);
        SettingRegistry.updateSettings();
        TokenRegistry.updateTokens();
        LanguageLoader.loadProperties();
        SqlHandler.setupSqlDriver();
        MapRegistry.updateRegistry();
        TeamRegistry.updateRegistry();
        AstryxCommandRegistry.init(mavenData);

        LOGGER.info("Thank you {} for giving me life", devId);
    }

    public void onRuntime() {
        boolean runBot = SettingRegistry.enableBot();

        if (runBot) {
            Bot discordBot = new Bot();

            Thread thread = new Thread(discordBot::run);
            thread.start();
        } else {
            LOGGER.warn("Discord bot disabled in settings");
        }

        if (SettingRegistry.getBoolean("enable_update_mode")) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        UpdateFaceit cmd = new UpdateFaceit();
                        cmd.run(new String[]{"false"});

                        Thread.sleep(SettingRegistry.getUpdateDowntime());
                    } catch (Exception ignored) {}
                }
            });

            thread.start();
        }
    }

    public void onExit() {
        LOGGER.info("Completed GUI runtime, run_after_close: " + SettingRegistry.runAfterClose());

        if (!SettingRegistry.runAfterClose()) {
            LOGGER.info("Exiting program");

            System.exit(-1);
        }

        LOGGER.info("Running after close enabled");
    }

    public static TerminalController getTerminalController() {
        return terminalController;
    }

    public static void setTerminalController(TerminalController terminalController) {
        Astryx.terminalController = terminalController;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}