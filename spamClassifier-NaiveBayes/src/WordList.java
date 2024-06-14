import java.util.HashSet;
import java.util.Set;

/**
 * This class is an object storing a list of words.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 11-13-2023.
 */
public class WordList {
  private Set<String> words;

  public WordList() {
    this.words = new HashSet<>();
  }

  /** Add a word to the list */
  public void addWord(String word) {
    words.add(word);
  }

  /** Add one or more array of words to the list */
  public void addWords(String[]... words_N) {
    for (String[] words1 : words_N) {
      for (String word : words1) {
        words.add(word);
      }
    }
  }

  /** Return all the words in this list as an array */
  public String[] getAllWords() {
    return words.toArray(new String[words.size()]);
  }

  /** Remove a word from this list */
  public void removeWord(String word) {
    words.remove(word);
  }

  /** Remove all words from this list */
  public void removeAll() {
    words.clear();
  }

  /** Check if a word exists within this list */
  public boolean wordExists(String word) {
    return words.contains(word);
  }

  /** Return the number of words in this list */
  public int length() {
    return words.size();
  }
}
