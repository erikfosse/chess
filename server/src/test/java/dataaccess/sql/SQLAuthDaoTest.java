package dataaccess.sql;

import dataaccess.DatabaseManager;
import model.AuthRecord;
import model.exception.DataAccessException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDaoTest {

    private static SQLAuthDao authDao;
    private static Connection conn;

    @BeforeAll
    public static void setup() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        authDao = new SQLAuthDao();
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void init() throws SQLException {
        authDao.deleteData();
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        authDao.deleteData();
    }

    @Test
    public void authAddSuccess() throws SQLException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        Assertions.assertFalse(isTableEmpty());
    }

    @Test
    public void getauthSuccess() throws SQLException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        AuthRecord auth = authDao.getAuth("erikfosse");
        AuthRecord correctAuth = new AuthRecord("erikfosse", "verylongstringauth");
        Assertions.assertEquals(auth, correctAuth);
    }

    @Test
    public void deleteAllDataSuccess() throws SQLException {
        authDao.deleteData();
        Assertions.assertTrue(isTableEmpty());
    }

    @Test
    public void deleteOneAuthSuccess() throws SQLException {
        authDao.addAuth("erikfosse", "verylongstringauth");
        authDao.addAuth("erik", "someotherstringauth");
        authDao.delAuth("someotherstringauth");
        authDao.delAuth("verylongstringauth");
        Assertions.assertTrue(isTableEmpty());
    }

    private boolean isTableEmpty() throws SQLException {
        try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM authdata")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            } else {
                return false;
            }
        }
    }
}
