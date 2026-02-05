package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.List;
import java.util.function.Function;

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

    public ChessPiece(ChessPiece copy) {
        this.pieceColor = copy.pieceColor;
        this.type = copy.type;
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
        Function<Integer, Integer> add_one = (i) -> i + 1;
        Function<Integer, Integer> min_one = (i) -> i - 1;
        moves.addAll(bishop_rook_Helper(board, myPosition, add_one, add_one, x, y));
        moves.addAll(bishop_rook_Helper(board, myPosition, add_one, min_one, x, y));
        moves.addAll(bishop_rook_Helper(board, myPosition, min_one, add_one, x, y));
        moves.addAll(bishop_rook_Helper(board, myPosition, min_one, min_one, x, y));
        return moves;
    }

    private Collection<ChessMove> Rook(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        Function<Integer, Integer> add_one = (i) -> i + 1;
        Function<Integer, Integer> min_one = (i) -> i - 1;
        Function<Integer, Integer> add_none = (i) -> i;
        moves.addAll(bishop_rook_Helper(board, myPosition, add_one, add_none, x, y));
        moves.addAll(bishop_rook_Helper(board, myPosition, add_none, add_one, x, y));
        moves.addAll(bishop_rook_Helper(board, myPosition, min_one, add_none, x, y));
        moves.addAll(bishop_rook_Helper(board, myPosition, add_none, min_one, x, y));
        return moves;
    }

    private Collection<ChessMove> bishop_rook_Helper(ChessBoard board, ChessPosition myPosition, Function<Integer, Integer> x_func, Function<Integer, Integer> y_func, int a, int b) {
        var moves = new ArrayList<ChessMove>();
        while (this.checkBounds(x_func.apply(a), y_func.apply(b))) {
            var endPosition = new ChessPosition(x_func.apply(a), y_func.apply(b));
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                a = x_func.apply(a); b = y_func.apply(b);
            } else {
                if (pieceColor != board.getPiece(endPosition).pieceColor) {
                    moves.add(new ChessMove(myPosition, endPosition));
                }
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
                if (board.getPiece(endPosition) == null) {
                    moves.add(new ChessMove(myPosition, endPosition));
                } else {
                    if (pieceColor != board.getPiece(endPosition).pieceColor) {
                        moves.add(new ChessMove(myPosition, endPosition));
                    }
                }
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
                    } else {
                        if (pieceColor != board.getPiece(endPosition).pieceColor) {
                            moves.add(new ChessMove(myPosition, endPosition));
                        }
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
        Function<Integer, Integer> add_one = (i) -> i + 1;
        Function<Integer, Integer> min_one = (i) -> i - 1;
        Function<Integer, Integer> add_two = (i) -> i + 2;
        Function<Integer, Integer> min_two = (i) -> i - 2;
        Function<Integer, Integer> add_none = (i) -> i;

        if (this.pieceColor == ChessGame.TeamColor.WHITE) {
            if (board.getPiece(new ChessPosition(x + 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, add_one, add_none, x, y));
            } if (x == 2 && board.getPiece(new ChessPosition(x + 2, y)) == null && board.getPiece(new ChessPosition(x + 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, add_two, add_none, x, y));
            }
            if (this.checkBounds(x + 1, y + 1)) {
                var piece_1 = board.getPiece(new ChessPosition(x + 1, y + 1));
                if (piece_1 != null) {
                    if (piece_1.pieceColor == ChessGame.TeamColor.BLACK) {moves.addAll(pawnHelper(board, myPosition, add_one, add_one, x, y));}
                }
            }
            if (this.checkBounds(x + 1, y -1)) {
                var piece_2 = board.getPiece(new ChessPosition(x + 1, y - 1));
                if (piece_2 != null) {
                    if (piece_2.pieceColor == ChessGame.TeamColor.BLACK) {moves.addAll(pawnHelper(board, myPosition, add_one, min_one, x, y));}
                }
            }

        } else if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            if (board.getPiece(new ChessPosition(x - 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, min_one, add_none, x, y));
            } if (x == 7 && board.getPiece(new ChessPosition(x - 2, y)) == null && board.getPiece(new ChessPosition(x - 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, min_two, add_none, x, y));
            }
            if (this.checkBounds(x - 1, y + 1)) {
                var piece_1 = board.getPiece(new ChessPosition(x - 1, y + 1));
                if (piece_1 != null) {
                    if (piece_1.pieceColor == ChessGame.TeamColor.WHITE) {moves.addAll(pawnHelper(board, myPosition, min_one, add_one, x, y));}
                }
            }
            if (this.checkBounds(x - 1, y - 1)) {
                var piece_2 = board.getPiece(new ChessPosition(x - 1, y - 1));
                if (piece_2 != null) {
                    if (piece_2.pieceColor == ChessGame.TeamColor.WHITE) {moves.addAll(pawnHelper(board, myPosition, min_one, min_one, x, y));}
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnHelper (ChessBoard board, ChessPosition myPosition, Function<Integer, Integer> x_func, Function<Integer, Integer> y_func, int a, int b) {
        var moves = new ArrayList<ChessMove>();
        if (this.checkBounds(x_func.apply(a), y_func.apply(b))) {
            var endPosition = new ChessPosition(x_func.apply(a), y_func.apply(b));
            if (endPosition.getRow() == 8 || endPosition.getRow() == 1) {
                moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, endPosition));
            }
    }
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
