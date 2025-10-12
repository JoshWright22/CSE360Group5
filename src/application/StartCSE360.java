package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import application.pages.FirstPage;
import application.pages.SetupLoginSelectionPage;
import databasePart1.DatabaseHelper;

public class StartCSE360 extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final QuestionManager questionManager = new QuestionManager(databaseHelper);
	private static final AnswerManager answerManager = new AnswerManager(databaseHelper);
	private static final CommentManager commentManager = new CommentManager(databaseHelper);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			databaseHelper.connectToDatabase(); // Connect to the database
			questionManager.fetchQuestions(); // Populate questions from database
			answerManager.fetchAnswers(); // Populate answers from database
			commentManager.fetchComments(); // Populate comments from database
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
}
