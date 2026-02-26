package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import model.exception.IncorrectPasswordException;
import model.exception.IncorrectUsernameException;
import model.request.LoginRequest;
import model.result.LoginResult;

public class LoginHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = processRequest(ctx, LoginRequest.class);
        UserService service = new UserService();
        var result = service.login((LoginRequest) request);
        sendResult(ctx, result);
        switch (result) {
            case LoginResult r -> ctx.status(200);
            case IncorrectPasswordException r -> ctx.status(400);
            case IncorrectUsernameException r -> ctx.status(401);
            default -> ctx.status(500);
        }
    }
}
