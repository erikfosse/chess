package dataaccess.interfaces;

import model.AuthRecord;

public interface AuthInterface {
    public void addAuth(String username, String authToken);
    public AuthRecord getAuth(String username);
    public void delAuth(String authToken);
}
