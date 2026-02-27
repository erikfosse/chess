package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.request.DeleteRequest;
import model.result.DeleteResult;
import org.jetbrains.annotations.NotNull;
import service.DeleteService;

import java.util.Objects;

public class ClearApplicationHandler extends MyHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        DeleteService service = new DeleteService();
        var result = service.delete();
        sendResult(ctx, result, DeleteResult.class);
    }
}
