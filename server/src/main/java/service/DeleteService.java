package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthRecord;
import model.exception.UnauthorizedException;
import model.request.GeneralApi;
import model.result.DeleteResult;

public class DeleteService {
    public GeneralApi delete() {
        GameDao gameDao = new GameDao();
        AuthDao authDao = new AuthDao();
        UserDao userDao = new UserDao();
        gameDao.deleteData();
        authDao.deleteData();
        userDao.deleteData();
        return new DeleteResult();
    }
}
