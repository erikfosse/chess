package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.exception.GeneralException;
import service.exception.IncorrectPasswordException;
import service.exception.IncorrectUsernameException;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;

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
