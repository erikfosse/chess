package service;

import dataaccess.DatabaseManager;
import dataaccess.sql.SQLAuthDao;
import model.AuthRecord;
import model.GameRecord;
import model.request.GeneralApi;
import model.UserRecord;
import model.exception.AlreadyTakenException;
import model.exception.BadRequestException;
import model.exception.DataAccessException;
import model.request.*;
import model.result.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class GameServiceTest {

    private static UserService userService;
    private static GameService gameService;
    private static DeleteService deleteService;
    private static String auth;

    @BeforeAll
    public static void init() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        deleteService = new DeleteService();
        gameService = new GameService();
        userService = new UserService();
        deleteService.delete();
        GeneralApi reg = userService.register(new RegisterRequest("erik", "something", "gmail"));
        auth = ((RegisterResult) reg).authToken();
    }

    @BeforeEach
    public void setup() throws SQLException, DataAccessException {
        deleteService.delete();
        userService.register(new RegisterRequest("erik", "some", "gmail"));
        GeneralApi authHolder = userService.login(new LoginRequest("erik", "some"));
        auth = ((LoginResult) authHolder).authToken();
    }

    @AfterAll
    public static void cleanup() throws SQLException, DataAccessException {
        deleteService.delete();
    }

    @Test
    public void successCreation() throws SQLException, DataAccessException {
        var result = gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        Object createGameResult = new CreateGameResult(1);
        Assertions.assertEquals(createGameResult.getClass(), result.getClass());
    }

    @Test
    public void creationBadRequest() throws SQLException, DataAccessException {
        GeneralApi result = null;
        try {
            result = gameService.createGame(auth, new CreateGameRequest(null));
        } catch (Exception e) {
            Assertions.assertNull(result);
        }

        GeneralApi result2 = null;
        try {
            result2 = gameService.createGame(null, new CreateGameRequest("GoodGame"));
        } catch (Exception e) {
            Assertions.assertNull(result2);
        }

    }

    @Test
    public void successJoinGame() throws SQLException, DataAccessException {
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        var result = gameService.joinGame(auth, new JoinGameRequest("WHITE", 1));
        Object joinGameResult = new JoinGameResult();
        Assertions.assertEquals(joinGameResult.getClass(), result.getClass());
    }

    @Test
    public void joinGameBadRequest() throws SQLException, DataAccessException {
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        GeneralApi result = null;
        try {
            result = gameService.joinGame(auth, new JoinGameRequest(null, 1));
        } catch (Exception e) {
            Assertions.assertNull(result);
        }

        GeneralApi result1 = null;
        try {
            result1 = gameService.joinGame(auth, new JoinGameRequest("WHITE", null));
        } catch (Exception e) {
            Assertions.assertNull(result1);
        }
    }

    @Test
    public void successListGames() throws SQLException, DataAccessException {
        gameService.createGame(auth, new CreateGameRequest("Game_1"));
        gameService.joinGame(auth, new JoinGameRequest("WHITE", 1));
        var result = gameService.listGames(new ListGamesRequest(auth));
        Object listGamesResult = new ListGamesResult(new ArrayList<GameRecord>());
        Assertions.assertEquals(listGamesResult.getClass(), result.getClass());
    }

    @Test
    public void listGamesBadRequest() throws SQLException, DataAccessException {
        gameService.createGame(auth, new CreateGameRequest("Game_1"));
        gameService.joinGame(auth, new JoinGameRequest("WHITE", 1));
        GeneralApi result = null;
        try {
            result = gameService.listGames(new ListGamesRequest(null));
        } catch (Exception e) {
            Assertions.assertNull(result);
        }
    }
}
