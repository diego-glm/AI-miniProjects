# Monkey Business Detection using Bayesian Networks

This project implements a Bayesian Network to predict the location of a monkey on a grid based on inputs from motion and sound sensors.

## Project Structure

- `BayesNet`: Implements the Bayesian Network, including methods to initialize and update probabilities based on sensor inputs.
- `Coordinate`: Represents a 2D coordinate with an optional associated probability.
- `DistribList`: Manages distributions of probabilities for various sensors.
- `Formulas`: Contains helper methods for mathematical operations and probability calculations.
- `Main`: Handles user input and runs the Bayesian Network based on provided data.

## Usage

To run the program, compile and execute the `Main` class. The program will prompt you for the following inputs:

1. The text file containing the problem data (grid dimensions and sensor observations).
2. Whether to display debugging information (y/n).

After providing the necessary inputs, the program will initialize the Bayesian network and calculate the probability distribution of the monkey's current location based on the sensor observations. The initial distribution and the predicted locations at each time step will be displayed.

## Algorithm

The project uses a Bayesian Network to model the probability distribution of the monkey's location on a grid. The network considers the following inputs:

- Motion sensor 1 (detectorM1)
- Motion sensor 2 (detectorM2)
- Sound sensor (soundSensor)

The algorithm updates the probability distribution of the monkey's location based on the inputs from these sensors, using Bayes' Theorem to combine prior and likelihood probabilities.

## Acknowledgments

This project was developed as part of a college assignment. The code is provided "as is" without any warranties or guarantees.

## Author

Diego Lopez