//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.logging;

import com.kjersti.astryx.common.Maven;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.w3c.dom.Text;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;


public class AstryxLogManager extends LogManager {
    private static final Logger LOGGER = AstryxLogManager.getLogger("LogManager");

    private static final MultiOutputStream output = new MultiOutputStream(System.out);

    public static void addOutput(OutputStream stream) {
        LOGGER.info("Added output stream of class " + stream.getClass().getSimpleName());
        output.addStream(stream);
    }

    public static Logger getLogger(String name) {
        Maven maven = new Maven();

        name = padStringToMinLength(name, 4);
        String appName = maven.getBotName();
        return LogManager.getLogger(appName + "." + name);
    }

    public static String padStringToMinLength(String input, int minLength) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        } else if (input.length() >= minLength) {
            return input;
        } else {
            StringBuilder paddedString = new StringBuilder(input);
            while(paddedString.length() < minLength) {
                paddedString.append(' ');
            }
            return paddedString.toString();
        }
    }
}
