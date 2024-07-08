package chess;

import java.util.Objects;

import chess.ChessPiece.PieceType;

/**
 * Represents moving a chess piece on a chessboard
 */
public class ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private PieceType promotionPiece;

    /**
     * @param startPosition  Starting Position
     * @param endPosition    Ending Position
     * @param promotionPiece Type of piece to promote a pawn to if pawn promotion is
     *                       part of the chess move
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @param startPosition Starting Position
     * @param endPosition   Ending Position
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessMove other = (ChessMove) obj;
        return Objects.equals(startPosition, other.startPosition) && Objects.equals(endPosition, other.endPosition)
                && promotionPiece == other.promotionPiece;
    }

}
