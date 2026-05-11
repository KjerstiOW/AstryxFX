//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry.object;

import com.kjersti.astryx.common.util.JsonManager;

import java.util.Arrays;
import java.util.List;

public class MapRegistryObject {
    private final String mapId;
    private final String mapType;
    private final String color;
    private final String[] validNames;
    private final String submapParentId;

    public MapRegistryObject(String mapId, String mapType, String color, String[] validNames, String submapParentId) {
        this.mapId = mapId;
        this.mapType = mapType;
        this.color = color;
        this.validNames = validNames;
        this.submapParentId = submapParentId;
    }

    public MapRegistryObject(String[] sqlRow) {
        this.mapId = sqlRow[0];
        this.mapType = sqlRow[1];
        this.color = sqlRow[2];
        this.submapParentId = sqlRow[4];
        List<String> loweredNames = JsonManager.parseJsonStringListToList(sqlRow[3]);
        this.validNames = loweredNames.stream()
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }

    public String getMapId() {
        return this.mapId;
    }

    public String getMapType() {
        return this.mapType;
    }

    public String[] getValidNames() {
        return this.validNames;
    }

    public String getSubmapParentId() {
        return this.submapParentId;
    }

    public boolean isValidName(String name) {
        return Arrays.asList(this.validNames).contains(name.toLowerCase());
    }

    @Override
    public String toString() {
        return "MapRegistryObject{" +
                "mapId='" + mapId + '\'' +
                ", mapType='" + mapType + '\'' +
                ", validNames=" + Arrays.toString(validNames) +
                ", submapParentId='" + submapParentId + '\'' +
                '}';
    }
}
