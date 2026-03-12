package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.request.GeneralApi;
import model.result.RegisterResult;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import model.exception.UnauthorizedException;
import model.request.LogoutRequest;
import model.result.LogoutResult;

public class LogoutHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = new LogoutRequest(ctx.header("authorization"));
        UserService service = new UserService();
        try {
            var result = service.logout(request);
            sendResult(ctx, result, LogoutResult.class);
        } catch (Exception e) {
            sendResult(ctx, e, LogoutResult.class);
        }
    }
}
