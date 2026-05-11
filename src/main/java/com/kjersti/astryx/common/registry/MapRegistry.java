//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.registry.object.MapRegistryObject;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MapRegistry {
    private static List<MapRegistryObject> registry;

    public static String[] getSubmapsFromParentId(String parentId) {

        return registry.stream()
                .filter((obj) -> obj.getSubmapParentId()
                        .equals(parentId))
                .map(MapRegistryObject::getMapId)
                .toArray(String[]::new);
    }

    public static String getMapIdFromName(String name) {
        Optional<String> mapId = registry.stream()
                .filter((obj) -> Arrays.stream(obj.getValidNames())
                        .toList()
                        .contains(name.toLowerCase()))
                .map(MapRegistryObject::getMapId)
                .findFirst();

        return mapId.orElse(null);
    }

    public static String getTypeFromId(String mapId) {
        Optional<String> mapType = registry.stream()
                .filter((obj) -> obj.getMapId().equals(mapId))
                .map(MapRegistryObject::getMapType)
                .findFirst();

        return mapType.orElse(null);
    }

    public static void updateRegistry() {
        String mapRegistryTable = SettingRegistry.getMapRegistryTable();
        String command = "SELECT * FROM " + mapRegistryTable;

        List<MapRegistryObject> newRegistry = new ArrayList<>();
        SqlObject array = SqlHandler.executeRegistryQuery(command);

        String[][] dataRows = array.getDataWithoutColumnNames();

        for (String[] row : dataRows) {
            MapRegistryObject obj = new MapRegistryObject(row);
            newRegistry.add(obj);
        }

        registry = newRegistry;
        Astryx.LOGGER.info("Registered " + registry.size() + " maps(s)");
    }

    public static List<MapRegistryObject> getRegistry() {
        return registry.stream().toList();
    }
}
