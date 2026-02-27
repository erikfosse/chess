package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.exception.AlreadyTakenException;
import model.exception.ErrorResult;
import org.jetbrains.annotations.NotNull;
import model.exception.BadRequestException;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import service.UserService;

public class RegisterHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = processRequest(ctx, RegisterRequest.class);
        UserService service = new UserService();
        var result = service.register((RegisterRequest) request);
        sendResult(ctx, result, RegisterResult.class);
    }
}
