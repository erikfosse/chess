package client;

import java.io.PrintStream;
import java.util.Scanner;

public class ClientMain {

    private static String uiState;
    private static final String LOGGED_OUT = "LOGGED_OUT";
    private static final String LOGGED_IN = "LOGGED_IN";

    public static void main(String[] args) {
        uiState = LOGGED_OUT;
        run();
    }

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        PrintStream out = System.out;
        commandline(System.out);
        while (true) {
            switch (uiState) {
                case LOGGED_OUT -> preLoginUI(out, scanner);
                case LOGGED_IN -> postLoginUI(out, scanner);
            }
        }
    }

    public static void preLoginUI(PrintStream out, Scanner scanner) {
        outer:
        while (scanner.hasNext()) {
            String[] commands = getline(scanner);
            switch (commands[0]) {
                case "help" -> preLoginHelp(System.out);
                case "login" -> login(System.out, commands);
                case "register" -> register(System.out);
                case "quit" -> {
                    break outer;
                }
                default -> System.out.println("Incorrect Command");
            }
            commandline(System.out);
            break;
        }
    }

    public static void postLoginUI(PrintStream out, Scanner scanner) {
        outer:
        while (scanner.hasNext()) {
            String[] commands = getline(scanner);
            switch (commands[0]) {
                case "create" -> preLoginHelp(out);
                case "list" -> login(out, commands);
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

    public static void commandline(PrintStream out) {
        out.printf("[%s] >>> ", uiState);
    }

    public static String[] getline(Scanner scanner) {
        String line = scanner.nextLine();
        String[] commands = line.split(" ");
        return commands;
    }

    public static void preLoginHelp(PrintStream out) {
        out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create and account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }

    public static void postLoginHelp(PrintStream out) {
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

    public static void register(PrintStream out) {

    }

    public static void login(PrintStream out, String[] commands) {
        if (commands.length == 4) {
            uiState = LOGGED_IN;
        }
    }
}
