import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an object that mimics a Map using a key of output coordinates
 * to value of list containing a given coordinates plus the probabilities such that
 *
 * P(A1|B1) = P(A1=x1,y1 | B1=x2,y2)
 * P(a|b) = P(Value.x, Value.y|Key.x Key.y) = Value.prob
 *
 *        key          value
 * Map = [x2,y2] : [x1,y1,P(A1|B1)]
 *     = [B1]    : [ A1,  P(A1|B1)]
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-24-2023.
 */
public class DistribList {
  // Map from each "given" locations to their "outcomes".
  //Index: Output, Element: List of locations and probabilities
  private Map<Coordinate, List<Coordinate>> distribution;
  // Default probability for outcome with given that was not implicitly specified
  private double defaultProb;
  // Shared size of the map
  public int m, n;

  public DistribList(int m, int n) {
    this.distribution = new HashMap<>();
    this.m = m;
    this.n = n;
    resetDistr(m, n);
  }

  /** Generate Key for all possable locations on the map */
  public void resetDistr(int m, int n) {
    for (int r = 0; r < m; r++) {
      for (int c = 0; c < n; c++)
        distribution.put(new Coordinate(r, c), new ArrayList<>());
    }
  }

  /** Given a specific location on the map (Key = given), append the value to the list (Value.add(outcome))
   *  Ensure outcome is a Coordinate with a potability.
   *  Ensure given  is a Coordinate without probability.
  */
  public void addOutcomeToGiven(Coordinate outcome, Coordinate given) {
    // Correct use of the method
    testCoordinate(given);
    testOutcome(outcome);
    existInList(distribution.get(given));

    distribution.get(given).add(outcome);
  }

  /** Set the default probability for any unspecified locations (not within the distribution) */
  public void addDefaultOutcome(double prob) {
    defaultProb = prob;
  }

  /** For a specified given (Key - given), return all elements that were specifically added
   *   Ex) Key: given_i => Return = Value: List< Output_i = (x_i, y_i,), P_i(Output_i|Given_i)>
   *
   *  Note: For probabilities not within this list, assume default probabilities
   *        (call getOutcomeProb())
   */
  public List<Coordinate> getOutcomeList(Coordinate given) {
    // Correct use of the method
    testCoordinate(given);

    List<Coordinate> specificList = distribution.get(given);
    return specificList;
  }

  /** For a specified location in the map, return all elements that were specifically added
   *   Ex) Key: x2, y2 => Return = Value: List<(x_i, y_i, P_i(x_i, y_i|x2, y2))>
   *
   *  Note: For probabilities not within this list, assume default probabilities
   *        (call getOutcomeProb())
   */
  public List<Coordinate> getOutcomeList(int r, int c) {
    return getOutcomeList(new Coordinate(r, c));
  }

  /** Return the specific probability for P(specific_outcome|given) */
  public double getOutcomeProb(Coordinate specific_outcome, Coordinate given) {
    // Correct use of the method
    testCoordinate(specific_outcome);
    List<Coordinate> all_outcome = getOutcomeList(given);

    for (Coordinate outcome : all_outcome) {
      if (outcome.col() == specific_outcome.col() && outcome.row() == specific_outcome.row())
        return outcome.prob();
    }

    return defaultProb;
  }

  /** Return the specific probability for P(A = r1, c1|B = r2, c2) */
  public double getOutcomeProb(int r1, int c1, int r2, int c2) {
    return getOutcomeProb(new Coordinate(r1, c1), new Coordinate(r2, c2));
  }


  private void testCoordinate(Coordinate a) {
    if (a.prob() != null)
      throw new IllegalArgumentException("Coordinate cannot have an associated probability. It must represent \"given previous location or sound sensor.\"");
    //return false;
  }

  private void testOutcome(Coordinate a) {
    if (a.prob() == null)
      throw new IllegalArgumentException("Outcome must have an associated probability");
    //return false;
  }

  private void existInList(Object a) {
    if (a == null)
      throw new IllegalArgumentException("There is no element associated in the list.");
  }

}
