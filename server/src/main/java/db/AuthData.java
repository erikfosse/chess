package db;

import model.AuthRecord;

import java.util.HashMap;
import java.util.Map;

public class AuthData {
    private static Map<String, AuthRecord> authTokens = new HashMap<>();
    public static void addAuth(AuthRecord auth) {
        authTokens.put(auth.authToken(), auth);
    }
    public static AuthRecord getAuth(String auth) {
        return authTokens.get(auth);
    }
    public static void delAuth(String auth) {
        authTokens.remove(auth);
    }
    public static void deleteData() {
        authTokens = new HashMap<>();
    }
}