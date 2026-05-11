//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.registry.object;

import com.kjersti.astryx.common.util.JsonManager;

import java.math.BigInteger;
import java.util.Arrays;

public class TeamRegistryObject {
    private final String teamId;
    private final BigInteger codesId;
    private final BigInteger[] categoryIds;
    private final BigInteger[] whitelistedIds;
    private final BigInteger[] whitelistedRoles;
    private final String[] players;

    public TeamRegistryObject(String teamId, BigInteger[] categoryIds, BigInteger codesId, BigInteger[] whitelistedIds, BigInteger[] whitelistedRoles, String[] players) {
        this.teamId = teamId;
        this.categoryIds = categoryIds;
        this.codesId = codesId;
        this.whitelistedIds = whitelistedIds;
        this.whitelistedRoles = whitelistedRoles;
        this.players = players;
    }

    public TeamRegistryObject(String[] sqlRow) {
        this.teamId = sqlRow[0];
        this.categoryIds = JsonManager.parseJsonBigIntListToList(sqlRow[1]).toArray(new BigInteger[0]);
        this.codesId = BigInteger.valueOf(Long.parseLong(sqlRow[2]));
        this.whitelistedIds = JsonManager.parseJsonBigIntListToList(sqlRow[3]).toArray(new BigInteger[0]);
        this.whitelistedRoles = JsonManager.parseJsonBigIntListToList(sqlRow[4]).toArray(new BigInteger[0]);
        this.players = JsonManager.parseJsonStringListToList(sqlRow[5]).toArray(new String[0]);
    }

    public static BigInteger parseStringToBigInt(String str) {
        return BigInteger.valueOf(Long.parseLong(str));
    }

    public String getTeamId() {
        return this.teamId;
    }

    public BigInteger[] getCategoryIds() {
        return this.categoryIds;
    }

    public BigInteger getCodesId() {
        return this.codesId;
    }

    public BigInteger[] getWhitelistedIds() {
        return this.whitelistedIds;
    }

    public BigInteger[] getWhitelistedRoles() {
        return this.whitelistedRoles;
    }

    public String[] getPlayers() {
        return this.players;
    }

    public boolean matchesChannelId(BigInteger channelId) {
        return channelId.equals(this.codesId);
    }

    public boolean matchesCategoryId(BigInteger categoryId) {
        return Arrays.asList(this.categoryIds).contains(categoryId);
    }

    public String toString() {
        return "TeamRegistryObject{teamId='" + this.teamId + "', codesId=" + this.codesId + ", categoryIds=" + Arrays.toString(this.categoryIds) + ", whitelistedIds=" + Arrays.toString(this.whitelistedIds) + ", whitelistedRoles=" + Arrays.toString(this.whitelistedRoles) + ", players=" + Arrays.toString(this.players) + "}";
    }
}
