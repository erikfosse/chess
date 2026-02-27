package service;

import model.AuthRecord;
import chess.ChessGame;
import model.GameRecord;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import model.exception.BadRequestException;
import model.exception.UnauthorizedException;
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
        if (authToken == null || request.gameName() == null) {
            return new BadRequestException();
        }
        var result = checkAuthData(authToken);
        if (result instanceof UnauthorizedException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            ChessGame chessGame = new ChessGame();
            gameDao.addGame(request.gameName(), chessGame);
            int gameID = gameDao.getNumGames();
            return new CreateGameResult(gameID);
        }
        return null;
    }

    public GeneralApi joinGame(String authToken, JoinGameRequest request) {
        if (authToken == null || request.gameID() == null || request.playerColor() == null) {
            return new BadRequestException();
        }
        var result = checkAuthData(authToken);
        if (result instanceof UnauthorizedException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            gameDao.editGame(request.gameID(), request.playerColor(), auth.username());
            return new JoinGameResult();
        } else {
            return null;
        }
    }

    public GeneralApi listGames(ListGamesRequest request) {
        if (request.authToken() == null) {
            return new BadRequestException();
        }
        var result = checkAuthData(request.authToken());
        if (result instanceof UnauthorizedException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            var username = auth.username();
            var gamesList = gameDao.getAllGames(username);
            ArrayList<SingleGameResult> gamesAll = new ArrayList<>();
            if (gamesList == null) {
                return new ListGamesResult();
            }
            for (GameRecord game : gamesList) {
                var temp = new SingleGameResult(game.gameID(),
                        game.whiteUsername(), game.blackUsername());
                gamesAll.add(temp);
            }
            return new ListGamesResult(gamesAll);
        } else {
            return null;
        }
    }

    public static Object checkAuthData(String authToken) {
        AuthDao authdao = new AuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        return Objects.requireNonNullElseGet(authData, UnauthorizedException::new);
    }
}
