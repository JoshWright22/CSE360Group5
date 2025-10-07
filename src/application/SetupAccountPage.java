package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import application.evaluators.PasswordEvaluator;
import application.evaluators.UserNameRecognizer;
import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users. Users
 * provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {

	private final DatabaseHelper databaseHelper;

	// DatabaseHelper to handle database operations.
	public SetupAccountPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
	 * Displays the Setup Account page in the provided stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {
		// Input fields for userName, password, and invitation code
		// PHASE 0: Username field
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter a new username");
		userNameField.setMaxWidth(250);

		// PHASE 0: Password field
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter a password");
		passwordField.setMaxWidth(250);
		
		// PHASE 0: Invite code field
		TextField inviteCodeField = new TextField();
		inviteCodeField.setPromptText("Enter a valid invitation code");
		inviteCodeField.setMaxWidth(100);
		
		// PHASE 1: First name field
		TextField firstNameField = new TextField();
		firstNameField.setPromptText("Enter your first name");
		firstNameField.setMaxWidth(250);
		
		// PHASE 1: Last name field
		TextField lastNameField = new TextField();
		lastNameField.setPromptText("Enter your last name");
		lastNameField.setMaxWidth(250);
		
		// PHASE 1: Email field
		TextField emailField = new TextField();
		emailField.setPromptText("Enter your email address");
		emailField.setMaxWidth(250);

		// Label to display error messages for invalid input or registration issues
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		Button setupButton = new Button("Create Account");

		setupButton.setOnAction(a -> {
			// Retrieve user input
			String userName = userNameField.getText();
			String password = passwordField.getText();
			String code = inviteCodeField.getText();

			// Validate username
			String userValidationResult = UserNameRecognizer.checkForValidUserName(userName);
			if (!userValidationResult.isEmpty()) {
				errorLabel.setText(userValidationResult);
				return;
			}

			// Validate password
			String passValidationResult = PasswordEvaluator.evaluatePassword(password);
			if (!passValidationResult.isEmpty()) {
				errorLabel.setText(passValidationResult);
				return;
			}
			
			// PHASE 1: Add 

			try {
				// Check if the user already exists
				if (!databaseHelper.doesUserExist(userName)) {

					// Validate the invitation code
					if (databaseHelper.validateInvitationCode(code)) {

						// Create a new user and register them in the database
						User user = new User(userName, password, UserRole.STUDENT);
						databaseHelper.register(user);

						// Navigate to the Welcome Login Page
						new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
					} else {
						errorLabel.setText("Please enter a valid invitation code");
					}
				} else {
					errorLabel.setText("This username is taken! Please use another to setup an account");
				}

			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, setupButton, errorLabel);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("Account Setup");
		primaryStage.show();
	}
}
