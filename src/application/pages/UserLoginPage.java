package application.pages;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import application.StartCSE360;
import application.User;
import application.UserRole;
import databasePart1.*;

/**
 * UserLoginPage with dev login bypass and selectable role.
 */
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

            // Dev bypass: skips validation and DB
            if (enableDevLogin.isSelected() && userName.equals("dev") && password.equals("dev")) {
                UserRole selectedRole = UserRole.STUDENT;

                if (adminRole.isSelected()) selectedRole = UserRole.ADMIN;
                else if (reviewerRole.isSelected()) selectedRole = UserRole.REVIEWER;
                else if (instructorRole.isSelected()) selectedRole = UserRole.INSTRUCTOR;
                else if (staffRole.isSelected()) selectedRole = UserRole.STAFF;

                User devUser = new User("dev", "dev", selectedRole);
                StartCSE360.setCurrentUser(devUser);
                WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
                welcomeLoginPage.show(primaryStage, devUser);
                return;
            }

            // normal login flow goes here (unchanged)
            errorLabel.setText("Normal login flow not implemented in dev mode.");
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(
                enableDevLogin,
                studentRole, adminRole, reviewerRole, instructorRole, staffRole,
                userNameField, passwordField, loginButton, errorLabel
        );

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
