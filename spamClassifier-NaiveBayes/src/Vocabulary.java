import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is an object storing a list of words and a counter of their occurrence.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 11-13-2023.
 */
public class Vocabulary {
  // Map containing the keys of words and their respective value of occurrence
  private Map<String, Integer> vocab;
  // Total emails that were used to derive this vocabulary
  private int total_email;

  public Vocabulary() {
    this.vocab = new HashMap<>();
  }

  /**
   * Add a new word with a value or increment existing word by the value.
   *
   * @param word the specified word
   * @param value the value for this word
   */
  public void addWordValue(String word, int value) {
    Integer curr_val = vocab.get(word);
    Integer new_val = Integer.valueOf(value);
    if (curr_val == null) {
      vocab.put(word, new_val);
    } else {
      vocab.replace(word, curr_val+new_val);
      //throw new IllegalArgumentException("There is already a probability associated with this word \""+ word +"\".");
    }
  }

  /**
   * Change a words value within this list.
   *
   * @param word the specified word
   * @param value the new value for the word
   */
  public void changeWordValue(String word, int value) {
    Integer val = Integer.valueOf(value);
    if (vocab.get(word) == null) {
      throw new IllegalArgumentException("The word \""+ word +"\" does not exist in the list.");
    } else {
      vocab.replace(word, val);
    }
  }

  /** Set the total email that were used to derive this vocabulary */
  public void setEmailTrainTotal(int total) {
    total_email = total;
  }

  /** Convert all the values of the words to their probabilities. Note, the values must be doubles. */
  public void convertAllValue() {
    vocab.replaceAll((key, value) -> (value+1) / (total_email+2));
  }

  /** Return the value associated with the specified word. */
  public int getValue(String word) {
    Integer val = vocab.get(word);
    if (val == null)
      return 0;
    return val;
  }

  /** Get an array of all words in the vocabulary (without values) */
  public String[] getAllWords() {
    return vocab.keySet().toArray(new String[vocab.size()]);
  }

  /** Return the total email that were used to derive this vocabulary */
  public int getEmailTrained() {
    return total_email;
  }

  /** Return the number of words in this vocabulary */
  public int length() {
    return vocab.size();
  }

  // Print the vocabulary by printing out the words.
  // If printVal true, then also print the values associated with the words
  public void printVocab(boolean printVal) {
    Set<String> words = vocab.keySet();
    int i = 0;
    int size = words.size();

    System.out.print("{");
    for (String word : words) {
      System.out.printf("'%s'", word);
      if  (printVal) {
        System.out.print(": " + vocab.get(word));
      }
      if (i < size-1)
        System.out.print(",");
      i++;
    }
    System.out.println("}");
  }

}
