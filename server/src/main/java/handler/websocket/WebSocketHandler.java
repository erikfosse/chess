package handler.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.sql.SQLAuthDao;
import dataaccess.sql.SQLGameDao;
import io.javalin.websocket.*;
import model.AuthRecord;
import model.GameRecord;
import model.JsonSerialization;
import model.exception.AlreadyTakenException;
import model.exception.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private SQLGameDao gameDao;
    private SQLAuthDao authDao;

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println(("Websocket connected"));
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            gameDao = new SQLGameDao();
            authDao = new SQLAuthDao();
            UserGameCommand command;
            command = (UserGameCommand) JsonSerialization.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE -> {
                    var command_1 = (MakeMoveCommand) JsonSerialization.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(command.getAuthToken(), command.getGameID(), command_1.getMove(), ctx.session);
                }
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, int gameID, Session session) throws Exception {
        connections.add(session);
        loadGame(session, gameID);
        if (isValidAuth(authToken) && isValidGameID(gameID)) {
            String playerName = getUserName(authToken);
            String color = getUserColor(authToken, gameID);
            var message = String.format("%s has joined as %s", playerName, color);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, notification);
        }
        else {
            var message = "Invalid authtoken or gameID";
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcast(session, error);
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException, AlreadyTakenException {
        String playerName = getUserName(authToken);
        removePlayerFromGame(authToken, gameID);
        var message = String.format("%s left the game", playerName);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.remove(session);
        connections.broadcast(session, notification);
    }

    private void resign(String authToken, int gameID) throws IOException {
        try {
            var playerName = getUserName(authToken);
            gameDao.resignGame(gameID);
            var message = String.format("%s has resigned the game", playerName);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, notification);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            ChessGame game = gameDao.getGame(gameID).game();
            var playerName = getUserName(authToken);
            ChessPosition pos = move.getStartPosition();
            if (game.validMoves(pos).contains(move)) {
                game.makeMove(move);
                gameDao.updateGame(gameID, game);
                loadGame(null, gameID);
                var message = makeMoveMessage(game, move, playerName);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(session, notification);

                var checkMessage = checkGameMessage(game);
                if (!checkMessage.isEmpty()) {
                    notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
                    connections.broadcast(null, notification);
                }
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private String checkGameMessage(ChessGame game) {
        List<ChessGame.TeamColor> colors = List.of(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.WHITE);
        String message = "";
        for (ChessGame.TeamColor color : colors) {
            if (game.isInCheck(color)) {
                message = String.format("%s is in check", color);
            } if (game.isInCheckmate(color)) {
                message = String.format("%s is in checkmate", color);
            } if (game.isInStalemate(color)) {
                message = String.format("%s is in stalemate", color);
            }
        }
        return message;
    }

    private String makeMoveMessage(ChessGame game, ChessMove move, String playerName) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = game.getBoard().getPiece(startPos);
        HashMap<Integer, String> letters = new HashMap<>(Map.of(
                1, "A", 2, "B", 3, "C", 4, "D",
                5, "E", 6, "F", 7, "G", 8, "H"
        ));
        var start = letters.get(startPos.getColumn()) + startPos.getRow();
        var end = letters.get(endPos.getColumn()) + endPos.getRow();
        return String.format("%s moved %s from %s to %s", playerName, piece, start, end);
    }

    private String getUserName(String authToken) throws IOException {
        try {
            AuthRecord user = authDao.getAuth(authToken);
            return user.username();
        } catch (DataAccessException e) {
            throw new IOException();
        }
    }

    private String getUserColor(String authToken, int gameID) throws IOException {
        String playerName = getUserName(authToken);
        try {
            GameRecord game = gameDao.getGame(gameID);
            if (game.blackUsername().equals(playerName)) {
                return "BLACK";
            } else {
                return "WHITE";
            }
        } catch (DataAccessException e) {
            throw new IOException();
        }

    }

    private void loadGame(Session session, int gameID) throws IOException {
        try {
            ChessGame game = gameDao.getGame(gameID).game();
            var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast(session, loadGameMessage);
        } catch (DataAccessException e) {
            throw new IOException();
        }
    }

    private void removePlayerFromGame(String authToken, int gameID) throws IOException, AlreadyTakenException {
        try {
            String color = getUserColor(authToken, gameID);
            GameRecord game = gameDao.getGame(gameID);
            gameDao.editGame(gameID, color, null);
        } catch (DataAccessException e) {
            throw new IOException();
        }
    }

    private boolean isValidAuth(String authToken) throws IOException {
        try {
            return authDao.getAuth(authToken) != null;
        } catch (DataAccessException e) {
            throw new IOException();
        }
    }

    private boolean isValidGameID(int gameID) throws IOException {
        try {
            return gameDao.getGame(gameID) != null;
        } catch (DataAccessException e) {
            throw new IOException();
        }
    }

}
