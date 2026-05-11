package com.kjersti.astryx.api;

import com.kjersti.astryx.common.logging.AstryxLogManager;
import okhttp3.*;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class ApiHandler {
    public static Logger LOGGER = AstryxLogManager.getLogger("api");

    private static String user = null;

    public static String getRequest(String endpoint, Map<String, String> headers, Map<String, String> queryParameters) {
        OkHttpClient client = new OkHttpClient();

        endpoint = addQueryParameters(endpoint, queryParameters);

        Request.Builder builder = new Request.Builder()
                .url(endpoint)
                .get();

        builder = addHeaders(builder, headers);

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 429) {
                    LOGGER.error("Rate limit exceeded.");
                } else {
                    LOGGER.error(response);
                }
            }

            return response.body().string();

        } catch (IOException e) {
            LOGGER.warn(e.toString());
        }

        return "";
    }

    public static String getRequest(String endpoint, Map<String, String> headers, Map<String, String> queryParameters,
                                    boolean suppressWarnings) {
        OkHttpClient client = new OkHttpClient();

        endpoint = addQueryParameters(endpoint, queryParameters);

        MediaType json = MediaType.get("application/json; charset=utf-8");
        Request.Builder builder = new Request.Builder()
                .url(endpoint)
                .get();

        builder = addHeaders(builder, headers);

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 429) {
                    LOGGER.error("Rate limit exceeded.");
                } else if (!suppressWarnings){
                    LOGGER.error(response);
                }
            }

            return response.body().string();

        } catch (IOException e) {
            if (!suppressWarnings) {
                LOGGER.warn(e.toString());
            }
        }

        return "";
    }

    public static Request.Builder addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) return builder;

        for (Map.Entry<String, String> set: headers.entrySet()) {
            builder.addHeader(set.getKey(), set.getValue());
        }

        return builder;
    }

    public static String addQueryParameters(String endpoint, Map<String, String> queries) {
        if (queries == null || queries.isEmpty()) return endpoint;

        StringBuilder urlWithParams = new StringBuilder(endpoint);
        boolean isFirst = !endpoint.contains("?");

        for (Map.Entry<String, String> entry : queries.entrySet()) {
            if (isFirst) {
                urlWithParams.append("?");
                isFirst = false;
            } else {
                urlWithParams.append("&");
            }

            try {
                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                urlWithParams.append(encodedKey).append("=").append(encodedValue);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Could not encode query parameters on " + endpoint);
            }
        }

        return urlWithParams.toString();
    }

    public static boolean hasUser() {
        return user != null;
    }

    public static String getCurrentUser() {
        return user;
    }

    public static void setUser(String user) {
        if (hasUser()) {
            LOGGER.warn("Unable to change concurrent API user, already being used by " + ApiHandler.user);
        }

        ApiHandler.user = user;
    }

    public static void resetUser() {
        ApiHandler.user = null;
    }
}
