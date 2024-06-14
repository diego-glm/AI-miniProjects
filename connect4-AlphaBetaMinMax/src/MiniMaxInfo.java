/**
 * Description of MyClass
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 10-05-2023.
 */
public record MiniMaxInfo(int value, byte col) {
  public MiniMaxInfo(int value) {
    this(value, (byte) -5);
  }
}
