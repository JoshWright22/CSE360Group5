package application.pages;

import application.StartCSE360;
import application.User;
import application.UserRole;
import application.eval.PasswordEvaluator;
import application.eval.UserNameRecognizer;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The SetupAdmin class handles the setup process for creating an administrator
 * account. This is intended to be used by the first user to initialize the
 * system with admin credentials.
 */
public class AdminSetupPage {

	private final DatabaseHelper databaseHelper;

	public AdminSetupPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		// Input fields for userName and password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Admin userName");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(250);

		Button setupButton = new Button("Setup");

		setupButton.setOnAction(a -> {
			// Retrieve user input
			String userName = userNameField.getText();
			String password = passwordField.getText();

			// Validate username
			String userValidationResult = UserNameRecognizer.checkForValidUserName(userName);
			if (!userValidationResult.isEmpty()) {
				System.err.println(userValidationResult);
				return;
			}

			// Validate password
			String passValidationResult = PasswordEvaluator.evaluatePassword(password);
			if (!passValidationResult.isEmpty()) {
				System.err.println(passValidationResult);
				return;
			}

			// Create a new User object with ADMIN role and register in the database
			User user = StartCSE360.getDatabaseHelper().createUser(userName, password, UserRole.ADMIN);
			System.out.println("Administrator setup completed.");

			// Navigate to the Welcome Login Page
			new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
		});

		VBox layout = new VBox(10, userNameField, passwordField, setupButton);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("Administrator Setup");
		primaryStage.show();
	}
}
