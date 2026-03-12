package dataaccess.interfaces;

import chess.ChessGame;
import db.GameData;
import model.GameRecord;
import model.exception.AlreadyTakenException;
import model.exception.BadRequestException;
import model.exception.DataAccessException;
import model.exception.SQLConnException;

import java.sql.SQLException;
import java.util.Collection;

public interface GameInterface {
    public void addGame(String gameName, ChessGame game) throws SQLException, DataAccessException;
    public GameRecord getGame(Integer gameID) throws SQLException, DataAccessException;
    public Collection<GameRecord> getAllGames(String username) throws SQLException, DataAccessException;
    public Integer getNumGames();
    public void editGame(Integer gameID, String playerColor, String username)
            throws DataAccessException, SQLException;
    public void deleteData() throws SQLException, DataAccessException;
}
