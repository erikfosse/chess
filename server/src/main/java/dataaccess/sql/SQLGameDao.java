package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameInterface;
import model.GameRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDao implements GameInterface {

    private Integer numGames;

    public SQLGameDao() throws DataAccessException, SQLConnException, SQLException {
        DatabaseManager.createDatabase();
        this.numGames = 0;
    }

    @Override
    public Integer getNumGames() {
        return numGames;
    }

    @Override
    public void addGame(String gameName, ChessGame game) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
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
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }
    @Override
    public GameRecord getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                "SELECT * FROM gamedata WHERE gameID=?"
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
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }

    @Override
    public Collection<GameRecord> getAllGames (String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            ArrayList<GameRecord> allGames = new ArrayList<>();
            try (var ps = conn.prepareStatement(
                    """
                            SELECT * FROM gamedata;
                            """
            )) {
                try (var rs = ps.executeQuery()) {
                    int gameID = 0;
                    String json = "";
                    String white = "";
                    String black = "";
                    String gameName = "";
                    while (rs.next()) {
                        gameID = rs.getInt("gameID");
                        white = rs.getString("whiteUserName");
                        black = rs.getString("blackUserName");
                        gameName = rs.getString("gameName");
                        json = rs.getString("jsonGame");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        allGames.add(new GameRecord(gameID, white, black, gameName, game));
                    }
                } return allGames;
            }
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }

    @Override
    public void editGame(Integer gameID, String playerColor, String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String color = "";
            if (playerColor.equals("WHITE")) {
                color = "whiteUserName";
            } else {
                color = "blackUserName";
            }
            String sql = String.format("UPDATE gamedata SET %s=? WHERE gameID=?", color);
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
            int userID = findUserID(username);
            try (var ps = conn.prepareStatement(
                    "INSERT INTO usergamerelation (userID, gameID) VALUES (?, ?)"
            )) {
                ps.setInt(1, userID);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }

    @Override
    public void deleteData() throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("TRUNCATE TABLE gamedata")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new SQLConnException();
            }
            try (var ps = conn.prepareStatement("TRUNCATE TABLE usergamerelation")) {
                ps.executeUpdate();
            } catch (SQLException u) {
                throw new SQLConnException();
            }
        } catch (SQLConnException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer findUserID(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "SELECT userID FROM userdata WHERE username=?"
            )) {
                ps.setString(1, username);
                int userID = 0;
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        userID = rs.getInt("userID");
                    }
                    return userID;
                }
            }
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }
}
