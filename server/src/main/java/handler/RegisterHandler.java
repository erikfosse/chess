package handler;

import com.google.gson.Gson;
import db.AllUserData;
import chess.UserRecord;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class RegisterHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws  Exception {
        ctx.result("Hello BYU!");
        var headers = ctx.headerMap();
        UserRecord user = new UserRecord(headers.get("username"),
                headers.get("pass"),
                headers.get("email"));

        users.add(user);
        listNames(ctx);
    }

    private void listNames(Context ctx) {
        String jsonNames = new Gson().toJson(Map.of("users", users));
        ctx.json(jsonNames);
    }
}
