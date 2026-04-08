package websocket;

import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);
    void error(ErrorMessage errorMessage);
    void loadGame(LoadGameMessage loadGameMessage);
}
