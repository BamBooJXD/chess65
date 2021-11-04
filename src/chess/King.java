package chess;

import java.util.stream.IntStream;

/**
 *
 * @author Xudong Jiang
 * @author Yuting Chen
 */
public class King extends Piece {

    /**
     * A boolean indicates if this king has castled.
     */
    private Boolean isCastled;
    /**
     * A boolean indicates if this king has moved.
     */
    private Boolean isMoved = false;

    public King(Boolean isWhite) {
        super(isWhite);
        isCastled = false;
    }

    @Override
    public Boolean isValidMove(Board b, Position s, Position e) {
        // check that we don't move to a place where piece is with the same 
        // color already there.
        if (b.getPiece(e) != null && b.getPiece(s).isWhite == b.getPiece(e).isWhite) {
            return false;
        }

        // check regular move
        int diffX = Math.abs(s.x - e.x);
        int diffY = Math.abs(s.y - e.y);
        if (diffX + diffY == 1) {
            Position directions[] = {
                new Position(-1, -1),
                new Position(-1, +1),
                new Position(1, 1),
                new Position(1, -1),
                new Position(0, 1),
                new Position(0, -1),
                new Position(1, 0),
                new Position(-1, 0)
            };
            for (Position direction : directions) {
                if (b.getPiece(e.x + direction.x, e.y + direction.y) != null
                        && b.getPiece(e.x + direction.x, e.y + direction.y)
                                .getClass().getName().equals("chess.King")) {
                    if(!new Position(e.x+direction.x, e.y+direction.y).equals(s)){
                        return false;
                    }
                }
            }
            return true;
        }

        return isCastling(b, s, e);
    }

    @Override
    public String toString() {
        return super.toString() + 'K';
    }

    private Boolean isCastling(Board b, Position s, Position e) {
        if (isCastled || isMoved) {
            return false;
        }

        // check if correct moves
        int diffX = Math.abs(s.x - e.x);
        int diffY = Math.abs(s.y - e.y);
        if (diffY != 2 || diffX != 0) {
            return false;
        }

        // check if correct start positions
        if (isWhite) {
            if (!s.equals(new Position(7, 4))) {
                return false;
            }
        } else {
            if (!s.equals(new Position(0, 4))) {
                return false;
            }
        }

        // check squares between the rook and the knight is unoccupied and unchecked
        // right castle
        for (int i : IntStream.range(s.y + 1, e.y + 1).toArray()) {
            if (b.getPiece(s.x, i) != null || b.inCheck(new Position(s.x, i), !isWhite)) {
                return false;
            }
        }

        //left castle
        for (int i : IntStream.range(e.y - 1, s.y).toArray()) {
            if (b.getPiece(s.x, i) != null || b.inCheck(new Position(s.x, i), !isWhite)) {
                return false;
            }
        }

        // check if the the destination.y + 1 is a rook and a not-moved one
        if (s.y < e.y) {
            // right castle
            if (b.getPiece(e.x, e.y + 1) != null
                    && b.getPiece(e.x, e.y + 1).getClass().getName().equals("chess.Rook")
                    && !((Rook) b.getPiece(e.x, e.y + 1)).hasMoved()) {
                return true;
            }
        } else if (b.getPiece(s.x, e.y - 2) != null
                && b.getPiece(s.x, e.y - 2).getClass().getName().equals("chess.Rook")
                && !((Rook) b.getPiece(s.x, e.y - 2)).hasMoved()) {
            return true;
        }
        return false;
    }

    @Override
    public void applyMove(Board b, Position s, Position e) {
        int diffX = Math.abs(s.x - e.x);
        int diffY = Math.abs(s.y - e.y);
        if (diffX == 0 || diffY == 2) {
            isCastled = true;
        }
        isMoved = true;
    }

}
