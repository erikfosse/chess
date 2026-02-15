package server;

import handler.HelloHandler;
import handler.RegisterHandler;
import io.javalin.Javalin;


public class ServerMain {

    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
        new ServerMain().run();
    }

    public void run() {
        var chessWeb = Javalin.create().start(8080);
        chessWeb.get("/hello", new HelloHandler());
        chessWeb.post("/user", new RegisterHandler());
    }

}
