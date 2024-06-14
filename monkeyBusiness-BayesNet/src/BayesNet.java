/**
 * Emulates a bayes net specifically for the Monkey Business problem.
 * It implements a 2d array for storing the probability P(c|m1,m2,s)
 * Each CPT graph is stored in a distribution list were there is a
 * default probability for any output except for the ones that are
 * specificated.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-24-2023.
 */
public class BayesNet {

  // Dimensions of the grid.
  private int rows, cols;

  // 2d grid containing probabilities.
  private double[][] lastLoc; // Current iteration
  private double[][] nextLoc; // Next iteration

  // Distribution list for current Location.
  private DistribList currentLoc;

  // Distribution list for motion sensor 1 (contains one element).
  private DistribList detectorM1;

  // Distribution list for motion sensor 2 (contains one element).
  private DistribList detectorM2;

  // Distribution list for sound sensor
  private DistribList soundSensor;

  // Set to true if debugging
  private boolean debug = false;

  // How many times the probability was calculated given new input
  private int timestep = 0;

  public BayesNet(int m, int n, boolean debug) {
    rows = m;
    cols = n;
    this.debug = debug;
    // Initializing the list to every possible location
    currentLoc  = new DistribList(m, n);
    detectorM1  = new DistribList(m, n);
    detectorM2  = new DistribList(m, n);
    soundSensor = new DistribList(m, n);

    // Initialize every element to equally possible location in L
    setAll(Formulas.equalize(m, n));

    // Setting the default for locations that are not implicitly added
    currentLoc.addDefaultOutcome(0.0);
    detectorM1.addDefaultOutcome(0.05);
    detectorM2.addDefaultOutcome(0.05);
    soundSensor.addDefaultOutcome(0.0);

    // Fill out the probabilities according to the problem
    Formulas.populate_C(currentLoc, 1);
    Formulas.populate_M(detectorM1, true);
    Formulas.populate_M(detectorM2, false);
    Formulas.populate_S(soundSensor);

    if (debug) { // Debugging information
      System.out.println("Last Location Distribution:");
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          System.out.printf("Last location: (%d, %d), prob: %3.8f %n", r, c, lastLoc[r][c]);
        }
      }

      System.out.println("\nCurrent location distribution:");
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          System.out.printf("Last location: (%d, %d)%n", r, c);
          for (Coordinate coord : currentLoc.getOutcomeList(r, c)) {
            System.out.printf(" Current location: (%d, %d), prob: %3.8f %n",
                              coord.row(), coord.col(), coord.prob());
          }
        }
      }

      System.out.println("\nMotion sensor #1 (top left) distribution:");
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          System.out.printf("Current location: (%d, %d), true prob: %3.8f %n",
                            r, c, detectorM1.getOutcomeProb(r, c, 0, 0));
        }
      }

      System.out.println("\nMotion sensor #2 (bottom right) distribution:");
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          System.out.printf("Current location: (%d, %d), true prob: %3.8f %n",
                            r, c, detectorM2.getOutcomeProb(r, c, 0, 0));
        }
      }

      System.out.println("\nSound distribution:");
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          System.out.printf("Current location: (%d, %d)%n", r, c);
          for (Coordinate coord : soundSensor.getOutcomeList(r, c)) {
            System.out.printf("  Sound reported at: (%d, %d), true prob: %3.8f %n",
                              coord.row(), coord.col(), coord.prob());
          }
        }
      }
    }

    // Print out initial distribution.
    System.out.println("\nInitial distribution of monkey's last location:");
    printPrediction(lastLoc);

  }

  /**
   *  This method can be call to update the distribution according to the given inputs
   *
   * @param m1 Sensor 1 detection on or off
   * @param m2 Sensor 2 detection on or off
   * @param sound Sound sensor specified location
   */
  public void runDistributions(boolean m1, boolean m2, Coordinate sound) {
    System.out.printf("Observation: Motion1: %B, Motion2: %B, Sound location: (%d, %d)%n", m1, m2, sound.row(), sound.col());
    System.out.printf("Monkey's predicted current location at time step: %d%n", timestep++);

    // Preforms the probability for every current location base on the sum of given every last location
    double multi;
    for (int r1 = 0; r1 < rows; r1++) {
      for (int c1 = 0; c1 < cols; c1++) {
        if (debug) System.out.printf("  Calculating total prob for current location (%d, %d)%n", r1, c1);
        multi = 0;
        for (int r2 = 0; r2 < rows; r2++) {
          for (int c2 = 0; c2 < cols; c2++) {
            //c=(r1,c1), l=(r2,c2): P(l)P(c|l)P(m1|c)P(m2|c)P(s|c)
            double p_l = lastLoc[r2][c2];
            double p_cl  = currentLoc.getOutcomeProb(r1, c1, r2, c2);
            double p_m1c = detectorM1.getOutcomeProb(r1, c1, 0, 0);
            if (!m1) p_m1c = Formulas.minus(1.0, p_m1c);
            double p_m2c = detectorM2.getOutcomeProb(r1, c1, 0, 0);
            if (!m2) p_m2c = Formulas.minus(1.0, p_m2c);
            double p_sc = soundSensor.getOutcomeProb(sound.row(), sound.col(), r1, c1);
            if (debug) System.out.printf("    Probs being multiplied for last location (%d, %d): %f %f %f %f %f%n",
                                          r2, c2, p_l, p_cl, p_m1c, p_m2c, p_sc);
            multi = Formulas.add(Formulas.multi(p_l, p_cl, p_m1c, p_m2c, p_sc), multi);
          }
        }
        nextLoc[r1][c1] = multi; // Store
      }
    }

    if (debug) {
      System.out.println("\nBefore normalization:");
      printPrediction(nextLoc);
      System.out.println("\nAfter normalization:");
    }

    // Normalize data and update last location for next iteration
    Formulas.normalize(lastLoc, nextLoc);
    printPrediction(lastLoc);
  }

  public void setAll(double val) {
    lastLoc = new double[rows][cols];
    nextLoc = new double[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++)
        lastLoc[r][c] = val;
    }
  }

  public void printPrediction(double[][] lst) {
    for (int r = 0; r < lst.length; r++) {
      for (int c = 0; c < lst[1].length; c++) {
        System.out.printf("  %3.8f ", lst[r][c]);
      }
      System.out.print("\n");
    }
    System.out.print("\n");
  }
}
