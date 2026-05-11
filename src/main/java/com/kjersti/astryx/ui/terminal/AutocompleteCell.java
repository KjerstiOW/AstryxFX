package com.kjersti.astryx.ui.terminal;

import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.ui.UIMain;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import org.apache.logging.log4j.Logger;

public class AutocompleteCell extends ListCell<String> {
    public static final int Y_SIZE = 24;

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setStyle("");
        } else {
            setText(item);

            ListView<String> listView = getListView();
            if (listView != null) {
                SelectionModel<String> selectionModel = listView.getSelectionModel();
                int selectedIndex = selectionModel.getSelectedIndex();

                if (getIndex() == selectedIndex) {
                    setStyle("-fx-background-color: blue; -fx-text-fill: #FFFFFF;");
                } else {
                    setStyle("-fx-background-color: black; -fx-text-fill: #FFFF00;");
                }
            }
        }
    }
}