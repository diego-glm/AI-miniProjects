import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The SearchAStar class is an object that preforms A* search on a given graph. Once run, the
 * constructor will set up a search tree with one element (the starting node). When startSearch is
 * run, it will build the search tree. It has a accessible node which represent the best optimal
 * goal state the algorithm found. The heuristic will be the time to traverse the Euclidean distance
 * at the twice the speed of the highest speed limit from starting to ending points. It implements a
 * priority queue.
 *
 * @author Diego Lopez
 * @version 1.0 09-19-2023
 */
public class SearchAStar {
  // Search elements
  PriQueue<Node, Double> frontier = new PriQueue<Node, Double>(true);
  Map<Location, Node> reached= new HashMap<>();
  RoadNetwork graph;
  Location end;
  Node currentN;
  // Tracking process
  Boolean trb;
  int totalNodeVisited;

  // Constructor
  public SearchAStar(RoadNetwork graph, Location start, Location end, int speeding, Boolean troubleshot) {
    this.graph = graph;
    this.end = end;
    this.trb = troubleshot;
    // Start of Search Tree
    double hCost = heuristic(start);
    currentN = new Node(start, speeding, 0.0, hCost, hCost);
    frontier.add(currentN, currentN.f_cost());
    reached.put(start, currentN);
    totalNodeVisited = 0;
  }


  /**
   * This will run an actual A* search on the data.
   *
   * @return Node The goal state node that has links back to the node that was traverse in the search tree.
   */
  public Node startSearch() {

    while (!frontier.isEmpty()) {
      currentN = frontier.remove();
      totalNodeVisited++;
      troubleshot("Visiting", currentN);
      if (isGoal(currentN)) {return currentN;}
      for (Node child: expand(currentN)) {
        if (!reached.containsKey(child.s()) || (child.f_cost() < reached.get(child.s()).f_cost()) ) {
          reached.put(child.s(), child);
          frontier.add(child, child.f_cost());
          troubleshot("Adding", child);
        } else {troubleshot("Skipping", child);}
      }
    }

    return null;
  }


  /**
   * This method will expand the node into it children and return all of them as a list of nodes.
   *
   * @param parent The current node of focus
   * @return List<Node> The children produced from the parent node
   */
  public List<Node> expand(Node parent) {
    List<Node> children = new ArrayList<>();
    int spd = parent.speeding();

    int active = 1;
    if (spd > 0) {
      active = 2;
    }
    for (int i = 0; i < active; i++) {
      for (Road path : actions(parent.s())) {
        Location child_s = result(path);
        double child_Gcost = parent.g_cost() + actionCost(path, i);
        double child_Hcost = heuristic(child_s);
        double child_Fcost = child_Gcost + child_Hcost;
        Node child = new Node(parent, child_s, spd-i, path, child_Gcost, child_Hcost, child_Fcost);
        children.add(child);
      }
    }

    return children;
  }


  /**
   * The possable road that the location s can traverse through.
   *
   * @param s The location of interest
   * @return List<Road> The roads connected to the location
   */
  public List<Road> actions(Location s) {
    return graph.getAdjacentRoads(s);
  }


  /** The ending location that is found if traversing through the given road
   *
   * @param choice The road of interest.
   * @return Location The end location of this road
   */
  public Location result(Road choice) {
    return graph.getLocation(choice.endId());
  }

  /**
   * The time that it will take to traverse a road.
   *
   * @param road The road of interest
   * @param speeding Whether or not speeding was taken (0 for no, and 1 for doubling the speed limit)
   * @return double The time it took to traverse the given road
   */
  public double actionCost(Road road, int speeding) {
    Location start = graph.getLocation(road.startId());
    Location end = graph.getLocation(road.endId());
    int speed = road.speedLimit();

    if (speeding == 1) {  speed *= 2; }

    return Geometry.predictTimeInSecFromLocations(start, end, graph, speed);
  }


  /**
   * The time it would take to traverse the Euclidean distance at the twice the speed of the highest speed limit
   * from starting to ending points
   *
   * @param s The given location
   * @return double The Time it took to each.
   */
  public double heuristic(Location s) {
    double time = Geometry.predictTimeInSecFromLocations(s, end, graph, 130); // highest speed is 65. If speeding 130.
    return time;
  }


  /**
   * Determine if a node is in a goal state.
   *
   * @param n The node in question
   * @return boolean true or false
   */
  public boolean isGoal(Node n) {
    return n.s().id() == end.id();
  }


  /**
   * For troubleshooting the A* search. It will print out relevant information of the search cycle and building of the search tree.
   *
   * @param message The type of message to print ("Visiting", "Skipping", "Adding")
   * @param n The specific node to which information will be extracted from
   */
  public void troubleshot(String message, Node n) {
    if (trb) {
      String spd = "false";
      if (n.previous() == null) {
        spd = "null";
      } else if ( n.speeding() < n.previous().speeding()) {
        spd = "true";
      }

      System.out.print("\n");
      switch (message) {
        case "Visiting":
          String parent = "null";
          if (!(n.previous() == null)) {
            parent = Long.toString(n.previous().s().id());
          }
          System.out.printf("\nVisiting [State= %d, parent= %s, speeding= %s, g= %f, h= %f, f= %f]",
                            n.s().id(), parent, spd, n.g_cost(), n.h_cost(), n.f_cost());
          break;
        case "Skipping":
          System.out.printf("\tSkipping [State= %d, parent= %d, speeding= %s, g= %f, h= %f, f= %f] (already on frontier with lower cost).",
                            n.s().id(), n.previous().s().id(), spd, n.g_cost(), n.h_cost(), n.f_cost());
          break;
        case "Adding":
          System.out.printf("\tAdding [State= %d, parent= %d, speeding= %s, g= %f, h= %f, f= %f] to frontier.",
                            n.s().id(), n.previous().s().id(), spd, n.g_cost(), n.h_cost(), n.f_cost());
          break;
      }
    }
  }
}
