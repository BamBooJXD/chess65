package chess;

/**
 *
 * @author Xudong Jiang
 * @author Yuting Chen
 */
public class Pawn extends Piece {

    /**
     * A boolean indicates if the last move of the previous turn of this piece
     * was a two step forward.
     */
    private Boolean twoStep = false;

    /**
     * A booean indicates if this is a promotion move.
     */
    private Boolean isPromotion = false;

    public Boolean getIsPromotion() {
        return isPromotion;
    }

    public Pawn(Boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Boolean isValidMove(Board b, Position s, Position e) {
        Position moveUp;
        Position twoMoveUp;
        Position rDiag;
        Position lDiag;
        int firstFileX;
        if (isWhite) {
            moveUp = new Position(s.x - 1, s.y);
            twoMoveUp = new Position(s.x - 2, s.y);
            rDiag = new Position(s.x - 1, s.y + 1);
            lDiag = new Position(s.x - 1, s.y - 1);
            firstFileX = 6;
        } else {
            moveUp = new Position(s.x + 1, s.y);
            twoMoveUp = new Position(s.x + 2, s.y);
            rDiag = new Position(s.x + 1, s.y + 1);
            lDiag = new Position(s.x + 1, s.y - 1);
            firstFileX = 1;
        }

        if (e.equals(moveUp)) {
            // one move up (or down)
            if (b.getPiece(e) == null) {
                return true;
            }
        } else if (e.equals(twoMoveUp)) {
            // two move up (or down)
            if (s.x == firstFileX && b.getPiece(e) == null
                    && b.getPiece(moveUp) == null) {
                return true;
            }
        } else if (e.equals(rDiag) || e.equals(lDiag)) {
            // capture or enpassant
            Position passantPiece = (b.getPiece(s).isWhite) ?
                    new Position(e.x + 1, e.y) : new Position(e.x - 1, e.y);

            if (b.getPiece(e) == null
                    && b.getPiece(passantPiece) != null
                    && b.getPiece(passantPiece).isWhite != b.getPiece(s).isWhite
                    && b.getPiece(passantPiece).getClass().getName().equals("chess.Pawn")
                    && ((Pawn) b.getPiece(passantPiece)).twoStep) {
                // enpassant
                return true;
            } else if (b.getPiece(e) != null
                    && b.getPiece(e).isWhite != b.getPiece(s).isWhite) {
                // capture
                return true;
            }
        }
        return false;
    }

    @Override
    public void applyMove(Board b, Position s, Position e) {
        twoStep = isPromotion = false;

        Position twoMoveUp;
        int lastFile;

        if (isWhite) {
            twoMoveUp = new Position(s.x - 2, s.y);
            lastFile = 0;
        } else {
            twoMoveUp = new Position(s.x + 2, s.y);
            lastFile = 7;
        }

        if (e.equals(twoMoveUp)) {
            // two move up (or down)
            twoStep = true;
        }
        if (e.x == lastFile) {
            isPromotion = true;
        }
    }

    @Override
    public String toString() {
        return super.toString() + 'P';
    }

    public void setTwoStep(Boolean twoStep) {
        this.twoStep = twoStep;
    }

    public Boolean getTwoStep() {
        return twoStep;
    }

}
