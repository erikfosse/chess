package service;

import model.AuthRecord;
import model.UserRecord;
import dataaccess.sql.SQLAuthDao;
import dataaccess.sql.SQLUserDao;
import model.exception.*;
import model.request.GeneralApi;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.UUID;

public class UserService {

    public GeneralApi register(RegisterRequest registerRequest) throws SQLException, DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if (username == null || password == null || email == null) {
            return new BadRequestException();
        }
        SQLUserDao userdao = new SQLUserDao();
        SQLAuthDao authdao = new SQLAuthDao();
        UserRecord user = userdao.getUser(username);
        if (user != null) {
            return new AlreadyTakenException();
        } else {
            String authToken = generateToken();
            userdao.addUser(username, password, email);
            authdao.addAuth(username, authToken);
            return new RegisterResult(username, authToken);
        }
    }

    public GeneralApi login(LoginRequest loginRequest) throws SQLException, DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null) {
            return new BadRequestException();
        }
        SQLUserDao userdao = new SQLUserDao();
        SQLAuthDao authdao = new SQLAuthDao();

        UserRecord user = userdao.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            return new UnauthorizedException();
        } else {
            String authToken = generateToken();
            authdao.addAuth(user.username(), authToken);
            return new LoginResult(user.username(), authToken);
        }
    }

    public GeneralApi logout(LogoutRequest logoutRequest) throws SQLException, DataAccessException {
        String authToken = logoutRequest.authToken();
        SQLAuthDao authdao = new SQLAuthDao();
        AuthRecord authData = authdao.getAuth(authToken);
        if (authData == null) {
            return new UnauthorizedException();
        } else {
            authdao.delAuth(authToken);
            return new LogoutResult();
        }
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
