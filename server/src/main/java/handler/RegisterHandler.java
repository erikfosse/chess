package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.exception.AlreadyTakenException;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import service.UserService;

public class RegisterHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var body_string = ctx.body();

        Gson gson = new Gson();
        var request = gson.fromJson(body_string, RegisterRequest.class);

        UserService service = new UserService();
        var response = service.register(request);
        if (response instanceof RegisterResult result) {
            var output_string = gson.toJson(result);
            ctx.json(output_string);
            ctx.status(200);
        } else if (response instanceof AlreadyTakenException error) {
            var output_string = gson.toJson(error);
            ctx.json(output_string);
            ctx.status(403);
        }
    }
}
