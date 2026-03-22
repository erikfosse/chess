package model;

import com.google.gson.Gson;

public class JsonSerialization {
    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static String toJson(Object body) {
        Gson gson = new Gson();
        return gson.toJson(body);
    }

    public static Object fromJson(String body, Class<?> requestClass) {
        Gson gson = new Gson();
        return gson.fromJson(body, requestClass);
    }
}
