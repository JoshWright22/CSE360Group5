package application.pages;

import java.sql.SQLException;

import application.StartCSE360;
import application.User;
import application.UserRole;
import application.eval.PasswordEvaluator;
import application.eval.UserNameRecognizer;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserLoginPage {

	private final DatabaseHelper databaseHelper;

	public UserLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter your user name");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter your password");
		passwordField.setMaxWidth(250);

		// Checkbox to enable dev login
		CheckBox enableDevLogin = new CheckBox("Enable dev login (development only)");
		enableDevLogin.setStyle("-fx-font-size: 11px; -fx-opacity: 0.9;");

		// Radio buttons to select role
		ToggleGroup roleGroup = new ToggleGroup();
		RadioButton studentRole = new RadioButton("Student");
		studentRole.setToggleGroup(roleGroup);
		studentRole.setSelected(true);
		RadioButton adminRole = new RadioButton("Admin");
		adminRole.setToggleGroup(roleGroup);
		RadioButton reviewerRole = new RadioButton("Reviewer");
		reviewerRole.setToggleGroup(roleGroup);
		RadioButton instructorRole = new RadioButton("Instructor");
		instructorRole.setToggleGroup(roleGroup);
		RadioButton staffRole = new RadioButton("Staff");
		staffRole.setToggleGroup(roleGroup);

		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		Button loginButton = new Button("Login");
		loginButton.setOnAction(a -> {
			String userName = userNameField.getText().trim();
			String password = passwordField.getText().trim();

			// dev bypass
			if (enableDevLogin.isSelected() && userName.equals("dev") && password.equals("dev")) {
				UserRole selectedRole = UserRole.STUDENT;
				if (adminRole.isSelected())
					selectedRole = UserRole.ADMIN;
				else if (reviewerRole.isSelected())
					selectedRole = UserRole.REVIEWER;
				else if (instructorRole.isSelected())
					selectedRole = UserRole.INSTRUCTOR;
				else if (staffRole.isSelected())
					selectedRole = UserRole.STAFF;

				User devUser = new User("dev", "dev", selectedRole);
				StartCSE360.setCurrentUser(devUser);
				new WelcomeLoginPage(databaseHelper).show(primaryStage, devUser);
				return;
			}

			// normal login flow
			String userValidationResult = UserNameRecognizer.checkForValidUserName(userName);
			if (!userValidationResult.isEmpty()) {
				errorLabel.setText(userValidationResult);
				return;
			}

			String passValidationResult = PasswordEvaluator.evaluatePassword(password);
			if (!passValidationResult.isEmpty()) {
				errorLabel.setText(passValidationResult);
				return;
			}

			try {
				UserRole role = databaseHelper.getUserRole(userName);
				if (role == null) {
					errorLabel.setText("User account doesn't exist or is invalid");
					return;
				}

				User user = new User(userName, password, role);
				if (databaseHelper.login(user)) {
					StartCSE360.setCurrentUser(user);
					new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
				} else {
					errorLabel.setText("Error logging in: invalid credentials");
				}
			} catch (SQLException e) {
				errorLabel.setText("Database error occurred");
				e.printStackTrace();
			}
		});

		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		layout.getChildren().addAll(enableDevLogin, studentRole, adminRole, reviewerRole, instructorRole, staffRole,
				userNameField, passwordField, loginButton, errorLabel);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("User Login");
		primaryStage.show();
	}
}
