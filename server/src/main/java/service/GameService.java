package service;

import model.AuthRecord;
import chess.ChessGame;
import model.GameRecord;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import model.exception.AlreadyTakenException;
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
        if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
            return new BadRequestException();
        }
        var result = checkAuthData(authToken);
        if (result instanceof UnauthorizedException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            try {
                gameDao.editGame(request.gameID(), request.playerColor(), auth.username());
            } catch (AlreadyTakenException e) {
                return e;
            }
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
            if (gamesList == null) {
                return new ListGamesResult();
            }
            return new ListGamesResult((ArrayList<GameRecord>) gamesList);
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
