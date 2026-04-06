package handler.websocket;

import chess.ChessGame;
import io.javalin.websocket.*;
import model.AuthRecord;
import model.GameRecord;
import model.JsonSerialization;
import model.exception.AlreadyTakenException;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println(("Websocket connected"));
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = (UserGameCommand) JsonSerialization.fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE ->
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN ->
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

    private String getUserName(String authToken) {
        AuthRecord user = db.AuthData.getAuth(authToken);
        return user.username();
    }

    private String getUserColor(String authToken, int gameID) {
        String playerName = getUserName(authToken);
        GameRecord game = db.GameData.getGame(gameID);
        if (game.blackUsername().equals(playerName)) {
            return "BLACK";
        } else {
            return "WHITE";
        }
    }

    private void loadGame(Session session, int gameID) throws IOException {
        ChessGame game = db.GameData.getGame(gameID).game();
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(session, loadGameMessage);
    }

    private void removePlayerFromGame(String authToken, int gameID) throws IOException, AlreadyTakenException {
        String color = getUserColor(authToken, gameID);
        GameRecord game = db.GameData.getGame(gameID);
        db.GameData.editGame(gameID, color, null);
    }

    private boolean isValidAuth(String authToken) {
        return db.AuthData.getAuth(authToken) != null;
    }

    private boolean isValidGameID(int gameID) {
        return db.GameData.getGame(gameID) != null;
    }

}
