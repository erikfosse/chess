package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.exception.AlreadyTakenException;
import service.exception.GeneralException;
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
        var outputString = gson.toJson(response);
        ctx.json(outputString);
        switch (response) {
            case RegisterResult r -> ctx.status(200);
            case AlreadyTakenException r -> ctx.status(403);
            case null -> ctx.status(400);
            default -> ctx.status(500);

        }
        if (response instanceof RegisterResult result) {

            ctx.status(200);
        } else if (response instanceof GeneralException error) {
            var output_string = gson.toJson(error);
            ctx.json(output_string);
            ctx.status(403);
        }
    }
}
