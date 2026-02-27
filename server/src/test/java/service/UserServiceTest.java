package service;

import model.exception.AlreadyTakenException;
import model.exception.BadRequestException;
import model.exception.UnauthorizedException;
import model.request.GeneralApi;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    private static UserService userService;

    @BeforeAll
    public static void init() {
        userService = new UserService();
    }

    @Test
    public void registerSuccess() {
        var result = userService.register(new RegisterRequest("erik", "something", "gmail"));
        Object RegisterResult = new RegisterResult("erik", "");
        Assertions.assertEquals(RegisterResult.getClass(), result.getClass());
    }

    @Test
    public void registerBadRequest() {
        var result = userService.register(new RegisterRequest("erik", null, "gmail"));
        Object RegisterResult = new BadRequestException();
        Assertions.assertEquals(RegisterResult.getClass(), result.getClass());
    }

    @Test
    public void registerAlreadyTaken() {
        userService.register(new RegisterRequest("erik", "thing", "gmail"));
        var result = userService.register(new RegisterRequest("erik", "something", "gmail"));
        Object RegisterResult = new AlreadyTakenException();
        Assertions.assertEquals(RegisterResult.getClass(), result.getClass());
    }

    @Test
    public void loginSuccess() {
        userService.register(new RegisterRequest("erik", "something", "gmail"));
        var result = userService.login(new LoginRequest("erik", "something"));
        Object LoginResult = new LoginResult("erik", "");
        Assertions.assertEquals(LoginResult.getClass(), result.getClass());
    }

    @Test
    public void loginBadRequest() {
        userService.register(new RegisterRequest("erik", "something", "gmail"));
        var result = userService.login(new LoginRequest(null, "something"));
        Object LoginResult = new BadRequestException();
        Assertions.assertEquals(LoginResult.getClass(), result.getClass());
    }

    @Test
    public void logoutSuccess() {
        GeneralApi res = userService.register(new RegisterRequest("erik", "something", "gmail"));
        var authToken = ((RegisterResult) res).authToken();
        GeneralApi result = userService.logout(new LogoutRequest(authToken));
        Object LogoutResult = new LogoutResult();
        Assertions.assertEquals(LogoutResult.getClass(), result.getClass());
    }

    @Test
    public void logoutBadRequest() {
        userService.register(new RegisterRequest("erik", "something", "gmail"));
        GeneralApi result = userService.logout(new LogoutRequest(null));
        Object LogoutResult = new UnauthorizedException();
        Assertions.assertEquals(LogoutResult.getClass(), result.getClass());
    }
}
