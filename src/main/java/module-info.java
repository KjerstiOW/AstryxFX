module com.kjersti.astryx {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires toml4j;
    requires discord4j.core;
    requires reactor.core;
    requires maven.model;
    requires plexus.utils;
    requires org.reactivestreams;
    requires discord4j.common;
    requires org.apache.logging.log4j;
    requires discord4j.discordjson;
    requires discord4j.rest;
    requires org.apache.logging.log4j.core;
    requires okhttp3;
    requires com.fasterxml.jackson.annotation;
    requires slf4j.api;
    requires annotations;
    requires org.reflections;
    requires com.fasterxml.jackson.databind;

    opens com.kjersti.astryx.ui to javafx.fxml;

    opens com.kjersti.astryx to javafx.fxml;
    exports com.kjersti.astryx;
    exports com.kjersti.astryx.ui to javafx.fxml;
    exports com.kjersti.astryx.common.util to javafx.fxml;
    opens com.kjersti.astryx.common.util to javafx.fxml;
    exports com.kjersti.astryx.common.logging to javafx.fxml, org.apache.logging.log4j.core;
    opens com.kjersti.astryx.common.logging to javafx.fxml;
    exports com.kjersti.astryx.ui.terminal to javafx.fxml;
    opens com.kjersti.astryx.ui.terminal to javafx.fxml;
}
