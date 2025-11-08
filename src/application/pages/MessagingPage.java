package application.pages;

import java.time.format.DateTimeFormatter;
import java.util.List;

import application.StartCSE360;
import application.User;
import application.obj.Message;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Provides a UI for sending and receiving private messages between users.
 * Supports messaging between students and reviewers.
 */
public class MessagingPage {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private User currentUser;

	/**
	 * Shows the the entrance point for the private messaging flow.
	 * 
	 * @param primaryStage		Window to display in
	 * @param userHomePage		Cached previous page for back button
	 */
	public void show(Stage primaryStage, UserHomePage userHomePage) {
		currentUser = StartCSE360.getCurrentUser();

		VBox mainLayout = new VBox(10);
		mainLayout.setStyle("-fx-padding: 20;");

		// Header
		Label titleLabel = new Label("Messages");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		// User selection section
		HBox userSelectionBox = createUserSelectionBox();

		// Messages display area
		VBox messagesBox = new VBox(10);
		messagesBox.setPrefHeight(300);
		ScrollPane messagesScrollPane = new ScrollPane(messagesBox);
		messagesScrollPane.setFitToWidth(true);
		messagesScrollPane.setContent(messagesBox);

		// Message composition area
		HBox compositionBox = createCompositionBox(messagesBox, primaryStage, userHomePage);

		// Back button
		Button backButton = new Button("Back to Home");
		backButton.setOnAction(e -> userHomePage.show(primaryStage));

		mainLayout.getChildren().addAll(titleLabel, userSelectionBox, new Separator(), new Label("Recent Messages:"),
				messagesScrollPane, new Separator(), compositionBox, backButton);

		Scene scene = new Scene(mainLayout, 900, 700);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Messaging");
	}

	private HBox createUserSelectionBox() {
		HBox box = new HBox(10);
		box.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

		Label label = new Label("Message with:");
		label.setStyle("-fx-font-weight: bold;");

		TextField userField = new TextField();
		userField.setPromptText("Enter username...");
		userField.setPrefWidth(200);

		box.getChildren().addAll(label, userField);
		return box;
	}

	private HBox createCompositionBox(VBox messagesBox, Stage primaryStage, UserHomePage userHomePage) {
		HBox box = new HBox(10);

		Label toLabel = new Label("To:");
		TextField toField = new TextField();
		toField.setPromptText("Enter recipient username...");
		toField.setPrefWidth(200);

		Label messageLabel = new Label("Message:");
		TextArea messageArea = new TextArea();
		messageArea.setPrefHeight(80);
		messageArea.setWrapText(true);

		Button sendButton = new Button("Send Message");
		sendButton.setStyle("-fx-font-size: 12px; -fx-padding: 10;");
		sendButton.setOnAction(e -> {
			String recipient = toField.getText().trim();
			String content = messageArea.getText().trim();

			if (recipient.isEmpty() || content.isEmpty()) {
				showAlert("Error", "Please fill in all fields.");
				return;
			}

			StartCSE360.getMessageManager().sendMessage(currentUser.getUserName(), recipient, content);

			messageArea.clear();
			toField.clear();
			showAlert("Success", "Message sent!");

			// Refresh messages
			refreshMessages(messagesBox);
		});

		VBox leftBox = new VBox(5);
		leftBox.getChildren().addAll(toLabel, toField, messageLabel, messageArea);

		box.getChildren().addAll(leftBox, sendButton);
		return box;
	}

	private void refreshMessages(VBox messagesBox) {
		messagesBox.getChildren().clear();

		List<Message> messages = StartCSE360.getMessageManager().getMessagesFor(currentUser.getUserName());

		if (messages.isEmpty()) {
			Label emptyLabel = new Label("No messages yet.");
			messagesBox.getChildren().add(emptyLabel);
			return;
		}

		for (Message msg : messages) {
			HBox msgBox = createMessageBox(msg);
			messagesBox.getChildren().add(msgBox);
		}
	}

	private HBox createMessageBox(Message msg) {
		HBox box = new HBox(15);
		box.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
		box.setPrefHeight(80);

		String direction = msg.getReceiverName().equals(currentUser.getUserName()) ? "FROM" : "TO";
		String otherUser = msg.getReceiverName().equals(currentUser.getUserName()) ? msg.getSenderName()
				: msg.getReceiverName();

		VBox contentBox = new VBox(5);
		Label headerLabel = new Label(direction + ": " + otherUser);
		headerLabel.setStyle("-fx-font-weight: bold;");

		Label contentLabel = new Label(msg.getContent());
		contentLabel.setWrapText(true);
		contentLabel.setMaxWidth(500);

		Label timeLabel = new Label(msg.getSentTime().format(FORMATTER));
		timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");

		contentBox.getChildren().addAll(headerLabel, contentLabel, timeLabel);

		String readStatus = msg.isRead() ? "✓ Read" : "✗ Unread";
		Label readLabel = new Label(readStatus);
		readLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (msg.isRead() ? "#00aa00" : "#aa0000") + ";");

		box.getChildren().addAll(contentBox, readLabel);
		return box;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}