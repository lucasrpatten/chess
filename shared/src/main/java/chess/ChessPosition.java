package chess;

/**
 * Represents a single square position on a chess board
 */
public class ChessPosition {
    private int row;
    private int col;

    /**
     * Constructs a ChessPosition Object given row and column
     * 
     * @param row row number (1 for the bottom)
     * @param col column number (1 for the left)
     */
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }
}
