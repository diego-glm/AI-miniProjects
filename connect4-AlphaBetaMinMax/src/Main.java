import java.util.Scanner;

/**
 * The main function for running the connect-4 game against an A.I.
 * Part A: A.I. set up as Minimax algorithm.
 * Part B: A.I. set up as AlphaBetaMiniMax algorithm.
 * Part C: A.I. set up as AlphaBetaMiniMax algorithm with a limited depth and heuristic.
 *
 * Note: there is an intentional-unchecked value where if the first player to go first was
 *        other than 1 or 2. If n < 1, computer will play against itself. If n > 2, computer
 *        will play n-3 times. Used this for testing optimal plays.
 *
 * “I have neither given nor received unauthorized aid on this program.”
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-05-2023.
 */
public class Main {
  static Scanner scan;
  static AlphaBetaMiniMax search;
  static Board game;
  static boolean troubleshoot;
  static int goesFirst;

  public static void main(String[] args) {
    boolean stop = false;
    scan = new Scanner(System.in);
    while (!stop) {
      try {
        promptUserGameSetup();
      } catch (IllegalArgumentException e) {
        System.out.print("\n"+ e.getMessage() + "\n-------TRY AGAIN!-------\n\n");
        continue;
      }

      stop = runGame();
      game.resetBoard();
    }
    scan.close();
  }

  /**
   * The interface that runs the main functions for connect-4 game.
   *
   * @return true if the user does not want to play again. Otherwise false;
   */
  public static boolean runGame() {
    System.out.print("Who plays first? 1=human, 2=computer: ");
    goesFirst = Integer.parseInt(scan.nextLine());
    System.out.println();

    while (!game.hasWinner()) {
      MiniMaxInfo optimalAction = search.searchTree(game);
      System.out.println("\n" + game.to2DString());
      if (search.isRerun()) {
        System.out.println("This is a state that was previously pruned; re-running alpha beta from here.");
      }
      System.out.printf("Minimax value for this state: %d, optimal move: %d\n", optimalAction.value(), optimalAction.col());
      String player = "";
      switch (game.getPlayerToMoveNext()) {
        case MAX:
          player = "MAX";
          break;
        case MIN:
          player = "MIN";
          break;
      }
      System.out.printf("It is %s's turn! \n", player);

      while (true) {
        try {
          int move = -1;
          if (goesFirst == 1) {
            goesFirst++;
            System.out.print("Enter move: ");
            move = Integer.parseInt(scan.nextLine());
          } else {
            goesFirst--;
            System.out.printf("Computer chooses move: %d", optimalAction.col());
            move = optimalAction.col();
          }
          System.out.println();
          game = game.makeMove(move);
          break;
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Invalid columns choice.\n-------TRY AGAIN!-------\n\n");
          goesFirst--;
          System.out.println("\n" + game.to2DString());
          System.out.printf("Minimax value for this state: %d, optimal move: %d\n", optimalAction.value(), optimalAction.col());
          System.out.printf("It is %s's turn! \n", player);
          continue;

        }
      }
    }

    System.out.println("\nGame Over!");
    System.out.println(game.to2DString());
    String playerName = (goesFirst%2 == 0 ? "(human)" : "(computer)");
    switch (game.getPlayerToMoveNext()) {
      case MAX:
        System.out.println("The winner is MIN " + playerName);
        break;
      case MIN:
        System.out.println("The winner is MAX " + playerName);
        break;
    }

    System.out.print("Play again? (y/n) ");
    if (scan.nextLine().equals("n")) {
      return true;
    }
    System.out.println("\n\n");

    return false;

  }

  /**
   * Prompt the user for values for setting up the game and troubleshooting.
   * After getting these values, it will initialize the AI and initial game state.
   */
  public static void promptUserGameSetup() {
    // Mode/Part of the AI
    System.out.print("Run part A, B, or C? ");
    char part = Character.toUpperCase(scan.nextLine().charAt(0));
    if (part !=  'A' && part != 'B' && part != 'C') {
      throw new IllegalArgumentException("That part is invalid or does not existent");
    }

    //Debugging
    System.out.print("\nInclude debugging info? (y/n) ");
    char trb = Character.toLowerCase(scan.nextLine().charAt(0));
    if (trb != 'y' && trb != 'n') {
      throw new IllegalArgumentException("Invalid mode for troubleshooting");
    }
    if (trb == 'y') {
      troubleshoot = true;
    } else {
      troubleshoot = false;
    }
    try {
      //Dimensions of the game
      System.out.print("\nEnter rows: ");
      int rows = Integer.parseInt(scan.nextLine());
      System.out.print("\nEnter columns: ");
      int cols = Integer.parseInt(scan.nextLine());
      if (rows < 1 || cols < 1) {
        throw new IllegalArgumentException("Invalid range for the size of game");
      }

      System.out.print("\nEnter number in a row to win: \n");
      int consecWin = Integer.parseInt(scan.nextLine());
      if (consecWin > rows && consecWin > cols) {
        throw new IllegalArgumentException("Invalid \"number in a row\" given the size of the game");
      }

      int depth = 1;
      if (part == 'C') {
        System.out.print("Number of moves to look ahead (depth): \n");
        depth = Integer.parseInt(scan.nextLine());
        if (depth < 1)
          throw new IllegalArgumentException("Invalid depth value");
      }

      // Setup the AI and game
      search = new AlphaBetaMiniMax(rows, cols, consecWin, depth, part, troubleshoot);
      game = new Board(rows, cols, consecWin);
      search.createSearchTree(); // Start the search tree
    } catch (NumberFormatException e) {
      throw new NumberFormatException("Input is not a number");
    }

    // Summary of the search tree
    System.out.printf("\nTransposition table has %d states.\n", search.tableSize());
    if (part == 'B') {
      System.out.printf("The tree was pruned %d times.\n", search.pruneCount());
    }
    switch (search.guaranteedWinner(game)) {
      case MAX_WIN:
        System.out.println("First player has a guaranteed win with perfect play.");
        break;
      case MIN_WIN:
        System.out.println("Second player has a guaranteed win with perfect play.");
        break;
      case TIE:
        System.out.println("Neither player has a guaranteed win; game will end in tie with perfect play on both sides.");
        break;
    }
  }
}
