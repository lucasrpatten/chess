package chess;

import java.util.Collection;
import java.util.HashSet;

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
            return knightMoves(board, myPosition);
        case KING:
            return kingMoves(board, myPosition);
        case QUEEN:
            return queenMoves(board, myPosition);
        case ROOK:
            return rookMoves(board, myPosition);
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
        int startRow = (teamColor == TeamColor.WHITE) ? 2 : 7;
        int direction = (teamColor == TeamColor.WHITE) ? 1 : -1;

        // Move Up
        ChessPosition upOne = new ChessPosition(currentRow + direction, currentCol);
        if (board.getPiece(upOne) == null) {
            // Check for promotion when reaching the last row
            if (upOne.getRow() == 1 || upOne.getRow() == 8) {
                addPromotionMoves(moves, myPosition, upOne);
            }
            else {
                moves.add(new ChessMove(myPosition, upOne));
            }

            // Allowed to move up two if on starting row
            if (currentRow == startRow) {
                ChessPosition upTwo = new ChessPosition(currentRow + 2 * direction, currentCol);
                if (board.getPiece(upTwo) == null) {
                    moves.add(new ChessMove(myPosition, upTwo));
                }
            }
        }

        // Capture diagonally left
        if (currentCol != 1) {
            ChessPosition captureLeft = new ChessPosition(currentRow + direction, currentCol - 1);
            ChessPiece pieceLeft = board.getPiece(captureLeft);
            if (pieceLeft != null && pieceLeft.getTeamColor() != teamColor) {
                // Check for promotion when capturing on the last row
                if (captureLeft.getRow() == 1 || captureLeft.getRow() == 8) {
                    addPromotionMoves(moves, myPosition, captureLeft);
                }
                else {
                    moves.add(new ChessMove(myPosition, captureLeft));
                }
            }
        }

        // Capture diagonally right
        if (currentCol != 8) {
            ChessPosition captureRight = new ChessPosition(currentRow + direction, currentCol + 1);
            ChessPiece pieceRight = board.getPiece(captureRight);
            if (pieceRight != null && pieceRight.getTeamColor() != teamColor) {
                // Check for promotion when capturing on the last row
                if (captureRight.getRow() == 1 || captureRight.getRow() == 8) {
                    addPromotionMoves(moves, myPosition, captureRight);
                }
                else {
                    moves.add(new ChessMove(myPosition, captureRight));
                }
            }
        }

        return moves;
    }

    /**
     * Helper method to add promotion moves for a pawn reaching the promotion rank.
     * 
     * @param moves         The collection of valid moves to add to
     * @param startPosition The starting position of the move
     * @param endPosition   The ending position of the move (promotion position)
     */
    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition startPosition,
            ChessPosition endPosition) {
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
    }

    /**
     * Calculates the possible moves for pieces that can move any number of spaces
     * in a certain direction
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @param directions The directions the piece can move continously
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> continuousMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        for (int[] direction : directions) {
            int rowChange = direction[0];
            int colChange = direction[1];
            int row = currentRow;
            int col = currentCol;
            while (true) {
                row += rowChange;
                col += colChange;
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

    /**
     * Calculates a bishops valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = { { 1, 1 }, // up-right
                { 1, -1 }, // up-left
                { -1, 1 }, // down-right
                { -1, -1 } // down-left
        };
        return continuousMoves(board, myPosition, directions);
    }

    /**
     * Calculates a rooks valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = { { 1, 0 }, // up
                { -1, 0 }, // down
                { 0, 1 }, // right
                { 0, -1 } // left
        };
        return continuousMoves(board, myPosition, directions);
    }

    /**
     * Calculates a queens valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = { { 1, 1 }, // up-right
                { 1, -1 }, // up-left
                { -1, 1 }, // down-right
                { -1, -1 }, // down-left
                { 1, 0 }, // up
                { -1, 0 }, // down
                { 0, 1 }, // right
                { 0, -1 } // left
        };
        return continuousMoves(board, myPosition, directions);
    }

    /**
     * Calculates valid moves given a list of possible moves (used for knight and
     * king)
     * 
     * @param board         The current chess board
     * @param myPosition    The pieces current position
     * @param possibleMoves The allowed moves of the piece
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> singleMoves(ChessBoard board, ChessPosition myPosition, int[][] possibleMoves) {
        HashSet<ChessMove> moves = new HashSet<>();

        // check possible moves
        for (int[] move : possibleMoves) {
            int row = move[0];
            int col = move[1];
            // Make sure move is on the board
            if (row < 1 || row > 8 || col < 1 || col > 8) {
                continue;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(newPosition);
            // Empty Square or Capture
            if (piece == null || piece.teamColor != teamColor) {
                moves.add(new ChessMove(myPosition, newPosition));
            }
        }
        return moves;
    }

    /**
     * Calculates a knights valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] possibleMoves = { { currentRow + 2, currentCol + 1 }, // up 2 right 1
                { currentRow + 2, currentCol - 1 }, // up 2 left 1
                { currentRow - 2, currentCol + 1 }, // down 2 right 1
                { currentRow - 2, currentCol - 1 }, // down 2 left 1
                { currentRow + 1, currentCol + 2 }, // right 2 up 1
                { currentRow + 1, currentCol - 2 }, // right 2 down 1
                { currentRow - 1, currentCol + 2 }, // left 2 up 1
                { currentRow - 1, currentCol - 2 } // left 2 down 1
        };
        return singleMoves(board, myPosition, possibleMoves);
    }

    /**
     * Calculates the kings valid moves
     * 
     * @param board      The current chess board
     * @param myPosition The pieces current position
     * @return HashSet of valid moves
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] possibleMoves = { { currentRow + 1, currentCol }, // up
                { currentRow - 1, currentCol }, // down
                { currentRow, currentCol + 1 }, // right
                { currentRow, currentCol - 1 }, // left
                { currentRow + 1, currentCol + 1 }, // up-right
                { currentRow + 1, currentCol - 1 }, // up-left
                { currentRow - 1, currentCol + 1 }, // down-right
                { currentRow - 1, currentCol - 1 } // down-left
        };

        return singleMoves(board, myPosition, possibleMoves);
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
