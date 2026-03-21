package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_ROOK;

public class ChessUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
    private static final String[] COL_HEADERS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final String[] ROW_HEADERS = {"8", "7", "6", "5", "4", "3", "2", "1"};
    public static final String BLACK = "BLACK";
    public static final String WHITE = "WHITE";


    private ChessUI(ChessGame game, String color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawHeaders(out, color);
        drawChessBoard(out, game, color);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        new ChessUI(game, WHITE);
    }

    public static void run(ChessGame game, String color) {
        new ChessUI(game, color);
    }

    private static void drawHeaders(PrintStream out, String color) {
        setBlack(out);
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
        out.print("    ");
        setBlack(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_BOLD);
        out.print(SET_TEXT_COLOR_DARK_GREY);
        String wideEmpty = "  \u2003\u2002\u2005";
        out.print(wideEmpty.repeat(prefixLength));
        out.print(headerText);
        out.print(wideEmpty.repeat(suffixLength));
        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, ChessGame game, String color) {
        if (color.equals("BLACK")) {
            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                drawRowOfSquares(out, game, boardRow);
            }
        } else {
            for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {
                drawRowOfSquares(out, game, boardRow);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, ChessGame game, int row) {

        int count = row;
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if (isEven(count)) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_WHITE);
                } else {
                    out.print(SET_BG_COLOR_DARK_GREEN);
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
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
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
                count++;
            }
            printVerticalHeader(out, ROW_HEADERS[row], squareRow);
            setBlack(out);
            out.println();
        }
    }

    private static void printVerticalHeader(PrintStream out, String headerText, int squareRow) {
        if (squareRow == 1) {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_DARK_GREY);
            out.printf(" %s ", headerText);
        } else {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_LIGHT_GREY);
            out.print("   ");
        }
    }


    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static boolean isEven(int num) {
        return Math.floorMod(num, 2) == 0;
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
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
