package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.request.GeneralApi;
import model.result.CreateGameResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.exception.UnauthorizedException;
import model.request.CreateGameRequest;

public class CreateGameHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var authToken = ctx.header("authorization");
        var request = processRequest(ctx, CreateGameRequest.class);
        GameService service = new GameService();
        try {
            var result = service.createGame(authToken, (CreateGameRequest) request);
            sendResult(ctx, result, CreateGameResult.class);
        } catch (Exception e) {
            sendResult(ctx, e, CreateGameResult.class);
        }
    }
}
