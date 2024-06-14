# Spam Email Classifier with Naive Bayes Classification 

This project implements a spam email classifier using a Naive Bayes algorithm. The classifier is trained on a set of spam and non-spam (ham) emails and can be tested on a separate set of emails to evaluate its performance.

## Project Structure

The project consists of the following Java files:

- `Classifier`: Handles training and testing of the spam classifier.
- `Converter`: Provides methods to convert email files into vocabulary and word lists.
- `Main`: Main class to execute the training and testing process.
- `Spham`: Enum representing spam and ham categories.
- `Vocabulary`: Stores the occurrence of words in spam and ham emails and provides methods to manipulate and retrieve word data.
- `WordList`: Manages a list of unique words from emails.

## Usage

1. Compile the Java files.
2. Run the `Main` class.
3. When prompted, provide the file paths for the training and testing sets of spam and ham emails.
4. The program will train the classifier using the training sets and then test it on the testing sets, reporting the accuracy of the classification.

## Algorithm
The Naive Bayes Classification algorithm used in this project is based on the following principles:

1. **Training**: The algorithm reads the training sets of spam and ham emails and counts the occurrences of each word in each set. These word counts are stored in a `Vocabulary` object for each class (spam and ham).

2. **Testing**: For each email in the testing set, the algorithm calculates the log-probability of the email being spam or ham based on the word counts in the respective `Vocabulary` objects. It uses a smoothing technique (adding 1 to the word counts) to handle unseen words during testing.

3. **Classification**: The algorithm compares the log-probabilities of the email being spam and ham. If the log-probability of spam is greater than or equal to the log-probability of ham, the email is classified as spam. Otherwise, it is classified as ham.

4. **Evaluation**: The program keeps track of the correctly and incorrectly classified emails and reports the overall accuracy at the end.

## Acknowledgments

This project was developed as part of a college assignment. The code is provided "as is" without any warranties or guarantees.

## Author

Diego Lopez