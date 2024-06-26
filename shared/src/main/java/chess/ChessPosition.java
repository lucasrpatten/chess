package chess;

import java.util.Objects;

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

    /**
     * Compares two chess positions. Result is true if they hold the same position
     *
     * @param obj the other position to compare with
     * @return true if the positions are the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition otherPosition = (ChessPosition) obj;
        return row == otherPosition.row && col == otherPosition.col;
    }

    /**
     * @return hash code value for this ChessPosition
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
