package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.exception.*;
import model.request.GeneralApi;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import model.request.LoginRequest;
import model.result.LoginResult;

public class LoginHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = processRequest(ctx, LoginRequest.class);
        UserService service = new UserService();
        try {
            var result = service.login((LoginRequest) request);
            sendResult(ctx, result, LoginResult.class);
        } catch (Exception e) {
            var exe = (GeneralApi) e;
            sendResult(ctx, exe, LoginResult.class);
        }
    }
}
