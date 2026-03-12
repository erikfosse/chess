package dataaccess.sql;

import dataaccess.DatabaseManager;
import model.AuthRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDaoTest {

    private static SQLAuthDao authDao;
    private static Connection conn;

    @BeforeAll
    public static void setup() throws SQLConnException, DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        authDao = new SQLAuthDao();
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void init() throws DataAccessException {
        authDao.deleteData();
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        authDao.deleteData();
    }

    @Test
    public void authAddSuccess() throws SQLConnException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        Assertions.assertFalse(isTableEmpty());
    }

    @Test
    public void authAddFail() throws SQLConnException {
        try {
            authDao.addAuth(null, "verylongstringauth");
        } catch (SQLConnException e) {
            Assertions.assertTrue(isTableEmpty());
        }
    }

    @Test
    public void getauthSuccess() throws DataAccessException {
        String token = "verylongstringauth";
        authDao.addAuth("erikfosse", token);
        AuthRecord auth = authDao.getAuth(token);
        AuthRecord correctAuth = new AuthRecord("erikfosse", "verylongstringauth");
        Assertions.assertEquals(auth, correctAuth);
    }

    @Test
    public void getauthFail() throws DataAccessException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        AuthRecord auth = authDao.getAuth("erikfe");
        Assertions.assertNull(auth);
    }

    @Test
    public void deleteAllDataSuccess() throws DataAccessException {
        authDao.deleteData();
        Assertions.assertTrue(isTableEmpty());
    }

    @Test
    public void deleteOneAuthSuccess() throws DataAccessException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        authDao.addAuth("erik", "someotherstringauth");
        authDao.delAuth("someotherstringauth");
        authDao.delAuth("verylongstringauth");
        Assertions.assertTrue(isTableEmpty());
    }

    @Test
    public void deleteOneAuthFail() throws DataAccessException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        authDao.addAuth("erik", "someotherstringauth");
        authDao.delAuth("someothersngauth");
        authDao.delAuth("verylongstrinth");
        Assertions.assertFalse(isTableEmpty());
    }

    private boolean isTableEmpty() throws SQLConnException {
        try {
            try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM authdata")) {
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
