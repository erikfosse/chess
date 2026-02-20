package service;

import chess.AuthRecord;
import chess.GameRecord;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import service.exception.GeneralException;
import service.exception.IncorrectAuthException;
import service.request.CreateGameRequest;
import service.request.GeneralApi;
import service.request.JoinGameRequest;
import service.request.ListGamesRequest;
import service.result.ListGamesResult;
import service.result.LogoutResult;
import service.result.SingleGameResult;

import java.util.ArrayList;

public class GameService {
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

    public GeneralApi createGame(CreateGameRequest request) {

    }

    public GeneralApi joinGame(JoinGameRequest request) {

    }

    private Object checkAuthData(String authToken) {
        AuthDao authdao = new AuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        if (authData == null) {
            return new IncorrectAuthException("Error: unauthorized");
        } else {
            return authData;
        }
    }
}
