/**
 * The Node class is a data type that is used in creating a search tree. This particular one the following:
 *    The current state: Location and Speeding amount.
 *    The cost of this path: g(n) h(n) and f(n).
 *    The previous node and the relative actions (path) that node took to reach this node.
 * All of these element are accessible.
 *
 * @author Diego Lopez
 * @version 1.0 09-19-2023
 */
public record Node(Node previous, Location s, int speeding, Road path, double g_cost, double h_cost, double f_cost) {
  public Node(Location s, int speeding, double g_cost, double h_cost,  double f_cost) {
    this(null, s, speeding, null, g_cost, h_cost, f_cost);
  }
}
