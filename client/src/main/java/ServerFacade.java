import com.google.gson.Gson;
import model.request.*;
import model.result.*;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class ServerFacade {

    private static ServerConnector server;
    private static String host;
    private static int port;

    public ServerFacade(String host, int port) {
        server = new ServerConnector();
        ServerFacade.host = host;
        ServerFacade.port = port;
    }

    public LoginResult login(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        LoginRequest request = new LoginRequest(username, password);
        String jsonString = toJson(request);
        HttpResponse<String> response = server.doPost(host, port, "/session", jsonString, null);
        return (LoginResult) fromJson(response.body(), LoginResult.class);
    }

    public LogoutResult logout(String authToken) throws URISyntaxException, IOException, InterruptedException {
        LogoutRequest request = new LogoutRequest(authToken);
        HttpResponse<String> response = server.doDelete(host, port, "/session");
        return (LogoutResult) fromJson(response.body(), LogoutResult.class);
    }

    public HttpResponse<String> register(String username, String password, String email) throws URISyntaxException, IOException, InterruptedException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String jsonString = toJson(request);
        return server.doPost(host, port, "/user", jsonString, null);
    }

    public ListGamesResult listGames(String authToken) throws URISyntaxException, IOException, InterruptedException {
        ListGamesRequest request = new ListGamesRequest(authToken);
        HttpResponse<String> response = server.doGet(host, port, "/game", authToken);
        return (ListGamesResult) fromJson(response.body(), RegisterResult.class);
    }

    public JoinGameResult joinGame(String authToken, String playerColor, int gameID) throws URISyntaxException, IOException, InterruptedException {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        String jsonString = toJson(request);
        HttpResponse<String> response = server.doPut(host, port, "/user", jsonString, authToken);
        return (JoinGameResult) fromJson(response.body(), RegisterResult.class);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws URISyntaxException, IOException, InterruptedException {
        CreateGameRequest request = new CreateGameRequest(gameName);
        String jsonString = toJson(request);
        HttpResponse<String> response = server.doPost(host, port, "/game", jsonString, authToken);
        return (CreateGameResult) fromJson(response.body(), RegisterResult.class);
    }

    public DeleteResult clear(String authToken) throws URISyntaxException, IOException, InterruptedException {
        DeleteRequest request = new DeleteRequest(authToken);
        HttpResponse<String> response = server.doDelete(host, port, "/db");
        return (DeleteResult) fromJson(response.body(), RegisterResult.class);
    }

    private String toJson(Object body) {
        Gson gson = new Gson();
        return gson.toJson(body);
    }

    private Object fromJson(String body, Class<?> requestClass) {
        Gson gson = new Gson();
        return gson.fromJson(body, requestClass);
    }

    private static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}