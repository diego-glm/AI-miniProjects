import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A representation of a Q-table used in reinforcement learning, which stores
 * the Q-values for each state-action pair.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 12-04-2023.
 */
public class Q_table {

  // Q-table
  public Map<Integer, HashMap<Integer, Double>> q_values;

  public Q_table() {
    q_values = new HashMap<>();
  }

  /**
   * Get all actions associated with a given state.
   *
   * @param state the state for which to retrieve all the actions.
   *
   * @return a Set of Integers, which represents all the actions associated
   *         with a given state.
   */
  public Set<Integer> get_allActionsOf(int state) {
    if (!q_values.containsKey(state)) {
      q_values.put(state, new HashMap<Integer, Double>());
    }

    return q_values.get(state).keySet();
  }

  /**
   * Get the q_values associated with the given state and action.
   * If one does not exist, initializing it to default 0.0.
   *
   * @param state  the state in a reinforcement learning problem. It is an integer
   *               value
   *               that identifies the current state of the environment.
   * @param action the action that is being taken in a particular state.
   *
   * @return the Q-value of the specified action for the given state.
   */
  public double get_valueOf(int state, int action) {
    if (!q_values.containsKey(state)) {
      q_values.put(state, new HashMap<>());
    }

    if (!q_values.get(state).containsKey(action)) {
      q_values.get(state).put(action, 0.0);
    }

    return q_values.get(state).get(action);
  }

  /**
   * Updates the Q-value in the table given state-action pair.
   *
   * @param state the state of the environment.
   * @param action the action taken in a particular state.
   * @param q the updated Q-value for a specific state-action pair.
   */
  public void update(int state, int action, double q) {
    q_values.get(state).put(action, q);
  }
}
