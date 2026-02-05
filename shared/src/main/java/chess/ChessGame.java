package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        this.board.resetBoard();
        this.team = TeamColor.WHITE;
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
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        var piece_color = piece.getTeamColor();
        var valid_moves = new ArrayList<ChessMove>();
        var moves = piece.pieceMoves(this.board, startPosition);
        for (var move : moves) {
            var test_board = new ChessBoard(this.board);
            var endPos = move.getEndPosition();
            test_board.addPiece(startPosition, null);
            test_board.addPiece(endPos, piece);
            if (!isInCheck_test(piece_color, test_board)) {
                valid_moves.add(move);
            }
        }
        return valid_moves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        TeamColor oppo_color;
        if (teamColor == TeamColor.WHITE) {
            oppo_color = TeamColor.BLACK;
        } else {
            oppo_color = TeamColor.WHITE;
        }

        var pos_moves = this.teamMoves(oppo_color, this.board);
        var kingPos = this.kingPosition(teamColor, this.board);
        for (ChessMove move : pos_moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        } return false;
    }

    public boolean isInCheck_test(TeamColor teamColor, ChessBoard gameboard) {
        TeamColor oppo_color;
        if (teamColor == TeamColor.WHITE) {
            oppo_color = TeamColor.BLACK;
        } else {
            oppo_color = TeamColor.WHITE;
        }

        var pos_moves = this.teamMoves(oppo_color, gameboard);
        var kingPos = this.kingPosition(teamColor, gameboard);
        for (ChessMove move : pos_moves) {
            if (move.getEndPosition().equals(kingPos)) {
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
    private Collection<ChessMove> teamMoves(ChessGame.TeamColor team, ChessBoard gameboard) {
        var moves = new ArrayList<ChessMove>();
        var chessIterator = gameboard.iterator();
        while (chessIterator.hasNext()) {
            ChessPosition pos = chessIterator.getPosition();
            var piece = chessIterator.next();
            if (piece == null) {
                continue;
            } if (piece.getTeamColor() == team) {
                moves.addAll(piece.pieceMoves(gameboard, pos));
            }
        }
        return moves;
    }

    /**
     * Returns the position of the King, if king is not found returns null
     */
    private ChessPosition kingPosition(ChessGame.TeamColor team, ChessBoard gameboard) {
        var chessIterator = gameboard.iterator();
        while (chessIterator.hasNext()) {
            ChessPosition pos = chessIterator.getPosition();
            var piece = chessIterator.next();
            if (piece == null) {
                continue;
            } if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == team) {
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
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, getBoard());
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "team=" + team +
                ", board=" + board +
                '}';
    }
}
