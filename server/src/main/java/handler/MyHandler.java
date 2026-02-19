package handler;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;
import service.UserService;
import service.request.GeneralApi;
import service.request.LoginRequest;

public class MyHandler {
    public Object processRequest(@NotNull Context ctx, Class<?> RequestClass) throws Exception {
        var body_string = ctx.body();
        Gson gson = new Gson();
        return gson.fromJson(body_string, RequestClass);
    }

    public void sendResult(@NotNull Context ctx, GeneralApi response) {
        Gson gson = new Gson();
        var outputString = gson.toJson(response);
        ctx.json(outputString);
    }
}
