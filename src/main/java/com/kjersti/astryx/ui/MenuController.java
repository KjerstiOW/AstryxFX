package com.kjersti.astryx.ui;

import com.kjersti.astryx.ui.terminal.TerminalController;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public static final int MENU_OFFSET_X = 8;
    public static final int X_OFFSET = 10;
    public static final int Y_OFFSET = 19;

    @FXML
    private Button exitMenu, terminalButton;

    @FXML
    private Pane terminalPane;

    @FXML
    ImageView openMenu;

    @FXML
    private Pane menuPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openMenu.setLayoutX(X_OFFSET+2);
        openMenu.setLayoutY(Y_OFFSET);

        //menuPane.setLayoutX(-200);
        menuPane.setLayoutX(MENU_OFFSET_X - 200);
        menuPane.setLayoutY(Y_OFFSET);

        exitMenu.setOnMouseClicked(event -> {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), menuPane);
            transition.setToX(0);

            transition.play();
        });

        openMenu.setOnMouseClicked(event -> {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), menuPane);
            transition.setToX(200);

            transition.play();
        });
    }
}
