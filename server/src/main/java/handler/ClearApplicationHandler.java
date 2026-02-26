package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.exception.IncorrectAuthException;
import model.request.DeleteRequest;
import model.request.LogoutRequest;
import model.result.DeleteResult;
import model.result.LogoutResult;
import org.jetbrains.annotations.NotNull;
import service.DeleteService;
import service.UserService;

import java.util.Objects;

public class ClearApplicationHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var request = new DeleteRequest(ctx.headerMap().get("authorization"));
        DeleteService service = new DeleteService();
        var result = service.delete(request.authToken());
        sendResult(ctx, result);
        if (Objects.requireNonNull(result) instanceof DeleteResult) {
            ctx.status(200);
        } else {
            ctx.status(500);
        }
    }
}
