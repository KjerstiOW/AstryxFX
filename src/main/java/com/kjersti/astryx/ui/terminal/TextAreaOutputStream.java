package com.kjersti.astryx.ui.terminal;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;


public class TextAreaOutputStream extends OutputStream {

    private final TextArea textArea;

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        Platform.runLater(() -> {
            textArea.appendText(String.valueOf((char) b));
            autoScroll();
        });
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) {
        Platform.runLater(() -> {
            textArea.appendText(new String(b, off, len));
            autoScroll();
        });
    }

    @Override
    public void flush() {}

    private void autoScroll() {
        textArea.setScrollTop(Double.MAX_VALUE);
    }
}
