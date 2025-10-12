package application.pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import application.User;
import application.UserRole;
import application.eval.PasswordEvaluator;
import application.eval.UserNameRecognizer;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the
 * appropriate page upon successful login.
 */
public class UserLoginPage {

	private final DatabaseHelper databaseHelper;

	public UserLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		// Input field for the user's userName, password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter your user name");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter your password");
		passwordField.setMaxWidth(250);

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		Button loginButton = new Button("Login");

		loginButton.setOnAction(a -> {
			// Retrieve user inputs
			String userName = userNameField.getText().trim();
			String password = passwordField.getText().trim();

			// Validate user name
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

			try {
				User user = new User(userName, password, null);
				WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);

				// Retrieve the user's role from the database using userName
				UserRole role = databaseHelper.getUserRole(userName);

				if (role != null) {
					user.setRole(role);
					if (databaseHelper.login(user)) {
						welcomeLoginPage.show(primaryStage, user);
					} else {
						// Display an error if the login fails
						errorLabel.setText("Error logging in");
					}
				} else {
					// Display an error if the account does not exist
					errorLabel.setText("User account doesn't exist or is invalid (NULL UserRole)");
				}

			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("User Login");
		primaryStage.show();
	}
}
