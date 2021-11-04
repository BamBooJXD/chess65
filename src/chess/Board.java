package chess;

/**
 *
 * @author Xudong Jiang
 * @author Yuting Chen
 */
public class Board {

    /**
     * This is the board pieces in a 2d array.
     */
    final private Piece board[][] = new Piece[8][8];
    /**
     * This is the next promotion piece got from user.
     */
    private char nextPromotion = 'Q';

    /**
     * This constructor creates the board in initial state.
     *
     * @param x just a variable to distict from the other constructor
     */
    public Board(boolean x) {
        resetBoard();
    }

    public Board() {
    }

    /**
     * Resets the board to the initial state.
     */
    private void resetBoard() {
        board[0][0] = new Rook(false);
        board[0][7] = new Rook(false);
        board[0][1] = new Knight(false);
        board[0][6] = new Knight(false);
        board[0][2] = new Bishop(false);
        board[0][5] = new Bishop(false);
        board[0][3] = new Queen(false);
        board[0][4] = new King(false);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false);
        }

        board[7][0] = new Rook(true);
        board[7][7] = new Rook(true);
        board[7][1] = new Knight(true);
        board[7][6] = new Knight(true);
        board[7][2] = new Bishop(true);
        board[7][5] = new Bishop(true);
        board[7][3] = new Queen(true);
        board[7][4] = new King(true);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(true);
        }
    }

    /**
     * This function returns true if the move given in strings is valid move.
     *
     * @param s the starting position in string
     * @param e the ending position in string.
     * @param inturn a boolean indicates the color of the playing player
     * @return true if the move given in strings is valid move.
     */
    public boolean checkMove(String s, String e, Boolean inturn) {
        return checkMoveHelper(new Position(s), new Position(e), inturn);
    }

    /**
     * This function apply a valid move from s to e on the board. it takes the
     * position as Strings.
     *
     * @param s the starting position in String.
     * @param e the ending position in String.
     */
    public void applyMove(String s, String e) {
        applyMoveHelper(new Position(s), new Position(e));
    }

    /**
     * Set the type of next promotion piece.
     *
     * @param nextPromotion the type of the next promotion.
     */
    public void setNextPromotion(char nextPromotion) {
        if (nextPromotion == 'Q' || nextPromotion == 'B' || nextPromotion == 'R'
                || nextPromotion == 'N') {
            this.nextPromotion = nextPromotion;
        }
        else{
            this.nextPromotion = 'Q';
        }
    }

    public char getNextPromotion() {
        return nextPromotion;
    }

    /**
     * Returns true if this is a valid position on a chess board.
     *
     * @param p the position to be checked.
     * @return true if this is a valid position on a chess board.
     */
    private boolean isValidPos(Position p) {
        return p.x >= 0 && p.x <= 7 && p.y >= 0 && p.y <= 7;
    }

    /**
     * This function apply a valid move from s to e on the board.
     *
     * @param s the starting position.
     * @param e the ending position.
     */
    private void applyMoveHelper(Position s, Position e) {
        board[s.x][s.y].applyMove(this, s, e);
        int diffX = Math.abs(s.x - e.x);
        int diffY = Math.abs(s.y - e.y);

        // if castling
        if (board[s.x][s.y].getClass().getName().equals("chess.King")
                && diffY == 2) {
            board[e.x][e.y] = board[s.x][s.y];
            board[s.x][s.y] = null;
            // if right castling
            if (s.y < e.y) {
                board[e.x][e.y - 1] = board[e.x][e.y + 1];
                board[e.x][e.y + 1] = null;
            } else {
                board[e.x][e.y + 1] = board[e.x][e.y - 2];
                board[e.x][e.y - 2] = null;
            }
        } // else if enpassant
        else if (board[s.x][s.y].getClass().getName().equals("chess.Pawn")
                && diffX == diffY && board[e.x][e.y] == null) {
            Position passantPiece = (board[s.x][s.y].isWhite) ? new Position(e.x + 1, e.y) : new Position(e.x - 1, e.y);

            board[e.x][e.y] = board[s.x][s.y];
            board[s.x][s.y] = null;
            board[passantPiece.x][passantPiece.y] = null;
        } // else if regular move
        else {
            board[e.x][e.y] = board[s.x][s.y];
            board[s.x][s.y] = null;
        }
        // check if promotion
        if (board[e.x][e.y].getClass().getName().equals("chess.Pawn")
                && ((Pawn) board[e.x][e.y]).getIsPromotion()) {
            board[e.x][e.y] = generatePromoted(board[e.x][e.y].isWhite);
            nextPromotion = 'Q';
        }

        // reset all the two steps flag in the all the opponent's pawns
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null
                        && board[i][j].isWhite != board[e.x][e.y].isWhite
                        && board[i][j].getClass().getName().equals("chess.Pawn")) {
                    ((Pawn) (board[i][j])).setTwoStep(false);
                }
            }
        }
    }

    /**
     * This function returns true if generally this is a legal move. from the
     * prespictive of the piece and the general rules of the board.
     *
     * @param s the position of the current piece.
     * @param e the position we want to set the piece in.
     * @return true if move is legal.
     */
    private Boolean checkMoveHelper(Position s, Position e, Boolean color) {
        // check invalid start or end
        if (!isValidPos(s) || !isValidPos(e)) {
            return false;
        }
        if (board[s.x][s.y] == null || board[s.x][s.y].isWhite != color) {
            return false;
        }
        // check the destination is not a king (can't capture any of the kings)
        if (board[e.x][e.y] != null && board[e.x][e.y].getClass().getName().equals("chess.King")) {
            return false;
        }
        // create temp state to check if check would happen to the player 
        // if moved this move
        Piece captured = board[e.x][e.y];
        board[e.x][e.y] = board[s.x][s.y];
        board[s.x][s.y] = null;
        if (inCheck(board[e.x][e.y].isWhite)) {
            board[s.x][s.y] = board[e.x][e.y];
            board[e.x][e.y] = captured;
            return false;
        }

        board[s.x][s.y] = board[e.x][e.y];
        board[e.x][e.y] = captured;
        return board[s.x][s.y].isValidMove(this, s, e);
    }

    /**
     * Get the piece in the position p.
     *
     * @param p the position to get the piece occupying.
     * @return the piece in the position p or null.
     */
    public Piece getPiece(Position p) {
        if (!isValidPos(p)) {
            return null;
        }
        return board[p.x][p.y];
    }

    /**
     * Get the piece in the position (x,y).
     *
     * @param x the x of pos
     * @param y the y of pos
     * @return the piece in the position (x,y or null.
     */
    public Piece getPiece(int x, int y) {
        if (!isValidPos(new Position(x, y))) {
            return null;
        }
        return board[x][y];
    }

    /**
     * Prints the board as the specified format states.
     */
    public void print() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            System.out.print("   ");
                        } else {
                            System.out.print("## ");
                        }
                    } else {
                        if (j % 2 != 0) {
                            System.out.print("   ");
                        } else {
                            System.out.print("## ");
                        }
                    }
                } else {
                    System.out.print(board[i][j].toString() + " ");
                }
            }
            System.out.println((8 - i));
        }
        System.out.println(" a  b  c  d  e  f  g  h\n");
    }

    /**
     * Checks if the piece in position p is checked from pieces of the color
     * isWhite
     *
     * @param p the position to be checked for check.
     * @param isWhite the color of the piece to check from.
     * @return True if this piece in p is checked by pieces of color isWhite
     */
    public Boolean inCheck(Position p, Boolean isWhite) {
        if (!isValidPos(p)) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null && board[i][j].isWhite == isWhite) {
                    // a piece to attack
                    if (board[i][j].isValidMove(this, new Position(i, j), p)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the king with the color "white" is checked. isWhite the color
     * of the king to check for
     *
     * @param white the color of the piece to check for.
     * @return True if the king with the color "white" is checked.
     */
    public boolean inCheck(Boolean white) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null && board[i][j].isWhite == white
                        && board[i][j].getClass().getName().equals("chess.King")) {
                    return inCheck(new Position(i, j), !white);
                }
            }
        }
        return false;
    }

    /**
     * Returns true if a checkmate has occured to the king with color "isWhite"
     *
     * @param isWhite The color of the king to be tested to be in a checkmate.
     * @return true if a checkmate has occured to the king with color "isWhite"
     * @throws Exception if no king with this color is found.
     */
    Boolean checkmate(Boolean isWhite) throws Exception {
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
        Piece king;
        Piece temp;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null && board[i][j].getClass().getName().equals("chess.King")
                        && board[i][j].isWhite == isWhite) {
                    // we got the king
                    if (!inCheck(new Position(i, j), !isWhite)) {
                        return false;
                    }
                    // save the original place of the king
                    king = board[i][j];
                    board[i][j] = null;
                    for (Position direction : directions) {
                        // check if current move is valid
                        if (!isValidPos(new Position(i + direction.x, j + direction.y))) {
                            continue;
                        }

                        // simulates the move
                        temp = board[i + direction.x][j + direction.y];
                        if (temp != null && temp.isWhite == king.isWhite) {
                            // not a valid move
                            continue;
                        }
                        board[i + direction.x][j + direction.y] = king;
                        if (!inCheck(new Position(i + direction.x, j + direction.y), !isWhite)) {
                            board[i + direction.x][j + direction.y] = temp;
                            board[i][j] = king;
                            return false;
                        }
                        board[i + direction.x][j + direction.y] = temp;
                    }
                    board[i][j] = king;
                    return true;
                }
            }
        }

        // something is not right (A king is not found)
        throw new Exception("A king is not found !");
    }

    /**
     * Test apply move.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard8() {
        Board b = new Board();
        // test castling
        b.board[7][4] = new King(Boolean.TRUE);
        b.board[7][0] = new Rook(Boolean.TRUE);

        if (b.board[7][4].isValidMove(b, new Position(7, 4), new Position(7, 2))) {
            b.applyMoveHelper(new Position(7, 4), new Position(7, 2));
            if (!(b.board[7][2].getClass().getName().equals("chess.King")
                    && b.board[7][3].getClass().getName().equals("chess.Rook"))) {
                return false;
            }
        }

        // test enpassant
        b.board[5][5] = new Pawn(Boolean.TRUE);
        b.board[4][5] = new Pawn(Boolean.FALSE);
        ((Pawn) (b.board[4][5])).setTwoStep(Boolean.TRUE);
        if (b.board[5][5].isValidMove(b, new Position(5, 5), new Position(4, 6))) {
            b.applyMoveHelper(new Position(5, 5), new Position(4, 6));
            if (!(b.board[5][5] == null && b.board[4][5] == null
                    && b.board[4][6].getClass().getName().equals("chess.Pawn"))) {
                return false;
            }
        }

        b.applyMoveHelper(new Position(4, 6), new Position(3, 6));
        if (!(b.board[3][6].getClass().getName().equals("chess.Pawn")
                && b.board[4][6] == null)) {
            return false;
        }

        return true;
    }

    /**
     * Test check and checkmate.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard7() throws Exception {
        Board b = new Board();
        //b.board[2][2] = new Queen(Boolean.TRUE);
        b.board[2][4] = new King(Boolean.FALSE);
        if (b.inCheck(new Position(2, 4), true)) {
            return false;
        }
        b.board[2][2] = new Queen(Boolean.TRUE);
        if (!b.inCheck(new Position(2, 4), true)) {
            return false;
        }
        b.board[2][4] = null;
        b.board[3][5] = new King(Boolean.FALSE);
        b.board[4][4] = new Pawn(Boolean.TRUE);
        if (!b.inCheck(new Position(2, 4), true)) {
            return false;
        }
        b.board[6][5] = new King(Boolean.TRUE);
        b.board[6][0] = new Rook(Boolean.FALSE);
        if (!b.inCheck(new Position(6, 5), Boolean.FALSE)) {
            return false;
        }
        b.board[7][5] = new King(Boolean.TRUE);
        b.board[6][5] = null;
        b.board[7][1] = new Rook(Boolean.FALSE);

        if (!b.inCheck(new Position(6, 5), Boolean.FALSE)) {
            return false;
        }
        if (!b.checkmate(Boolean.TRUE)) {
            return false;
        }

        b = new Board();
        b.board[7][5] = new King(Boolean.TRUE);
        b.board[6][5] = new Queen(Boolean.FALSE);
        if (b.checkmate(Boolean.TRUE)) {
            return false;
        }
        b.board[4][7] = new Bishop(Boolean.FALSE);
        if (!b.checkmate(Boolean.TRUE)) {
            return false;
        }

        return true;
    }

    /**
     * Test all possible moves of the Queen.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard6() {
        Board b = new Board();
        b.board[4][4] = new Queen(Boolean.TRUE);
        b.board[6][6] = new Pawn(Boolean.TRUE);
        b.board[6][2] = new Pawn(Boolean.FALSE);
        Position corrects[] = {
            new Position(0, 0),
            new Position(0, 4),
            new Position(1, 1),
            new Position(1, 4),
            new Position(1, 7),
            new Position(2, 2),
            new Position(2, 4),
            new Position(2, 6),
            new Position(3, 3),
            new Position(3, 4),
            new Position(3, 5),
            new Position(4, 0),
            new Position(4, 1),
            new Position(4, 2),
            new Position(4, 3),
            new Position(4, 5),
            new Position(4, 6),
            new Position(4, 7),
            new Position(5, 3),
            new Position(5, 4),
            new Position(5, 5),
            new Position(6, 2),
            new Position(6, 4),
            new Position(7, 4),};
        int k = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.board[4][4].isValidMove(b, new Position(4, 4), new Position(i, j))) {
                    if (!new Position(i, j).equals(corrects[k++])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Test all possible moves of the Bishop.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard5() {
        Board b = new Board();
        b.board[4][4] = new Bishop(Boolean.TRUE);
        b.board[6][6] = new Pawn(Boolean.TRUE);
        b.board[6][2] = new Pawn(Boolean.FALSE);
        Position corrects[] = {
            new Position(0, 0),
            new Position(1, 1),
            new Position(1, 7),
            new Position(2, 2),
            new Position(2, 6),
            new Position(3, 3),
            new Position(3, 5),
            new Position(5, 3),
            new Position(5, 5),
            new Position(6, 2)
        };
        int k = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.board[4][4].isValidMove(b, new Position(4, 4), new Position(i, j))) {
                    if (!new Position(i, j).equals(corrects[k++])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Test all possible moves of the Knight.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard4() {
        Board b = new Board();
        b.board[5][5] = new Knight(Boolean.TRUE);
        b.board[4][7] = new Pawn(Boolean.FALSE);
        Position pos[] = {
            new Position(3, 6),
            new Position(3, 4),
            new Position(4, 7),
            new Position(4, 3),
            new Position(7, 4),
            new Position(7, 6),
            new Position(6, 7),
            new Position(6, 7)
        };

        for (Position po : pos) {
            if (!b.board[5][5].isValidMove(b, new Position(5, 5), po)) {
                return false;
            }
        }
        if (b.board[5][5].isValidMove(b, new Position(5, 5), new Position(7, 7))) {
            return false;
        }

        return true;
    }

    /**
     * Test all possible moves of the pawn.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard3() {
        Board b = new Board();

        // test regular one step up move
        b.board[1][5] = new Pawn(Boolean.FALSE);
        if (!b.board[1][5].isValidMove(b, new Position(1, 5), new Position(2, 5))) {
            return false;
        }
        b.board[1][5] = new Pawn(Boolean.FALSE);
        if (b.board[1][5].isValidMove(b, new Position(1, 5), new Position(0, 5))) {
            return false;
        }

        b.board[1][5] = new Pawn(Boolean.TRUE);
        if (b.board[1][5].isValidMove(b, new Position(1, 5), new Position(2, 5))) {
            return false;
        }
        b.board[1][5] = new Pawn(Boolean.TRUE);
        if (!b.board[1][5].isValidMove(b, new Position(1, 5), new Position(0, 5))) {
            return false;
        }

        b.board[1][5] = new Pawn(Boolean.TRUE);
        b.board[0][5] = new Pawn(Boolean.TRUE);
        if (b.board[1][5].isValidMove(b, new Position(1, 5), new Position(0, 5))) {
            return false;
        }

        // test regular two move up
        b = new Board();
        b.board[6][5] = new Pawn(Boolean.TRUE);
        if (!b.board[6][5].isValidMove(b, new Position(6, 5), new Position(4, 5))) {
            return false;
        }
        b.board[1][5] = new Pawn(Boolean.FALSE);
        if (!b.board[1][5].isValidMove(b, new Position(1, 5), new Position(3, 5))) {
            return false;
        }
        b.board[1][5] = new Pawn(Boolean.FALSE);
        b.board[3][5] = new Pawn(Boolean.FALSE);
        if (b.board[1][5].isValidMove(b, new Position(1, 5), new Position(3, 5))) {
            return false;
        }
        // test diagonal capture
        b = new Board();
        b.board[5][5] = new Pawn(Boolean.TRUE);
        b.board[4][6] = new Pawn(Boolean.FALSE);
        if (!b.board[5][5].isValidMove(b, new Position(5, 5), new Position(4, 6))) {
            return false;
        }
        b.board[5][5] = new Pawn(Boolean.TRUE);
        b.board[4][6] = new Pawn(Boolean.TRUE);
        if (b.board[5][5].isValidMove(b, new Position(5, 5), new Position(4, 6))) {
            return false;
        }
        b.board[4][6] = null;
        if (b.board[5][5].isValidMove(b, new Position(5, 5), new Position(4, 6))) {
            return false;
        }

        // test enpassant
        b.board[4][5] = new Pawn(Boolean.FALSE);
        if (b.board[5][5].isValidMove(b, new Position(5, 5), new Position(4, 6))) {
            return false;
        }
        ((Pawn) (b.board[4][5])).setTwoStep(Boolean.TRUE);
        if (!b.board[5][5].isValidMove(b, new Position(5, 5), new Position(4, 6))) {
            return false;
        }

        // test random move
        b = new Board();
        b.board[5][5] = new Pawn(Boolean.TRUE);
        if (b.board[5][5].isValidMove(b, new Position(5, 5), new Position(7, 7))) {
            return false;
        }
        return true;
    }

    /**
     * Test all possible moves of the rook.
     *
     * @return True if all test cases has passed.
     */
    private static Boolean testBoard2() {
        Board b = new Board();
        b.board[0][0] = new Rook(Boolean.TRUE);
        // test diagonal move
        if (b.board[0][0].isValidMove(b, new Position(0, 0), new Position(1, 1))) {
            return false;
        }

        // test regular correct move
        if (!b.board[0][0].isValidMove(b, new Position(0, 0), new Position(7, 0))) {
            return false;
        }
        if (!b.board[0][0].isValidMove(b, new Position(0, 0), new Position(0, 7))) {
            return false;
        }

        // test piece in between
        b.board[0][5] = new Rook(Boolean.TRUE);
        if (b.board[0][0].isValidMove(b, new Position(0, 0), new Position(0, 7))) {
            return false;
        }

        // test capture
        b.board[0][5] = null;
        b.board[0][7] = new Rook(Boolean.TRUE);
        if (b.board[0][0].isValidMove(b, new Position(0, 0), new Position(0, 7))) {
            return false;
        }

        b.board[0][7] = new Rook(Boolean.FALSE);
        if (!b.board[0][0].isValidMove(b, new Position(0, 0), new Position(0, 7))) {
            return false;
        }

        return true;
    }

    /**
     * Test all cases of a king.
     *
     * @return true if all the test cases has passed.
     */
    private static Boolean testBoard1() {
        Board b = new Board();
        b.board[7][4] = new King(Boolean.TRUE);

        // test no rook
        if (b.board[7][4].isValidMove(b, new Position(7, 4), new Position(7, 6))) {
            return false;
        }

        b.board[7][0] = new Rook(Boolean.TRUE);

        // test rook
        if (!b.board[7][4].isValidMove(b, new Position(7, 4), new Position(7, 2))) {
            return false;
        }

        b.board[7][7] = new Rook(Boolean.TRUE);

        // test rook
        if (!b.board[7][4].isValidMove(b, new Position(7, 4), new Position(7, 6))) {
            return false;
        }

        b.board[7][7].applyMove(b, null, null);

        // test moved rook
        if (b.board[7][4].isValidMove(b, new Position(7, 4), new Position(7, 6))) {
            return false;
        }

        // test regular move
        if (!b.board[7][4].isValidMove(b, new Position(7, 4), new Position(6, 5))) {
            return false;
        }

        if (!b.board[7][4].isValidMove(b, new Position(7, 4), new Position(7, 5))) {
            return false;
        }

        if (b.board[7][4].isValidMove(b, new Position(7, 4), new Position(5, 4))) {
            return false;
        }

        // test correct start position
        b.board[7][4] = null;
        b.board[7][5] = new King(Boolean.TRUE);

        if (!b.board[7][5].isValidMove(b, new Position(7, 5), new Position(7, 6))) {
            return false;
        }

        // test capture
        b.board[7][4] = new Rook(Boolean.TRUE);
        if (b.board[7][5].isValidMove(b, new Position(7, 5), new Position(7, 4))) {
            return false;
        }

        b.board[7][4] = new Rook(Boolean.FALSE);
        if (!b.board[7][5].isValidMove(b, new Position(7, 5), new Position(7, 4))) {
            return false;
        }

        return true;
    }

    /**
     * This main is calling all testing functions and warning the user if any
     * one failed.
     *
     * @param args unused
     * @throws Exception unused
     */
    public static void main(String[] args) throws Exception {
        if (!testBoard1()) {
            System.out.println("failed test 1");
            return;
        }
        if (!testBoard2()) {
            System.out.println("failed test 2");
            return;
        }

        if (!testBoard3()) {
            System.out.println("failed test 3");
            return;
        }
        if (!testBoard4()) {
            System.out.println("failed test 4");
            return;
        }
        if (!testBoard5()) {
            System.out.println("failed test 5");
            return;
        }

        if (!testBoard6()) {
            System.out.println("failed test 6");
            return;
        }
        if (!testBoard7()) {
            System.out.println("failed test 7");
            return;
        }
        if (!testBoard8()) {
            System.out.println("failed test 8");
            return;
        }

        System.out.println("All tests has passed");
    }

    private Piece generatePromoted(Boolean white) {
        switch (nextPromotion) {
            case 'Q':
                return new Queen(white);
            case 'B':
                return new Bishop(white);
            case 'N':
                return new Knight(white);
            case 'R':
                return new Rook(white);
        }
        return null;
    }

}
