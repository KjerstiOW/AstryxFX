//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry;

import java.io.BufferedReader;
import java.io.FileReader;

public class PatchRegistry {
    public PatchRegistry() {
    }

    public static String getPatch() {
        // Test Line
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\Overwatch\\.build.info"));

            String patch;
            try {
                br.readLine();

                String line = br.readLine();
                String[] splitLine = line.split("\\|");
                String patchAndBuild = splitLine[splitLine.length - 3];
                int finalPeriodIndex = patchAndBuild.lastIndexOf(".");

                patch = patchAndBuild.substring(0, finalPeriodIndex);
            } catch (Throwable var7) {
                try {
                    br.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }

                throw var7;
            }

            br.close();

            return patch;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
