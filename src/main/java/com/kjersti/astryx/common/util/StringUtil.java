package com.kjersti.astryx.common.util;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.common.annotations.ProgramCommandWrapper;
import com.kjersti.astryx.common.registry.AstryxCommandRegistry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StringUtil {
    public static String padString(String str, int length) {
        return String.format("%-" + length + "s", str);
    }

    public static int getMaxLength(List<String> items) {
        AtomicInteger maxLength = new AtomicInteger();

        items.forEach(str -> {
            if (str.length() > maxLength.get()) {
                maxLength.set(str.length());
            }
        });

        return maxLength.get();
    }

    @SafeVarargs
    public static String getEnumeratedList(List<String>... lists) {
        if (lists.length == 0 || lists[0].isEmpty()) {
            return "";
        }

        int size = lists[0].size();
        for (List<String> list : lists) {
            if (list.size() != size) {
                Astryx.LOGGER.error("Attempted to create a list with differently sized lists");
                return "RuntimeError";
            }
        }

        StringBuilder builder = new StringBuilder();

        int[] paddings = new int[lists.length + 1];
        paddings[0] = String.valueOf(size).length() + 1;
        for (int i = 0; i < lists.length; i++) {
            paddings[i + 1] = getMaxLength(lists[i]) + 1;
        }

        for (int i = 0; i < size; i++) {
            builder.append(padString(String.valueOf(i + 1), paddings[0]));

            for (int j = 0; j < lists.length; j++) {
                builder.append(padString(lists[j].get(i), paddings[j + 1]));
            }
            builder.append("\n");
        }

        return builder.toString().trim();
    }

    public static String getSQLLocation(String db, String sqlTable) {
        return db + "." + sqlTable;
    }

    public static boolean canBeCastedToInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean canBeCastedToBoolean(String str) {
        try {
            Boolean.parseBoolean(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatUnix(long unix) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unix), ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    public static boolean isValidDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean parseBooleanFromBinaryString(String str) {
        try {
            int binary = Integer.parseInt(str);

            return parseBooleanFromBinary(binary);
        } catch (Exception e) {
            try {
                return Boolean.parseBoolean(str);
            } catch(Exception e2) {
                return false;
            }
        }
    }

    public static boolean parseBooleanFromBinary(int binary) {
        if (binary == 0) {
            return false;
        }

        return true;
    }
}
