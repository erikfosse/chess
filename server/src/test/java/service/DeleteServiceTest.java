package service;

import dataaccess.memory.MemoryAuthDao;
import dataaccess.memory.MemoryGameDao;
import dataaccess.memory.MemoryUserDao;
import model.request.CreateGameRequest;
import model.request.GeneralApi;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class DeleteServiceTest {

    private static DeleteService deleteService;
    private static UserService userService;
    private static GameService gameService;

    @BeforeAll
    public static void init() {
        deleteService = new DeleteService();
        gameService = new GameService();
        userService = new UserService();
    }

    @Test
    @Order(1)
    public void successDelete() {
        GeneralApi reg = userService.register(new RegisterRequest("erikf", "somethingf", "gmail"));
        String auth = ((RegisterResult) reg).authToken();
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        deleteService.delete();

        MemoryUserDao userdao = new MemoryUserDao();
        MemoryAuthDao authdao = new MemoryAuthDao();
        MemoryGameDao gameDao = new MemoryGameDao();
        Assertions.assertNull(userdao.getUser("erik"));
        Assertions.assertNull(authdao.getAuth("erik"));
        Assertions.assertEquals(0, gameDao.getNumGames());
    }

    @Test
    @Order(2)
    public void failDelete() {
        GeneralApi reg = userService.register(new RegisterRequest("erik", "something", "gmail"));
        userService.login(new LoginRequest("erik", "something"));
        String auth = ((RegisterResult) reg).authToken();
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));

        MemoryUserDao userdao = new MemoryUserDao();
        MemoryAuthDao authdao = new MemoryAuthDao();
        MemoryGameDao gameDao = new MemoryGameDao();
        Assertions.assertNotNull(userdao.getUser("erik"));
        Assertions.assertNotEquals(0, gameDao.getNumGames());
    }
}
