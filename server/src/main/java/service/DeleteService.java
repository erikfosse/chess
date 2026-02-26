package service;

import chess.ChessGame;
import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthRecord;
import model.exception.GeneralException;
import model.exception.IncorrectAuthException;
import model.request.CreateGameRequest;
import model.request.GeneralApi;
import model.result.CreateGameResult;
import model.result.DeleteResult;

import static service.GameService.checkAuthData;

public class DeleteService {
    public GeneralApi delete(String authToken) {
        var result = checkAuthData(authToken);
        if (result instanceof IncorrectAuthException exception) {
            return exception;
        } else if (result instanceof AuthRecord auth) {
            GameDao gameDao = new GameDao();
            AuthDao authDao = new AuthDao();
            UserDao userDao = new UserDao();
            gameDao.deleteData();
            authDao.deleteData();
            userDao.deleteData();
            return new DeleteResult();
        } else {
            return new GeneralException("Error");
        }
    }
}
