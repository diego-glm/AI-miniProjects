import java.util.HashMap;
import java.util.Map;

/**
 * The A.I. that can play connect-4 with heuristic-AlphaBeta-Minimax algorithm.
 *    Utility:   It is the (10000.0 * rows * cols / moves) as positive for MAX winning state or
 *               negate for MIN winning state.
 *    Heuristic: In a given state, the available position which creates multiple consecutive connect X
 *               plus an inverse relationship with the amount of movements to reach such a state.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-05-2023.
 */
public class AlphaBetaMiniMax {
  // The initial state of the game
  private Board initial_state;
  // Part C: limiting the search tree depth
  private int limit;
  // ('A') for Minimax; ('B') adding pruning (AlphaBeta); ('C') adding heuristic
  private char mode;
  // Troubleshoot information to command line
  private boolean troubleshoot;
  // Search tree as a map containing the optimal movement for a given state
  private Map<Board, MiniMaxInfo> table;
  // Keeping track of when the algorithm reruns on a prune state
  private boolean rerun;
  // Count of pruning activations.
  private int prune;

  /**
   * Constructor for setting up initial values for AlphaBetaMiniMax
   *
   * @param rows      size of the board's rows
   * @param cols      size of the board's columns
   * @param consecWin number of consecutive pieces to determine a win
   * @param depth     the maximum depth that the search tree can go to
   * @param mode      which mode of the AI to run
   * @param troubleshoot whether to display debugging information to console
   */
  public AlphaBetaMiniMax(int rows, int cols, int consecWin, int depth, char mode, boolean troubleshoot) {
    this.initial_state = new Board(rows, cols, consecWin);
    this.limit = depth;
    this.mode = mode;
    this.troubleshoot = troubleshoot;
  }


