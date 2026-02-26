package service;

import model.AuthRecord;
import chess.ChessGame;
import model.GameRecord;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import model.exception.GeneralException;
import model.exception.IncorrectAuthException;
import model.request.CreateGameRequest;
import model.request.GeneralApi;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;
import model.result.SingleGameResult;

import java.util.ArrayList;
import java.util.Objects;

public class GameService {
    public GeneralApi createGame(String authToken, CreateGameRequest request) {
        var result = checkAuthData(authToken);
        if (result instanceof IncorrectAuthException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            ChessGame chessGame = new ChessGame();
            int gameID = gameDao.getNumGames();
            gameDao.addGame(gameID, request.gameName(), chessGame);
            return new CreateGameResult(gameID);
        } else {
            return new GeneralException("Error");
        }
    }

    public GeneralApi joinGame(String authToken, JoinGameRequest request) {
        var result = checkAuthData(authToken);
        if (result instanceof IncorrectAuthException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            gameDao.editGame(request.gameID(), request.playerColor(), auth.username());
            return new JoinGameResult();
        } else {
            return new GeneralException("Error");
        }
    }

    public GeneralApi listGames(ListGamesRequest request) {
        var result = checkAuthData(request.authToken());
        if (result instanceof IncorrectAuthException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            var username = auth.username();
            var gamesList = gameDao.getAllGames(username);
            ArrayList<SingleGameResult> gamesAll = new ArrayList<>();
            for (GameRecord game : gamesList) {
                var temp = new SingleGameResult(game.gameID(),
                        game.whiteUsername(), game.blackUsername());
                gamesAll.add(temp);
            }
            return new ListGamesResult(gamesAll);
        } else {
            return new GeneralException("Error");
        }
    }

    public static Object checkAuthData(String authToken) {
        AuthDao authdao = new AuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        return Objects.requireNonNullElseGet(authData, () -> new IncorrectAuthException("Error: unauthorized"));
    }
}
