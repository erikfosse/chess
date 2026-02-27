package server;

import handler.*;
import io.javalin.Javalin;


public class Server {

    private final Javalin javalin;

    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);
    }

    public Server() {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", new RegisterHandler());
        javalin.post("/session", new LoginHandler());
        javalin.delete("/session", new LogoutHandler());
        javalin.get("/game", new ListGamesHandler());
        javalin.post("/game", new CreateGameHandler());
        javalin.put("/game", new JoinGameHandler());
        javalin.delete("/db", new ClearApplicationHandler());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
