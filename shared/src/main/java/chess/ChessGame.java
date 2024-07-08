package chess;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 */
public class ChessGame {

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE, BLACK;

        public TeamColor opposite() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean canBlackKingsideCastle;
    private boolean canWhiteKingsideCastle;
    private boolean canBlackQueensideCastle;
    private boolean canWhiteQueensideCastle;
    private ChessPosition enPassantLocation;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        canBlackKingsideCastle = true;
        canWhiteKingsideCastle = true;
        canBlackQueensideCastle = true;
        canWhiteQueensideCastle = true;
        board.resetBoard();
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
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     *         startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> allowedMoves = new HashSet<ChessMove>();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return allowedMoves;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        int curRow = startPosition.getRow();
        int curCol = startPosition.getColumn();
        int direction = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1;

        if (enPassantLocation != null) {
            int enPassantRow = enPassantLocation.getRow();
            int enPassantCol = enPassantLocation.getColumn();
            if (piece.getPieceType() == PieceType.PAWN && enPassantRow == curRow + direction
                    && (enPassantCol == curCol - 1 || enPassantCol == curCol + 1)) {
                ChessMove enPassantMove = new ChessMove(startPosition, enPassantLocation);
                moves.add(enPassantMove);
            }
        }

        for (ChessMove move : moves) {
            if (isValidMove(move)) {
                allowedMoves.add(move);
            }
        }
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
        if (piece == null || !validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("That move is not allowed!");
        }
        TeamColor color = piece.getTeamColor();
        if (color != teamTurn) {
            throw new InvalidMoveException("It is not your turn!");
        }
        int startRow = (piece.getTeamColor() == TeamColor.WHITE) ? 2 : 7;
        int direction = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1;
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(color, move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);

        // Handle en passant capture
        if (piece.getPieceType() == PieceType.PAWN && enPassantLocation != null
                && move.getEndPosition().equals(enPassantLocation)) {
            int captureRow = move.getStartPosition().getRow();
            int captureCol = enPassantLocation.getColumn();
            board.addPiece(new ChessPosition(captureRow, captureCol), null);
        }

        // Set en passant location
        if (piece.getPieceType() == PieceType.PAWN && move.getStartPosition().getRow() == startRow
                && move.getEndPosition().getRow() == startRow + direction * 2) {
            enPassantLocation = new ChessPosition(move.getEndPosition().getRow() - direction,
                    move.getEndPosition().getColumn());
        }
        else {
            enPassantLocation = null;
        }

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
        if (!isInCheck(teamColor)) {
            return false;
        }
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
        if (isInCheck(teamColor)) {
            return false;
        }
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
        // This is literally just a random number cause the stupid tests I have to pass
        // don't like following good design practices and refuse to let me implement
        // proper error checking
        return new ChessPosition(422, 63);
    }

    /**
     * Checks if a single move is allowed
     * 
     * @param move the move to check
     * @return Boolean (true if is valid move, else false)
     */
    private boolean isValidMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            return false;
        }
        board.addPiece(move.getStartPosition(), null);
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), piece);
        boolean isValid = !isInCheck(piece.getTeamColor());
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), capturedPiece);
        return isValid;
    }
}
