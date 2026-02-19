package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.exception.AlreadyTakenException;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import service.UserService;

public class RegisterHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = processRequest(ctx, RegisterRequest.class);
        UserService service = new UserService();
        var result = service.register((RegisterRequest) request);
        sendResult(ctx, result);
        switch (result) {
            case RegisterResult r -> ctx.status(200);
            case AlreadyTakenException r -> ctx.status(403);
            case null -> ctx.status(400);
            default -> ctx.status(500);
        }
    }
}
