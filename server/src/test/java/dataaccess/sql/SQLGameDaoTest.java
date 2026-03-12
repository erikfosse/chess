package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import model.GameRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDaoTest {

    private static SQLGameDao gameDao;
    private static Connection conn;

    @BeforeAll
    public static void setup() throws SQLConnException, DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        gameDao = new SQLGameDao();
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void init() throws SQLConnException {
        gameDao.deleteData();
    }

    @AfterAll
    public static void cleanup() throws SQLConnException {
        gameDao.deleteData();
    }

    @Test
    public void deleteAllDataSuccess() throws SQLConnException {
        gameDao.deleteData();
        Assertions.assertTrue(isTableEmpty());
    }

    @Test
    public void addGameSuccess() throws SQLConnException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white v. black", game);
        Assertions.assertFalse(isTableEmpty());
    }

    @Test
    public void addGameFail() throws SQLConnException {
        ChessGame game = new ChessGame();
        try {
            gameDao.addGame(null, null);
        } catch (SQLConnException e) {
            Assertions.assertTrue(isTableEmpty());
        }
    }

    @Test
    public void getGameSuccess() throws SQLConnException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white v. black", game);
        int gameID = gameDao.getNumGames();
        GameRecord returnedGame = gameDao.getGame(gameID);
        GameRecord goodGame = new GameRecord(1, null, null,"white v. black", game);
        Assertions.assertEquals(returnedGame, goodGame);
    }

    @Test
    public void getGameFail() throws SQLConnException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white v. black", game);
        int gameID = gameDao.getNumGames();
        GameRecord returnedGame = gameDao.getGame(gameID+1);
        GameRecord goodGame = new GameRecord(1, null, null,"white v. black", game);
        Assertions.assertNotEquals(returnedGame, goodGame);
    }

    @Test
    public void getAllGamesSuccess() throws SQLConnException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white", game);
        gameDao.addGame("black", game);
        gameDao.addGame("white v. black", game);
        gameDao.addGame("white and black", game);
        Collection<GameRecord> games = gameDao.getAllGames("thing");
        Assertions.assertEquals(games.toArray().length, 4);
    }

    @Test
    public void getAllGamesFail() throws SQLConnException {
        try {
            ChessGame game = new ChessGame();
            gameDao.addGame("white", game);
            gameDao.addGame("black", game);
            gameDao.addGame("white v. black", game);
            gameDao.addGame(null, null);
        } catch (Exception e) {
            Collection<GameRecord> games = gameDao.getAllGames("thing");
            Assertions.assertNotEquals(games.toArray().length, 4);
        }

    }

    @Test
    public void editGameSuccess() throws SQLConnException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white", game);
        gameDao.editGame(1, "WHITE", "erikfosse");
        GameRecord thisGame = gameDao.getGame(1);
        Assertions.assertEquals(thisGame.whiteUsername(), "erikfosse");
    }

    @Test
    public void editGameFail() throws SQLConnException {
        ChessGame game = new ChessGame();
        gameDao.addGame("white", game);
        gameDao.editGame(1, "WHITE", "erikfosse");
        GameRecord thisGame = gameDao.getGame(2);
        Assertions.assertNotEquals(thisGame.whiteUsername(), "erikfosse");
    }

    private boolean isTableEmpty() throws SQLConnException {
        try {
            try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM gamedata")) {
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
