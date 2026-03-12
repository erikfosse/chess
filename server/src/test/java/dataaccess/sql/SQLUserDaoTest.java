package dataaccess.sql;

import dataaccess.DatabaseManager;
import model.UserRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDaoTest {

    private static SQLUserDao userDao;

    @BeforeAll
    public static void setup() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        userDao = new SQLUserDao();
    }

    @BeforeEach
    public void init() throws SQLConnException {
        userDao.deleteData();
    }

    @AfterAll
    public static void cleanup() throws SQLConnException {
        userDao.deleteData();
    }

    @Test
    public void deleteDataSuccess() throws SQLConnException {
        userDao.deleteData();
    }

    @Test
    public void addUserSuccess() throws SQLConnException {
        userDao.addUser("erikfosse", "this", "gmail");
    }

    @Test void getUserSuccess() throws SQLConnException {
        userDao.addUser("erikfosse", "this", "gmail");
        UserRecord user = userDao.getUser("erikfosse");
        boolean hashpass = BCrypt.checkpw("this", user.password());
        UserRecord correctUser = new UserRecord("erikfosse", "this", "gmail");
        Assertions.assertEquals(user.username(), correctUser.username());
        Assertions.assertEquals(user.email(), correctUser.email());
        Assertions.assertTrue(hashpass);
    }
}
