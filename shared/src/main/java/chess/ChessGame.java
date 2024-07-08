package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

        // En passant
        if (enPassantLocation != null) {
            int enPassantRow = enPassantLocation.getRow();
            int enPassantCol = enPassantLocation.getColumn();
            if (piece.getPieceType() == PieceType.PAWN && enPassantRow == curRow + direction
                    && (enPassantCol == curCol - 1 || enPassantCol == curCol + 1)) {
                ChessMove enPassantMove = new ChessMove(startPosition, enPassantLocation);
                moves.add(enPassantMove);
            }
        }

        // Castling
        if (piece.getPieceType() == PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE && board.getPiece(new ChessPosition(1, 5)) != null
                    && board.getPiece(new ChessPosition(1, 5)).getPieceType().equals(PieceType.KING)) {
                if (board.getPiece(new ChessPosition(1, 8)) != null
                        && board.getPiece(new ChessPosition(1, 8)).getPieceType().equals(PieceType.ROOK)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(1, 7)));
                }
                if (board.getPiece(new ChessPosition(1, 1)) != null
                        && board.getPiece(new ChessPosition(1, 1)).getPieceType().equals(PieceType.ROOK)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(1, 3)));
                }
            }
            else if (board.getPiece(new ChessPosition(8, 5)) != null
                    && board.getPiece(new ChessPosition(8, 5)).getPieceType().equals(PieceType.KING)) {
                if (board.getPiece(new ChessPosition(8, 8)) != null
                        && board.getPiece(new ChessPosition(8, 8)).getPieceType().equals(PieceType.ROOK)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(8, 7)));
                }
                if (board.getPiece(new ChessPosition(8, 1)) != null
                        && board.getPiece(new ChessPosition(8, 1)).getPieceType().equals(PieceType.ROOK)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(8, 3)));
                }
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

        // Handle en passant capture
        if (piece.getPieceType() == PieceType.PAWN && enPassantLocation != null
                && move.getEndPosition().equals(enPassantLocation)) {
            int captureRow = move.getStartPosition().getRow();
            int captureCol = enPassantLocation.getColumn();
            board.addPiece(new ChessPosition(captureRow, captureCol), null);
        }

        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(color, move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);

        // Set en passant location
        if (piece.getPieceType() == PieceType.PAWN && move.getStartPosition().getRow() == startRow
                && move.getEndPosition().getRow() == startRow + direction * 2) {
            enPassantLocation = new ChessPosition(move.getEndPosition().getRow() - direction,
                    move.getEndPosition().getColumn());
        }
        else {
            enPassantLocation = null;
        }

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        boolean isWhite = piece.getTeamColor() == TeamColor.WHITE;
        ChessPosition kingStartPosition = isWhite ? new ChessPosition(1, 5) : new ChessPosition(8, 5);
        ChessPosition kingsideKingEndPosition = isWhite ? new ChessPosition(1, 7) : new ChessPosition(8, 7);
        ChessPosition queensideKingEndPosition = isWhite ? new ChessPosition(1, 3) : new ChessPosition(8, 3);
        ChessPosition kingsideRookStartPosition = isWhite ? new ChessPosition(1, 8) : new ChessPosition(8, 8);
        ChessPosition queensideRookStartPosition = isWhite ? new ChessPosition(1, 1) : new ChessPosition(8, 1);
        ChessPosition kingsideRookEndPosition = isWhite ? new ChessPosition(1, 6) : new ChessPosition(8, 6);
        ChessPosition queensideRookEndPosition = isWhite ? new ChessPosition(1, 4) : new ChessPosition(8, 4);

        if (startPosition.equals(kingStartPosition)) {
            if (isWhite) {
                canWhiteKingsideCastle = false;
                canWhiteQueensideCastle = false;
            }
            else {
                canBlackKingsideCastle = false;
                canBlackQueensideCastle = false;
            }
        }
        else if (startPosition.equals(kingsideRookStartPosition)) {
            if (isWhite) {
                canWhiteKingsideCastle = false;
            }
            else {
                canBlackKingsideCastle = false;
            }
        }
        else if (startPosition.equals(queensideRookStartPosition)) {
            if (isWhite) {
                canWhiteQueensideCastle = false;
            }
            else {
                canBlackQueensideCastle = false;
            }
        }

        // Handle castling
        if (piece.getPieceType() == PieceType.KING) {
            if (startPosition.equals(kingStartPosition)) {
                if (endPosition.equals(kingsideKingEndPosition)) {
                    // Move the rook for kingside castling
                    ChessPiece rook = board.getPiece(kingsideRookStartPosition);
                    board.addPiece(kingsideRookStartPosition, null);
                    board.addPiece(kingsideRookEndPosition, rook);
                }
                else if (endPosition.equals(queensideKingEndPosition)) {
                    // Move the rook for queenside castling
                    ChessPiece rook = board.getPiece(queensideRookStartPosition);
                    board.addPiece(queensideRookStartPosition, null);
                    board.addPiece(queensideRookEndPosition, rook);
                }
            }
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

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        boolean isWhite = piece.getTeamColor() == TeamColor.WHITE;
        ChessPosition kingStartPosition = isWhite ? new ChessPosition(1, 5) : new ChessPosition(8, 5);
        ChessPosition kingsideKingEndPosition = isWhite ? new ChessPosition(1, 7) : new ChessPosition(8, 7);
        ChessPosition queensideKingEndPosition = isWhite ? new ChessPosition(1, 3) : new ChessPosition(8, 3);

        if (piece.getPieceType() == ChessPiece.PieceType.KING && startPosition.equals(kingStartPosition)
                && (endPosition.equals(kingsideKingEndPosition))) {
            if (isInCheck(piece.getTeamColor())) {
                return false;
            }
            ChessPosition position = new ChessPosition(startRow, 6);
            if (board.getPiece(position) != null || board.getPiece(new ChessPosition(startRow, 7)) != null
                    || !isValidMove(new ChessMove(startPosition, position))) {
                return false;
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING && startPosition.equals(kingStartPosition)
                && (endPosition.equals(queensideKingEndPosition))) {
            if (isInCheck(piece.getTeamColor())) {
                return false;
            }
            ChessPosition position1 = new ChessPosition(startRow, 4);
            ChessPosition position2 = new ChessPosition(startRow, 2);
            if (board.getPiece(position1) != null || board.getPiece(position2) != null
                    || board.getPiece(new ChessPosition(startRow, 3)) != null
                    || !isValidMove(new ChessMove(startPosition, position1))
                    || !isValidMove(new ChessMove(startPosition, position2))) {
                return false;
            }
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
