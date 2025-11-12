package application.pages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import application.StartCSE360;
import application.User;
import application.UserRole;
import application.obj.Question;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UserHomePage {

	public void show(Stage primaryStage) {
		showScene(primaryStage);
	}

	private void showScene(Stage primaryStage) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		double screenWidth = screenBounds.getWidth();
		double screenHeight = screenBounds.getHeight();

		primaryStage.setMinWidth(screenWidth * 0.4);
		primaryStage.setMinHeight(screenHeight * 0.6);
		primaryStage.setMaxWidth(screenWidth * 0.4);
		primaryStage.setMaxHeight(screenHeight * 0.6);

		User currentUser = StartCSE360.getCurrentUser();

		Label userLabel = (currentUser != null) ? new Label("Hello, " + currentUser.getUserName() + "!")
				: new Label("Hello, Anonymous!");
		userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// reviewer navigation buttons
		HBox reviewerButtons = new HBox(10);
		reviewerButtons.setAlignment(Pos.CENTER_LEFT);

		if (currentUser != null && currentUser.getRole() == UserRole.REVIEWER) {
			Button reviewMgmtButton = new Button("Manage My Reviews");
			Button profileButton = new Button("Edit My Profile");
			Button messagingButton = new Button("Messaging");

			reviewMgmtButton.setOnAction(e -> {
				ReviewManagementPage reviewPage = new ReviewManagementPage();
				reviewPage.show(primaryStage, this);
			});
			profileButton.setOnAction(e -> {
				ReviewerProfilePage profilePage = new ReviewerProfilePage();
				profilePage.show(primaryStage, this);
			});
			messagingButton.setOnAction(e -> {
				MessagingPage messagingPage = new MessagingPage();
				messagingPage.show(primaryStage, this);
			});

			reviewerButtons.getChildren().addAll(reviewMgmtButton, profileButton, messagingButton);
		}

		// questions section
		VBox questionsBox = new VBox(5);
		populateQuestions(questionsBox, StartCSE360.getQuestionManager().getQuestionSet(), primaryStage);

		TextField searchField = new TextField();
		searchField.setPromptText("Search...");
		searchField.textProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal.isEmpty())
				populateQuestions(questionsBox, StartCSE360.getQuestionManager().getQuestionSet(), primaryStage);
		});

		Button searchButton = new Button("Search");
		searchButton.setOnAction(e -> {
			String input = searchField.getText();
			Set<Question> questions = new HashSet<>();
			if (!input.isEmpty()) {
				for (String word : input.trim().split("\\s+")) {
					questions.addAll(StartCSE360.getQuestionManager().searchByTag(word));
				}
			} else {
				questions = StartCSE360.getQuestionManager().getQuestionSet();
			}
			populateQuestions(questionsBox, questions, primaryStage);
		});

		HBox searchBox = new HBox(10, searchField, searchButton);
		searchBox.setAlignment(Pos.TOP_RIGHT);

		ScrollPane questionsScrollPane = new ScrollPane(questionsBox);
		questionsScrollPane.setFitToWidth(true);

		// post question section
		VBox postBox = new VBox(10);
		Label titleLabel = new Label("Title:");
		TextArea titleTextArea = new TextArea();
		titleTextArea.setPrefHeight(2);
		Label bodyLabel = new Label("Body:");
		TextArea bodyTextArea = new TextArea();
		Button postButton = new Button("Post question");
		postButton.setOnAction(e -> {
			Question question = StartCSE360.getQuestionManager().createNewQuestion(currentUser.getUserName(),
					LocalDateTime.now(), titleTextArea.getText(), bodyTextArea.getText(), new ArrayList<>());
			addQuestionLabel(questionsBox, question, primaryStage);
		});
		postBox.getChildren().addAll(titleLabel, titleTextArea, bodyLabel, bodyTextArea, postButton);

		// Request Reviewer HBox
		HBox requestReviewerHBox = new HBox(10);
		requestReviewerHBox.setAlignment(Pos.CENTER_RIGHT);
		// Request Reviewer Role button
		Button requestReviewerBtn = new Button("Request Reviewer Role");
		requestReviewerBtn.setOnAction(a -> {
			StartCSE360.getDatabaseHelper().insertIntoPendingReviewers(StartCSE360.getCurrentUser().getUserName());
		});

		requestReviewerHBox.getChildren().add(requestReviewerBtn);

		VBox layout = new VBox(15);
		layout.setStyle("-fx-alignment: top-center; -fx-padding: 20;");
		layout.getChildren().addAll(userLabel, reviewerButtons, searchBox, questionsScrollPane, new Separator(),
				postBox,
				requestReviewerHBox);

		Scene userScene = new Scene(layout, 800, 400);
		primaryStage.setScene(userScene);
		primaryStage.setTitle("User Page");

		questionsBox.prefHeightProperty().bind(userScene.heightProperty().multiply(0.75));
		postBox.prefHeightProperty().bind(userScene.heightProperty().multiply(0.25));
	}

	private void setQuestionLabelStyle(Label questionLabel) {
		questionLabel.setStyle(
				"-fx-font-size: 18px; -fx-background-color: #f0f0f0; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: transparent; -fx-border-radius: 5;");
		questionLabel.setOnMouseEntered(f -> questionLabel.setStyle(
				"-fx-font-size: 18px; -fx-background-color: #f0f0f0; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #ADD0F2; -fx-border-radius: 5;"));
		questionLabel.setOnMouseExited(f -> questionLabel.setStyle(
				"-fx-font-size: 18px; -fx-background-color: #f0f0f0; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: transparent; -fx-border-radius: 5;"));
	}

	private void addQuestionLabel(VBox questionsBox, Question question, Stage primaryStage) {
		Label questionLabel = new Label(question.getTitle());
		questionLabel.setWrapText(true);
		questionLabel.setMaxWidth(Double.MAX_VALUE);
		questionLabel.setPrefHeight(50);

		setQuestionLabelStyle(questionLabel);

		questionLabel.setOnMouseClicked(f -> {
			QuestionPage questionPage = new QuestionPage();
			questionPage.show(primaryStage, this, question);
		});

		questionsBox.getChildren().add(questionLabel);
	}

	private void populateQuestions(VBox questionsBox, Set<Question> questions, Stage primaryStage) {
		questionsBox.getChildren().clear();
		for (Question question : questions) {
			addQuestionLabel(questionsBox, question, primaryStage);
		}
	}
}
