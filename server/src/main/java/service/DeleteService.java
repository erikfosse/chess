package service;

import dataaccess.sql.SQLAuthDao;
import dataaccess.sql.SQLGameDao;
import dataaccess.sql.SQLUserDao;
import model.exception.DataAccessException;
import model.exception.SQLConnException;
import model.request.GeneralApi;
import model.result.DeleteResult;

import java.sql.SQLException;

public class DeleteService {
    public GeneralApi delete() throws SQLConnException, DataAccessException, SQLException {
        SQLGameDao gameDao = new SQLGameDao();
        SQLAuthDao authDao = new SQLAuthDao();
        SQLUserDao userDao = new SQLUserDao();
        gameDao.deleteData();
        authDao.deleteData();
        userDao.deleteData();
        return new DeleteResult();
    }
}
