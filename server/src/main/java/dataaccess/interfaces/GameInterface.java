package dataaccess.interfaces;

import chess.ChessGame;
import db.GameData;
import model.GameRecord;
import model.exception.AlreadyTakenException;
import model.exception.BadRequestException;
import model.exception.SQLConnException;

import java.sql.SQLException;
import java.util.Collection;

public interface GameInterface {
    public void addGame(String gameName, ChessGame game) throws SQLException, SQLConnException;
    public GameRecord getGame(Integer gameID) throws SQLException, SQLConnException;
    public Collection<GameRecord> getAllGames(String username) throws SQLException, SQLConnException;
    public Integer getNumGames();
    public void editGame(Integer gameID, String playerColor, String username) throws AlreadyTakenException, SQLException, BadRequestException, SQLConnException;
    public void deleteData() throws SQLException, SQLConnException;
}
