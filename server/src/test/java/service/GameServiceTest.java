package service;

import model.AuthRecord;
import model.GameRecord;
import model.UserRecord;
import model.exception.AlreadyTakenException;
import model.exception.BadRequestException;
import model.request.*;
import model.result.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameServiceTest {

    private static UserService userService;
    private static GameService gameService;
    private static String auth;

    @BeforeAll
    public static void init() {
        gameService = new GameService();
        userService = new UserService();
        GeneralApi reg = userService.register(new RegisterRequest("erik", "something", "gmail"));
        auth = ((RegisterResult) reg).authToken();
    }

    @Test
    public void successCreation() {
        var result = gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        Object createGameResult = new CreateGameResult(1);
        Assertions.assertEquals(createGameResult.getClass(), result.getClass());
    }

    @Test
    public void creationBadRequest() {
        var result = gameService.createGame(auth, new CreateGameRequest(null));
        Object badRequestException = new BadRequestException();
        Assertions.assertEquals(badRequestException.getClass(), result.getClass());

        var result_2 = gameService.createGame(null, new CreateGameRequest("GoodGame"));
        Object badRequestException1 = new BadRequestException();
        Assertions.assertEquals(badRequestException1.getClass(), result_2.getClass());
    }

    @Test
    public void successJoinGame() {
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        var result = gameService.joinGame(auth, new JoinGameRequest("WHITE", 1));
        Object joinGameResult = new JoinGameResult();
        Assertions.assertEquals(joinGameResult.getClass(), result.getClass());
    }

    @Test
    public void joinGameBadRequest() {
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        var result = gameService.joinGame(auth, new JoinGameRequest(null, 1));
        Object badRequestException = new BadRequestException();
        Assertions.assertEquals(badRequestException.getClass(), result.getClass());

        var result_1 = gameService.joinGame(auth, new JoinGameRequest("WHITE", null));
        Object badRequestException1 = new BadRequestException();
        Assertions.assertEquals(badRequestException1.getClass(), result_1.getClass());
    }

    @Test
    public void successListGames() {
        gameService.createGame(auth, new CreateGameRequest("Game_1"));
        gameService.joinGame(auth, new JoinGameRequest("WHITE", 1));
        var result = gameService.listGames(new ListGamesRequest(auth));
        Object listGamesResult = new ListGamesResult(new ArrayList<GameRecord>());
        Assertions.assertEquals(listGamesResult.getClass(), result.getClass());
    }

    @Test
    public void listGamesBadRequest() {
        gameService.createGame(auth, new CreateGameRequest("Game_1"));
        gameService.joinGame(auth, new JoinGameRequest("WHITE", 1));
        var result = gameService.listGames(new ListGamesRequest(null));
        Object badRequestException = new BadRequestException();
        Assertions.assertEquals(badRequestException.getClass(), result.getClass());
    }
}
