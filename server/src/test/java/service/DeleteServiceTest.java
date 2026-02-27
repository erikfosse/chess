package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.request.CreateGameRequest;
import model.request.GeneralApi;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
    public void successDelete() {
        GeneralApi reg = userService.register(new RegisterRequest("erik", "something", "gmail"));
        String auth = ((RegisterResult) reg).authToken();
        gameService.createGame(auth, new CreateGameRequest("GoodGame"));
        deleteService.delete();

        UserDao userdao = new UserDao();
        AuthDao authdao = new AuthDao();
        GameDao gameDao = new GameDao();
        Assertions.assertNull(userdao.getUser("erik"));
        Assertions.assertNull(authdao.getAuth("erik"));
        Assertions.assertEquals(0, gameDao.getNumGames());
    }
}
