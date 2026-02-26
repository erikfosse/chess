package dataaccess.interfaces;

import chess.ChessGame;
import model.GameRecord;

import java.util.Collection;

public interface GameInterface {
    public void addGame(Integer gameID, String gameName, ChessGame game);
    public GameRecord getGame(Integer gameID);
    public Collection<GameRecord> getAllGames(String username);
}
