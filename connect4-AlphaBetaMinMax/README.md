# Connect-4 AI Game

This project is a Connect-4 game that includes an AI opponent using Minimax, Alpha-Beta Pruning, and heuristic evaluation algorithms.

## Project Structure

- `Main`: Runs the Connect-4 game, allows user to play against the AI, and handles game setup and interaction.
- `GameState`: Enum representing the game states: `IN_PROGRESS`, `MAX_WIN`, `MIN_WIN`, `TIE`.
- `MiniMaxInfo`: Record for storing the value and column of a Minimax move.
- `Player`: Enum representing the players: `MAX` and `MIN`.
- `AlphaBetaMiniMax`: Implements the AI using Minimax with Alpha-Beta pruning and heuristic evaluation.
- `Board`: Represents the game board, manages moves, and checks game state.

## Usage

1. Run the `Main` class to start the game.
2. Follow the prompts to set up the game, including:
    - Selecting the AI mode (A: Minimax, B: Alpha-Beta, C: Alpha-Beta with heuristic and depth limit).
    - Enabling or disabling debugging information.
    - Entering the board dimensions and number of consecutive pieces needed to win.
3. Play against the AI by choosing whether the human or the AI goes first.
4. Enter your move when prompted, or watch the AI make its move.

The game will continue until a win or tie is detected. You can choose to play again or exit after each game.

## Algorithm

The AI uses three different algorithms based on the selected mode:
- **Minimax Algorithm (Mode A)**: Explores all possible moves to find the optimal one, considering the opponent's optimal responses.
- **Alpha-Beta Pruning (Mode B)**: Optimizes the Minimax algorithm by pruning branches that cannot affect the final decision, reducing the number of nodes evaluated.
- **Alpha-Beta Pruning with Heuristic (Mode C)**: Adds a heuristic evaluation function and a depth limit to the Alpha-Beta pruning, allowing the AI to make decisions faster by evaluating non-terminal states.

## Acknowledgments

This project was developed as part of a college assignment. The code is provided "as is" without any warranties or guarantees.

## Author

Diego Lopez