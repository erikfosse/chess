package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import model.GameRecord;
import model.exception.DataAccessException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameDaoTest {

    private static SQLGameDao gameDao;
    private static Connection conn;

    @BeforeAll
    public static void setup() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        gameDao = new SQLGameDao();
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void init() throws SQLException {
        gameDao.deleteData();
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        gameDao.deleteData();
    }

    @Test
    public void addGameSuccess() throws SQLException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white v. black", game);
        Assertions.assertFalse(isTableEmpty());
    }

    @Test
    public void getGameSuccess() throws SQLException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white v. black", game);
        int gameID = gameDao.getNumGames();
        GameRecord returnedGame = gameDao.getGame(gameID);
        GameRecord goodGame = new GameRecord(1, null, null,"white v. black", game);
        Assertions.assertEquals(returnedGame, goodGame);
    }

    private boolean isTableEmpty() throws SQLException {
        try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM gamedata")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            } else {
                return false;
            }
        }
    }
}
