import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * An implementation of Q-learning algorithm for an AI agent who
 * learn and make decisions in the game of Nim.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 12-04-2023.
 */
public class Q_LearningAI {

  private double gamma;

  private double alpha;

  private Q_table table;

  private int epis;

  private Nim_Game game;

  public Q_LearningAI(Nim_Game game, int episode, double gamma, double alpha) {
    this.game = game;
    this.epis = episode;
    this.gamma = gamma;
    this.alpha = alpha;

    this.table = new Q_table();

    runTraining();
  }

  public Q_LearningAI(Nim_Game game, int episode) {
    this(game, episode, 0.9, 1.0);
  }

  /**
   * Run a training loop where the agent takes actions in a game, updates
   * its Q-table based on the rewards and the maximum Q-value of the next state,
   * and repeats until the specified number of episodes is reached.
   */
  public void runTraining() {
    int curr_state, next_state, action, r;
    double minmax;
    double curr_q, new_q;

    while (epis > 0) {
      // displayQTable(); // Troubleshooting
      while (game.getWinner() == 0) {

        action = policy();
        curr_state = game.get_state();
        game.take_stick(action);
        next_state = game.get_state();

        // System.out.println(curr_state + "-" + action + "->" + next_state); // Troubleshooting

        r = reward();
        curr_q = table.get_valueOf(curr_state, action);
        minmax = max_min(next_state)[1];

        new_q = curr_q + alpha * (r + gamma * minmax - curr_q); // Update equation

        //Troubleshooting
        // if (curr_state == 1012 && action == 11) System.out.printf("%.1f = %.1f + %.1f
        // * (%d + %.1f * %.1f - %.1f)\n",
        // new_q, curr_q, alpha, r, gamma, minmax, curr_q);

        table.update(curr_state, action, new_q);
      }

      game.resetPiles();
      epis--;
    }
  }

 /**
  * The policy function generates a random move based on the current state of the game.
  *
  * @return an integer value that represents the pile and number of sticks to
  * be removed. The pile is represented by the tens digit and the number of sticks is represented by
  * the ones digit.
  */
  private int policy() {
    Random random = new Random();
    String state = Integer.toString(game.get_state());
    int rand_option;
    int rand_pile = 0;
    int rand_sticks;

    state = state.substring(1, state.length());
    rand_option = random.nextInt(game.nonEmpty_piles());
    for (int i = 1; i < state.length(); i++) {
      if (state.charAt(i) != '0')
        rand_option--;
      if (rand_option == -1) {
        rand_pile = i;
        break;
      }
    }
    int total_sticks = Integer.parseInt(String.valueOf(state.charAt(rand_pile)));
    rand_sticks = random.nextInt(1, total_sticks + 1);

    return rand_pile * 10 + rand_sticks;
  }

 /**
  * The "reward" function which is of 1000 if the first player wins, -1000 if second player to wins,
  * and 0 if there is no winner.
  *
  * @return an integer value representing the reward of the current state of the game.
  */
  private int reward() {
    if (game.getWinner() == 1)
      return 1000;
    if (game.getWinner() == 2)
      return -1000;
    return 0;
  }

  /**
   * Finds the maximum or minimum q value from a set of actions based on a given state. If the first player has the turn to go,
   * return the minimum for the state. Otherwise, second player has the turn and return the minimum.
   *
   * @param s a state in the environment.
   *        First digit represents the current play to go next,
   *        Rest of digit represents the amount of stick in the pile as ordered from left to right
   * @return an array of type double with first element being an action and the second element being the q_value
   *        which represents the maximum or minimum.
   */
  private double[] max_min(int s) {
    boolean max = true;
    double q;
    double[] best = { 0.0, 0.0 };

    if ((s / 1000) % 1000 == 1) {
      max = true;
      best[1] = Double.NEGATIVE_INFINITY;
    } else if ((s / 1000) % 1000 == 2) {
      max = false;
      best[1] = Double.POSITIVE_INFINITY;
    }

    Set<Integer> actions = table.get_allActionsOf(s);

    for (int a : actions) {
      q = get_QValueOf(s, a);

      if (max ? q > best[1] : q < best[1]) {
        best[0] = a;
        best[1] = q;
      }
    }

    if (best[1] == Double.POSITIVE_INFINITY || best[1] == Double.NEGATIVE_INFINITY) {
      best[1] = 0.0;
    }

    return best;
  }

  /**
   * Takes a string representing the current state of the game and returns
   * the best move to make.
   *
   * @param state the current state of the game. It is expected to be
   * a string that can be converted to an integer.
   * @return  the best move in form of an array`double` array.
   */
  public double[] bestMove(String state) {
    return max_min(Integer.parseInt(state));
  }

  /**
   * The function returns the Q-value of a given state-action pair.
   *
   * @param state the current state of the environment.
   * @param action The "action" parameter represents the action taken in a specific state.
   * @return the Q-value of a specific state-action pair.
   */
  public double get_QValueOf(int state, int action) {
    return table.get_valueOf(state, action);
  }

  /**
   * The function returns a sorted version of the qTable.
   *
   * @return a TreeMap object that contains Integer keys and HashMap values.
   * The TreeMap is ordered based on the natural ordering of the Integer keys.
   */
  public TreeMap<Integer, HashMap<Integer, Double>> getQTable() {
    TreeMap<Integer, HashMap<Integer, Double>> qTable_ordered;
    qTable_ordered = new TreeMap<Integer, HashMap<Integer, Double>>(table.q_values);
    return qTable_ordered;
  }

  // Debugging Purposes
  public void displayQTable() {
    String state_str, action_str;
    int s, a;
    double q_val;
    TreeMap<Integer, HashMap<Integer, Double>> q_order;
    TreeMap<Integer, Double> a_order;

    q_order = getQTable();
    Set<Integer> states = q_order.keySet();
    Iterator<Integer> state_stream = states.iterator();

    System.out.println("\nNew Game:");
    while (state_stream.hasNext()) {
      s = state_stream.next();

      a_order = new TreeMap<Integer, Double>(q_order.get(s));
      Set<Integer> actions = a_order.keySet();
      Iterator<Integer> action_stream = actions.iterator();
      while (action_stream.hasNext()) {

        a = action_stream.next();
        q_val = get_QValueOf(s, a);
        state_str = (String.valueOf(s).charAt(0) == '1' ? "A" : "B") + String.valueOf(s).substring(1, 4);
        action_str = String.valueOf(a);

        System.out.println("Q[" + state_str + ", " + action_str + "] = " + q_val);
      }
    }
  }

}