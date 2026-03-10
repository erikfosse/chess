package service;

import dataaccess.memory.MemoryAuthDao;
import dataaccess.memory.MemoryGameDao;
import dataaccess.memory.MemoryUserDao;
import model.request.GeneralApi;
import model.result.DeleteResult;

public class DeleteService {
    public GeneralApi delete() {
        MemoryGameDao gameDao = new MemoryGameDao();
        MemoryAuthDao authDao = new MemoryAuthDao();
        MemoryUserDao userDao = new MemoryUserDao();
        gameDao.deleteData();
        authDao.deleteData();
        userDao.deleteData();
        return new DeleteResult();
    }
}
