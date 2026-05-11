package com.kjersti.astryx.ui.terminal;

import com.kjersti.astryx.common.annotations.ProgramCommandWrapper;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import reactor.util.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutocompleteHandler {
    private static final int AUTOCOMPLETE_PADDING = 5;
    public static final int MAX_VISIBLE_ROWS = 5;

    private final String[] allCommandIds;
    private final TextField terminalInput;
    private final ListView<String> autocompleteList;

    private int autocompleteIndex;
    private String[] autoCompleteSuggs;

    private final List<String> previousCommands;

    private final TerminalController controller;
    public AutocompleteHandler(@NonNull ListView<String> autocompleteList,
                               @NonNull TextField terminalInput,
                               @NonNull TerminalController controller) {
        this.autocompleteIndex = 0;
        this.allCommandIds = AstryxCommandRegistry.visibleCommands.stream()
                .map(ProgramCommandWrapper::getId)
                .toArray(String[]::new);
        this.autoCompleteSuggs = new String[0];
        this.terminalInput = terminalInput;
        this.autocompleteList = autocompleteList;
        this.previousCommands = new ArrayList<>();
        this.controller = controller;

        previousCommands.add("");

        Platform.runLater(this::setupAutoComplete);
    }

    public void setupAutoComplete() {
        autocompleteList.setCellFactory(listView -> new AutocompleteCell());

        setYPosition();
        addFocusListener();
        addTextListener();
        updateList();
    }

    public void setYPosition() {
        double yPos = terminalInput.getLayoutY();
        double ySize = terminalInput.getScaleY();

        autocompleteList.setLayoutY(
                yPos-ySize-AUTOCOMPLETE_PADDING-
                        Math.min(
                                MAX_VISIBLE_ROWS * AutocompleteCell.Y_SIZE,
                                autocompleteList.getItems().size() * AutocompleteCell.Y_SIZE
                        )
        );
    }

    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleEnterPress();
        } else if (event.getCode() == KeyCode.TAB && autocompleteList.isVisible()) {
            handleTabPress();
        } else if (event.getCode() == KeyCode.DOWN) {
            handleDownPress();
        } else if (event.getCode() == KeyCode.UP) {
            handleUpPress();
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            handleBackspacePress();
        }

        event.consume();
    }

    public void handleEnterPress() {
        updateSelectedCell(0);
        autocompleteList.setVisible(false);
    }

    public void handleTabPress() {
        if (autoCompleteSuggs.length == 0) return;

        terminalInput.setText(autoCompleteSuggs[autocompleteIndex]);
        terminalInput.requestFocus();

        updateSelectedCell(0);

        autocompleteList.setVisible(false);

        terminalInput.positionCaret(terminalInput.getText().length());
    }

    public void handleUpPress() {
        if (terminalInput.getText().equals("")) {
            String prevCommand = previousCommands.get(previousCommands.size()-1);

            terminalInput.setText(prevCommand);
        } else {
            updateSelectedCell(autocompleteIndex-1);
        }
    }

    public void handleDownPress() {
        updateSelectedCell(autocompleteIndex+1);
    }

    public void handleBackspacePress() {
        updateSelectedCell(0);
        autocompleteList.setVisible(false);
    }

    public void addFocusListener() {
        terminalInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                autocompleteList.setVisible(false);
            }
        });
    }

    public void addTextListener() {
        terminalInput.textProperty().addListener((obs, oldText, newText) -> {
            updateSelectedCell(0);
            updateSuggestions(newText);

            if (newText.equals("") || autoCompleteSuggs.length == 0 ) {
                autocompleteList.setVisible(false);

                return;
            }

            autocompleteList.prefHeightProperty().bind(
                    autocompleteList.fixedCellSizeProperty().multiply(
                            Math.min(MAX_VISIBLE_ROWS, autocompleteList.getItems().size())));

            autocompleteList.setVisible(true);
            setYPosition();
        });
    }

    public void updateSelectedCell(int newCellIndex) {
        ObservableList<String> items = FXCollections.observableArrayList(autoCompleteSuggs);

        if (newCellIndex >= items.size()) {
            autocompleteIndex = 0;
        } else if (newCellIndex < 0) {
            autocompleteIndex = items.size()-1;
        } else {
            autocompleteIndex = newCellIndex;
        }

        autocompleteList.getSelectionModel().select(autocompleteIndex);
        updateList();
    }

    public void updateSuggestions(String text) {
        if (text.isEmpty()) {
            autoCompleteSuggs = new String[0];
            updateList();
            return;
        }

        autoCompleteSuggs = Arrays.stream(allCommandIds)
                .filter(obj -> obj.startsWith(text))
                .toArray(String[]::new);

        updateList();
    }

    private void updateList() {

        ObservableList<String> items = FXCollections.observableArrayList(autoCompleteSuggs);

        autocompleteList.setItems(items);
        autocompleteList.getSelectionModel().select(autocompleteIndex);
        autocompleteList.refresh();
    }

    public void addPreviousCommand(String command) {
        previousCommands.add(command);
    }
}
