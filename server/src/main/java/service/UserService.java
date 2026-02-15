package service;

import chess.UserRecord;
import dataaccess.UserDao;

import java.util.UUID;

public class UserService {

    public GeneralApi register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        UserRecord response = UserDao.getUser(username);
        if (response != null) {
            return new AlreadyTakenException("user already taken");
        } else {
            String authToken = generateToken();
            UserDao.addUser(username, password, email);
            UserDao.addAuth(username, authToken);
            return new RegisterResult(username, authToken);
        }
    }
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
