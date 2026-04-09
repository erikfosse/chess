package client;//import serverfacade.ServerConnector;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameRecord;
import model.JsonSerialization;
import model.exception.ResponseException;
import model.result.*;
import serverfacade.ServerFacade;
import ui.ChessUI;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import static chess.ChessPiece.PieceType.*;
import static client.UserState.*;
import static ui.EscapeSequences.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Client implements NotificationHandler {
    private static String authToken;
    private static ArrayList<GameRecord> games;
    private static UserState status;
    private static ServerFacade serverFacade;
    private static WebSocketFacade ws;
    private static int currentGameID;
    private static String currentGameColor;
    private static final Map<String, Integer> LETTERS = new HashMap<>(Map.of(
            "A", 1, "B", 2, "C", 3, "D", 4,
            "E", 5, "F", 6, "G", 7, "H", 8
    ));
    private static final Map<String, ChessPiece.PieceType> PIECES = new HashMap<>(Map.of(
            "KING", KING, "QUEEN", QUEEN, "ROOK", ROOK, "BISHOP", BISHOP,
            "KNIGHT", KNIGHT, "PAWN", PAWN
    ));
    public Client(String host, int port) throws ResponseException {
        serverFacade = new ServerFacade(host, port);
        ws = new WebSocketFacade(host, port, this);
    }
    public static void main(String[] args) {
        assert args.length == 2;
        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Port number should be an integer");
        }
        try {
            Client client = new Client(args[0], port);
            client.run();
        } catch (ResponseException e) {
            System.out.println("Error: connection to server could not be established.");
        }
    }
    public static void run() {
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;
        status = LOGGED_OUT;
        while (true) {
            printCommandline(System.out);
            switch (status) {
                case LOGGED_OUT -> preLoginUI(out, scanner);
                case LOGGED_IN -> postLoginUI(out, scanner);
                case IN_GAME -> gameUI(out, scanner);
                case OBSERVER -> observerUI(out, scanner);
                case QUIT -> {
                    return;
                }
            }
        }
    }
    public void notify(NotificationMessage notification) {
        System.out.println("\n[NOTIFICATION] " + notification.getNotificationMessage());
        printCommandline(System.out);
    }
    public void error(ErrorMessage errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED);
        System.out.println("[ERROR] " + errorMessage.getErrorMessage());
        System.out.print(RESET_TEXT_COLOR);
        printCommandline(System.out);
    }
    public void loadGame(LoadGameMessage loadGameMessage) {
        var record = games.get(currentGameID-1);
        var updateRecord = new GameRecord(record.gameID(), record.whiteUsername(),
                record.blackUsername(), record.gameName(),
                loadGameMessage.getGame(), record.resigned());
        games.add(currentGameID-1, updateRecord);
        displayGame(currentGameID-1, currentGameColor, null);
        printCommandline(System.out);
    }
    private static void preLoginUI(PrintStream out, Scanner scanner) {
        if (scanner.hasNext()) {
            String[] commands = getLine(scanner);
            switch (commands[0]) {
                case "help" -> preLoginHelp(out);
                case "login" -> login(out, commands);
                case "register" -> register(out, commands);
                case "quit" -> status = QUIT;
                default -> out.println("Unrecognized Command");
            }
        }
    }
    private static void register(PrintStream out, String[] param) {
        if (param.length != 4) {
            out.println("Incorrect number of parameters: please enter <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.register(param[1], param[2], param[3]);
            registerLoginSwitch(out, result);
            if (result.statusCode() == 200) {
                RegisterResult res = (RegisterResult) JsonSerialization.fromJson(result.body(), RegisterResult.class);
                authToken = res.authToken();
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }
    private static void login(PrintStream out, String[] param) {
        if (param.length != 3) {
            out.println("Incorrect number of parameters: please enter <USERNAME> <PASSWORD>");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.login(param[1], param[2]);
            registerLoginSwitch(out, result);
            if (result.statusCode() == 200) {
                LoginResult res = (LoginResult) JsonSerialization.fromJson(result.body(), LoginResult.class);
                authToken = res.authToken();
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }
    private static void registerLoginSwitch(PrintStream out, HttpResponse<String> result) {
        switch (result.statusCode()) {
            case 200 -> status = LOGGED_IN;
            case 400 -> out.println("Incorrect input");
            case 401 -> out.println("Unauthorized to fulfill this request");
            case 403 -> out.println("Already Taken");
            default -> out.println("An Error has occurred");
        }
    }
    private static void preLoginHelp(PrintStream out) {
        out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create and account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }
    private static void postLoginUI(PrintStream out, Scanner scanner) {
        if (scanner.hasNext()) {
            String[] commands = getLine(scanner);
            if (!isAuthorized()) {
                out.println("Unauthorized");
                return;
            }
            try {
                games = getGames(out);
            } catch (Exception e) {
                out.println("Error");
            }
            switch (commands[0]) {
                case "create" -> createGame(out, commands);
                case "list" -> listGames(out, commands);
                case "join" -> joinGame(out, commands);
                case "observe" -> observeGame(out, commands);
                case "logout" -> logoutGame(out, commands);
                case "help" -> postLoginHelp(out);
                case "quit" -> status = QUIT;
                default -> System.out.println("Unrecognized Command");
            }
        }
    }
    private static void createGame(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: please enter <NAME>");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.createGame(authToken, param[1]);
            gameSwitch(out, result);
            if (result.statusCode() == 200) {
                CreateGameResult res = (CreateGameResult) JsonSerialization.fromJson(result.body(), CreateGameResult.class);
                out.println("Success list games to find ID");
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }
    private static void listGames(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("No parameters are needed for list");
            return;
        }
        try {
            games = getGames(out);
            if (games != null && games.isEmpty()) {
                out.println("No active games");
                return;
            }
            int i = 1;
            for (GameRecord game : games) {
                out.printf("#%d - %s %n  white: %s, %n  black: %s%n", i, game.gameName(), game.whiteUsername(), game.blackUsername());
                i++;
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }
    private static ArrayList<GameRecord> getGames(PrintStream out) throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> result = serverFacade.listGames(authToken);
        gameSwitch(out, result);
        if (result.statusCode() == 200) {
            ListGamesResult res = (ListGamesResult) JsonSerialization.fromJson(result.body(), ListGamesResult.class);
            return res.games();
        }
        return null;
    }
    private static Integer getGameID(PrintStream out, int num) throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> result = serverFacade.listGames(authToken);
        gameSwitch(out, result);
        if (result.statusCode() == 200) {
            ListGamesResult res = (ListGamesResult) JsonSerialization.fromJson(result.body(), ListGamesResult.class);
            int i = 1;
            for (GameRecord game : res.games()) {
                if (num == i) {
                    return game.gameID();
                }
                i++;
            }
        }
        return 0;
    }
    private static void joinGame(PrintStream out, String[] param) {
        if (param.length != 3) {
            out.println("Incorrect number of parameters: please enter <ID> [WHITE|BLACK]");
            return;
        }
        try {
            int id = Integer.parseInt(param[1]);
            int gameID = getGameID(out, id);
            if (games.isEmpty()) {
                out.print("There are not active games. Please create a game to start.");
            }
            if (gameID == 0) {
                out.printf("No existing game for ID: %d%n", id);
                return;
            }
            HttpResponse<String> result = serverFacade.joinGame(authToken, gameID, param[2].toUpperCase());
            gameSwitch(out, result);
            if (result.statusCode() == 200) {
                String color = param[2].toUpperCase();
                currentGameColor = color;
                currentGameID = id;
                ws.connect(UserGameCommand.CommandType.CONNECT, authToken, id);
                status = IN_GAME;
            }
        } catch (Exception e) {
            out.println("An Error occured");
        }
    }
    private static void observeGame(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: please enter <ID>");
            return;
        }
        int id = Integer.parseInt(param[1]);
        displayGame(id, ChessUI.WHITE, null);
        status = OBSERVER;
    }
    private static void logoutGame(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("No parameters are needed for logout");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.logout(authToken);
            gameSwitch(out, result);
            if (result.statusCode() == 200) {
                LogoutResult res = (LogoutResult) JsonSerialization.fromJson(result.body(), LogoutResult.class);
                status = LOGGED_OUT;
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }
    private static void postLoginHelp(PrintStream out) {
        out.println("""
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK} - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """);
    }
    private static void gameUI(PrintStream out, Scanner scanner) {
        if (scanner.hasNext()) {
            String[] commands = getLine(scanner);
            if (!isAuthorized()) {
                out.println("Unauthorized");
                return;
            }
            try {
                games = getGames(out);
            } catch (Exception e) {
                out.println("Error");
            }
            switch (commands[0]) {
                case "redraw" -> redrawBoard(out, commands);
                case "leave" -> leaveGame(out, commands);
                case "move" -> makeMove(out, commands);
                case "resign" -> resignGame(out, commands);
                case "highlight" -> highlightMoves(out, commands);
                case "help" -> gameHelp(out);
                default -> System.out.println("Unrecognized Command");
            }
        }
    }
    private static void redrawBoard(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("No parameters are needed for redraw");
            return;
        }
        displayGame(currentGameID, currentGameColor, null);
    }
    private static void leaveGame(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("No parameters are needed for leave");
            return;
        }
        try {
            ws.leave(UserGameCommand.CommandType.LEAVE, authToken, currentGameID);
        } catch (ResponseException e) {
            out.println("Error: could not connect to the server.");
        }
        status = LOGGED_IN;
    }
    private static void resignGame(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("No parameters are needed for resign");
            return;
        }
        try {
            ws.resign(UserGameCommand.CommandType.RESIGN, authToken, currentGameID);
        } catch (ResponseException e) {
            out.println("Error: could not connect to the server.");
        }
        status = LOGGED_IN;
    }
    private static void makeMove(PrintStream out, String[] param) {
        if (param.length == 3 || param.length == 4) {
            try {
                ChessMove move;
                if (param.length == 3) {
                    move = makeChessMove(param[1].charAt(1), param[1].charAt(0), param[2].charAt(1), param[2].charAt(0), null);
                } else {
                    move = makeChessMove(param[1].charAt(1), param[1].charAt(0), param[2].charAt(1), param[2].charAt(0), param[3]);
                }
                ws.makeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, currentGameID, move);
            } catch (ResponseException e) {
                out.println("Error: could not connect to the server.");
            }
        } else {
            out.println("Incorrect number of parameters: <Start> <Finish> <Promotion>");
        }
    }
    private static void highlightMoves(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: <Start>");
            return;
        }
        var num1 = Integer.parseInt(String.valueOf(param[1].charAt(1)));
        var letter1 = LETTERS.get(String.valueOf(param[1].charAt(0)).toUpperCase());
        ChessGame game = games.get(currentGameID-1).game();
        var pos = new ChessPosition(num1, letter1);
        var piece = game.getBoard().getPiece(pos);
        ArrayList<ChessMove> moves = (ArrayList<ChessMove>) piece.pieceMoves(game.getBoard(), pos);
        displayGame(currentGameID, currentGameColor, moves);
    }
    private static ChessMove makeChessMove(char row1, char col1, char row2, char col2, String piece) {
        var num1 = Integer.parseInt(String.valueOf(row1));
        var letter1 = LETTERS.get(String.valueOf(col1).toUpperCase());
        var num2 = Integer.parseInt(String.valueOf(row2));
        var letter2 = LETTERS.get(String.valueOf(col2).toUpperCase());
        var start = new ChessPosition(num1, letter1);
        var end = new ChessPosition(num2, letter2);
        ChessPiece.PieceType newPiece;
        if (piece==null) {
            newPiece = null;
        } else {
            newPiece = PIECES.get(piece.toUpperCase());
        }
        return new ChessMove(start, end, newPiece);
    }
    private static void gameHelp(PrintStream out) {
        out.println("""
                redraw - the chess board
                leave - leave the game
                move <Start> <Finish> <Promotion> - moves a piece at the start position
                    - to the end position. If a pawn is being promoted, include its promotion
                    - piece.
                    - Ex: c1 d5 queen
                resign - admit defeat and end the game
                highlight <Start> - highlights all possible moves for the start piece
                help - with possible commands
                """);
    }
    private static void observerUI(PrintStream out, Scanner scanner) {
        if (scanner.hasNext()) {
            String[] commands = getLine(scanner);
            if (!isAuthorized()) {
                out.println("Unauthorized");
                return;
            }
            try {
                games = getGames(out);
            } catch (Exception e) {
                out.println("Error");
            }
            switch (commands[0]) {
                case "redraw" -> redrawBoard(out, commands);
                case "leave" -> leaveGame(out, commands);
                case "highlight" -> highlightMoves(out, commands);
                case "help" -> observerHelp(out);
                default -> System.out.println("Unrecognized Command");
            }
        }
    }
    private static void observerHelp(PrintStream out) {
        out.println("""
                redraw - the chess board
                leave - leave the game
                highlight - highlights all possible moves
                help - with possible commands
                """);
    }
    private static void gameSwitch(PrintStream out, HttpResponse<String> result) {
        switch (result.statusCode()) {
            case 200 -> out.print("");
            case 400 -> out.println("Incorrect input");
            case 401 -> out.println("Unauthorized to fulfill this request");
            case 403 -> out.println("Already taken");
            default -> out.println("An Error has occurred");
        }
    }
    private static void printCommandline(PrintStream out) {
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        String statement = switch (status) {
            case LOGGED_OUT -> "LOGGED_OUT";
            case LOGGED_IN -> "LOGGED_IN";
            case IN_GAME -> "IN_GAME";
            case OBSERVER -> "OBSERVER";
            case QUIT -> "";
            default -> "ERROR";
        };
        if (statement.isEmpty()) {
            return;
        }
        out.printf("[%s] >>> ", statement);
    }
    private static void displayGame(int index, String color, ArrayList<ChessMove> moves) {
        for (GameRecord game : games) {
            if (game.gameID().equals(index)) {
                ChessUI.run(game.game(), color, moves);
            }
        }
    }
    private static String[] getLine(Scanner scanner) {
        String line = scanner.nextLine();
        return line.split(" ");
    }
    private static boolean isAuthorized() {
        return !authToken.isEmpty();
    }
}
