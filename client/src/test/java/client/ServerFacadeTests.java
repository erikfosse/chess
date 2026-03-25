package client;

import model.JsonSerialization;
import model.result.ListGamesResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import java.net.http.HttpResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("localhost", port);
    }

    @BeforeEach
    public void setup() {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            return;
        }
    }

    @AfterAll
    static void stopServer() {
        try {
            serverFacade.clear();
        } catch (Exception _) {}
        server.stop();
    }


    @Test
    public void clearSuccess() {
        try {
            serverFacade.register("erik", "pass", "email");
            serverFacade.clear();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void clearFail() {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void loginSuccess() {
        try {
            serverFacade.register("erik", "pass", "email");
            HttpResponse<String> response = serverFacade.login("erik", "pass");
            Assertions.assertEquals(200, response.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void loginFail() {
        try {
            serverFacade.register("erik", "pass", "email");
            HttpResponse<String> response = serverFacade.login("erik", "pass1");
            Assertions.assertNotEquals(200, response.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void registerSuccess() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "email");
            Assertions.assertEquals(200, response.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void registerFail() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", null);
            Assertions.assertNotEquals(200, response.statusCode());
        } catch (Exception e) {
            return;
        }
    }

    @Test
    public void logoutSuccess() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            HttpResponse<String> result = serverFacade.logout(authToken);
            Assertions.assertEquals(200, result.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void logoutFail() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            HttpResponse<String> result = serverFacade.logout("the");
            Assertions.assertNotEquals(200, result.statusCode());
        } catch (Exception e) {
            return;
        }
    }

    @Test
    public void createGameSuccess() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            HttpResponse<String> result = serverFacade.createGame(authToken, "Game1");
            Assertions.assertEquals(200, result.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void createGameFail() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            HttpResponse<String> result = serverFacade.createGame("the", "Game1");
            Assertions.assertNotEquals(200, result.statusCode());
        } catch (Exception e) {
            return;
        }
    }

    @Test
    public void listGamesSuccess() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            serverFacade.createGame(authToken, "Game1");
            serverFacade.createGame(authToken, "Game2");
            serverFacade.createGame(authToken, "Game3");
            HttpResponse<String> result = serverFacade.listGames(authToken);
            ListGamesResult listResult = (ListGamesResult) JsonSerialization.fromJson(result.body(), ListGamesResult.class);
            Assertions.assertEquals(3, listResult.games().size());
            Assertions.assertEquals(200, result.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void listGamesFail() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            serverFacade.createGame(authToken, "Game1");
            serverFacade.createGame(authToken, "Game2");
            serverFacade.createGame(authToken, "Game3");
            HttpResponse<String> result = serverFacade.listGames("authToken");
            ListGamesResult listResult = (ListGamesResult) JsonSerialization.fromJson(result.body(), ListGamesResult.class);
            Assertions.assertNotEquals(3, listResult.games().size());
            Assertions.assertNotEquals(200, result.statusCode());
        } catch (Exception e) {
            return;
        }
    }

    @Test
    public void joinGameSuccess() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            serverFacade.createGame(authToken, "Game1");
            HttpResponse<String> result = serverFacade.joinGame(authToken, 1, "WHITE");
            Assertions.assertEquals(200, result.statusCode());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void joinGameFail() {
        try {
            HttpResponse<String> response = serverFacade.register("erik", "pass", "gmail");
            RegisterResult res = (RegisterResult) JsonSerialization.fromJson(response.body(), RegisterResult.class);
            String authToken = res.authToken();
            serverFacade.createGame(authToken, "Game1");
            HttpResponse<String> result = serverFacade.joinGame("authToken", 1, "WHITE");
            Assertions.assertNotEquals(200, result.statusCode());
        } catch (Exception e) {
            return;
        }
    }
}
