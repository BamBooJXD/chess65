package chess;

/**
 *
 * @author Xudong Jiang
 * @author Yuting Chen
 */
public class Knight extends Piece{

    public Knight(Boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Boolean isValidMove(Board b, Position s, Position e) {
        if (b.getPiece(e) != null && b.getPiece(s).isWhite == b.getPiece(e).isWhite) {
            return false;
        }

        int diffX = Math.abs(s.x - e.x);
        int diffY = Math.abs(s.y - e.y);
        return diffX * diffY == 2;
    }

    @Override
    public void applyMove(Board b, Position s, Position e) {
    }

    @Override
    public String toString() {
        return super.toString()+"N"; 
    }
    
    
}
