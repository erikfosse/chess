package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.request.LoginRequest;
import service.request.RegisterRequest;

public class LoginHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var body_string = ctx.body();

        Gson gson = new Gson();
        var request = gson.fromJson(body_string, LoginRequest.class);
        UserService service = new UserService();
        var response = service.login(request);
    }
}
