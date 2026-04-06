package model;

import chess.ChessGame;

public record GameRecord(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, boolean resigned) {

    public GameRecord(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(gameID, whiteUsername, blackUsername, gameName, game, false);
    }
}
