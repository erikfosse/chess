package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.interfaces.UserInterface;
import model.UserRecord;
import model.exception.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLUserDao implements UserInterface {

    private Connection conn;

    public SQLUserDao() throws DataAccessException, SQLException {
        this.conn = DatabaseManager.getConnection();
    }

    @Override
    public void addUser(String username, String password, String email) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (username.matches("[a-zA-Z0-9]+")) {
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
        }
    }
    @Override
    public UserRecord getUser(String username) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(
                "SELECT userID, username, password, email FROM userData WHERE username=?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                UserRecord user = null;
                while (rs.next()) {
                    var hashPassword = rs.getString("password");
                    var email = rs.getString("email");
                    user = new UserRecord(username, hashPassword, email);
                }
                return user;
            }
        }
    }
    @Override
    public void deleteData() throws SQLException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE userData")) {
            preparedStatement.executeUpdate();
        }
    }
}
