//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjersti.astryx.Astryx;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonManager {
    public JsonManager() {
    }

    public static List<String> parseJsonStringListToList(String str) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(str);

        return matcher.results()
                .map((m) -> m.group(1))
                .toList();
    }

    public static List<BigInteger> parseJsonBigIntListToList(String str) {
        str = str.substring(1, str.length() - 1);
        String[] splitList = str.split(",");
        List<String> trimmedStrings = Arrays.stream(splitList).map(String::trim).toList();

        return trimmedStrings.stream()
                .filter(s -> !s.isEmpty())
                .map((obj) -> BigInteger.valueOf(Long.parseLong(obj)))
                .toList();
    }

    public static Map<String, Object> readJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, Map.class);
    }
}
