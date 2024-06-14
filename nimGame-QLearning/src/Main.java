import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/*“I have neither given nor received unauthorized aid on this program.”
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 12-04-2023.
 */
public class Main {
  /*AI Q Leaning instance */
  static Q_LearningAI q_leaning;

  public static void main(String[] args) throws Exception {
    int pile0, pile1, pile2, training;
    Nim_Game game;

    Scanner scan = new Scanner(System.in);

    // Get input from user
    do {
      System.out.print("Sticks in pile 0: ");
      pile0 = Integer.parseInt(scan.nextLine());
      System.out.print("Sticks in pile 1: ");
      pile1 = Integer.parseInt(scan.nextLine());
      System.out.print("Sticks in pile 2: ");
      pile2 = Integer.parseInt(scan.nextLine());
      if (0 <= pile0 && 0 <= pile1 && 0 <= pile2 &&
          pile0 < 10 && pile1 < 10 && pile2 < 10) break;
      System.out.println("Bad input. Only values for stick should be 0-9 per pile. \nTry Again!\n");

    } while (true);

    do {
      System.out.print("Number of simulated games for the AI to do: ");
      training = Integer.parseInt(scan.nextLine());
      if (training > 0) break;
      System.out.println("The number of training the AI must do must be at least 1. \nTry Again\n");
    } while (true);
    System.out.println();


    game = new Nim_Game(pile0, pile1, pile2); // Create an instance of the nim game
    q_leaning = new Q_LearningAI(game, training); // Create and train AI with Q learning

    // Game Start
    System.out.printf("Initial board is %d-%d-%d, simulating %d games. \n\n", pile0, pile1, pile2, training);
    System.out.println("Final Q-values: \n");
    displayQTable();

    // Play game against AI.
    boolean again = true;
    while (again) {
      game.resetPiles();
      String[] plys = new String[2];
      String state = "";
      int x;

      System.out.print("\nWho moves first, (1) User or (2) Computer? ");
      x = Integer.parseInt(scan.nextLine());
      System.out.println();
      if (x == 1) {
        plys[0] = "user";
        plys[1] = "computer";
      } else {
        plys[0] = "computer";
        plys[1] = "user";
      }

      x = 0;
      while (game.getWinner() == 0) {
        String act = "";
        state = get_stateStr(game);

        do { // Get correct input from current player
          System.out.printf("Player %c (%s)\'s turn; board is (%c, %c, %c). \n",
          (state.charAt(0) == '1' ? 'A' : 'B'), plys[x],
          state.charAt(1), state.charAt(2), state.charAt(3));

          try {
            if (plys[x].equals("user")) { // User's turn

              System.out.print("What pile? ");
              act = scan.nextLine();
              if (act.charAt(0) == '-') throw new ArrayIndexOutOfBoundsException("Pile option cannot be negative.");

              System.out.print("How many? ");
              act += scan.nextLine();
              if (act.charAt(1) == '-' || act.charAt(1) == '0') throw new ArrayIndexOutOfBoundsException("Sticks option cannot be negative or zero.");

            } else { // Computer's turns
              act = Integer.toString((int) q_leaning.bestMove(state)[0]);
              act = (act.length() < 2 ? "0" + act : act);
              System.out.printf("Computer chooses pile %c and removes %c.\n", act.charAt(0), act.charAt(1));
            }

            game.take_stick(Integer.parseInt(act));

            break;
          } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage() + "\nTry Again!\n");
          }
        } while (true);
        System.out.println();

        if (x == 0) {
          x++;
        } else {
          x--;
        }
      }

      System.out.printf("Game over.\nWinner is %c (%s). \n",
          (game.getWinner() == 1 ? 'A' : 'B'), plys[x]);

      do {
        System.out.print("\nPlay again? (1) Yes (2) No:  ");
        x = Integer.parseInt(scan.nextLine());
        if (x == 1 || x == 2) break;

        System.out.println("Bad input try again.");

      } while (true);
      if (x == 2)
        again = false;
    }

    scan.close();
  }

  public static void displayQTable() {
    String state_str, action_str;
    int s, a;
    double q_val;
    TreeMap<Integer, HashMap<Integer, Double>> q_order;
    TreeMap<Integer, Double> a_order;

    q_order = q_leaning.getQTable();
    Set<Integer> states = q_order.keySet();
    Iterator<Integer> state_stream = states.iterator();

    while (state_stream.hasNext()) {
      s = state_stream.next();

      a_order = new TreeMap<Integer, Double>(q_order.get(s));
      Set<Integer> actions = a_order.keySet();
      Iterator<Integer> action_stream = actions.iterator();
      while (action_stream.hasNext()) {

        a = action_stream.next();
        q_val = q_leaning.get_QValueOf(s, a);
        state_str = (String.valueOf(s).charAt(0) == '1' ? "A" : "B") + String.valueOf(s).substring(1, 4);
        action_str = String.valueOf(a);

        action_str = (action_str.length() < 2 ? "0" + action_str : action_str);

        System.out.println("Q[" + state_str + ", " + action_str + "] = " + q_val);
      }
    }
  }

  public static String get_stateStr(Nim_Game game) {
    return "" + game.get_state();
  }
}