  /**
   *  Start the initial iteration of the search tree from the the initial state.
   *  Could also be use to restart the search tree if it was already created.
   */
  public void createSearchTree() {
    table = new HashMap<>();
    rerun = true;
    prune = 0;
    searchTree(initial_state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    rerun = false;
  }

  /**
   * Helper function that can recieves a state and runs the searchTree on
   *    alpha = - infinity & beta = infinity
   * Used for the initial state or when a state that was not visited (pruned)
   * is needed for the game.
   *
   * @param s an arbitrary state
   * @return the utility/heuristic value of the given state at the best action (column)
   */
  public MiniMaxInfo searchTree(Board s) {
    return searchTree(s, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
  }

  /**
   * Recursively preforms minimax search (with Aplha-Beta (mode = B) and heuristic (mode = C))
   * It relays on the recursive call to be made by the helper function bestMove(). Otherwise, base case will be strigger
   * by a state that was already reached, terminal state, (mode = B) pruning , or (mode = C) reached depth limit.
   *
   * @param s     an arbitrary state
   * @param alpha track of the maximum value achievable from previous visited child state
   * @param beta  track of the maximum value achievable from previous visited child state
   * @param depth track of the depth in the search tree (recursive calls)
   * @return the utility/heuristic value of the given state at the best action (column)
   */
  public MiniMaxInfo searchTree(Board s, int alpha, int beta, int depth) {
    MiniMaxInfo info;

    // state is in table
    if (table.containsKey(s)) {
      return table.get(s);

      // IS-TERMINAL
    } else if (s.getGameState() != GameState.IN_PROGRESS) {
      if (!rerun) {
        rerun = true;
      }
      info = new MiniMaxInfo(utility(s));
      appendTable(s, info);

      // IS-CUTOFF
    } else if (mode == 'C' && isCutOff(s, depth)) {
      info = eval(s);
      appendTable(s, info);

      // TO-MOVE MIN/MAX
    } else {
      if (!rerun) {
        rerun = true;
      }
      info = bestMove(s, alpha, beta, depth);
    }

    return info;
  }

  /**
   * The helper function to searchTree() that preforms the minimax and (mode = B or C) alpha-beta
   * in a recursive manner. For MIN state, find the child with the lowest value. For MAX, find the
   * child with the highest value.
   *
   * @param s     an arbitrary state
   * @param alpha the maximum value found so far
   * @param beta  the minimum value found so far
   * @param depth the current depth of the initial search
   * @return the utility/heuristic value of the child of the state with it respective action (column)
   */
  private MiniMaxInfo bestMove(Board s, int alpha, int beta, int depth) {
    MiniMaxInfo best_move;
    MiniMaxInfo new_move;
    Board child_s;
    boolean[] actions = action(s);
    boolean isMax;

    if (s.getPlayerToMoveNext() == Player.MAX) {
      best_move = new MiniMaxInfo(Integer.MIN_VALUE);
      isMax = true;
    } else {
      best_move = new MiniMaxInfo(Integer.MAX_VALUE);
      isMax = false;
    }

    for (int i = 0; i < actions.length; i++) {
      if (actions[i]) {
        child_s = s.makeMove(i);
        new_move = searchTree(child_s, alpha, beta, ++depth);
        if (isMax ? new_move.value() > best_move.value() : new_move.value() < best_move.value()) {
          best_move = new MiniMaxInfo(new_move.value(), (byte) i);
          if (mode == 'B' || mode == 'C') {
            if (isMax) {
              alpha = max(alpha, best_move.value());
            } else {
              beta = min(beta, best_move.value());
            }
          }
        }
        if ((mode == 'B' || mode == 'C') && (isMax ? best_move.value() >= beta : best_move.value() <= alpha)) {
          prune++;
          return best_move;
        }
      }
    }
    appendTable(s, best_move);
    return best_move;
  }

  /**
   * Determine which of the given integers is the highest
   *
   * @param var1 first integer
   * @param var2 second integer
   * @return the integer of the highest value
   */
  private int max(int var1, int var2) {
    if (var1 < var2) {
      return var2;
    }
    return var1;
  }

  /**
   * Determine which of the given integers is the lowest
   *
   * @param var1 first integer
   * @param var2 second integer
   * @return the integer of the lowest value
   */
  private int min(int var1, int var2) {
    if (var1 > var2) {
      return var2;
    }
    return var1;
  }

  /**
   * Determine the utility of a state which is bias towards a
   * lowest move taken state which is positive for MAX win and
   * negative MIN win. Zero for tie
   *
   * @param s an arbitrary state that is terminated (winner or tie)
   * @return the utility of the given state
   */
  private int utility(Board s) {
    int value = 10000 * s.getRows() * s.getCols() / s.getNumberOfMoves();
    // Heuristic causes a maximum of (value + 8). It should take a terminating state than a heuristic (in process) state.
    if (mode == 'C') {
      value += 9;
    }
    switch (s.getGameState()) {
      case MIN_WIN:
        value *= -1;
        break;
      case TIE:
        value = 0;
        break;
    }
    return value;
  }

  /**
   * Creates a heuristic for a state that has not terminated get.
   * Particularly, it finds the best action (column) were the current player
   * will have multiple actions on his next turn.
   *    Heuristic: the best action that yields multiple (consecutive_connect_X - 1)
   *
   * @param s an arbitrary state
   * @return  the heuristic value and the action (columns) of this state.
   */
  public MiniMaxInfo eval(Board s) {
    int value = 10000 * s.getRows() * s.getCols() / s.getNumberOfMoves();
    int goodMove;
    boolean[] actions = action(s);
    byte col = -1;
    int maxHeuristic = -2;
    int currHeuristic;

    if (s.getPlayerToMoveNext().equals(Player.MAX)) {
      goodMove = 1;
    } else {
      goodMove = -1;
    }
    for (int i = 0; i < actions.length; i++) {
      if (actions[i]) {
        currHeuristic = s.totalConseToWinAt(s.getLowestFreeRow(i), i);
        if (maxHeuristic < currHeuristic) {
          maxHeuristic = currHeuristic;
          col = (byte) i;
        }
      }
    }

    return new MiniMaxInfo(goodMove * (maxHeuristic + value), col);
  }


  /**
   * The available columns that the next player can choose from
   *
   * @param s an arbitrary state
   * @return a list of booleans were the array location represents the columns
   *         of the state with true = available to choose as next action and
   *         false = it is not available.
   */
  private boolean[] action(Board s) {
    boolean[] available_cols = new boolean[s.getCols()];
    for (int i = 0; i < s.getCols(); i++) {
      available_cols[i] = !s.isColumnFull(i);
    }
    return available_cols;
  }

  /**
   * Determines if the current depth of the search has reached the limit of search depth
   *
   * @param s     the current state of the search tree
   * @param depth the depth of the search
   * @return      true for depth limit has been reached. Otherwise, false.
   */
  private boolean isCutOff(Board s, int depth) {
    if (depth >= limit) {
      return true;
    }
    return false;
  }

  /**
   * Add the given state with it optimal action with is accosiated value
   * to the search tree.
   *
   * @param s    an arbitrary state
   * @param info the value and action for the given state
   */
  private void appendTable(Board s, MiniMaxInfo info) {
    if (troubleshoot) {
      String act = String.valueOf(info.col());
      if (act == null) {
        act = "null";
      }
      System.out.printf(" %s -> MiniMaxInfo[value=%d, action=%s]\n", s.toString(), info.value(), act);
    }
    table.put(s, info);
  }

  /**
   * The number of states in the search tree
   *
   * @return the size of the table which represents the search tree
   */
  public int tableSize() {
    return table.size();
  }

  /**
   * The number of times the algorithm pruned a state.
   * Note, if a prune state were to be given to the algorithm,
   * it will cause an expansion of the search state which will
   * might cause the number of prune to increase.
   *
   * @return the number of prunes so far
   */
  public int pruneCount() {
    return prune;
  }

  /**
   * Keeps track if a state that was prune was expanded.
   * Note this method should be called after the first call to
   * createSearchTree(). It should be also called after each
   * search up a state "searchTree(Board s)" to check if the
   * state was not expanded. Otherwise, it will not reset to
   * false for the next expansion of the searchTree.
   *
   * @return true if the state search up from the tree was expanded
   */
  public boolean isRerun() {
    if (rerun) {
      rerun = false;
      return true;
    }
    return false;
  }

  /**
   * After the initial expansion of the search tree, return the predicted
   * winning state for the given state as long both player play optimally.
   *
   * @param s an arbitrary state
   * @return the predicted winning state of the given state
   */
  public GameState guaranteedWinner(Board s) {
    int value = 0;
    GameState future = GameState.TIE;

    if (table.containsKey(s)) {
      value = table.get(s).value();
    }

    if (value > 0) {
      future = GameState.MAX_WIN;
    } else if (value < 0) {
      future = GameState.MIN_WIN;
    }

    return future;
  }
}
