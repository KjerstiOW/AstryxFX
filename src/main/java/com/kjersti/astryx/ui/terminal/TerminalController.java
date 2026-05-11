package com.kjersti.astryx.ui.terminal;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.logging.TextAreaAppender;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class TerminalController implements Initializable {
    @FXML
    public AnchorPane terminalPane;

    @FXML
    private TextArea terminalOutput;

    @FXML
    private TextField terminalInput;

    @FXML
    private ListView<String> autocompleteList;

    private AutocompleteHandler handler;

    private boolean getNextInput;
    private CompletableFuture<String> inputFuture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Astryx.setTerminalController(this);

        Font font = Font.loadFont(
                Objects.requireNonNull(
                            getClass().getResource("/com/kjersti/astryx/ui/font/JetBrainsMono-Regular.ttf")
                        )
                        .toExternalForm(), 12);

        terminalInput.setFont(font);
        terminalOutput.setFont(font);

        TextAreaAppender.setTextArea(terminalOutput);

        handler = new AutocompleteHandler(autocompleteList, terminalInput, this);

        Platform.runLater(terminalInput::requestFocus);
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        terminalInput.requestFocus();
        handler.handleKeyPress(keyEvent);

        if (keyEvent.getCode() == KeyCode.ENTER) {
            String input = terminalInput.getText();

            if (getNextInput && inputFuture != null) {
                inputFuture.complete(input);
                getNextInput = false;
            } else {
                handler.addPreviousCommand(input);
                AstryxCommandRegistry.onCommandLineInput(input);
            }

            terminalInput.setText("");
        }
    }

    @FXML
    public void onOutputTerminalClicked() {
        terminalInput.requestFocus();
    }

    public CompletableFuture<String> getNextFuture() {
        inputFuture = new CompletableFuture<>();
        getNextInput = true;

        return inputFuture;
    }

    public String getNextInput() {
        try {
            return getNextFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isFreeInput() {
        return !getNextInput;
    }
}
