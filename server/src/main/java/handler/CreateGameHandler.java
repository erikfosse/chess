package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.exception.IncorrectAuthException;
import service.request.CreateGameRequest;
import service.request.ListGamesRequest;
import service.request.LoginRequest;
import service.result.LogoutResult;

public class CreateGameHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var authToken = ctx.headerMap().get("authorization");
        var request = processRequest(ctx, CreateGameRequest.class);
        GameService service = new GameService();
        var result = service.createGame(authToken, (CreateGameRequest) request);
        sendResult(ctx, result);
        switch (result) {
            case LogoutResult r -> ctx.status(200);
            case IncorrectAuthException r -> ctx.status(401);
            default -> ctx.status(500);
        }
    }
}
