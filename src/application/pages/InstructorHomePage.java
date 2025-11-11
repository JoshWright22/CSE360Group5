package application.pages;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.StartCSE360;
import application.User;
import application.UserRole;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InstructorHomePage {
	public void show(Stage primaryStage) {
		VBox layout = new VBox();

		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		TableView<User> userTable = new TableView<>();
		setupUserTable(userTable, primaryStage);

		layout.getChildren().addAll(userTable);
		Scene instructorScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(instructorScene);
		primaryStage.setTitle("Instructor Page");
	}

	private void setupUserTable(TableView<User> userTable, Stage primaryStage) {
		// Create user column
		TableColumn<User, String> usernameCol = createUsernameColumn();

		// Create reviews column
		TableColumn<User, Void> reviewsCol = createReviewsColumn(primaryStage);

		// Create actions column
		TableColumn<User, Void> actionCol = createActionsColumn(userTable);

		userTable.getColumns().addAll(usernameCol, reviewsCol, actionCol);
		userTable.setEditable(true);

		userTable.getItems().clear();

		String fetchPending = "SELECT * FROM PendingReviewers";
		try (Statement stmt = StartCSE360.getDatabaseHelper().getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(fetchPending)) {
			while (rs.next()) {
				String userName = rs.getString("userName");

				User user = StartCSE360.getDatabaseHelper().fetchUser(userName);
				if (user != null) {
					userTable.getItems().add(user);
				} else {
					System.out.println("Skipping null user fetched for username: " + userName);
				}
			}

			System.out.println("Loaded PendingReviewers into table.");
		} catch (SQLException e) {
			System.err.println("Failed to fetch from PendingReviewers.");
			e.printStackTrace();
		}
	}

	private void removeUserFromTable(String userName, TableView<User> userTable) {
		StartCSE360.getDatabaseHelper().removeFromPendingReviewers(userName);
		for (User u : userTable.getItems()) {
			if (u == null)
				continue;
			if (u.getUserName().equals(userName)) {
				userTable.getItems().remove(u);
				break;
			}
		}
	}

	private TableColumn<User, String> createUsernameColumn() {
		TableColumn<User, String> usernameCol = new TableColumn<>("Username");
		usernameCol.setCellValueFactory(cellData -> {
			User u = cellData.getValue();
			if (u == null || u.getUserName() == null) {
				return new SimpleStringProperty("");
			}
			return new SimpleStringProperty(u.getUserName());
		});
		return usernameCol;
	}

	private TableColumn<User, Void> createReviewsColumn(Stage primaryStage) {
		TableColumn<User, Void> reviewsCol = new TableColumn<>("Reviews");
		reviewsCol.setCellFactory(col -> new TableCell<User, Void>() {
			private final Button reviewsBtn = new Button("Reviews");

			{
				reviewsBtn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
				reviewsBtn.setOnAction(a -> {
					User target = getTableView().getItems().get(getIndex());
					if (target == null)
						return;
					UserReviewsPage userReviewsPage = new UserReviewsPage();
					userReviewsPage.show(primaryStage, target);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(reviewsBtn);
				}
			}
		});
		return reviewsCol;
	}

	private TableColumn<User, Void> createActionsColumn(TableView<User> userTable) {
		TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
		actionCol.setCellFactory(col -> new TableCell<User, Void>() {
			private final Button acceptBtn = new Button("Accept");
			private final Button rejectBtn = new Button("Reject");
			private final HBox buttonBox = new HBox(10, acceptBtn, rejectBtn);

			{
				acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
				rejectBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

				acceptBtn.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					if (user == null)
						return;
					removeUserFromTable(user.getUserName(), userTable);
					StartCSE360.getDatabaseHelper().updateUserRole(user.getUserName(), UserRole.REVIEWER);
					System.out.println("Accepted: " + user.getUserName());
				});

				rejectBtn.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());
					if (user == null)
						return;
					removeUserFromTable(user.getUserName(), userTable);
					System.out.println("Rejected: " + user.getUserName());
				});

				buttonBox.setAlignment(Pos.CENTER);
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(buttonBox);
				}
			}
		});
		return actionCol;
	}
}
