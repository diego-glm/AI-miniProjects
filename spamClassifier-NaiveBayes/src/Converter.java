import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is an object storing the boiler computation code needed by the
 * classifier class. It mainly contains text file interpretation, Vocabulary merging,
 * and string filtering.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 11-13-2023.
 */
public class Converter {

  /**
   * Within a active scanner reading from a email sample file, it will return all
   * words that were in the next upcoming email.
   *
   * @param scan reading from a source containing emails samples
   * @return word list of the occurrence of every unique words in the email.
   */
  public static WordList stream_emails(Scanner scan) {
    WordList wordAppeared = new WordList();
    String[] words;
    String line;

    boolean stop = false;
    while (scan.hasNextLine() && !stop) {
      line = scan.nextLine();
      if (line.length() == 0) { // Skip empty lines
        continue;
      } else if (line.charAt(0) == '<') { // Skip tags
        if (line.charAt(1) == '/' && line.charAt(2) == 'B') { // End of the current email
          stop = true;
        }
        continue;
      }

      // Analysis the current line of the email and appending the appropriate words to the list
      line = removePuncLowercase(line);
      words = line_to_words(line);
      for (String word : words) {
        if (!wordAppeared.wordExists(word))
          wordAppeared.addWord(word);
      }
    }
    return wordAppeared;
  }

  public static void file_to_vocab(String fileName, Vocabulary vocab) throws FileNotFoundException {
    Scanner scan = new Scanner(new File(fileName));
    WordList repeatedWords = new WordList();
    String[] words;
    int count_email = 0;
    String line;

    while (scan.hasNextLine()) {
      line = scan.nextLine();
      if (line.length() == 0) { // Skip empty lines
        continue;
      } else if (line.charAt(0) == '<') { // Skip tags
        if (line.charAt(1) == 'S') { // The start of a new email
          count_email++;
          repeatedWords.removeAll();
        }
        continue;
      }

      // Analysis the current line of the email and appending the appropriate words to the list
      // and update the appearance of the word per email
      line = removePuncLowercase(line);
      words = line_to_words(line);
      for (String word : words) {
        if (!repeatedWords.wordExists(word)) {
          vocab.addWordValue(word, 1);
          repeatedWords.addWord(word);
        }
      }
    }

    scan.close();
    vocab.setEmailTrainTotal(count_email);
  }

  // Return a word list containing the all the words form two Vocabularies once.
  public static WordList allWordsCombined(Vocabulary vocab1, Vocabulary vocab2) {
    WordList combine = new WordList();
    combine.addWords(vocab1.getAllWords(), vocab2.getAllWords());
    return combine;
  }

  // Remove punctuation and put every letter lower case,
  private static String removePuncLowercase(String line) {
    String sentence = line.replaceAll("[^\\p{L} ]", "").toLowerCase();
    // int len = sentence.length();
    // if (sentence.charAt(len-1) == ' ')
    //   sentence = sentence.substring(0, len-1);
    return sentence;
  }

  // Convert a string that represents a sentence into a array of string representing words
  private static String[] line_to_words(String line) {
    return line.split("\\s+");
  }

  // Some testing
  public static void main(String[] args) throws Exception {
    String lineTest = "It's \ta Word (!@#$%^&*{}[];':\")<>,.\n";
    String[] wordsTest;

    System.out.printf("\"%s\"\n", lineTest);
    lineTest = removePuncLowercase(lineTest);
    System.out.printf("\"%s\"\n", lineTest);

    wordsTest = line_to_words(lineTest);
    for(int i = 0; i < wordsTest.length; i++) {
      System.out.printf("\"%s\" : ", wordsTest[i]);
    }

  }
}
