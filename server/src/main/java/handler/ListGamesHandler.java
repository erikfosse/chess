package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import service.exception.IncorrectAuthException;
import service.exception.IncorrectPasswordException;
import service.exception.IncorrectUsernameException;
import service.request.ListGamesRequest;
import service.request.LoginRequest;
import service.result.ListGamesResult;
import service.result.LoginResult;
import service.result.LogoutResult;

public class ListGamesHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = new ListGamesRequest(ctx.headerMap().get("authorization"));
        GameService service = new GameService();
        var result = service.listGames(request);
        sendResult(ctx, result);
        switch (result) {
            case LogoutResult r -> ctx.status(200);
            case IncorrectAuthException r -> ctx.status(401);
            default -> ctx.status(500);
        }
    }
}
