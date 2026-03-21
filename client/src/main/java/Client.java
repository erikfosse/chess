//import ServerConnector;

import model.result.RegisterResult;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Client {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
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
        commandline(System.out);

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
            String[] commands = getline(scanner);
            switch (commands[0]) {
                case "help" -> preLoginHelp(out);
                case "login" -> login(out, commands);
                case "register" -> register(out, commands);
                case "quit" -> {
                    break outer;
                }
                default -> out.println("Incorrect Command");
            }
            commandline(out);
            break;
        }
    }

    private static void register(PrintStream out, String[] param) {
        if (param.length != 3) {
            out.println("Incorrect number of parameters: please enter <USERNAME> <PASSWORD> <EMAIL>");
        }
        try {
            HttpResponse<String> result = serverFacade.register(param[0], param[1], param[2]);
            registerLoginSwitch(out, result);
        } catch (Exception e) {
            out.println("Incorrect parameter input");
        }

    }

    private static void login(PrintStream out, String[] param) {
        if (param.length != 2) {
            out.println("Incorrect number of parameters: please enter <USERNAME> <PASSWORD>");
        }
        try {
            HttpResponse<String> result = serverFacade.register(param[0], param[1], param[2]);
            registerLoginSwitch(out, result);
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
            String[] commands = getline(scanner);
            switch (commands[0]) {
                case "create" -> preLoginHelp(out);
                case "list" -> list(out, commands);
                case "join" -> register(out);
                case "observe" -> register(out);
                case "logout" -> register(out);
                case "help" -> postLoginHelp(out);
                case "quite" -> {
                    break outer;
                }
                default -> System.out.println("Incorrect Command");
            }
            commandline(System.out);
            break;
        }
    }

    private static void

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

    private static void commandline(PrintStream out) {
        out.printf("[%s] >>> ", status);
    }

    private static String[] getline(Scanner scanner) {
        String line = scanner.nextLine();
        String[] commands = line.split(" ");
        return commands;
    }
}
