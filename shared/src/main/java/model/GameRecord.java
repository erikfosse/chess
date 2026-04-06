package model;

import chess.ChessGame;

public record GameRecord(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

}
