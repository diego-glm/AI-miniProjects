

/**
 * A representation game of Nim game, where players take turns removing sticks
 * from piles until there are no sticks left.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 12-04-2023.
 */

public class Nim_Game {
  // The active piles of sticks
  private int[] piles;
  // Copy of the initial piles of sticks
  private final int[] orig_piles;
  // Next player to play
  private Player next_ply;
 

  public Nim_Game(int... stick_per_pile) {
    piles = new int[stick_per_pile.length];
    orig_piles = new int[stick_per_pile.length];
    next_ply = Player.MAX;

    for (int i = 0; i < stick_per_pile.length; i++) {
      piles[i] = stick_per_pile[i];
      orig_piles[i] = stick_per_pile[i];
    }
  }

  /**
   * Set the piles back to the original state and first player back to
   * being the current player.
   */
  public void resetPiles() {
    next_ply = Player.MAX;
    for (int i = 0; i < orig_piles.length; i++) {
      piles[i] = orig_piles[i];
    }
  }

  /**
   * Getthe current state of the game.
   *
   * @return integer value representing the state of the game as
   *    digit 4th is the current player, 1 for first player, 2 for second player
   *    digit 3rd amount of sticks in pile 0
   *    digit 2nd amount of sticks in pile 1
   *    digit 1st amount of sticks in pile 2
   */
  public int get_state() {
    int s = 0;

    s += next_ply.getNumber() * 1000;
    for (int i = 0; i < piles.length; i++) {
      s += piles[i] * Math.pow(10, piles.length - 1 - i);
    }

    return s;
  }

  /**
   * Check the current winning state of the game
   * @return an integer that represent first player wins (1),
   *         second player wins (2), in progress (0)
   */
  public int getWinner() {

    for (int i = 0; i < piles.length; i++) {
      if (piles[i] != 0) {
        return 0;
      }
    }

    return next_ply.getNumber();
  }

  /**
   * Get the number of non-empty piles in an array.
   *
   * @return count of non-empty piles (more or equal to one stick in teh pile).
   */
  public int nonEmpty_piles() {
    int count = 0;
    for (int i = 0; i < piles.length; i++) {
      if (piles[i] != 0)
        count++;
    }

    return count;
  }

  /**
   * Updates the number of sticks in a pile based on an action,
   * and switches to the next player.
   *
   * @param action the action of taking sticks from a pile. It is an
   *               integer value that consists of two digits. The first digit represents the pile number (loc), and
   *               the second digit represents the number of sticks to be taken from that pile (sticks).
   * @throws ArrayIndexOutOfBoundsException for if attempting to remove move sticks than what the piles has
   */

  public void take_stick(int action) throws ArrayIndexOutOfBoundsException {
    int loc = (action / 10) % 10;
    int sticks =  action % 10;
    if (loc > piles.length && 0 <= loc)
      throw new ArrayIndexOutOfBoundsException("There are only 3 piles (0, 1, 2). Pile "+ loc + " does not exits.");
    if (piles[loc] < sticks && 0 < sticks)
      throw new ArrayIndexOutOfBoundsException("Pile " + loc + " only has " + piles[loc] + " sticks which is less than the sticks you want to take (" + sticks + ")");
    piles[loc] -= sticks;
    next_ply = next_ply.otherPlayer();
  }
}
