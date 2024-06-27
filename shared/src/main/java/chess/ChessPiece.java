package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import chess.ChessGame.TeamColor;

/**
 * Represents a single chess piece
 */
public class ChessPiece {
    private PieceType pieceType;
    private ChessGame.TeamColor teamColor;

    /**
     * Constructs a ChessPiece object given the color and piece type
     * 
     * @param pieceColor Color of the piece
     * @param type       The type of the piece
     */
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
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
        return teamColor;
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
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (pieceType) {
        case PAWN:
            return pawnMoves(board, myPosition);
        case BISHOP:
            return bishopMoves(board, myPosition);
        case KNIGHT:
        case KING:
        case QUEEN:
        case ROOK:
        default:
            throw new RuntimeException(String.format("Unknown piece type: %s", pieceType.toString()));
        }
    }

    /**
     * Calculates a pawns valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        // // Check to ensure the pawn won't (somehow) be moving off the board
        // if ((teamColor == TeamColor.WHITE && currentRow == 7) || (teamColor ==
        // TeamColor.WHITE && currentRow == 1)) {
        // return moves;
        // }
        int startRow = (teamColor == TeamColor.WHITE) ? 2 : 7;
        int direction = (teamColor == TeamColor.WHITE) ? 1 : -1;

        // Move Up
        ChessPosition upOne = new ChessPosition(currentRow + direction, currentCol);
        if (board.getPiece(upOne) == null) {
            moves.add(new ChessMove(myPosition, upOne));

            // Allowed to move up two if on starting row
            if (currentRow == startRow) {
                ChessPosition upTwo = new ChessPosition(currentRow + 2 * direction, currentCol);
                if (board.getPiece(upTwo) == null) {
                    moves.add(new ChessMove(myPosition, upTwo));
                }
            }
        }

        // Capture diagonally left
        ChessPosition captureLeft = new ChessPosition(currentRow + direction, currentCol - 1);
        ChessPiece pieceLeft = board.getPiece(captureLeft);
        if (pieceLeft != null && pieceLeft.getTeamColor() != teamColor) {
            moves.add(new ChessMove(myPosition, captureLeft));
        }

        // Capture diagonally right
        ChessPosition captureRight = new ChessPosition(currentRow + direction, currentCol + 1);
        ChessPiece pieceRight = board.getPiece(captureRight);
        if (pieceRight != null && pieceRight.getTeamColor() != teamColor) {
            moves.add(new ChessMove(myPosition, captureRight));
        }

        return moves;
    }

    /**
     * Calculates a bishops valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = { { 1, 1 }, // up-right
                { 1, -1 }, // up-left
                { -1, 1 }, // down-right
                { -1, -1 } // down-left
        };

        for (int[] direction : directions) {
            int rowChange = direction[0];
            int colChange = direction[1];
            int row = currentRow;
            int col = currentCol;
            while (true) {
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(newPosition);
                // if empty allowed to move there
                if (piece == null) {
                    moves.add(new ChessMove(myPosition, newPosition));
                }
                else {
                    // If enemy piece there, capture
                    if (piece.teamColor != teamColor) {
                        moves.add(new ChessMove(myPosition, newPosition));
                    }
                    // Can't jump over piece
                    break;
                }
            }
        }

        return moves;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pieceType == null) ? 0 : pieceType.hashCode());
        result = prime * result + ((teamColor == null) ? 0 : teamColor.hashCode());
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
        ChessPiece other = (ChessPiece) obj;
        if (pieceType != other.pieceType)
            return false;
        if (teamColor != other.teamColor)
            return false;
        return true;
    }

}
