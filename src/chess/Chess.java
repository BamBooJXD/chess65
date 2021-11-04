package chess;

import java.util.Scanner;

/**
 *
 * @author Xudong Jiang
 * @author Yuting Chen
 */
public class Chess {

    /**
     * A boolean indicates the player color in turn.
     */
    private static Boolean inturn = true; // white to start

    /**
     * The scanner of the input
     */
    private static Scanner sc = new Scanner(System.in);

    private static boolean winner;
    private static boolean draw = false;
    private static boolean offerDraw = false;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception throws when one of the kings is missing.
     * which means wrong processing.
     */
    public static void main(String[] args) throws Exception {
        Board b = new Board(true);
        String input;
        b.print();
        while (true) {
            b.setNextPromotion('Q');
            System.out.print(((inturn) ? "White" : "Black") + "'s move: ");
            input = sc.nextLine();
            if (input.equals("resign")) {
                winner = !inturn;
                break;
            } else if (input.equals("draw")) {
                if (offerDraw) {
                    draw = true;
                    break;
                } else {
                    continue;
                }
            } else {
                String splits[] = input.split(" ");
                if (splits.length == 3 && splits[2].equals("draw?")) {
                    offerDraw = true;
                } else if (splits.length == 3 && splits[2].length() == 1) {
                    b.setNextPromotion(splits[2].charAt(0));
                }
                if (b.checkMove(splits[0], splits[1], inturn)) {
                    b.applyMove(splits[0], splits[1]);
                } else {
                    System.out.println("Illegal move, try again");
                    continue;
                }
            }

            inturn = !inturn;
            System.out.println();
            b.print();
            if (b.checkmate(inturn)) {
                System.out.println("Checkmate");
                winner = !inturn;
                break;
            }
            if (b.inCheck(inturn)) {
                System.out.println("Check");
            }
        }
        if (draw) {
            System.out.println("draw");
        } else {
            if (winner) {
                System.out.println("White wins");
            } else {
                System.out.println("Black wins");
            }
        }
    }

}

