//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.registry.object.TeamRegistryObject;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamRegistry {
    private static List<TeamRegistryObject> registry;

    public TeamRegistry() {
    }

    public static List<TeamRegistryObject> getRegistry() {
        return registry.stream().toList();
    }

    public static void updateRegistry() {
        String sqlTeamRegistry = SettingRegistry.getTeamRegistryTable();
        String command = "SELECT * FROM " + sqlTeamRegistry;

        List<TeamRegistryObject> newRegistry = new ArrayList<>();
        SqlObject array = SqlHandler.executeRegistryQuery(command);
        String[][] dataRows = array.getDataWithoutColumnNames();

        for (String[] row : dataRows) {
            TeamRegistryObject obj = new TeamRegistryObject(row);
            newRegistry.add(obj);
        }

        registry = newRegistry;
        Astryx.LOGGER.info("Registered " + registry.size() + " team(s)");
    }

    public static String getTeamIdFromChannelId(BigInteger channelId) {
        Optional<TeamRegistryObject> row = registry.stream()
                .filter((obj) -> obj.getCodesId()
                        .equals(channelId))
                .findFirst();

        return row.isEmpty() ? null : row.get().getTeamId();
    }

    public static String getTeamIdFromCategoryId(BigInteger categoryId) {
        Optional<TeamRegistryObject> row = registry.stream()
                .filter((obj) -> obj.matchesCategoryId(categoryId))
                .findFirst();

        return row.isEmpty() ? null : row.get().getTeamId();
    }

    public static List<BigInteger> getAllCodeChannelIds() {
        return registry.stream()
                .map(TeamRegistryObject::getCodesId)
                .toList();
    }
}
