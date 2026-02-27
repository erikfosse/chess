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
            case ROOK -> this.rook(board, myPosition);
            case BISHOP -> this.bishop(board, myPosition);
            case KNIGHT -> this.knight(board, myPosition);
            case KING -> this.king(board, myPosition);
            case QUEEN -> this.queen(board, myPosition);
            case PAWN -> this.pawn(board, myPosition);
            case null, default -> throw new RuntimeException("Not implemented");
        };
    }

    private Collection<ChessMove> bishop(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        Function<Integer, Integer> addOne = (i) -> i + 1;
        Function<Integer, Integer> minOne = (i) -> i - 1;
        moves.addAll(bishopRookHelper(board, myPosition, addOne, addOne, x, y));
        moves.addAll(bishopRookHelper(board, myPosition, addOne, minOne, x, y));
        moves.addAll(bishopRookHelper(board, myPosition, minOne, addOne, x, y));
        moves.addAll(bishopRookHelper(board, myPosition, minOne, minOne, x, y));
        return moves;
    }

    private Collection<ChessMove> rook(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        Function<Integer, Integer> addOne = (i) -> i + 1;
        Function<Integer, Integer> minOne = (i) -> i - 1;
        Function<Integer, Integer> addNone = (i) -> i;
        moves.addAll(bishopRookHelper(board, myPosition, addOne, addNone, x, y));
        moves.addAll(bishopRookHelper(board, myPosition, addNone, addOne, x, y));
        moves.addAll(bishopRookHelper(board, myPosition, minOne, addNone, x, y));
        moves.addAll(bishopRookHelper(board, myPosition, addNone, minOne, x, y));
        return moves;
    }

    private Collection<ChessMove> bishopRookHelper(ChessBoard board, ChessPosition myPosition, Function<Integer,
                                                   Integer> xFunc, Function<Integer, Integer> yFunc, int a, int b) {
        var moves = new ArrayList<ChessMove>();
        while (this.checkBounds(xFunc.apply(a), yFunc.apply(b))) {
            var endPosition = new ChessPosition(xFunc.apply(a), yFunc.apply(b));
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(myPosition,endPosition));
                a = xFunc.apply(a); b = yFunc.apply(b);
            } else {
                if (pieceColor != board.getPiece(endPosition).pieceColor) {
                    moves.add(new ChessMove(myPosition, endPosition));
                }
                break;
            }
        };
        return moves;
    }


    private Collection<ChessMove> knight(ChessBoard board, ChessPosition myPosition) {
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
            var x0 = pos.getRow();
            var y0 = pos.getColumn();
            if (this.checkBounds(x0, y0)) {
                var endPosition = new ChessPosition(x0, y0);
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

    private Collection<ChessMove> king(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (this.checkBounds(i, j)) {
                    var endPosition = new ChessPosition(i, j);
                    if (board.getPiece(endPosition) == null) {
                        moves.add(new ChessMove(myPosition,endPosition));
                    } else if ((pieceColor != board.getPiece(endPosition).pieceColor)) {
                        moves.add(new ChessMove(myPosition, endPosition));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> queen(ChessBoard board, ChessPosition myPosition) {
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        var moves = new ArrayList<ChessMove>(this.bishop(board, myPosition));
        moves.addAll(this.rook(board, myPosition));
        return moves;
    }

    private Collection<ChessMove> pawn(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var x = myPosition.getRow();
        var y = myPosition.getColumn();
        Function<Integer, Integer> addOne = (i) -> i + 1;
        Function<Integer, Integer> minOne = (i) -> i - 1;
        Function<Integer, Integer> addTwo = (i) -> i + 2;
        Function<Integer, Integer> minTwo = (i) -> i - 2;
        Function<Integer, Integer> addNone = (i) -> i;

        if (this.pieceColor == ChessGame.TeamColor.WHITE) {
            if (board.getPiece(new ChessPosition(x + 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, addOne, addNone, x, y));
            } if (x == 2 && board.getPiece(new ChessPosition(x + 2, y)) == null && board.getPiece(new ChessPosition(x + 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, addTwo, addNone, x, y));
            }
            if (this.checkBounds(x + 1, y + 1)) {
                var piece1 = board.getPiece(new ChessPosition(x + 1, y + 1));
                if (piece1 != null) {
                    if (piece1.pieceColor == ChessGame.TeamColor.BLACK) {moves.addAll(pawnHelper(board, myPosition, addOne, addOne, x, y));}
                }
            }
            if (this.checkBounds(x + 1, y -1)) {
                var piece2 = board.getPiece(new ChessPosition(x + 1, y - 1));
                if (piece2 != null) {
                    if (piece2.pieceColor == ChessGame.TeamColor.BLACK) {moves.addAll(pawnHelper(board, myPosition, addOne, minOne, x, y));}
                }
            }

        } else if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            if (board.getPiece(new ChessPosition(x - 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, minOne, addNone, x, y));
            } if (x == 7 && board.getPiece(new ChessPosition(x - 2, y)) == null && board.getPiece(new ChessPosition(x - 1, y)) == null) {
                moves.addAll(pawnHelper(board, myPosition, minTwo, addNone, x, y));
            }
            if (this.checkBounds(x - 1, y + 1)) {
                var piece1 = board.getPiece(new ChessPosition(x - 1, y + 1));
                if (piece1 != null) {
                    if (piece1.pieceColor == ChessGame.TeamColor.WHITE) {moves.addAll(pawnHelper(board, myPosition, minOne, addOne, x, y));}
                }
            }
            if (this.checkBounds(x - 1, y - 1)) {
                var piece2 = board.getPiece(new ChessPosition(x - 1, y - 1));
                if (piece2 != null) {
                    if (piece2.pieceColor == ChessGame.TeamColor.WHITE) {moves.addAll(pawnHelper(board, myPosition, minOne, minOne, x, y));}
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnHelper (ChessBoard board, ChessPosition myPosition, Function<Integer,
                                              Integer> xFunc, Function<Integer, Integer> yFunc, int a, int b) {
        var moves = new ArrayList<ChessMove>();
        if (this.checkBounds(xFunc.apply(a), yFunc.apply(b))) {
            var endPosition = new ChessPosition(xFunc.apply(a), yFunc.apply(b));
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
