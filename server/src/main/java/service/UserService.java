package service;

import chess.AuthRecord;
import chess.UserRecord;
import dataaccess.AuthDao;
import dataaccess.UserDao;
import service.exception.AlreadyTakenException;
import service.exception.GeneralException;
import service.exception.IncorrectPasswordException;
import service.exception.IncorrectUsernameException;
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
        UserRecord user = userdao.getUser(username);
        if (user != null) {
            return new AlreadyTakenException("Error: already taken");
        } else {
            String authToken = generateToken();
            userdao.addUser(username, password, email);
            authdao.addAuth(username, authToken);
            return new RegisterResult(username, authToken);
        }
    }
    public GeneralApi login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        UserDao userdao = new UserDao();
        AuthDao authdao = new AuthDao();

        UserRecord user = userdao.getUser(username);
        if (user == null) {
            return new IncorrectUsernameException("Error: username does not exist");
        } else if (!user.password().equals(password)) {
            return new IncorrectPasswordException("Error: incorrect password");
        } else {
            String authToken = generateToken();
            authdao.addAuth(user.username(), authToken);
            return new LoginResult(user.username(), authToken);
        }
    }
    public LogoutResult logout(LogoutRequest logoutRequest) {
        String authToken = logoutRequest.authToken();
        AuthDao authdao = new AuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        if (authData == null) {
            return new IncorrectAuthTokenException();

        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
