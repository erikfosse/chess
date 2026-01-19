package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case ROOK -> this.Rook(board, myPosition);
            case BISHOP -> this.Bishop(board, myPosition);
            case KNIGHT -> this.Knight(board, myPosition);
            case KING -> this.King(board, myPosition);
            case QUEEN -> this.Queen(board, myPosition);
            case PAWN -> this.Pawn(board, myPosition);
            case null, default -> throw new RuntimeException("Not implemented");
        };
    }

    private Collection<ChessMove> Bishop(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        while (this.checkBounds(x + 1, y + 1)) {
            var endPosition = new ChessPosition(x + 1, y + 1);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                x++; y++;
            } else {
                break;
            }
        };
        while (this.checkBounds(x - 1, y - 1)) {
            var endPosition = new ChessPosition(x - 1, y - 1);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                x--; y--;
            } else {
                break;
            }
        };
        while (this.checkBounds(x + 1, y - 1)) {
            var endPosition = new ChessPosition(x + 1, y - 1);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                x++; y--;
            } else {
                break;
            }
        };
        while (this.checkBounds(x - 1, y + 1)) {
            var endPosition = new ChessPosition(x - 1, y + 1);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                x--; y++;
            } else {
                break;
            }
        };
        return moves;
    }

    private Collection<ChessMove> Knight(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        var posMoves = new ArrayList<ChessPosition>(List.of(
                new ChessPosition(x + 2, y + 1),
                new ChessPosition(x + 2, y - 1),
                new ChessPosition(x - 2, y + 1),
                new ChessPosition(x - 2, y - 1),
                new ChessPosition(x + 1, y + 2),
                new ChessPosition(x - 1, y + 2),
                new ChessPosition(x + 1, y - 2),
                new ChessPosition(x - 1, y - 2)
        ));
        for (var pos : posMoves) {
            var x_0 = pos.getRow();
            var y_0 = pos.getColumn();
            if (this.checkBounds(x_0, y_0)) {
                var endPosition = new ChessPosition(x_0, y_0);
                moves.add(new ChessMove(myPosition, endPosition));
            }
        }
        return moves;
    }

    private Collection<ChessMove> King(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (this.checkBounds(i, j)) {
                    var endPosition = new ChessPosition(i, j);
                    if (board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(myPosition,endPosition));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> Queen(ChessBoard board, ChessPosition myPosition) {
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        var moves = new ArrayList<ChessMove>(this.Bishop(board, myPosition));
        moves.addAll(this.Rook(board, myPosition));
        return moves;
    }

    private Collection<ChessMove> Pawn(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        if (this.pieceColor == ChessGame.TeamColor.WHITE) {
            if (this.checkBounds(x, y + 1)) {
                var endPosition = new ChessPosition(x, y + 1);
                if (board.getPiece(endPosition) == null) {moves.add(new ChessMove(myPosition, endPosition));}
            } if (this.checkBounds(x, y + 2)) {
                var endPosition = new ChessPosition(x, y + 2);
                if (y == 1 && board.getPiece(endPosition) == null) {moves.add(new ChessMove(myPosition, endPosition));}
            } if (this.checkBounds(x + 1, y + 1)) {
                var endPosition = new ChessPosition(x + 1, y + 1);
                if (board.getPiece(endPosition) != null) {moves.add(new ChessMove(myPosition, endPosition));}
            } if (this.checkBounds(x - 1, y + 1)) {
                var endPosition = new ChessPosition(x + 1, y + 1);
                if (board.getPiece(endPosition) != null) {moves.add(new ChessMove(myPosition, endPosition));}
            }
        } else if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            if (this.checkBounds(x, y - 1)) {
                var endPosition = new ChessPosition(x, y - 1);
                if (board.getPiece(endPosition) == null) {moves.add(new ChessMove(myPosition, endPosition));}
            } if (this.checkBounds(x, y - 2)) {
                var endPosition = new ChessPosition(x, y - 2);
                if (y == 1 && board.getPiece(endPosition) == null) {moves.add(new ChessMove(myPosition, endPosition));}
            } if (this.checkBounds(x + 1, y - 1)) {
                var endPosition = new ChessPosition(x + 1, y - 1);
                if (board.getPiece(endPosition) != null) {moves.add(new ChessMove(myPosition, endPosition));}
            } if (this.checkBounds(x - 1, y - 1)) {
                var endPosition = new ChessPosition(x + 1, y - 1);
                if (board.getPiece(endPosition) != null) {moves.add(new ChessMove(myPosition, endPosition));}
            }
        }
        return moves;
    }

    private Collection<ChessMove> Rook(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        while (this.checkBounds(x + 1, y)) {
            var endPosition = new ChessPosition(x + 1, y);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                x++;
            } else {
                break;
            }
        };
        while (this.checkBounds(x - 1, y)) {
            var endPosition = new ChessPosition(x - 1, y);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                x--;
            } else {
                break;
            }
        };
        while (this.checkBounds(x, y + 1)) {
            var endPosition = new ChessPosition(x, y + 1);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                y++;
            } else {
                break;
            }
        };
        while (this.checkBounds(x, y - 1)) {
            var endPosition = new ChessPosition(x, y - 1);
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                y--;
            } else {
                break;
            }
        };
        return moves;
    }

    private boolean checkBounds(int x, int y) {
        return x <= 8 && x >= 1 && y <= 8 && y >= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
