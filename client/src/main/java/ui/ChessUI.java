package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_ROOK;

public class ChessUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
    private static final String[] COL_HEADERS = {"H", "G", "F", "E", "D", "C", "B", "A"};
    private static final String[] ROW_HEADERS = {"1", "2", "3", "4", "5", "6", "7", "8"};
    public static final String BLACK = "BLACK";
    public static final String WHITE = "WHITE";


    private ChessUI(ChessGame game, String color, ArrayList<ChessMove> moves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawHeaders(out, color);
        drawChessBoard(out, game, color, moves);
        out.print(RESET);
        drawHeaders(out, color);
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition start = new ChessPosition(2, 2);
        ChessPosition end;
        for (int i = 1; i < 7; i++) {
            end = new ChessPosition(2 + i, 2 + i);
            moves.add(new ChessMove(start, end));
        }
        new ChessUI(game, WHITE, moves);
    }

    public static void run(ChessGame game, String color, ArrayList<ChessMove> moves) {
        System.out.print(ERASE_SCREEN);
        new ChessUI(game, color, moves);
    }

    private static void drawHeaders(PrintStream out, String color) {
        setBlack(out);
        out.print(SET_BG_COLOR_WHITE);
        out.print("   ");
        if (color.equals("BLACK")) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                drawHeader(out, COL_HEADERS[boardCol]);
            }
        } else {
            for (int boardCol = BOARD_SIZE_IN_SQUARES - 1; boardCol >= 0; --boardCol) {
                drawHeader(out, COL_HEADERS[boardCol]);
            }
        }
        out.print(SET_BG_COLOR_WHITE);
        out.print("   ");
        setBlack(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_BOLD);
        out.print(SET_TEXT_COLOR_DARK_GREY);
        out.print("\u2002\u2005" + headerText + "\u2002\u2004");
        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, ChessGame game,  String color,
                                       ArrayList<ChessMove> moves) {
        if (color.equals("BLACK")) {
            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                printRow(out, game, color, boardRow, moves);
            }
        } else {
            for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {
                printRow(out, game, color, boardRow, moves);
            }
        }
    }

    private static void printRow(PrintStream out, ChessGame game, String color, int row,
                                 ArrayList<ChessMove> moves) {

        printVerticalHeader(out, row);
        if (color.equals("WHITE")) {
            printChessRowHelperWhite(out, game, row, moves);
        } else {
            printChessRowHelperBlack(out, game, row, moves);
        }
        printVerticalHeader(out, row);
        setBlack(out);
        out.println();
    }

    private static void printChessRowHelperWhite(PrintStream out, ChessGame game, int row,
                                                 ArrayList<ChessMove> moves) {
        int count = row;
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (isEven(count)) {
                printBlackSpot(out, moves, row, boardCol);
            } else {
                printWhiteSpot(out, moves, row, boardCol);
            }
            rowHelperFunction(out, row, boardCol, game);
            count++;
        }
    }

    private static void printChessRowHelperBlack(PrintStream out, ChessGame game, int row,
                                                 ArrayList<ChessMove> moves) {
        int count = row;
        for (int boardCol = BOARD_SIZE_IN_SQUARES - 1; boardCol >= 0 ; --boardCol) {
            if (isEven(count)) {
                printWhiteSpot(out, moves, row, boardCol);
            } else {
                printBlackSpot(out, moves, row, boardCol);
            }
            rowHelperFunction(out, row, boardCol, game);
            count++;
        }
    }

    private static void rowHelperFunction(PrintStream out, int row, int boardCol, ChessGame game) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        var position = new ChessPosition(row + 1, boardCol + 1);
        var piece = game.getBoard().getPiece(position);
        if (piece != null) {
            out.print(EMPTY.repeat(prefixLength));
            printPlayer(out, piece.getPieceType(), piece.getTeamColor());
            out.print(EMPTY.repeat(suffixLength));
        } else {
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
        }
    }

    private static void printWhiteSpot(PrintStream out, ArrayList<ChessMove> moves, int row, int col) {
        printSpotHelper(out, moves, row, col, SET_BG_COLOR_YELLOW, SET_BG_COLOR_LIGHT_GREY);
    }

    private static void printBlackSpot(PrintStream out, ArrayList<ChessMove> moves, int row, int col) {
        printSpotHelper(out, moves, row, col, SET_BG_COLOR_GREEN, SET_BG_COLOR_DARK_GREEN);
    }

    private static void printSpotHelper(PrintStream out, ArrayList<ChessMove> moves, int row, int col, String color1, String color2) {
        if (checkStartMove(moves, row, col)) {
            out.print(SET_BG_COLOR_BLUE);
        } else if (checkMoves(moves, row, col)) {
            out.print(color1);
        } else {
            out.print(color2);
        }
    }

    private static boolean checkMoves(ArrayList<ChessMove> moves, int row, int col) {
        if (moves==null) {
            return false;
        }
        for (ChessMove move : moves) {
            var pos = move.getEndPosition();
            if (row+1 == pos.getRow() && col+1 == pos.getColumn()) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkStartMove(ArrayList<ChessMove> moves, int row, int col) {
        if (moves==null || moves.isEmpty()) {
            return false;
        }
        var move = moves.getFirst().getStartPosition();
        return row+1 == move.getRow() && col+1 == move.getColumn();
    }

    private static void printVerticalHeader(PrintStream out, int row) {
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_WHITE);
        out.print(" " + ROW_HEADERS[row] + " ");
    }

    private static boolean isEven(int num) {
        return Math.floorMod(num, 2) == 0;
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, ChessPiece.PieceType piece, ChessGame.TeamColor color) {
        if (color.equals(ChessGame.TeamColor.BLACK)) {
            out.print(SET_TEXT_COLOR_BLACK);
        } else {
            out.print(SET_TEXT_COLOR_WHITE);
        }
        String chessPiece = switch (color) {
            case BLACK -> switch (piece) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case KNIGHT -> BLACK_KNIGHT;
                case BISHOP -> BLACK_BISHOP;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
                default -> null;
            };
            case WHITE -> switch (piece) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case KNIGHT -> WHITE_KNIGHT;
                case BISHOP -> WHITE_BISHOP;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
                default -> null;
            };
        };
        out.print(chessPiece);
    }
}
