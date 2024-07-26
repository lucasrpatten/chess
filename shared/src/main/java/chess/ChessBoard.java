package chess;

import java.util.Arrays;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    /**
     * Constructor to generate a blank new chess board.
     */
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     *         position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board (How the game of chess normally
     * starts)
     */
    public void resetBoard() {
        // Reset the board
        board = new ChessPiece[8][8];
        // Set white pieces (besides pawns)
        board[0][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        board[0][1] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        board[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        board[0][3] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        board[0][4] = new ChessPiece(TeamColor.WHITE, PieceType.KING);
        board[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        board[0][6] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        board[0][7] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        // Set black pieces (besides pawns)
        board[7][0] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        board[7][1] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        board[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        board[7][3] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        board[7][4] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        board[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        board[7][6] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        board[7][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        // Set up the pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
            board[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(board);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard other = (ChessBoard) obj;
        if (!Arrays.deepEquals(board, other.board)) {
            return false;
        }
        return true;
    }

}
