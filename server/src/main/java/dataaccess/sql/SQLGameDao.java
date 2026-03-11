package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameInterface;
import db.GameData;
import model.GameRecord;
import model.exception.AlreadyTakenException;
import model.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDao implements GameInterface {

    private Connection conn;

    public SQLGameDao() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        DatabaseManager.initializeTables();
        this.conn = DatabaseManager.getConnection();
    }

    @Override
    public void addGame(String gameName, ChessGame game) {
        try (var preparedStatement = conn.prepareStatement("INSERT INTO "))
    }
    @Override
    public GameRecord getGame(Integer gameID) {

    }
    @Override
    public Collection<GameRecord> getAllGames (String username) {

    }
    @Override
    public Integer getNumGames() {

    }
    @Override
    public void editGame(Integer gameID, String playerColor, String username) throws AlreadyTakenException {

    }
    @Override
    public void deleteData() {

    }
}
