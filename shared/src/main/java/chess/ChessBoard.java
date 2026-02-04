package chess;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece [][] chessboard;

    public ChessBoard() {
        this.chessboard = new ChessPiece [8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int x = position.getRow() - 1;
        int y = position.getColumn() - 1;
        chessboard [x][y] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int x = position.getRow() - 1;
        int y = position.getColumn() - 1;
        return chessboard [x][y];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor color;
        for (int i = 0; i < 8; i += 7) {
            if (i == 0) {
                color = ChessGame.TeamColor.WHITE;
            } else {
                color = ChessGame.TeamColor.BLACK;
            }
            chessboard[i][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            chessboard[i][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            chessboard[i][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            chessboard[i][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            chessboard[i][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            chessboard[i][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            chessboard[i][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            chessboard[i][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        }
        for (int i = 1; i < 8; i += 5) {
            if (i == 1) {
                color = ChessGame.TeamColor.WHITE;
            } else {
                color = ChessGame.TeamColor.BLACK;
            }
            for (int j = 0; j < 8; j += 1) {
                chessboard[i][j] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
            }
        }
    }

    public class BoardIterator implements Iterator<ChessPiece> {
        int i = 0;
        int j = 0;
        ChessPiece [][] board;

        public BoardIterator() {
            this.board = ChessBoard.this.chessboard;
        }

        @Override
        public boolean hasNext() {
            return checkBound(i, j);
        }

        @Override
        public ChessPiece next() {
            ChessPiece piece = chessboard[i][j];
            if (j == 7) {i++; j=0;}
            else {i++; j++;}
            return piece;
        }

        public ChessPosition getPosition() {
            return new ChessPosition(i + 1, j + 1);
        }

        private boolean checkBound(int i, int j) {
            return (i <= 7 && i >= 0 && j <= 7 && j >= 0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(chessboard, that.chessboard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessboard);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "chessboard=" + Arrays.toString(chessboard) +
                '}';
    }
}
