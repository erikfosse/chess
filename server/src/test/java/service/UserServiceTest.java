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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    private static UserService userService;
    private static DeleteService deleteService;

    @BeforeEach
    public void init() {
        userService = new UserService();
    }

    @Test
    public void registerSuccess() {
        var result = userService.register(new RegisterRequest("erikjfjf", "something", "gmail"));
        Object registerResult = new RegisterResult("erik", "");
        Assertions.assertEquals(registerResult.getClass(), result.getClass());
    }

    @Test
    public void registerBadRequest() {
        var result = userService.register(new RegisterRequest("erik", null, "gmail"));
        Object badRequestException = new BadRequestException();
        Assertions.assertEquals(badRequestException.getClass(), result.getClass());
    }

    @Test
    public void registerAlreadyTaken() {
        userService.register(new RegisterRequest("erik", "thing", "gmail"));
        var result = userService.register(new RegisterRequest("erik", "something", "gmail"));
        Object alreadyTakenException = new AlreadyTakenException();
        Assertions.assertEquals(alreadyTakenException.getClass(), result.getClass());
    }

    @Test
    public void loginSuccess() {
        userService.register(new RegisterRequest("erik", "something", "gmail"));
        var result = userService.login(new LoginRequest("erik", "something"));
        Object loginResult = new LoginResult("erik", "");
        Assertions.assertEquals(loginResult.getClass(), result.getClass());
    }

    @Test
    public void loginBadRequest() {
        userService.register(new RegisterRequest("erik", "something", "gmail"));
        var result = userService.login(new LoginRequest(null, "something"));
        Object badRequestException = new BadRequestException();
        Assertions.assertEquals(badRequestException.getClass(), result.getClass());
    }

    @Test
    public void logoutSuccess() {
        GeneralApi res = userService.register(new RegisterRequest("erikf", "something", "gmail"));
        var authToken = ((RegisterResult) res).authToken();
        GeneralApi result = userService.logout(new LogoutRequest(authToken));
        Object logoutResult = new LogoutResult();
        Assertions.assertEquals(logoutResult.getClass(), result.getClass());
    }

    @Test
    public void logoutBadRequest() {
        userService.register(new RegisterRequest("erik", "something", "gmail"));
        GeneralApi result = userService.logout(new LogoutRequest(null));
        Object unauthorizedException = new UnauthorizedException();
        Assertions.assertEquals(unauthorizedException.getClass(), result.getClass());
    }
}
