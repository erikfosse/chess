package db;

import chess.GameRecord;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameData {
    private static final Map<Integer, GameRecord> games = new HashMap<>();
    private static final Map<String, ArrayList<GameRecord>> gamesByUser = new HashMap<>();
    public static void addGame(GameRecord game) {
        games.put(game.gameID(), game);
        if (game.blackUsername() != null) {
            gamesByUser.computeIfPresent(game.blackUsername(), (key, list) -> {
                list.add(game);
                return list;
            });
            gamesByUser.computeIfAbsent(game.blackUsername(), T -> new ArrayList<GameRecord>()).add(game);
        }
        if (game.whiteUsername() != null) {
            gamesByUser.computeIfPresent(game.whiteUsername(), (key, list) -> {
                list.add(game);
                return list;
            });
            gamesByUser.computeIfAbsent(game.whiteUsername(), T -> new ArrayList<GameRecord>()).add(game);
        }
    }
    public static GameRecord getGame(Integer gameID) {
        return games.get(gameID);
    }
    public static Collection<GameRecord> getAllGames(String username) {
        return gamesByUser.get(username);
    }
}
