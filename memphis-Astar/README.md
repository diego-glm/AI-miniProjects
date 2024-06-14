# A* Search for Optimal Path in Road Network

This Java program implements the A* search algorithm to find the most optimal path between two locations in a road network. It utilizes speed limits and allows for a specified number of speeding instances to optimize travel time.

## Project Structure

- `Location`: Represents a geographical location with latitude and longitude.
- `Main`: The implementation of the A* search algorithm.
- `Node`: Represents a node in the search tree with details about the current state and path cost.
- `Road`: Defines a road segment with start and end locations, speed limit, and name.
- `RoadNetwork`: Manages locations and roads, provides methods to access graph data.
- `SearchAStar`: Implements the A* search algorithm with heuristic functions and pathfinding logic.
- `Geometry`: Contains utility methods for geometric calculations and direction handling.
- `PriQueue`: Implements a priority queue used in the A* algorithm for node prioritization.

## Usage

To run the program, compile and execute the `Main` class. The program will prompt you for the following inputs:

1. Starting location ID
2. Ending location ID
3. Number of times allowed to speed
4. Whether to enable debugging information (y/n)

After providing the necessary inputs, the program will run the A* search algorithm to find the optimal path between the starting and ending locations. It will display the total travel time, the number of nodes visited during the search, the route found, and the GPS directions.

## Algorithm

The program uses the A* search algorithm, combining path cost (`g_cost`) with a heuristic (`h_cost`) to guide the search towards the goal efficiently. It considers road speed limits and optimizes based on predicted travel times.

## Acknowledgments

This project was developed as part of a college assignment. The code is provided "as is" without any warranties or guarantees.

## Author

Diego Lopez