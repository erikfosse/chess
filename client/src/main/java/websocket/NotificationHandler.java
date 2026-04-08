package websocket;

import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class NotificationHandler {
    void notify(NotificationMessage notification) {
        System.out.println(notification.getNotificationMessage());
    }

    void error(ErrorMessage errorMessage) {
        System.out.println(errorMessage.getErrorMessage());
    }

    ChessGame loadGame(LoadGameMessage loadGameMessage) {
        return loadGameMessage.getGame();
    }
}
