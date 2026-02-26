package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import model.exception.IncorrectAuthException;
import model.request.LogoutRequest;
import model.result.LogoutResult;

public class LogoutHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = new LogoutRequest(ctx.headerMap().get("authorization"));
        UserService service = new UserService();
        var result = service.logout(request);
        sendResult(ctx, result);
        switch (result) {
            case LogoutResult r -> ctx.status(200);
            case IncorrectAuthException r -> ctx.status(401);
            default -> ctx.status(500);
        }
    }
}
