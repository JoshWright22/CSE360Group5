package application.pages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import application.StartCSE360;
import application.UserRole;
import application.obj.Answer;
import application.obj.Question;
import application.obj.Review;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * QuestionPage represents the user interface for a given question
 * It displays the question and its corresponding answers
 */

public class QuestionPage {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public void show(Stage primaryStage, UserHomePage userHomePage, Question question) {
		List<Answer> answers = question.getAnswers();

		// creates an HBox that holds the back and delete buttons for questions
		HBox removeBox = createRemoveBox(primaryStage, userHomePage, question, answers);

		// creates an HBox that holds the title
		HBox titleBox = createTitleBox(primaryStage, userHomePage, question, answers);

		// creates an HBox that holds the author and date
		HBox headerBox = createHeaderBox(primaryStage, userHomePage, question, answers);

		// creates an HBox that holds the body
		VBox bodyBox = createBodyBox(primaryStage, userHomePage, question, answers);

		// creates a VBox where all answers are listed
		VBox answerBox = new VBox(15);
		answerBox.setPrefHeight(400);

		// creates scroll pane for answers
		ScrollPane answersScrollPane = new ScrollPane(answerBox);
		answersScrollPane.setFitToWidth(true);
		answersScrollPane.setContent(answerBox);

		// populate answers if any exist
		populateAnswerBox(answerBox, question, answers);

		// create text area and button to submit answers
		TextArea answerTextArea = new TextArea();
		Button postButton = new Button("Submit Answer");
		postButton.setOnMouseClicked(e -> {

			Answer answer = StartCSE360.getAnswerManager().createNewAnswer(StartCSE360.getCurrentUser().getUserName(),
					LocalDateTime.now(), answerTextArea.getText());
			StartCSE360.getQuestionManager().addAnswerToQuestion(question, answer);
			addAnswerLabel(answerBox, answer);
		});

		VBox layout = new VBox();
		layout.setStyle("-fx-alignment: top-center; -fx-padding: 20;");
		layout.getChildren().addAll(removeBox, titleBox, bodyBox, headerBox, new Separator(), answersScrollPane, answerTextArea,
				postButton);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("User Login");
		primaryStage.show();
	}

