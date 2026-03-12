package handler;

import com.google.gson.Gson;
import model.exception.*;
import model.result.LoginResult;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;
import model.request.GeneralApi;

public class MyHandler {
    public Object processRequest(@NotNull Context ctx, Class<?> requestClass) throws BadRequestException {
        var bodyString = ctx.body();
        Gson gson = new Gson();
        return gson.fromJson(bodyString, requestClass);
    }

    public void sendResult(@NotNull Context ctx, GeneralApi result, Class<?> resultClass) {

        switch (result) {
            case GeneralApi res when resultClass.isInstance(res) -> ctx.status(200);
            case BadRequestException r -> ctx.status(400);
            case UnauthorizedException r -> ctx.status(401);
            case AlreadyTakenException r -> ctx.status(403);
            case SQLConnException r -> ctx.status(500);
            default -> ctx.status(500);
        }

        if (result instanceof Exception e) {
            ErrorResult errorResult = new ErrorResult(e.getMessage());
            convertToJson(ctx, errorResult);
        } else {
            convertToJson(ctx, result);
        }
    }

    private void convertToJson(@NotNull Context ctx, GeneralApi response) {
        Gson gson = new Gson();
        var outputString = gson.toJson(response);
        ctx.json(outputString);
        System.out.println(outputString);
    }
}
