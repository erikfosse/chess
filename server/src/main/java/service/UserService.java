package service;

import model.AuthRecord;
import model.UserRecord;
import dataaccess.AuthDao;
import dataaccess.UserDao;
import model.exception.AlreadyTakenException;
import model.exception.IncorrectAuthException;
import model.exception.IncorrectPasswordException;
import model.exception.IncorrectUsernameException;
import model.request.GeneralApi;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;

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
    public GeneralApi logout(LogoutRequest logoutRequest) {
        String authToken = logoutRequest.authToken();
        AuthDao authdao = new AuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        if (authData == null) {
            return new IncorrectAuthException("Error: unauthorized");
        } else {
            authdao.delAuth(authToken);
            return new LogoutResult();
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
