//import serverfacade.ServerConnector;

import model.GameRecord;
import model.JsonSerialization;
import model.exception.ResponseException;
import model.result.*;
import serverfacade.ServerFacade;
import ui.ChessUI;
import client.UserState;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import static client.UserState.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static String authToken;
    private static ArrayList<GameRecord> games;
    private static UserState status;
    private static ServerFacade serverFacade;
    private static WebSocketFacade ws;

    public static void main(String[] args) {
        assert args.length == 2;
        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Port number should be an integer");
        }
        run(args[0], port);
    }

    public static void run(String host, int port) {
        serverFacade = new ServerFacade(host, port);
        String url = "http://" + host + ":" + port;
        try {
            ws = new WebSocketFacade(url, new NotificationHandler());
        } catch (ResponseException e) {
            System.out.println("Error: could not connect to the server.");
        }

        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;

        status = LOGGED_OUT;
        while (true) {
            printCommandline(System.out);
            switch (status) {
                case LOGGED_OUT -> preLoginUI(out, scanner);
                case LOGGED_IN -> postLoginUI(out, scanner);
                case IN_GAME -> gameUI(out, scanner);
                case QUIT -> {
                    return;
                }
            }
        }
    }

    private static void preLoginUI(PrintStream out, Scanner scanner) {
        outer:
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
            if (games != null ? games.isEmpty() : false) {
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
                displayGame(id, color);
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }

    private static void observeGame(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: please enter <ID>");
            return;
        }
        int id = Integer.parseInt(param[1]);
        displayGame(id, ChessUI.WHITE);
    }

    private static void displayGame(int index, String color) {
        for (GameRecord game : games) {
            if (game.gameID().equals(index)) {
                ChessUI.run(game.game(), color);
            }
        }
    }

    private static void logoutGame(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("No parameters are needed for list");
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
                case "redraw" -> ;
                case "leave" -> ;
                case "move" -> ;
                case "resign" -> ;
                case "highlight" -> ;
                case "help" -> gameHelp(out);
                default -> System.out.println("Unrecognized Command");
            }
        }
    }

    private static void redrawBoard(PrintStream out) {

    }

    private static void gameHelp(PrintStream out) {
        out.println("""
                redraw - the chess board
                leave - leave the game
                move <Start> <Finish> <Promotion> - moves a piece at the start coordinate
                    - to the end position. If a pawn is being promoted, include its promotion
                    - piece.
                resign - admit defeat and end the game
                highlight - highlights all possible moves
                help - with possible commands
                """);
    }

    private static void gameSwitch(PrintStream out, HttpResponse<String> result) {
        switch (result.statusCode()) {
            case 200 -> out.print("");
            case 400 -> out.println("Incorrect input");
            case 401 -> out.println("Unauthorized to fulfill this request");
            case 403 -> out.println("Username/Password already taken. Please select another and try again");
            default -> out.println("An Error has occurred");
        }
    }

    private static void printCommandline(PrintStream out) {
        String statement = switch (status) {
            case LOGGED_OUT -> "LOGGED_OUT";
            case LOGGED_IN -> "LOGGED_IN";
            case IN_GAME -> "IN_GAME";
            case QUIT -> "";
            default -> "ERROR";
        };
        if (statement.isEmpty()) {
            return;
        }
        out.printf("[%s] >>> ", statement);
    }

    private static String[] getLine(Scanner scanner) {
        String line = scanner.nextLine();
        return line.split(" ");
    }

    private static boolean isAuthorized() {
        return !authToken.isEmpty();
    }
}
