package dataaccess.interfaces;

import chess.ChessGame;
import db.GameData;
import model.GameRecord;
import model.exception.AlreadyTakenException;

import java.util.Collection;

public interface GameInterface {
    public void addGame(String gameName, ChessGame game);
    public GameRecord getGame(Integer gameID);
    public Collection<GameRecord> getAllGames(String username);
    public Integer getNumGames();
    public void editGame(Integer gameID, String playerColor, String username) throws AlreadyTakenException;
    public void deleteData();
}
