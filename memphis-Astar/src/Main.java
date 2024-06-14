import java.io.InputStream;
import java.util.Scanner;

/**
 * The main for running the program that gets graph of locations and roads. Given two locations and the amount of times to speed, it
 * will implement A* search to find the most optimal path which is based on distance and speed limit.
 *
 * @author Diego Lopez
 * @version 1.0 09-19-2023
 */
public class Main {
  static RoadNetwork graph;

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    readGraph("memphis-medium.txt");
    // Prompt User
    System.out.print("Enter starting location ID: ");
    long start = Long.parseLong(scan.nextLine());
    if (start == 0)
      System.exit(1);

    System.out.print("\nEnter ending location ID: ");
    long end = Long.parseLong(scan.nextLine());
    if (end == 0)
      System.exit(1);

    System.out.print("\nHow many times are you allowed to speed? ");
    int spd = Integer.parseInt(scan.nextLine());
    if (spd < 0)
      System.exit(1);

    System.out.print("\nDo you want debugging information (y/n)? ");
    String verbatim = scan.nextLine();
    Boolean trb = false;
    if (verbatim.equals("y")) {
      trb = true;
    } else if (verbatim.equals("n")) {
      trb = false;
    } else {
      System.out.println("\nBad input");
      System.exit(1);
    }

    Location st = graph.getLocation(start);
    Location ed = graph.getLocation(end);

    SearchAStar gps = new SearchAStar(graph, st, ed, spd, trb); // sets up the parameters
    Node route = gps.startSearch(); // Actually preforms A*

    // Print out to result
    System.out.printf("\n\nTotal travel time in seconds: %f ", route.g_cost());
    System.out.printf("\nNumber of nodes visited: %d \n", gps.totalNodeVisited);

    System.out.println("\nRoute found is: ");
    printRoute(route);

    System.out.println("\nGPS directions: ");
    printDirections(route);

    System.out.println("You have arrived!");

    scan.close();
  }


  /**
   * The given string is interpreted as the file with a graph name which the
   * program will store as a RoadNetwork and update the global graph.
   *
   * @param filename a string that represent a file name with path
   */
  public static void readGraph(String filename) {
    InputStream is = Main.class.getResourceAsStream(filename);
    if (is == null) {
      System.err.println("Bad filename: " + filename);
      System.exit(1);
    }
    Scanner scan = new Scanner(is);

    graph = new RoadNetwork();

    while (scan.hasNextLine()) {
      String line = scan.nextLine();
      String[] pieces = line.split("\\|");

      if (pieces[0].equals("location")) {
        long id = Long.parseLong(pieces[1]);
        double lat = Double.parseDouble(pieces[2]);
        double longi = Double.parseDouble(pieces[3]);
        Location loc = new Location(id, lat, longi);
        graph.addLocation(loc);
      } else if (pieces[0].equals("road")) {
        long startId = Long.parseLong(pieces[1]);
        long endId = Long.parseLong(pieces[2]);
        int speed = Integer.parseInt(pieces[3]);
        String name = pieces[4];
        Road r1 = new Road(startId, endId, speed, name);
        Road r2 = new Road(endId, startId, speed, name);
        graph.addRoad(r1);
        graph.addRoad(r2);
      }
    }
    scan.close();
  }


  /**
   * Prints put the "Route found is" section which is basic data of one path in a search tree
   *
   * @param route the goal state within the search tree.
   */
  public static void printRoute(Node route) {
    String spd = "not speeding";
    if (route.speeding() == 1) {
      spd = "speeding";
    }
    if (route.previous() == null) {
      System.out.printf("%d (starting location)\n", route.s().id());
    } else {
      printRoute(route.previous());
      System.out.printf("%d (%s, %s)\n", route.s().id(), route.path().name(), spd);
    }
  }


  /**
   * Print the "GPS directions:" recursively with help of an helper function
   *
   * @param currentNode
   */
  public static void printDirections(Node currentNode) {
    String back_orient = Geometry.compassDirection(currentNode.previous().previous().s(), currentNode.previous().s());
    String left_right = Geometry.compassDirLeftRight(back_orient, currentNode.previous().s(), currentNode.s());
    double[] track = printHelper(currentNode);

    System.out.printf("Turn %s onto %s\n", left_right, currentNode.path().name());
    System.out.printf("\tDrive for %.2f miles (%.2f seconds)\n", track[0], track[1]);
  }


  /**
   * Helper function that recursively goes from bottom of the search tree to the top. Returning from the
   * recursive call will return necessary data in double[]. If the route
   * name is the same, then the information will be "carry on" with the accumulation of the current node
   * (distance and time). Once the names are different, print out the carry on data and restart the carry on of
   * data. It also implement the Geometry class to calculate the direction (NESW or left/right).
   * Base case would be being 2 nodes before the reaching the top of the search tree.
   *
   * @param currentNode
   * @return double[] The carry on data which is used for paths that are taken on the same road consecutively.
   */
  public static double[] printHelper(Node currentNode) {
    // Previous state info:
    Node backwardNode = currentNode.previous();
    Location back_Loc = backwardNode.s();
    Road back_rd = backwardNode.path();
    // Current state info:
    Location curr_Loc = currentNode.s();
    Road curr_rd = currentNode.path();
    // Info from previous-->current
    double miles = Geometry.getDistanceInMiles(back_Loc, curr_Loc);
    double time = currentNode.g_cost() - backwardNode.g_cost();

    if (backwardNode.previous() == null) { // Base case: Last parent node reached.
      return new double[] { miles, time, 1.0 };
    }

    double[] track = printHelper(backwardNode); // --Recursive call--

    boolean printInfoSoFar = true;

    if (curr_rd.name().equals(back_rd.name())) { // Same road name as previous, do not print, carry-on miles and time
      track[0] += miles;
      track[1] += time;
      printInfoSoFar = false;
    }

    // This is for finding the turning left or right according to previousorientation
    // If ...loc0->loc1->loc2 & loc0->loc1=="NS or EW", then return left/right
    // according to "longitDiff or latDiff from loc1->loc2, respectively"
    String back_orient = Geometry.compassDirection(backwardNode.previous().s(), back_Loc); // loc0->loc1 == "NESW"
    String left_right = Geometry.compassDirLeftRight(back_orient, back_Loc, curr_Loc);    // "longitDiff/latDiff from
                                                                                          // loc1->loc2"

    if (printInfoSoFar) {
      if (track[2] == 1.0) { // Print what has been carry-on "since the starting location."

        String curr_orient = Geometry.compassDirection(back_Loc, curr_Loc);
        System.out.printf("Head %s on %s\n", curr_orient, back_rd.name());
        System.out.printf("\tDrive for %.2f miles (%.2f seconds)\n", track[0], track[1]);

      } else { // Print what has been carry-on "from non-starting location"

        System.out.printf("Turn %s onto %s\n", left_right, back_rd.name());
        System.out.printf("\tDrive for %.2f miles (%.2f seconds)\n", track[0], track[1]);
      }
      // If you printed carry-on, then it means "dump info on screen" and "restart track" with current nodes values.
      return new double[] { miles, time, 0.0 };
    }

    return track; // Carry-on miles and time to return recursive call

  }
}
