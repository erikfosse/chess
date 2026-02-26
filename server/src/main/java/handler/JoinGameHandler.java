package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.exception.IncorrectAuthException;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.result.LogoutResult;

public class JoinGameHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var authToken = ctx.headerMap().get("authorization");
        var request = processRequest(ctx, CreateGameRequest.class);
        GameService service = new GameService();
        var result = service.joinGame(authToken, (JoinGameRequest) request);
        sendResult(ctx, result);
        switch (result) {
            case LogoutResult r -> ctx.status(200);
            case IncorrectAuthException r -> ctx.status(401);
            default -> ctx.status(500);
    }
}
