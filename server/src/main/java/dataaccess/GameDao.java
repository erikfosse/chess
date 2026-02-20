package dataaccess;

import chess.ChessGame;
import chess.GameRecord;
import dataaccess.interfaces.GameInterface;
import db.GameData;

import java.util.Collection;

public class GameDao implements GameInterface {

    @Override
    public void addGame(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        GameData.addGame(new GameRecord(gameID, whiteUsername, blackUsername, gameName, game));
    }
    @Override
    public GameRecord getGame(Integer gameID) {
        return GameData.getGame(gameID);
    }
    @Override
    public Collection<GameRecord> getAllGames (String username) {
        return GameData.getAllGames(username);
    }
    public Integer getNumGames() {
        return GameData.getNumGames();
    }
}
