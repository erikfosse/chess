package db;

import chess.ChessGame;
import model.GameRecord;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameData {
    private static Map<Integer, GameRecord> games = new HashMap<>();
    private static Map<String, ArrayList<GameRecord>> gamesByUser = new HashMap<>();
    private static Integer numGames = 0;

    public static Integer getNumGames() {
        return numGames;
    }

    public static void addGame(String gameName, ChessGame game) {
        numGames++;
        games.put(numGames, new GameRecord(numGames, null, null, gameName, game));
    }

    public static GameRecord getGame(Integer gameID) {
        return games.get(gameID);
    }

    public static Collection<GameRecord> getAllGames(String username) {
        return gamesByUser.get(username);
    }

    public static void editGame(Integer gameID, String playerColor, String username) {
        GameRecord thisGame = games.get(gameID);
        GameRecord newGame;
        if (playerColor.equals("WHITE")) {
            newGame = new GameRecord(thisGame.gameID(), username, thisGame.blackUsername(),
                    thisGame.gameName(), thisGame.game());
        } else {
            newGame = new GameRecord(thisGame.gameID(), thisGame.whiteUsername(), username,
                    thisGame.gameName(), thisGame.game());
        }
        games.put(gameID, newGame);
        if (!gamesByUser.containsKey(username)) {
            gamesByUser.put(username, new ArrayList<>());
        }
        gamesByUser.get(username).add(newGame);
    }

    public static void deleteData() {
        games = new HashMap<>();
        gamesByUser = new HashMap<>();
        numGames = 0;
    }
}
