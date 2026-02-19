package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.exception.IncorrectAuthException;
import service.request.LogoutRequest;
import service.result.LogoutResult;

public class LogoutHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = processRequest(ctx, LogoutRequest.class);
        UserService service = new UserService();
        var result = service.logout((LogoutRequest) request);
        sendResult(ctx, result);
        switch (result) {
            case LogoutResult r -> ctx.status(200);
            case IncorrectAuthException r -> ctx.status(401);
            default -> ctx.status(500);
        }
    }
}
