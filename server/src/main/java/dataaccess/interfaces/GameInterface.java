package dataaccess.interfaces;

import chess.ChessGame;
import db.GameData;
import model.GameRecord;
import model.exception.AlreadyTakenException;

import java.sql.SQLException;
import java.util.Collection;

public interface GameInterface {
    public void addGame(String gameName, ChessGame game) throws SQLException;
    public GameRecord getGame(Integer gameID) throws SQLException;
    public Collection<GameRecord> getAllGames(String username) throws SQLException;
    public Integer getNumGames();
    public void editGame(Integer gameID, String playerColor, String username) throws AlreadyTakenException, SQLException;
    public void deleteData() throws SQLException;
}
