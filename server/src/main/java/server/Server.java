package server;

import dataaccess.DatabaseManager;
import handler.*;
import handler.websocket.WebSocketHandler;
import io.javalin.Javalin;
import model.exception.DataAccessException;

import java.sql.SQLException;


public class Server {

    private final Javalin javalin;

    public static void main(String[] args) throws DataAccessException, SQLException {
        Server server = new Server();
        DatabaseManager.createDatabase();
        server.run(8080);
    }

    public Server() {
        WebSocketHandler webSocketHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler())
                .post("/session", new LoginHandler())
                .delete("/session", new LogoutHandler())
                .get("/game", new ListGamesHandler())
                .post("/game", new CreateGameHandler())
                .put("/game", new JoinGameHandler())
                .delete("/db", new ClearApplicationHandler())
                .ws("/ws", ws -> {
                   ws.onConnect(webSocketHandler);
                   ws.onMessage(webSocketHandler);
                   ws.onClose(webSocketHandler);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Error: database failed to start");
        }
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
