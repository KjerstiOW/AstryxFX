//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.sql;

import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class SqlHandler {
    public static final Logger LOGGER = AstryxLogManager.getLogger("sql");
    public static final Logger FILE_LOGGER = LogManager.getLogger("com.kjersti.astryx.sql");

    public SqlHandler() {
    }

    public static void setupSqlDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("Loaded SQL driver");
        } catch (Exception var1) {
            LOGGER.error("Could not load SQL driver - try checking the xampp control panel");
            var1.printStackTrace();
            System.exit(1);
        }

    }

    public static SqlObject executeQuery(String database, String query) {
        String sqlUrl = getSqlUrl(database);
        String sqlUser = SettingRegistry.getSqlUser();
        String sqlPass = SettingRegistry.getSqlPass();

        try (Connection connection = DriverManager.getConnection(sqlUrl, sqlUser, sqlPass);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            PreparedStatement statement2 = connection.prepareStatement("SHOW PROCESSLIST");
            ResultSet result2 = statement2.executeQuery();

            SqlObject obj = new SqlObject(result);
            SqlObject obj2 = new SqlObject(result2);

            //System.out.println(obj2);

            FILE_LOGGER.info("SUCCESS, " + query);
            connection.close();
            statement.close();
            statement2.close();
            result.close();
            result2.close();

            return obj;
        } catch (SQLException e) {
            LOGGER.error("Could not execute SQL query. {}, " + query, e);
            FILE_LOGGER.error("FAILURE, {}" + query, e);
            return new SqlObject(new String[0][0]);
        }
    }

    public static void executeUpdate(String database, String updateQuery) {
        String sqlUrl = getSqlUrl(database);
        String sqlUser = SettingRegistry.getSqlUser();
        String sqlPass = SettingRegistry.getSqlPass();

        try (Connection connection = DriverManager.getConnection(sqlUrl, sqlUser, sqlPass);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            int rowsAffected = statement.executeUpdate();
            FILE_LOGGER.info("SUCCESS, " + updateQuery + ". Rows affected: " + rowsAffected);

            connection.close();
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Could not execute SQL update: " + updateQuery, e);
            FILE_LOGGER.error("FAILURE, " + updateQuery, e);
        }
    }

    public static SqlObject executeApiQuery(String query) {
        String apiDatabase = SettingRegistry.getApiDatabase();

        return executeQuery(apiDatabase, query);
    }

    public static void executeApiUpdate(String updateQuery) {
        String apiDatabase = SettingRegistry.getApiDatabase();

        executeUpdate(apiDatabase, updateQuery);
    }

    public static SqlObject executeMapQuery(String query) {
        String mapDatabase = SettingRegistry.getMapDatabase();

        return executeQuery(mapDatabase, query);
    }

    public static void executeMapUpdate(String updateQuery) {
        String mapDatabase = SettingRegistry.getMapDatabase();

        executeUpdate(mapDatabase, updateQuery);
    }

    public static SqlObject executeRegistryQuery(String query) {
        String registryDatabase = SettingRegistry.getRegistryDatabase();

        return executeQuery(registryDatabase, query);
    }

    public static void executeRegistryUpdate(String updateQuery) {
        String registryDatabase = SettingRegistry.getRegistryDatabase();

        executeUpdate(registryDatabase, updateQuery);
    }

    public static String getSqlUrl(String database) {
        return "jdbc:mysql://" + SettingRegistry.getSqlHost() + ":3306/" + database;
    }
}
