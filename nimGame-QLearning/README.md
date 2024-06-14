# Nim Game with Q-Learning AI

This project implements a Nim game with an AI player that uses Q-Learning to learn optimal strategies. The AI trains by playing simulated games and updates its Q-values based on the rewards and the maximum Q-value of the next state.

## Project Structure

- `Main`: The entry point of the program. It handles user input, initializes the game and AI, and manages the game loop.
- `Nim_Game`: Represents the Nim game, manages the game state, and handles player moves.
- `Player`: Enum to represent the players (MAX and MIN) in the game.
- `Q_LearningAI`: Implements the Q-Learning algorithm to train the AI on how to play the Nim game.
- `Q_table`: Manages the Q-values for different state-action pairs.

## Usage

1. Run the `Main` class.
2. Input the initial number of sticks in each pile (values should be between 0-9).
3. Input the number of simulated games for the AI to train.
4. Play the game by choosing whether the user or the computer moves first.
5. Take turns removing sticks from the piles as per the rules of Nim.
6. After each game, decide whether to play again or exit.

The game alternates turns between the user and the AI, and the AI's moves are determined by its learned Q-values.

## Algorithm

The Q-Learning algorithm is used for training the AI. The process involves:

1. **Initialization**: Set initial Q-values to 0.
2. **Policy**: Randomly select actions to explore the state-action space.
3. **Q-value Update**: Update Q-values based on the reward received and the maximum Q-value of the next state:

   \[
   Q(s, a) \leftarrow Q(s, a) + \alpha \left[ r + \gamma \max_{a'} Q(s', a') - Q(s, a) \right]
   \]

   where \( s \) is the current state, \( a \) is the action taken, \( r \) is the reward received, \( s' \) is the next state, \( \alpha \) is the learning rate, and \( \gamma \) is the discount factor.

4. **Training**: The AI plays numerous simulated games to update its Q-values and learn the optimal strategy.

## Acknowledgments

This project was developed as part of a college assignment. The code is provided "as is" without any warranties or guarantees.

## Author

Diego Lopez