//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SqlObject {
    private String[][] data;

    public SqlObject(ResultSet resultSet) {
        try {
            this.data = getResultSetAsStringArray(resultSet);
        } catch (Exception var3) {
            SqlHandler.LOGGER.info("No result set found");
            this.data = new String[0][0];
        }

    }

    public SqlObject(String[][] data) {
        this.data = data;
    }

    public String[][] getData() {
        return (String[][])this.data.clone();
    }

    public String[][] getDataWithoutColumnNames() {
        String[][] newArray = new String[this.data.length - 1][];
        System.arraycopy(this.data, 1, newArray, 0, this.data.length - 1);
        return newArray;
    }

    public String getValue(int row, int col) {
        return this.data[row][col];
    }

    public String[] getColumnNames() {
        return this.data[0];
    }

    public String[] getRow(int index) {
        return this.data[index];
    }

    public String[] getColumnEntries(int colIndex) {
        String[] columnData = new String[this.data.length - 1];

        for(int i = 1; i < this.data.length; ++i) {
            columnData[i - 1] = this.data[i][colIndex];
        }

        return columnData;
    }

    public void updateData(String update, int row, int column) {
        this.data[row][column] = update;
    }

    public static String[][] getResultSetAsStringArray(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String[]> rows = new ArrayList();
        String[] columnNames = new String[columnCount];

        for(int i = 1; i <= columnCount; ++i) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        rows.add(columnNames);

        int i;
        while(resultSet.next()) {
            String[] row = new String[columnCount];

            for(i = 1; i <= columnCount; ++i) {
                Object value = resultSet.getObject(i);
                row[i - 1] = convertToString(value);
            }

            rows.add(row);
        }

        String[][] resultArray = new String[rows.size()][columnCount];

        for(i = 0; i < rows.size(); ++i) {
            resultArray[i] = (String[])rows.get(i);
        }

        return resultArray;
    }

    private static String convertToString(Object value) {
        return value == null ? "null" : value.toString();
    }

    public static String arrayToString(String[][] array) {
        int[] maxLengths = new int[array[0].length];

        String[][] var3;
        int var4;
        int var5;
        String[] row;
        for(int i = 0; i < array[0].length; ++i) {
            var3 = array;
            var4 = array.length;

            for(var5 = 0; var5 < var4; ++var5) {
                row = var3[var5];
                maxLengths[i] = Math.max(maxLengths[i], row[i].length());
            }
        }

        StringBuilder sb = new StringBuilder();
        var3 = array;
        var4 = array.length;

        for(var5 = 0; var5 < var4; ++var5) {
            row = var3[var5];

            for(int i = 0; i < row.length; ++i) {
                sb.append(String.format("%-" + maxLengths[i] + "s ", row[i]));
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public String toString() {
        return arrayToString(this.data);
    }
}
