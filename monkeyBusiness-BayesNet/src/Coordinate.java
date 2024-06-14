/**
 * This is an object that mimics a coordinate which stores an x and y.
 * It can also optionally store an associated probability.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-24-2023.
 */
public record Coordinate(byte row, byte col, Double prob) {
  /** This constructor treats the object as a 2D coordinate with an associated probability. */
  public Coordinate(int r, int c, double p) {
    this((byte) r, (byte) c, Double.valueOf(p));
  }

  /** This constructor treats the object as a 2D coordinate only. */
  public Coordinate(int r, int c) {
    this((byte) r, (byte) c, null);
  }
}
