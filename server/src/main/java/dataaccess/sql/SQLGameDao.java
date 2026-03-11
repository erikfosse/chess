package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameInterface;
import model.GameRecord;
import model.exception.AlreadyTakenException;
import model.exception.DataAccessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class SQLGameDao implements GameInterface {

    private Integer numGames;
    private Connection conn;
    private SQLUserDao userDao;

    public SQLGameDao() throws DataAccessException, SQLException {
        this.conn = DatabaseManager.getConnection();
        userDao = new SQLUserDao();
        this.numGames = 0;
    }

    @Override
    public void addGame(String gameName, ChessGame game) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT INTO gamedata (gameName, jsonGame) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameName);
            String gameJson = new Gson().toJson(game);
            ps.setString(2, gameJson);
            ps.executeUpdate();

            ResultSet generatorKey = ps.getGeneratedKeys();
            if (generatorKey.next()) {
                numGames = generatorKey.getInt(1);
            }
        }
    }
    @Override
    public GameRecord getGame(Integer gameID) throws SQLException {
        try (var ps = conn.prepareStatement(
            "SELECT gameID, jsonGame FROM gameData WHERE gameID=?"
        )) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                String json = "";
                String white = "";
                String black = "";
                String gameName = "";
                while (rs.next()) {
                    white = rs.getString("whiteUserName");
                    black = rs.getString("blackUserName");
                    gameName = rs.getString("gameName");
                    json = rs.getString("jsonGame");
                }
                ChessGame game = new Gson().fromJson(json, ChessGame.class);
                return new GameRecord(gameID, white, black, gameName, game);
            }
        }
    }

    @Override
    public Collection<GameRecord> getAllGames (String username) {

    }

    @Override
    public Integer getNumGames() {
        return numGames;
    }

    @Override
    public void editGame(Integer gameID, String playerColor, String username) throws SQLException {
        try (var ps = conn.prepareStatement(
                "UPDATE gamedata SET ?=? WHERE gameID=?")) {
            if (playerColor.equals("WHITE")) {
                ps.setString(1, "whiteUserName");
            } else {
                ps.setString(1, "blackUserName");
            }
            ps.setString(2, username);
            ps.setInt(3, gameID);
        }
        try (var ps = conn.prepareStatement(
                "INSERT INTO usergamerelation (userID, gameID) VALUES (?, ?)"
                )) {

        }
    }

    @Override
    public void deleteData() throws SQLException {
        try (var ps = conn.prepareStatement("TRUNCATE TABLE gamedata")) {
            ps.executeUpdate();
        }
    }
}
