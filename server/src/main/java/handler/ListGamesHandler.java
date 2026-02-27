package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.result.ListGamesResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.exception.UnauthorizedException;
import model.request.ListGamesRequest;

public class ListGamesHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = new ListGamesRequest(ctx.header("authorization"));
        GameService service = new GameService();
        var result = service.listGames(request);
        sendResult(ctx, result, ListGamesResult.class);
    }
}
