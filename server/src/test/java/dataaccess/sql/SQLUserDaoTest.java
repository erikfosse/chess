package dataaccess.sql;

import dataaccess.DatabaseManager;
import model.UserRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDaoTest {

    private static Connection conn;
    private static SQLUserDao userDao;

    @BeforeAll
    public static void setup() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        userDao = new SQLUserDao();
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void init() throws DataAccessException {
        userDao.deleteData();
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        userDao.deleteData();
    }

    @Test
    public void deleteDataSuccess() throws DataAccessException {
        userDao.deleteData();
        Assertions.assertTrue(isTableEmpty());
    }

    @Test
    public void addUserSuccess() throws DataAccessException {
        userDao.addUser("erikfosse", "this", "gmail");
        Assertions.assertFalse(isTableEmpty());
    }

    @Test
    public void addUserFail() throws DataAccessException {
        userDao.addUser("erikfosse!%^", "this", "gmail");
        Assertions.assertTrue(isTableEmpty());
    }

    @Test void getUserSuccess() throws DataAccessException {
        userDao.addUser("erikfosse", "this", "gmail");
        UserRecord user = userDao.getUser("erikfosse");
        boolean hashpass = BCrypt.checkpw("this", user.password());
        UserRecord correctUser = new UserRecord("erikfosse", "this", "gmail");
        Assertions.assertEquals(user.username(), correctUser.username());
        Assertions.assertEquals(user.email(), correctUser.email());
        Assertions.assertTrue(hashpass);
    }

    @Test void getUserFail() throws DataAccessException {
        userDao.addUser("erikfosse", "this", "gmail");
        UserRecord user = userDao.getUser("erik");
        Assertions.assertNull(user);
    }

    private boolean isTableEmpty() throws SQLConnException {
        try {
            try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM userdata")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }
}
