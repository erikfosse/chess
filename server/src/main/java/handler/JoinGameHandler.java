package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.result.JoinGameResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.exception.UnauthorizedException;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;

public class JoinGameHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var authToken = ctx.header("authorization");
        var request = processRequest(ctx, JoinGameRequest.class);
        GameService service = new GameService();
        var result = service.joinGame(authToken, (JoinGameRequest) request);
        sendResult(ctx, result, JoinGameResult.class);
    }
}
