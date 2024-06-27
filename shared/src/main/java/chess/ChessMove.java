package chess;

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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((startPosition == null) ? 0 : startPosition.hashCode());
        result = prime * result + ((endPosition == null) ? 0 : endPosition.hashCode());
        result = prime * result + ((promotionPiece == null) ? 0 : promotionPiece.hashCode());
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
        ChessMove other = (ChessMove) obj;
        if (startPosition == null) {
            if (other.startPosition != null)
                return false;
        }
        else if (!startPosition.equals(other.startPosition))
            return false;
        if (endPosition == null) {
            if (other.endPosition != null)
                return false;
        }
        else if (!endPosition.equals(other.endPosition))
            return false;
        if (promotionPiece != other.promotionPiece)
            return false;
        return true;
    }

}
