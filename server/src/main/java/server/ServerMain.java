package server;

import chess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;


public class ServerMain {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);

        Javalin.create()
                .get("/hello", ctx -> ctx.result("Hello BYU!"))
                .start("127.0.0.1", 9999);

        System.out.println("Server is running... press Ctrl+C to stop.");
    }

    private static void handleHello(Context ctx) {
        ctx.result("Hello BYU!");
    }

    private static class HelloHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws  Exception {
            ctx.result("Hello BYU!");
        }
    }
}
