package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class RegisterHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var headers = ctx.headerMap();
        RegisterRequest user = new RegisterRequest(headers.get("username"),
                headers.get("pass"),
                headers.get("email"));
        UserService service = new UserService();
        var response = service.register(user);
        if (response instanceof RegisterResult result) {
            Gson gson =  new Gson();
            var output_string = gson.toJson(result);
            ctx.json(output_string);
        } else if (response instanceof AlreadyTakenException error) {
            Gson gson =  new Gson();
            var output_string = gson.toJson(error);
            ctx.json(output_string);
        }
    }
}
