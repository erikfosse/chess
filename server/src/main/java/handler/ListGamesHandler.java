package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.result.ListGamesResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.exception.IncorrectAuthException;
import model.request.ListGamesRequest;
import model.result.LogoutResult;

public class ListGamesHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = new ListGamesRequest(ctx.headerMap().get("authorization"));
        GameService service = new GameService();
        var result = service.listGames(request);
        sendResult(ctx, result);
        switch (result) {
            case ListGamesResult r -> ctx.status(200);
            case IncorrectAuthException r -> ctx.status(401);
            default -> ctx.status(500);
        }
    }
}
