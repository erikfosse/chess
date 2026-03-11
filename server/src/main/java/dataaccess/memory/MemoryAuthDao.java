package dataaccess.memory;

import model.AuthRecord;
import dataaccess.interfaces.AuthInterface;
import db.AuthData;

public class MemoryAuthDao implements AuthInterface {

    @Override
    public void addAuth(String username, String authToken) {
        AuthData.addAuth(new AuthRecord(username, authToken));
    }

    @Override
    public AuthRecord getAuth(String username) {
        return AuthData.getAuth(username);
    }

    @Override
    public void delAuth(String authToken) {
        AuthData.delAuth(authToken);
    }

    @Override
    public void deleteData() {
        AuthData.deleteData();
    }
}
