package application;

import java.sql.SQLException;

import application.pages.FirstPage;
import application.pages.SetupLoginSelectionPage;
import databasePart1.DatabaseHelper;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * A program that simulates a simple question-and-answer system.
 */
public class StartCSE360 extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final QuestionManager questionManager = new QuestionManager(databaseHelper);
	private static final AnswerManager answerManager = new AnswerManager(databaseHelper);
	private static final CommentManager commentManager = new CommentManager(databaseHelper);
	private static final ReviewManager reviewManager = new ReviewManager(databaseHelper);
	private static final ReviewerProfileManager reviewProfileManager = new ReviewerProfileManager(databaseHelper);
	private static final MessageManager messageManager = new MessageManager(databaseHelper);

	private static User currentUser = null;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			databaseHelper.connectToDatabase(); 	// Connect to the database
			answerManager.fetchAnswers(); 			// Populate answers from database
			questionManager.fetchQuestions(); 		// Populate questions from database
			commentManager.fetchComments(); 		// Populate comments from database
			reviewManager.fetchReviews();           // Populate reviews from database
			
			databaseHelper.connectToDatabase(); // Connect to the database
			answerManager.fetchAnswers(); // Populate answers from database
			questionManager.fetchQuestions(); // Populate questions from database
			commentManager.fetchComments(); // Populate comments from database
			reviewManager.fetchReviews(); // Populate reviews from database

			if (databaseHelper.isDatabaseEmpty()) {
				new FirstPage(databaseHelper).show(primaryStage);
			} else {
				new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static DatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	public static QuestionManager getQuestionManager() {
		return questionManager;
	}

	public static AnswerManager getAnswerManager() {
		return answerManager;
	}

	public static CommentManager getCommentManager() {
		return commentManager;
	}

	public static void setCurrentUser(User user) {
		currentUser = user;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static ReviewManager getReviewManager() {
		return reviewManager;
	}

	public static ReviewerProfileManager getReviewerProfileManager() {
		return reviewProfileManager;
	}

	public static MessageManager getMessageManager() {
		return messageManager;
	}

}
