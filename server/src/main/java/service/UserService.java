package service;

import chess.UserRecord;
import dataaccess.AuthDao;
import dataaccess.UserDao;
import service.exception.AlreadyTakenException;
import service.request.GeneralApi;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.LogoutResult;
import service.result.RegisterResult;

import java.util.UUID;

public class UserService {

    public GeneralApi register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        UserDao userdao = new UserDao();
        AuthDao authdao = new AuthDao();
        UserRecord response = userdao.getUser(username);
        if (response != null) {
            return new AlreadyTakenException("Error: already taken");
        } else {
            String authToken = generateToken();
            userdao.addUser(username, password, email);
            authdao.addAuth(username, authToken);
            return new RegisterResult(username, authToken);
        }
    }
    public LoginResult login(LoginRequest loginRequest) {

    }
    public LogoutRequest logout(LogoutResult logoutRequest) {

    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
