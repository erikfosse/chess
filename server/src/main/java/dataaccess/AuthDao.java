package dataaccess;

import chess.AuthRecord;
import dataaccess.interfaces.AuthInterface;
import db.AuthData;

public class AuthDao implements AuthInterface {

    @Override
    public void addAuth(String username, String authToken) {
        AuthData.addAuth(new AuthRecord(username, authToken));
    }

    @Override
    public AuthRecord getAuth(String username) {
        return AuthData.getAuth(username);
    }
}
