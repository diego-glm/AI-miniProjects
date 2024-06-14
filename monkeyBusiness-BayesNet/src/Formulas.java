import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Sets of operations needed for the Monkey Business bayes net.
 * It is also ensure minimum rounding error and increase precision
 * with the incorporation of the BigDecimal library.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-24-2023.
 */
public class Formulas {

  /** Return the equally likely probability for all placements in the map */
  public static double equalize(int m, int n) {
    double equally = BigDecimal.valueOf(1.0).divide(BigDecimal.valueOf(m * n), 28, RoundingMode.HALF_UP).doubleValue();
    return equally;
  }

  /** Appends the implicitly values for the current location given last location */
  public static void populate_C(DistribList _C, double total) {
    int counter;
    int m = _C.m;
    int n = _C.n;
    for (int c = 0; c < n; c++) {
      for (int r = 0; r < m; r++) {
        counter = manhattan1_count(r, c, m, n);

        double equally = BigDecimal.valueOf(total).divide(BigDecimal.valueOf(counter), 28, RoundingMode.HALF_UP).doubleValue();

        // Appends the valid locations with the shared likely hood for this given last location (c,r)
        if (r-1 >= 0)
          _C.getOutcomeList(r, c).add(new Coordinate(r-1, c, equally));
        if (c-1 >= 0)
          _C.getOutcomeList(r, c).add(new Coordinate(r, c-1, equally));
        if (c+1 < n)
          _C.getOutcomeList(r, c).add(new Coordinate(r, c+1, equally));
        if (r+1 < m)
          _C.getOutcomeList(r, c).add(new Coordinate(r+1, c, equally));
      }
    }
  }

  /** Appends the implicit values for the motion sensor given current location
   * This function looks for one manhattan distance locations.
   *   Use topLeft = true if specified for sensor location on top left.
   *   Otherwise, false for bottom right.
   */
  public static void populate_M(DistribList _M, boolean topLeft) {
    int m = _M.m;
    int n = _M.n;
    BigDecimal accuracy = BigDecimal.valueOf(0.9);
    BigDecimal decrease =  BigDecimal.valueOf(0.1);
    int startC, startR, nextC, nextR;
    if (topLeft) {
        startR = 0;
        startC = 0;
      } else {
        startR = m - 1;
        startC = n - 1;
      }

    for (int i = 0; (i < n || i < m) && i < 9; i++) {
      double prob = accuracy.subtract(decrease.multiply(BigDecimal.valueOf(i))).doubleValue();

      if (topLeft) {
        nextR = i;
        nextC = i;
      } else {
        nextR = m - 1 - i;
        nextC = n - 1 - i;
      }

      // Appends the valid locations with decreasing accuracy for this given location (c,r)
      if (i < n) {
        _M.getOutcomeList(0, 0).add(new Coordinate(startR, nextC, prob));
      }
      if (i < m) {
        _M.getOutcomeList(0, 0).add(new Coordinate(nextR, startC, prob));
      }
    }
  }

  /** Appends the implicit values for the sound sensor given current location
   * This method calculates two separate equally shared probability of
   * one manhattan and two manhattan.
   */
  public static void populate_S(DistribList _S) {
    int counter;
    int m = _S.m;
    int n = _S.n;
    // One manhattan distance
    populate_C(_S, 0.3);
    // Two manhattan distance plus center
    for (int c = 0; c < n; c++) {
      for (int r = 0; r < m; r++) {
        counter = manhattan2_count(r, c, m, n);

        double equally = BigDecimal.valueOf(0.1).divide(BigDecimal.valueOf(counter), 28, RoundingMode.HALF_UP).doubleValue();

        _S.getOutcomeList(r, c).add(new Coordinate(r, c, 0.6));
        if (r > 1)
          _S.getOutcomeList(r, c).add(new Coordinate(r-2, c, equally));
        if (c > 0 && r > 0)
          _S.getOutcomeList(r, c).add(new Coordinate(r-1, c-1, equally));
        if (c < n-1 && r > 0)
          _S.getOutcomeList(r, c).add(new Coordinate(r-1, c+1, equally));
        if (c > 1)
          _S.getOutcomeList(r, c).add(new Coordinate(r, c-2, equally));
        if (c < n-2)
          _S.getOutcomeList(r, c).add(new Coordinate(r, c+2, equally));
        if (c > 0 && r < m-1)
          _S.getOutcomeList(r, c).add(new Coordinate(r+1, c-1, equally));
        if (c < n-1 && r < m-1)
          _S.getOutcomeList(r, c).add(new Coordinate(r+1, c+1, equally));
        if (r < m-2)
          _S.getOutcomeList(r, c).add(new Coordinate(r+2, c, equally));
      }
    }
  }

  /** Helper function for populate_C */
  private static int manhattan1_count(int r, int c, int m, int n) {
    int counter;
    boolean colsEnds = (c == 0 || c == n-1);
    boolean rowsEnds = (r == 0 || r == m-1);
    if (colsEnds && rowsEnds) { // corners
      counter = 2;
    } else if (colsEnds || rowsEnds) { // walls
      counter = 3;
    } else { counter = 4; } // middle

    return counter;
  }

  /** Helper function for populate_S */
  private static int manhattan2_count(int r, int c, int m, int n) {
    int count = 0;
    if (r > 1) count++;
    if (c > 0 && r > 0) count++;
    if (c < n-1 && r > 0) count++;
    if (c > 1) count++;
    if (c < n-2) count++;
    if (c > 0 && r < m-1) count++;
    if (c < n-1 && r < m-1) count++;
    if (r < m-2) count++;

    return count;
  }

  /** Multiplication with BigDecimal */
  public static double multi(double... numbers) {
    BigDecimal total = BigDecimal.valueOf(1.0);
    for (double a : numbers) {
      total = total.multiply(BigDecimal.valueOf(a));
    }
    return total.doubleValue();
  }

  /** Addition with BigDecimal */
  public static double add(double... numbers) {
    BigDecimal total = BigDecimal.valueOf(0.0);
    for (double a : numbers) {
      total = total.add(BigDecimal.valueOf(a));
    }
    return total.doubleValue();
  }

  /** Subtraction with BigDecimal */
  public static double minus(double num1, double num2) {
    BigDecimal total = BigDecimal.valueOf(num1).subtract(BigDecimal.valueOf(num2));
    return total.doubleValue();
  }

  /** Normalize the data in array a2 and store it in a1*/
  public static void normalize(double[][] a1, double[][] a2) {
    BigDecimal sum = BigDecimal.valueOf(0.0);
    for (int r = 0; r < a2.length; r++) {
      for (int c = 0; c < a2[0].length; c++) {
        sum = sum.add(BigDecimal.valueOf(a2[r][c]));
      }
    }

    for (int r = 0; r < a1.length; r++) {
      for (int c = 0; c < a1[0].length; c++) {
        a1[r][c] = BigDecimal.valueOf(a2[r][c]).divide(sum, 28, RoundingMode.HALF_UP).doubleValue();
      }
    }
  }
}
