//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Maven {
    private String botName;
    private String botId;
    private String botVersion;
    private String devId;
    private String devName;

    public Maven() {
        updateAllData();
    }

    public void updateAllData() {
        updateBotName();
        updateBotId();
        updateBotVersion();
        updateDevId();
        updateDevName();
    }

    public String getDeveloperId() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            return (model.getDevelopers().get(0)).getId();
        } catch (FileNotFoundException e) {
            return "None (FNFE)";
        } catch (IOException e) {
            return "None (IOE)";
        } catch (XmlPullParserException e) {
            return "None (XPPE)";
        }
    }

    public void updateBotName() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            this.botName = model.getArtifactId();
        } catch (FileNotFoundException e) {
            this.botName =  "None (FNFE)";
        } catch (IOException e) {
            this.botName =  "None (IOE)";
        } catch (XmlPullParserException e) {
            this.botName =  "None (XPPE)";
        }
    }

    public void updateBotId() {
        this.botId = this.botName.toLowerCase();
    }

    public void updateBotVersion() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            this.botVersion = model.getVersion();
        } catch (FileNotFoundException e) {
            this.botVersion = "None (FNFE)";
        } catch (IOException e) {
            this.botVersion = "None (IOE)";
        } catch (XmlPullParserException e) {
            this.botVersion = "None (XPPE)";
        }
    }

    public void updateDevId() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            this.devId = (model.getDevelopers().get(0)).getId();
        } catch (FileNotFoundException e) {
            this.devId = "None (FNFE)";
        } catch (IOException e) {
            this.devId = "None (IOE)";
        } catch (XmlPullParserException e) {
            this.devId = "None (XPPE)";
        }
    }

    public void updateDevName() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            this.devName = (model.getDevelopers().get(0)).getName();
        } catch (FileNotFoundException e) {
            this.devName =  "None (FNFE)";
        } catch (IOException e) {
            this.devName =  "None (IOE)";
        } catch (XmlPullParserException e) {
            this.devName =  "None (XPPE)";
        }
    }

    public String getBotName() {
        return botName;
    }

    public String getBotId() {
        return botId;
    }

    public String getBotVersion() {
        return botVersion;
    }

    public String getDevId() {
        return devId;
    }

    public String getDevName() {
        return devName;
    }
}
