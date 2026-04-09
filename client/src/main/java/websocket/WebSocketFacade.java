package websocket;

import chess.ChessMove;
import model.JsonSerialization;
import model.exception.ResponseException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String host, int port, NotificationHandler notificationHandler) throws ResponseException {
        try {
            String url = "http://" + host + ":" + port;
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = (ServerMessage) JsonSerialization.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = (LoadGameMessage) JsonSerialization.fromJson(message, LoadGameMessage.class);
                            notificationHandler.loadGame(loadGameMessage);
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = (ErrorMessage) JsonSerialization.fromJson(message, ErrorMessage.class);
                            notificationHandler.error(errorMessage);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notificationMessage = (NotificationMessage) JsonSerialization.fromJson(message,
                                    NotificationMessage.class);
                            notificationHandler.notify(notificationMessage);
                        }
                        case null, default -> {
                            return;
                        }
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(UserGameCommand.CommandType commandType, String authToken, int gameID) throws ResponseException {
        standardSendCommand(commandType, authToken, gameID);
    }

    public void makeMove(UserGameCommand.CommandType commandType, String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(commandType, authToken, gameID, move);
            this.session.getBasicRemote().sendText(JsonSerialization.toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void leave(UserGameCommand.CommandType commandType, String authToken, int gameID) throws ResponseException {
        standardSendCommand(commandType, authToken, gameID);
    }

    public void resign(UserGameCommand.CommandType commandType, String authToken, int gameID) throws ResponseException {
        standardSendCommand(commandType, authToken, gameID);

    }

    private void standardSendCommand (UserGameCommand.CommandType commandType, String authToken, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(commandType, authToken, gameID);
            this.session.getBasicRemote().sendText(JsonSerialization.toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }
}
