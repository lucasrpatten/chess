package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 */
public class ChessPiece {
    private PieceType pieceType;
    private ChessGame.TeamColor pieceColor;

    /**
     * Constructs a ChessPiece object given the color and piece type
     * 
     * @param pieceColor Color of the piece
     * @param type       The type of the piece
     */
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
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
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to Does not take into
     * account moves that are illegal due to leaving the king in danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }
}
