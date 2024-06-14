import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is an object for replicating a Naive Bayes Classification
 * for emails which will classify email into spam or not-spam.
 *
 * This class requires to be initialized, train, and tested. It implements
 * the MAP hypothesis to classify the emails. It will ultimately compares the
 * log-probability for a more accessible range of value to compare probabilities.
 *
 * Additional note, Classifier will not store final log-probabilities for the
 * derived from the training set, rather it will store the count of words occurrences
 * (which are needed to derive the logs). This is more memory efficient but suffers
 * from time complexity.
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 11-13-2023.
 */
public class Classifier {
  // Track of how many emails from the spam train set did the given word appear
  Vocabulary count_spam_words;
  // Track of how many emails from the not-spam train set did the given word appear
  Vocabulary count_ham_words;
  // List contain all word found in the training set of emails
  private WordList allWords;
  // Counts how many feature words were found in the given test email
  private int features_count;
  // Tracks how many of the test emails were correct classified
  private int correct;
  // Tracks how many of the test emails were incorrect classified
  private int wrong;

  public Classifier() {
    count_spam_words = new Vocabulary();
    count_ham_words  = new Vocabulary();
    correct = 0;
    wrong = 0;

  }

  /**
   * Train the classifier with a given sample of spam emails and not-spam emails.
   * By train, it does not compute the log probabilities of the training data, rather
   * it stores a copy of information needed to compute the
   *
   * @param file_spam the name file containing the train sample of spam emails
   * @param file_ham  the name file containing the train sample of not-spam emails
   * @throws FileNotFoundException If given file was not found
   */
  public void train(String file_spam, String file_ham) throws FileNotFoundException {

    Converter.file_to_vocab(file_spam, count_spam_words);
    Converter.file_to_vocab(file_ham,  count_ham_words);
    allWords = Converter.allWordsCombined(count_spam_words, count_ham_words);
  }

  /**
   * Test the classifier with a sample of emails that are only spam or not-spam.
   *
   * @param file the file name containing the sample of emails
   * @param sph whether this file contains only spam or not-spam
   * @throws FileNotFoundException If given file was not found
   */
  public void test(String file, Spham sph) throws FileNotFoundException {
    Scanner scan = new Scanner(new File(file));
    WordList email;
    int count = 0;

    while (scan.hasNextLine()) {

      System.out.printf("TEST %d ", ++count);
      // Get a list of words that were found in a email iteratively
      email = Converter.stream_emails(scan);
      // Run classify to determine a prediction and compare with reality
      if (classify(email) == sph) {
        System.out.println("right");
        correct++; // Predicted correctly
      } else {
        System.out.println("wrong");
        wrong++; // Predicted incorrectly
      }
    }
    scan.close();
  }

  /** Print out the amount of emails that it tested on and how many of them were correctly classified. */
  public void printResults() {
    System.out.printf("Total: %d/%d emails classified correctly.\n", correct, correct+wrong);
  }

  /**
   * Classified a given email as spam or not-spam. It compares the sum of their log-probabilities
   * rather than their probabilities product.
   *
   * @param email contains a list of words that appear in the given email
   * @return SPAM if the log probability is higher than HAM. Otherwise, return HAM. IF equals,
   *          then return SPAM since it better to not to over-estimate for not-spam.
   */
  private Spham classify(WordList email) {
    double is_spam = probability(email, count_spam_words);
    double is_ham  = probability(email, count_ham_words);

    System.out.printf("%d/%d features true %.3f %.3f ", features_count, allWords.length(), is_spam, is_ham);

    if (is_spam >= is_ham) {
      System.out.print("spam ");
      return Spham.SPAM;
    }
    System.out.print("ham ");
    return Spham.HAM;

  }

  /**
   * Calculate the log-probability with smoothing for the given email and given list of counts of words
   * from the training sample. It also always re-calculates the probabilities.
   *
   * @param email contain a list of words that were found in the email
   * @param spham contains a list of words with their count in the spam/not-spam email training set
   * @return the log-probability of the email being spam/not-spam
   */
  private double probability(WordList email, Vocabulary spham) {
    String[] all_words = allWords.getAllWords();
    double ln_sum = 0.0;
    int sphamEmails = spham.getEmailTrained();
    int totalEmails = count_spam_words.getEmailTrained() + count_ham_words.getEmailTrained();

    features_count = 0;
    ln_sum += ln(sphamEmails) - ln(totalEmails);
    for (String word : all_words) {
      if (email.wordExists(word)) {
        features_count++;
        // ln(P_smooth(words|spham)) = ln(#word+1/#spham+2) = ln(#word+1)- ln(#spham+2)
        ln_sum += ln(spham.getValue(word)+1) - ln(sphamEmails+2);
      } else {
        // ln(P_smooth(~words|spham)) = ln([1 - P(words|spham)]_smooth) = ln(#spham-#word+1) - ln(#spham+2)
        ln_sum += ln(sphamEmails-spham.getValue(word)+1) - ln(sphamEmails+2);
      }
    }

    return ln_sum;
  }

  // Natural log
  private double ln(int num) {
    return Math.log(num);
  }
}