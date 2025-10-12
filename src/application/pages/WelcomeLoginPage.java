package application.pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import application.User;
import application.UserRole;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or
 * quit the application.
 */
public class WelcomeLoginPage {

	private final DatabaseHelper databaseHelper;

	public WelcomeLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {

		VBox layout = new VBox(5);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		Label welcomeLabel = new Label("Welcome!!");
		welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Button to navigate to the user's respective page based on their role
		Button continueButton = new Button("Continue to your Page");
		continueButton.setOnAction(a -> {
			UserRole role = user.getRole();
			System.out.println(role);

			if (role == UserRole.ADMIN) {
				new AdminHomePage().show(primaryStage);
			} else if (role == UserRole.STUDENT) {
				new UserHomePage().show(primaryStage);
			} 
		});

		// Button to quit the application
		Button quitButton = new Button("Quit");
		quitButton.setOnAction(a -> {
			databaseHelper.closeConnection();
			Platform.exit(); // Exit the JavaFX application
		});

		// "Invite" button for admin to generate invitation codes
		if (user.getRole() == UserRole.ADMIN) {
			Button inviteButton = new Button("Invite");
			inviteButton.setOnAction(a -> {
				new InvitationPage().show(databaseHelper, primaryStage);
			});
			layout.getChildren().add(inviteButton);
		}

		layout.getChildren().addAll(welcomeLabel, continueButton, quitButton);
		Scene welcomeScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Welcome Page");
	}
}