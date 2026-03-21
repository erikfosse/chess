//import ServerConnector;

import model.GameRecord;
import model.result.*;
import ui.ChessUI;

import java.io.PrintStream;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static String authToken;
    private static ArrayList<GameRecord> games;
    private static int status;
    private static final int LOGGED_OUT = -1;
    private static final int LOGGED_IN = 0;
    private static final int IN_GAME = 1;
    private static ServerFacade serverFacade;

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
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;
        printCommandline(System.out);

        status = LOGGED_OUT;
        while (true) {
            switch (status) {
                case LOGGED_OUT -> preLoginUI(out, scanner);
                case LOGGED_IN -> postLoginUI(out, scanner);
            }
        }
    }

    private static void preLoginUI(PrintStream out, Scanner scanner) {
        outer:
        while (scanner.hasNext()) {
            String[] commands = getLine(scanner);
            switch (commands[0]) {
                case "help" -> preLoginHelp(out);
                case "login" -> login(out, commands);
                case "register" -> register(out, commands);
                case "quit" -> {
                    break outer;
                }
                default -> out.println("Incorrect Command");
            }
            printCommandline(out);
            break;
        }
    }

    private static void register(PrintStream out, String[] param) {
        if (param.length != 3) {
            out.println("Incorrect number of parameters: please enter <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.register(param[0], param[1], param[2]);
            registerLoginSwitch(out, result);
            if (result.statusCode() == 200) {
                RegisterResult res = (RegisterResult) serverFacade.fromJson(result.body(), RegisterResult.class);
                authToken = res.authToken();
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }

    }

    private static void login(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: please enter <USERNAME> <PASSWORD>");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.register(param[0], param[1], param[2]);
            registerLoginSwitch(out, result);
            if (result.statusCode() == 200) {
                LoginResult res = (LoginResult) serverFacade.fromJson(result.body(), LoginResult.class);
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
            case 403 -> out.println("Username/Password already taken. Please select another and try again");
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
        outer:
        while (scanner.hasNext()) {
            String[] commands = getLine(scanner);
            if (isAuthorized()) {
                out.println("Unauthorized");
                break;
            }
            switch (commands[0]) {
                case "create" -> createGame(out, commands);
                case "list" -> listGames(out, commands);
                case "join" -> joinGame(out, commands);
                case "observe" -> observeGame(out, commands);
                case "logout" -> logoutGame(out, commands);
                case "help" -> postLoginHelp(out);
                case "quite" -> {
                    break outer;
                }
                default -> System.out.println("Incorrect Command");
            }
            printCommandline(System.out);
            break;
        }
    }

    private static void createGame(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("Incorrect number of parameters: please enter <NAME>");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.createGame(authToken, param[0]);
            gameSwitch(out, result);
            if (result.statusCode() == 200) {
                CreateGameResult res = (CreateGameResult) serverFacade.fromJson(result.body(), CreateGameResult.class);
                out.printf("Success: %s %d%n", param[0], res.gameID());
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }

    private static void listGames(PrintStream out, String[] param) {
        if (param.length != 0) {
            out.println("No parameters are needed for list");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.listGames(authToken);
            gameSwitch(out, result);
            if (result.statusCode() == 200) {
                ListGamesResult res = (ListGamesResult) serverFacade.fromJson(result.body(), ListGamesResult.class);
                games = res.games();
                for (GameRecord game : games) {
                    out.printf("#%d %s white: %s, black: %s%n", game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername());
                }
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }

    private static void joinGame(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: please enter <ID> [WHITE|BLACK]");
            return;
        }
        try {
            HttpResponse<String> result = serverFacade.joinGame(authToken, Integer.parseInt(param[0]), param[1]);
            gameSwitch(out, result);
            if (result.statusCode() == 200) {
                JoinGameResult res = (JoinGameResult) serverFacade.fromJson(result.body(), JoinGameResult.class);
            }
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }
    }

    private static void observeGame(PrintStream out, String[] param) {
        if (param.length != 1) {
            out.println("Incorrect number of parameters: please enter <ID>");
            return;
        }
        for (GameRecord game : games) {
            if (ServerFacade.isInt(param[0])) {
                int id = Integer.parseInt(param[0]);
                if (game.gameID().equals(id)) {
                    ChessUI.run(game.game(), ChessUI.WHITE);
                }
            }
        }
    }

    private static void logoutGame(PrintStream out, String[] param) {

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
        out.printf("[%s] >>> ", status);
    }

    private static String[] getLine(Scanner scanner) {
        String line = scanner.nextLine();
        return line.split(" ");
    }

    private static boolean isAuthorized() {
        return !authToken.isEmpty();
    }
}
