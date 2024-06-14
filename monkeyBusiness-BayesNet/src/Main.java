import java.io.File;
import java.util.Scanner;

/**
 * The main function for running the Monkey Business project.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-24-2023.
 */
public class Main {
  private static boolean debug = false;

  public static void main(String[] args) throws Exception {
    Scanner scan = new Scanner(System.in);
    BayesNet net;
    String file, input, items[];

    System.out.print("Enter text file for monkey business:");
    file =  scan.nextLine();

    System.out.print("\nDisplay debugging info (y/n):");
    input = scan.nextLine();
    if (input.equals("y")){
      debug = true;
    } else if (input.equals("n")) {
      debug = false;
    } else {
      scan.close();
      throw new IllegalArgumentException("Invalid Debug input");
    }
    System.out.println("\n");
    scan.close();

    scan = new Scanner(new File(file));
    items = scan.nextLine().split(" ");
    int m = Integer.parseInt(items[0]);
    int n = Integer.parseInt(items[1]);

    // Avoid using grids of size of 1x1, 1x2, 2x1
    if ((m <= 1 && n <= 1) || (m == 1 && n == 2) || (m == 2 && n == 1)) {
      scan.close();
      throw new IllegalArgumentException("Invalid map size input for problem.");
    }

    // Running the Bayes Nets over given file input
    boolean m1, m2;
    int r_s, c_s;
    net = new BayesNet(m, n, debug);
    while (scan.hasNextLine()) {
      items = scan.nextLine().split(" ");

      if (items[0].equals("1")) {
        m1 = true;
      } else if (items[0].equals("0")) {
        m1 = false;
      } else {
        scan.close();
        throw new IllegalArgumentException("Invalid m1 input for problem.");
      }

      if (items[1].equals("1")) {
        m2 = true;
      } else if (items[1].equals("0")) {
        m2 = false;
      } else {
        scan.close();
        throw new IllegalArgumentException("Invalid m2 input for problem.");
      }

      r_s = Integer.parseInt(items[2]);
      c_s = Integer.parseInt(items[3]);

      net.runDistributions(m1, m2, new Coordinate(r_s, c_s));
    }

    scan.close();
  }
}
