//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.registry.object.MapDataObject;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;

import java.util.ArrayList;
import java.util.List;

public class MapDataRegistry {

    public MapDataRegistry(String sqlTable) {
        this.updateRegistry(sqlTable);
    }

    public void updateRegistry(String sqlTable) {
        List<MapDataObject> newRegistry = new ArrayList<>();
        SqlObject array = SqlHandler.executeMapQuery("SELECT * FROM " + sqlTable);

        String[][] dataRows = array.getDataWithoutColumnNames();

        for (String[] row: dataRows) {
            MapDataObject obj = new MapDataObject(row);
            newRegistry.add(obj);
        }

        Astryx.LOGGER.info("Registered " + newRegistry.size() + " rows of map data from table '" + sqlTable + "'");
    }
}
