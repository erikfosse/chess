package service;

import model.AuthRecord;
import chess.ChessGame;
import model.GameRecord;
import dataaccess.sql.SQLAuthDao;
import dataaccess.sql.SQLGameDao;
import model.exception.AlreadyTakenException;
import model.exception.BadRequestException;
import model.exception.DataAccessException;
import model.exception.UnauthorizedException;
import model.request.CreateGameRequest;
import model.request.GeneralApi;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class GameService {
    public GeneralApi createGame(String authToken, CreateGameRequest request) throws DataAccessException, SQLException {
        if (authToken == null || request.gameName() == null) {
            throw new BadRequestException();
        }
        var result = checkAuthData(authToken);
        if (result instanceof UnauthorizedException exception) {
            throw exception;
        } else if (result instanceof AuthRecord auth) {
            SQLGameDao gameDao = new SQLGameDao();
            ChessGame chessGame = new ChessGame();
            gameDao.addGame(request.gameName(), chessGame);
            int gameID = gameDao.getNumGames();
            return new CreateGameResult(gameID);
        }
        return null;
    }

    public GeneralApi joinGame(String authToken, JoinGameRequest request) throws DataAccessException, SQLException {
        if (authToken == null || request.gameID() == null || request.playerColor() == null) {
            throw new BadRequestException();
        }
        if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
            throw new BadRequestException();
        }
        var result = checkAuthData(authToken);
        if (result instanceof UnauthorizedException exception) {
            throw exception;
        } else if (result instanceof AuthRecord auth) {
            SQLGameDao gameDao = new SQLGameDao();

            GameRecord preGame = gameDao.getGame(request.gameID());
            String color = request.playerColor();
            if ((color.equals("WHITE") && preGame.whiteUsername() != null) ||
                    (color.equals("BLACK") && preGame.blackUsername() != null)) {
                throw new AlreadyTakenException();
            }

            gameDao.editGame(request.gameID(), request.playerColor(), auth.username());
            return new JoinGameResult();
        } else {
            return null;
        }
    }

    public GeneralApi listGames(ListGamesRequest request) throws DataAccessException, SQLException {
        if (request.authToken() == null) {
            throw new BadRequestException();
        }
        var result = checkAuthData(request.authToken());
        if (result instanceof UnauthorizedException exception) {
            throw exception;
        } else if (result instanceof AuthRecord auth) {
            SQLGameDao gameDao = new SQLGameDao();
            var username = auth.username();

            var gamesList = gameDao.getAllGames();
            if (gamesList == null) {
                return new ListGamesResult();
            }
            return new ListGamesResult((ArrayList<GameRecord>) gamesList);
        } else {
            return null;
        }
    }

    private static Object checkAuthData(String authToken) throws DataAccessException, SQLException {
        SQLAuthDao authdao = new SQLAuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        return Objects.requireNonNullElseGet(authData, UnauthorizedException::new);
    }
}
