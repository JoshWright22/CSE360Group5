package application.pages;

import application.StartCSE360;
import application.User;
import application.UserRole;
import application.obj.Review;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Provides UI for reviewers to view and manage all their reviews. Allows
 * editing and deleting of reviews.
 */
public class ReviewManagementPage {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private User currentUser;

	public void show(Stage primaryStage, UserHomePage userHomePage) {
		currentUser = StartCSE360.getCurrentUser();

		if (currentUser.getRole() != UserRole.REVIEWER) {
			showAlert("Error", "Only reviewers can access this page.");
			userHomePage.show(primaryStage);
			return;
		}

		VBox mainLayout = new VBox(15);
		mainLayout.setStyle("-fx-padding: 20;");

		// Header
		Label titleLabel = new Label("My Reviews");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		// Statistics section
		VBox statsSection = createStatsSection();

		// Reviews list section
		VBox reviewsListBox = new VBox(10);
		reviewsListBox.setPrefHeight(400);

		ScrollPane reviewsScrollPane = new ScrollPane(reviewsListBox);
		reviewsScrollPane.setFitToWidth(true);
		reviewsScrollPane.setContent(reviewsListBox);

		// Refresh button
		Button refreshButton = new Button("Refresh List");
		refreshButton.setStyle("-fx-font-size: 12px;");
		refreshButton.setOnAction(e -> refreshReviewsList(reviewsListBox, primaryStage, userHomePage));

		// Back button
		Button backButton = new Button("Back to Home");
		backButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
		backButton.setOnAction(e -> userHomePage.show(primaryStage));

		mainLayout.getChildren().addAll(titleLabel, statsSection, new Separator(), new Label("Your Reviews:"), refreshButton,
				reviewsScrollPane, new Separator(), backButton);

		// Initial population
		refreshReviewsList(reviewsListBox, primaryStage, userHomePage);

		Scene scene = new Scene(mainLayout, 900, 750);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Review Management");
	}

	private VBox createStatsSection() {
		VBox box = new VBox(10);
		box.setStyle("-fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f5f5f5;");

		Set<Review> myReviews = StartCSE360.getReviewManager().getReviewsByUser(currentUser.getUserName());

		int totalReviews = myReviews.size();
		int questionReviews = (int) myReviews.stream().filter(Review::isQuestionReview).count();
		int answerReviews = (int) myReviews.stream().filter(Review::isAnswerReview).count();
		double avgRating = myReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

		HBox statsBox = new HBox(40);
		statsBox.setAlignment(Pos.CENTER_LEFT);

		statsBox.getChildren().addAll(createStatBox("Total Reviews", String.valueOf(totalReviews)),
				createStatBox("Question Reviews", String.valueOf(questionReviews)),
				createStatBox("Answer Reviews", String.valueOf(answerReviews)),
				createStatBox("Avg Rating", String.format("%.2f", avgRating)));

		box.getChildren().add(statsBox);
		return box;
	}

	private VBox createStatBox(String label, String value) {
		VBox box = new VBox(5);
		box.setAlignment(Pos.CENTER);

		Label labelL = new Label(label);
		labelL.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

		Label valueL = new Label(value);
		valueL.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		box.getChildren().addAll(labelL, valueL);
		return box;
	}

	private void refreshReviewsList(VBox reviewsListBox, Stage primaryStage, UserHomePage userHomePage) {
		reviewsListBox.getChildren().clear();

		Set<Review> myReviews = StartCSE360.getReviewManager().getReviewsByUser(currentUser.getUserName());

		if (myReviews.isEmpty()) {
			Label emptyLabel = new Label("No reviews yet. Start by reviewing questions or answers!");
			emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
			reviewsListBox.getChildren().add(emptyLabel);
			return;
		}

		for (Review review : myReviews) {
			VBox reviewBox = createReviewBox(review, reviewsListBox, primaryStage, userHomePage);
			reviewsListBox.getChildren().add(reviewBox);
		}
	}

	private VBox createReviewBox(Review review, VBox reviewsListBox, Stage primaryStage, UserHomePage userHomePage) {
		VBox box = new VBox(10);
		box.setStyle("-fx-padding: 15; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-color: #fafafa;");

		// Review type and target
		String reviewType = review.isQuestionReview() ? "Question Review" : "Answer Review";
		String targetInfo = review.isQuestionReview() ? "Question: " + review.getReviewedQuestion().getTitle()
				: "Answer by: " + review.getReviewedAnswer().getUserName();

		Label typeLabel = new Label(reviewType + " - " + targetInfo);
		typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
		typeLabel.setWrapText(true);

		// Rating
		String ratingStars = "★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());
		Label ratingLabel = new Label("Rating: " + ratingStars + " (" + review.getRating() + "/5)");
		ratingLabel.setStyle("-fx-font-size: 12px;");

		// Content preview
		Label contentLabel = new Label(review.getContent());
		contentLabel.setWrapText(true);
		contentLabel.setMaxWidth(800);
		contentLabel.setStyle("-fx-font-size: 11px;");

		// Date
		Label dateLabel = new Label("Posted: " + review.getCreationDate().format(FORMATTER));
		dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

		// Action buttons
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);

		Button editButton = new Button("Edit");
		editButton.setStyle("-fx-font-size: 11px;");
		editButton.setOnAction(e -> showEditDialog(review));

		Button deleteButton = new Button("Delete");
		deleteButton.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-background-color: #d32f2f;");
		deleteButton.setOnAction(e -> {
			if (confirmDelete()) {
				StartCSE360.getReviewManager().deleteReview(review);
				showAlert("Success", "Review deleted.");
				refreshReviewsList(reviewsListBox, primaryStage, userHomePage);
			}
		});

		buttonBox.getChildren().addAll(editButton, deleteButton);

		box.getChildren().addAll(typeLabel, ratingLabel, contentLabel, dateLabel, buttonBox);
		return box;
	}

	private void showEditDialog(Review review) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Review");

		VBox dialogBox = new VBox(10);
		dialogBox.setStyle("-fx-padding: 15;");

		Label contentLabel = new Label("Content:");
		TextArea contentArea = new TextArea(review.getContent());
		contentArea.setPrefHeight(100);
		contentArea.setWrapText(true);

		Label ratingLabel = new Label("Rating:");
		Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, review.getRating());
		ratingSpinner.setPrefWidth(80);

		dialogBox.getChildren().addAll(contentLabel, contentArea, ratingLabel, ratingSpinner);

		dialog.getDialogPane().setContent(dialogBox);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
			review.setContent(contentArea.getText());
			review.setRating(ratingSpinner.getValue());
			StartCSE360.getReviewManager().updateReview(review);
			showAlert("Success", "Review updated!");
		}
	}

	private boolean confirmDelete() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Delete");
		alert.setContentText("Are you sure you want to delete this review?");
		return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.OK;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}