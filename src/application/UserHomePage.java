package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * This page displays a Reddit-like browser with threads and a box to create new ones.
 */

public class UserHomePage {

    public void show(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: top-center; -fx-padding: 20;");

        // Welcome label
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Thread container
        VBox threadContainer = new VBox(10);
        threadContainer.setStyle("-fx-padding: 10;");

        // Scrollable list of threads
        ScrollPane scrollPane = new ScrollPane(threadContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);

        // Input area for new threads
        TextArea newThreadInput = new TextArea();
        newThreadInput.setPromptText("Write something to start a new thread...");
        newThreadInput.setWrapText(true);
        newThreadInput.setPrefRowCount(3);

        Button postButton = new Button("Post Thread");
        postButton.setOnAction(e -> {
            String text = newThreadInput.getText().trim();
            if (!text.isEmpty()) {
                Button threadButton = new Button(text);
                threadButton.setWrapText(true);
                threadButton.setMaxWidth(Double.MAX_VALUE);
                threadButton.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                    "-fx-padding: 10; " +
                    "-fx-background-radius: 5; " +
                    "-fx-text-alignment: left;"
                );

                // action when thread is clicked
                threadButton.setOnAction(ev -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thread Clicked");
                    alert.setHeaderText("Thread Content");
                    alert.setContentText(text);
                    alert.showAndWait();
                });

                threadContainer.getChildren().add(0, threadButton); // add new thread at top
                newThreadInput.clear();
            }
        });

        VBox newThreadBox = new VBox(10, newThreadInput, postButton);
        newThreadBox.setStyle("-fx-padding: 10; -fx-background-color: #eaeaea; -fx-background-radius: 5;");

        layout.getChildren().addAll(userLabel, scrollPane, newThreadBox);

        Scene userScene = new Scene(layout, 800, 400);

        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}
