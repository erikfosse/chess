package dataaccess;

import chess.ChessGame;
import model.GameRecord;
import dataaccess.interfaces.GameInterface;
import db.GameData;

import java.util.Collection;

public class GameDao implements GameInterface {

    @Override
    public void addGame(Integer gameID, String gameName, ChessGame game) {
        GameData.addGame(new GameRecord(gameID, null, null, gameName, game));
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
    public void editGame(Integer gameID, String playerColor, String username) {
        GameData.editGame(gameID, playerColor, username);
    }
    public void deleteData() {
        GameData.deleteData();
    }
}
