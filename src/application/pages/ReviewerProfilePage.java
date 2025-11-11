package application.pages;

import application.ReviewerProfileManager;
import application.StartCSE360;
import application.User;
import application.UserRole;
import application.obj.ReviewerProfile;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Provides UI for reviewers to create and edit their profiles. Displays
 * reviewer expertise, experience, and qualifications.
 */
public class ReviewerProfilePage {

	public void show(Stage primaryStage, UserHomePage userHomePage) {
		User currentUser = StartCSE360.getCurrentUser();

		if (currentUser.getRole() != UserRole.REVIEWER) {
			showAlert("Error", "Only reviewers can access this page.");
			userHomePage.show(primaryStage);
			return;
		}

		ReviewerProfileManager profileManager = StartCSE360.getReviewerProfileManager();
		ReviewerProfile profile = profileManager.getOrCreateProfile(currentUser.getUserName());

		VBox mainLayout = new VBox(15);
		mainLayout.setStyle("-fx-padding: 20;");

		// Header
		Label titleLabel = new Label("My Reviewer Profile");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		// Profile statistics section
		HBox statsBox = createStatsBox(profile);

		// Editable fields section
		VBox editableBox = createEditableFieldsBox(profile, profileManager, primaryStage, userHomePage);

		// Back button
		Button backButton = new Button("Back to Home");
		backButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
		backButton.setOnAction(e -> userHomePage.show(primaryStage));

		mainLayout.getChildren().addAll(titleLabel, statsBox, new Separator(), editableBox, new Separator(), backButton);

		Scene scene = new Scene(mainLayout, 900, 700);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Reviewer Profile");
	}

	private HBox createStatsBox(ReviewerProfile profile) {
		HBox box = new HBox(40);
		box.setStyle("-fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f5f5f5;");
		box.setAlignment(Pos.CENTER_LEFT);

		VBox totalReviewsBox = createStatBox("Total Reviews", String.valueOf(profile.getTotalReviews()));
		VBox averageRatingBox = createStatBox("Average Rating", String.format("%.2f", profile.getAverageRating()));
		VBox experienceBox = createStatBox("Years Experience", String.valueOf(profile.getYearsExperience()));

		box.getChildren().addAll(totalReviewsBox, averageRatingBox, experienceBox);
		return box;
	}

	private VBox createStatBox(String label, String value) {
		VBox box = new VBox(5);
		box.setAlignment(Pos.CENTER);

		Label labelL = new Label(label);
		labelL.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

		Label valueL = new Label(value);
		valueL.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		box.getChildren().addAll(labelL, valueL);
		return box;
	}

	private VBox createEditableFieldsBox(ReviewerProfile profile, ReviewerProfileManager profileManager, Stage primaryStage,
			UserHomePage userHomePage) {
		VBox box = new VBox(15);

		Label editLabel = new Label("Edit Profile");
		editLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Bio field
		Label bioLabel = new Label("Biography:");
		bioLabel.setStyle("-fx-font-weight: bold;");
		TextArea bioArea = new TextArea(profile.getBio());
		bioArea.setPrefHeight(80);
		bioArea.setWrapText(true);

		// Expertise field
		Label expertiseLabel = new Label("Areas of Expertise:");
		expertiseLabel.setStyle("-fx-font-weight: bold;");
		TextArea expertiseArea = new TextArea(profile.getExpertise());
		expertiseArea.setPrefHeight(80);
		expertiseArea.setWrapText(true);

		// Years of experience field
		Label yearsLabel = new Label("Years of Experience:");
		yearsLabel.setStyle("-fx-font-weight: bold;");
		Spinner<Integer> yearsSpinner = new Spinner<>(0, 100, profile.getYearsExperience());
		yearsSpinner.setPrefWidth(100);

		// Save button
		Button saveButton = new Button("Save Profile");
		saveButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
		saveButton.setOnAction(e -> {
			profile.setBio(bioArea.getText());
			profile.setExpertise(expertiseArea.getText());
			profile.setYearsExperience(yearsSpinner.getValue());

			profileManager.updateProfile(profile);
			showAlert("Success", "Profile updated successfully!");
		});

		// Reset button
		Button resetButton = new Button("Reset Changes");
		resetButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
		resetButton.setOnAction(e -> {
			bioArea.setText(profile.getBio());
			expertiseArea.setText(profile.getExpertise());
			yearsSpinner.getValueFactory().setValue(profile.getYearsExperience());
		});

		HBox buttonBox = new HBox(10);
		buttonBox.getChildren().addAll(saveButton, resetButton);

		box.getChildren().addAll(editLabel, bioLabel, bioArea, expertiseLabel, expertiseArea, yearsLabel, yearsSpinner,
				buttonBox);

		return box;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}