	private void addAnswerLabel(VBox answerBox, Answer answer) {
		VBox answerContainer = new VBox(10);
		answerContainer.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

		// Answer content and metadata
		HBox box = new HBox();
		Label answerLabel = new Label(answer.getContent());
		Label authorLabel = new Label(answer.getUserName());
		Label dateLabel = new Label(answer.getCreationDate().format(FORMATTER));

		answerLabel.setWrapText(true);
		answerLabel.setStyle("-fx-font-size: 14px;");

		Separator separator1 = new Separator();
		separator1.setOrientation(Orientation.VERTICAL);
		Separator separator2 = new Separator();
		separator2.setOrientation(Orientation.VERTICAL);

		box.getChildren().addAll(answerLabel, separator1, authorLabel, separator2, dateLabel);

		// Reviews section
		VBox reviewsBox = new VBox(5);
		reviewsBox.setStyle("-fx-padding: 10 0 0 20;");

		// Add reviews section header
		Label reviewsLabel = new Label("Reviews:");
		reviewsLabel.setStyle("-fx-font-weight: bold;");
		reviewsBox.getChildren().add(reviewsLabel);

		// Get reviews for this answer
		var reviews = StartCSE360.getReviewManager().getReviewsForAnswer(answer.getId());
		if (!reviews.isEmpty()) {
			for (Review review : reviews) {
				HBox reviewBox = new HBox(10);
				reviewBox.setStyle("-fx-padding: 5;");

				// Show rating as stars
				Label ratingLabel = new Label("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
				ratingLabel.setStyle("-fx-text-fill: #FFD700;");

				Label reviewContent = new Label(review.getContent());
				reviewContent.setWrapText(true);

				Label reviewAuthor = new Label("- " + review.getUserName());
				reviewAuthor.setStyle("-fx-font-style: italic;");

				reviewBox.getChildren().addAll(ratingLabel, reviewContent, reviewAuthor);
				reviewsBox.getChildren().add(reviewBox);
			}
		} else {
			Label noReviewsLabel = new Label("No reviews yet");
			noReviewsLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
			reviewsBox.getChildren().add(noReviewsLabel);
		}

		// Add review button (only show for reviewers)
		if (StartCSE360.getCurrentUser().getRole() == UserRole.REVIEWER) {
			HBox addReviewBox = new HBox(10);
			addReviewBox.setStyle("-fx-padding: 10 0 0 0;");

			TextArea reviewContent = new TextArea();
			reviewContent.setPromptText("Write your review...");
			reviewContent.setPrefRowCount(2);
			reviewContent.setWrapText(true);

			ComboBox<Integer> ratingCombo = new ComboBox<>();
			ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
			ratingCombo.setPromptText("Rating");

			Button submitReview = new Button("Add Review");
			submitReview.setOnAction(e -> {
				if (reviewContent.getText().trim().isEmpty() || ratingCombo.getValue() == null) {
					showAlert("Error", "Please provide both a review and rating");
					return;
				}

				Review review = StartCSE360.getReviewManager().createNewAnswerReview(StartCSE360.getCurrentUser().getUserName(),
						LocalDateTime.now(), reviewContent.getText().trim(), ratingCombo.getValue(), answer);

				if (review == null) {
					showAlert("Error", "Failed to create review. Please try again.");
					return;
				}

				// Refresh the reviews section
				reviewsBox.getChildren().clear();
				Label newReviewsLabel = new Label("Reviews:");
				newReviewsLabel.setStyle("-fx-font-weight: bold;");
				reviewsBox.getChildren().add(newReviewsLabel);

				var updatedReviews = StartCSE360.getReviewManager().getReviewsForAnswer(answer.getId());
				if (!updatedReviews.isEmpty()) {
					for (Review r : updatedReviews) {
						HBox newReviewBox = new HBox(10);
						newReviewBox.setStyle("-fx-padding: 5;");

						Label ratingLabel = new Label("★".repeat(r.getRating()) + "☆".repeat(5 - r.getRating()));
						ratingLabel.setStyle("-fx-text-fill: #FFD700;");

						Label reviewContentLabel = new Label(r.getContent());
						reviewContentLabel.setWrapText(true);

						Label reviewAuthorLabel = new Label("- " + r.getUserName());
						reviewAuthorLabel.setStyle("-fx-font-style: italic;");

						newReviewBox.getChildren().addAll(ratingLabel, reviewContentLabel, reviewAuthorLabel);
						reviewsBox.getChildren().add(newReviewBox);
					}
				}

				// Clear input fields
				reviewContent.clear();
				ratingCombo.setValue(null);
			});

			addReviewBox.getChildren().addAll(reviewContent, ratingCombo, submitReview);
			reviewsBox.getChildren().add(addReviewBox);
		}

		answerContainer.getChildren().addAll(box, reviewsBox);
		answerBox.getChildren().add(answerContainer);
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void populateAnswerBox(VBox answerBox, Question question, List<Answer> answers) {
		answerBox.getChildren().clear();
		for (Answer answer : answers) {
			addAnswerLabel(answerBox, answer);
		}
	}

	private HBox createRemoveBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers) {
		HBox removeBox = new HBox(6);
		Button backButton = new Button("<-");
		backButton.setOnMouseClicked(e -> {
			userHomePage.show(primaryStage);
		});
		removeBox.getChildren().add(backButton);
		removeBox.setAlignment(Pos.TOP_RIGHT);
		if (StartCSE360.getCurrentUser().getUserName().equals(question.getUserName())) {
			Button removeButton = new Button("X");
			removeButton.setAlignment(Pos.BASELINE_RIGHT);
			removeButton.setOnMouseClicked(e -> {
				StartCSE360.getQuestionManager().deleteQuestion(question);
				userHomePage.show(primaryStage);
			});
			removeBox.getChildren().add(removeButton);
		}
		return removeBox;
	}

	private HBox createTitleBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers) {
		HBox titleBox = new HBox(6);
		titleBox.setAlignment(Pos.TOP_CENTER);
		Label titleLabel = new Label(question.getTitle());
		titleLabel.setStyle("-fx-font-size: 32px;");
		titleBox.getChildren().addAll(titleLabel);
		return titleBox;
	}

	private HBox createHeaderBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers) {
		HBox headerBox = new HBox(6);
		headerBox.setAlignment(Pos.TOP_RIGHT);
		headerBox.setPrefWidth(Double.MAX_VALUE);
		Label authorLabel = new Label(question.getUserName());
		Label dateLabel = new Label(question.getCreationDate().format(FORMATTER));
		Separator headerSeparator = new Separator();
		headerSeparator.setOrientation(Orientation.VERTICAL);
		headerBox.getChildren().addAll(authorLabel, headerSeparator, dateLabel);
		return headerBox;
	}

	private VBox createBodyBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers) {
		VBox bodyBox = new VBox();
		bodyBox.setAlignment(Pos.TOP_CENTER);
		Label bodyLabel = new Label(question.getContent());
		bodyLabel.setStyle("-fx-font-size: 14px;");
		bodyLabel.setWrapText(true);
		bodyBox.getChildren().addAll(bodyLabel);
		return bodyBox;
	}
}
