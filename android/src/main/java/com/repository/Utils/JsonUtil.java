package com.repository.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by rich on 2016/4/15.
 */
public class JsonUtil {

    private static final Gson gson = new Gson();

    public static Map<String, Object> fromJson(String json) {
        return fromJson(json, Map.class);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return (T) gson.fromJson(json, typeOfT);
    }

    public static boolean isValidJson(String json) {
        try {
            fromJson(json);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }

    public static boolean isValidJson(String json, Type typeOfT) {
        try {
            fromJson(json, typeOfT);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }

}
