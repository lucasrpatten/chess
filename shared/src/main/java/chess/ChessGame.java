package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * For a class that can manage a chess game, making moves on a board
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private Boolean whiteKingMoved;
    private Boolean blackKingMoved;
    private Boolean whiteKingsideRookMoved;
    private Boolean whiteQueensideRookMoved;
    private Boolean blackKingsideRookMoved;
    private Boolean blackQueensideRookMoved;
    private ChessPosition[] enPassantPositions;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteKingsideRookMoved = false;
        whiteQueensideRookMoved = false;
        whiteKingsideRookMoved = false;
        blackQueensideRookMoved = false;
        enPassantPositions = new ChessPosition[2];
        board.resetBoard();
    }

    public ChessGame(ChessBoard board, TeamColor teamTurn, Boolean whiteKingMoved, Boolean blackKingMoved,
            Boolean whiteKingsideRookMoved, Boolean whiteQueensideRookMoved, Boolean blackKingsideRookMoved,
            Boolean blackQueensideRookMoved, ChessPosition[] enPassantPositions) {
        this.board = board;
        this.teamTurn = teamTurn;
        this.whiteKingMoved = whiteKingMoved;
        this.blackKingMoved = blackKingMoved;
        this.whiteKingsideRookMoved = whiteKingsideRookMoved;
        this.whiteQueensideRookMoved = whiteQueensideRookMoved;
        this.blackKingsideRookMoved = blackKingsideRookMoved;
        this.blackQueensideRookMoved = blackQueensideRookMoved;
        this.enPassantPositions = enPassantPositions;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE, BLACK;

        public TeamColor opposite() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     *         startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new HashSet<>();
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        HashSet<ChessMove> allowedMoves = moves.stream().filter(move -> isValidMove(move))
                .collect(Collectors.toCollection(HashSet::new));
        return allowedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        TeamColor color = piece.getTeamColor();
        if (color != teamTurn || !isValidMove(move)) {
            throw new InvalidMoveException("It is not your turn!");
        }
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);
        teamTurn = teamTurn.opposite();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        for (int i = 1; i <= 8; ++i) {
            for (int j = 1; j <= 8; ++j) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor().opposite() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 1; i <= 8; ++i) {
            for (int j = 1; j <= 8; ++j) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves.size() > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
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
        return board;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((teamTurn == null) ? 0 : teamTurn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessGame other = (ChessGame) obj;
        if (board == null) {
            if (other.board != null)
                return false;
        }
        else if (!board.equals(other.board))
            return false;
        if (teamTurn != other.teamTurn)
            return false;
        return true;
    }

    /**
     * Gets the location of the king of the specified team
     *
     * @param teamColor which team's king to get
     * @return the location of the king
     * @throws IllegalStateException if the king isn't on the board for
     */
    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                if (board.getPiece(currentPosition) != null
                        && board.getPiece(currentPosition).getTeamColor() == teamColor
                        && board.getPiece(currentPosition).getPieceType() == ChessPiece.PieceType.KING) {
                    return currentPosition;
                }
            }
        }
        throw new IllegalStateException("Could not find king for team " + teamColor.toString());
    }

    /**
     * Checks if a single move is allowed
     * 
     * @param move the move to check
     * @return Boolean (true if is valid move, else false)
     */
    private boolean isValidMove(ChessMove move) {
        ChessGame game = new ChessGame(board, teamTurn, whiteKingMoved, blackKingMoved, whiteKingsideRookMoved,
                whiteQueensideRookMoved, blackKingsideRookMoved, blackQueensideRookMoved, enPassantPositions);
        ChessPiece piece = game.board.getPiece(move.getStartPosition());
        if (piece == null) {
            return false;
        }
        TeamColor color = piece.getTeamColor();
        game.board.addPiece(move.getStartPosition(), null);
        game.board.addPiece(move.getEndPosition(), piece);
        if (game.isInCheck(color)) {
            return false;
        }
        return true;
    }
}
