package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var pos_moves = this.teamMoves(team);
        var kingPos = this.kingPosition(team);
        for (ChessMove move : pos_moves) {
            if (move.getEndPosition() == kingPos) {
                return true;
            }
        } return false;
    }

    /**
     * Returns all possible moves for a given team
     *
     * @param team which team to collect moves for
     * @return Collection of all possible moves
     */
    private Collection<ChessMove> teamMoves(ChessGame.TeamColor team) {
        var moves = new ArrayList<ChessMove>();
        var chessIterator = this.board.new BoardIterator();
        while (chessIterator.hasNext()) {
            ChessPosition pos = chessIterator.getPosition();
            var piece = chessIterator.next();
            if (piece.getTeamColor() == this.team) {
                moves.addAll(piece.pieceMoves(this.board, pos));
            }
        }
        return moves;
    }

    /**
     * Returns the position of the King, if king is not found returns null
     */
    private ChessPosition kingPosition(ChessGame.TeamColor team) {
        var chessIterator = this.board.new BoardIterator();
        while (chessIterator.hasNext()) {
            ChessPosition pos = chessIterator.getPosition();
            var piece = chessIterator.next();
            if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == this.team) {
                return pos;
            }
        } return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
