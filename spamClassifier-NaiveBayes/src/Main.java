import java.util.Scanner;

/**
 * The main function for running the Naive Bayes Classification project.
 * (A.K.A Email Spam Detection)
 *
 * @author Diego Gabriel Lopez Murillo
 * @version 1.0 11-13-2023.
 */
public class Main {

  public static void main(String[] args) throws Exception {
    Scanner scan = new Scanner(System.in);
    String trainSpam, trainHam, testSpam, testHam;

    // Input from user
    System.out.print("Training file for Spam: ");
    trainSpam = scan.nextLine();

    System.out.print("\nTraining file for Ham: ");
    trainHam = scan.nextLine();

    System.out.print("\nTesting file for Spam: ");
    testSpam = scan.nextLine();

    System.out.print("\nTesting file for Ham: ");
    testHam = scan.nextLine();
    System.out.println();

    scan.close();

    Classifier spamEmailDetector = new Classifier();

    spamEmailDetector.train(trainSpam, trainHam);

    spamEmailDetector.test(testSpam, Spham.SPAM);

    spamEmailDetector.test(testHam, Spham.HAM);

    spamEmailDetector.printResults();
  }

}