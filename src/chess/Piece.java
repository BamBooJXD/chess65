package chess;

/**
 * @author Xudong Jiang
 * @author Yuting Chen
 */
public abstract class Piece {

    /**
     * A boolean indicates the color of the piece.
     */
    protected Boolean isWhite;

    /**
     * Initiate piece with the specified color
     * @param isWhite the color of the piece
     */
    public Piece(Boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * This function return true if the move from s to e is valid regardless of
     * the check of the king. This function only check for validity and does not
     * change any variables in the class.
     *
     * @param b the current board
     * @param s the starting position of the move
     * @param e the end position of the move
     * @return true if this is a valid move regardless of the king check.
     */
    public abstract Boolean isValidMove(Board b, Position s, Position e);

    /**
     * Apply move from position s to position e. putting all required flags by
     * the moving pieces.
     *
     * @param b the current board.
     * @param s the starting position.
     * @param e the ending position.
     */
    public abstract void applyMove(Board b, Position s, Position e);

    @Override
    public String toString() {
        return (isWhite) ? "w" : "b";
    }

    /**
     * Calculate number of pieces between s and e inclusively.
     *
     * @param b the board.
     * @param s the starting position.
     * @param e the ending position.
     * @return number of pieces between s and e inclusively
     */
    static protected int numberOfPiecesBetween(Board b, Position s, Position e) {
        int diffx, diffy;
        if (e.x - s.x == 0) {
            diffx = 0;
        } else {
            diffx = (e.x - s.x) / Math.abs(e.x - s.x);
        }
        if ((e.y - s.y) == 0) {
            diffy = 0;
        } else {
            diffy = (e.y - s.y) / Math.abs(e.y - s.y);
        }

        Position vector = new Position(diffx, diffy);
        Position cur = new Position(-1, -1);
        cur.x = s.x;
        cur.y = s.y;
        int res = 0;
        while (!cur.equals(e)) {
            if (b.getPiece(cur) != null) {
                res++;
            }
            cur.x += vector.x;
            cur.y += vector.y;
        }
        if (b.getPiece(cur) != null) {
            res++;
        }
        return res;
    }
}
