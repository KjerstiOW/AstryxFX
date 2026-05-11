package com.kjersti.astryx.common.logging;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "TextAreaAppender", category = "Core", elementType = Appender.ELEMENT_TYPE)
public class TextAreaAppender extends AbstractAppender {

    private static TextArea textArea;

    public TextAreaAppender(String name, Layout<? extends Serializable> layout) {
        super(name, null, layout, false);
    }

    @PluginFactory
    public static TextAreaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout) {
        if (name == null) {
            LOGGER.error("No name provided for TextAreaAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new TextAreaAppender(name, layout);
    }

    @Override
    public void append(LogEvent event) {
        final String message = new String(getLayout().toByteArray(event));

        Platform.runLater(() -> textArea.appendText(message));
    }

    public static void setTextArea(TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }
}