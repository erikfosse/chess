package dataaccess.memory;

import chess.ChessGame;
import model.GameRecord;
import dataaccess.interfaces.GameInterface;
import db.GameData;
import model.exception.AlreadyTakenException;

import java.util.Collection;

public class MemoryGameDao implements GameInterface {

    @Override
    public void addGame(String gameName, ChessGame game) {
        GameData.addGame(gameName, game);
    }
    @Override
    public GameRecord getGame(Integer gameID) {
        return GameData.getGame(gameID);
    }
    @Override
    public Collection<GameRecord> getAllGames () {
        return GameData.getAllGames();
    }
    @Override
    public Integer getNumGames() {
        return GameData.getNumGames();
    }
    @Override
    public void editGame(Integer gameID, String playerColor, String username) throws AlreadyTakenException {
        GameData.editGame(gameID, playerColor, username);
    }
    @Override
    public void deleteData() {
        GameData.deleteData();
    }
}
