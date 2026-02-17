package dataaccess.interfaces;

import chess.ChessGame;
import chess.GameRecord;

import java.util.Collection;

public interface GameInterface {
    public void addGame(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
    public GameRecord getGame(Integer gameID);
    public Collection<GameRecord> getAllGames(String username);
}
