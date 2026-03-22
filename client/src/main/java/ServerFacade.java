import com.google.gson.Gson;
import model.JsonSerialization;
import model.request.*;
import model.result.*;
import model.JsonSerialization.*;
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

    public HttpResponse<String> login(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        LoginRequest request = new LoginRequest(username, password);
        String jsonString = JsonSerialization.toJson(request);
        return server.doPost(host, port, "/session", jsonString, "");
    }

    public HttpResponse<String> logout(String authToken) throws URISyntaxException, IOException, InterruptedException {
        return server.doDelete(host, port, "/session", authToken);
    }

    public HttpResponse<String> register(String username, String password, String email) throws URISyntaxException, IOException, InterruptedException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String jsonString = JsonSerialization.toJson(request);
        return server.doPost(host, port, "/user", jsonString, "");
    }

    public HttpResponse<String> listGames(String authToken) throws URISyntaxException, IOException, InterruptedException {
        return server.doGet(host, port, "/game", authToken);
    }

    public HttpResponse<String> joinGame(String authToken, int gameID, String playerColor) throws URISyntaxException, IOException, InterruptedException {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        String jsonString = JsonSerialization.toJson(request);
        return server.doPut(host, port, "/game", jsonString, authToken);
    }

    public HttpResponse<String> createGame(String authToken, String gameName) throws URISyntaxException, IOException, InterruptedException {
        CreateGameRequest request = new CreateGameRequest(gameName);
        String jsonString = JsonSerialization.toJson(request);
        return server.doPost(host, port, "/game", jsonString, authToken);
    }

    public HttpResponse<String> clear(String authToken) throws URISyntaxException, IOException, InterruptedException {
        return server.doDelete(host, port, "/db", "");
    }




}