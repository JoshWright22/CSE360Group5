package application.pages;

import application.StartCSE360;
import application.User;
import application.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.collections.FXCollections;

/**
 * The AdminPage class represents the user interface for the admin user. This
 * page displays a simple welcome message for the admin.
 */
public class AdminHomePage {

	private Button updateButton = new Button("Update Roles");

	/**
	 * Displays the admin page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {
		VBox layout = new VBox();

		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		// label to display the welcome message for the admin
		Label adminLabel = new Label("Hello, Admin!");

		adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		TableView<User> userTable = new TableView<>();
		setupUserTable(userTable);

		updateButton.setVisible(false);
		updateButton.setOnAction(e -> {
			updateDatabaseRoles(userTable);
		});
		layout.getChildren().addAll(adminLabel, updateButton, userTable);
		Scene adminScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(adminScene);
		primaryStage.setTitle("Admin Page");
	}

	/**
	 * Given a {@link TableView} with {@link User} as its generic object, this
	 * method sets up the TableView to show all users and their details.
	 * 
	 * @param userTable TableView to create/populate
	 */
	private void setupUserTable(TableView<User> userTable) {
		// Create User column
		TableColumn<User, String> usernameCol = new TableColumn<>("Username");
		usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));

		// Create Role column
		TableColumn<User, UserRole> roleCol = new TableColumn<>("Role");
		roleCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRole()));
		roleCol.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(UserRole.values())));
		roleCol.setOnEditCommit(event -> {
			User user = event.getRowValue();
			UserRole newRole = event.getNewValue();
			user.setRole(newRole);
			System.out.println("Updated role for " + user.getUserName() + " to " + newRole);
		});

		// Update Role styling and behavior
		roleCol.setCellFactory(column -> new TableCell<User, UserRole>() {
			private final ComboBox<UserRole> comboBox = new ComboBox<>(FXCollections.observableArrayList(UserRole.values()));
			{
				comboBox.setOnAction(event -> {
					User user = getTableRow().getItem();
					if (user != null) {
						UserRole oldRole = user.getRole();
						UserRole newRole = comboBox.getValue();

						// If the role actually changed
						if (newRole != null && !newRole.equals(oldRole)) {
							user.setRole(newRole);
							updateButton.setVisible(true);

							comboBox.setStyle("-fx-background-color: lightyellow;");
						} else {
							comboBox.setStyle("");
						}
					}
				});
			}

			@Override
			protected void updateItem(UserRole item, boolean empty) {
				super.updateItem(item, empty);

				if (empty) {
					setGraphic(null);
				} else {
					comboBox.setValue(item);
					setGraphic(comboBox);
				}
			}
		});

		userTable.getColumns().addAll(usernameCol, roleCol);
		userTable.setEditable(true);

		// Fetch Users from database and add them to table
		String query = "SELECT * FROM cse360users";
		try (ResultSet rs = StartCSE360.getDatabaseHelper().getStatement().executeQuery(query)) {
			while (rs.next()) {
				String userName = rs.getString("userName");
				String password = rs.getString("password");
				UserRole role = UserRole.valueOf(rs.getString("role").toUpperCase());

				User userToAdd = new User(userName, password, role);

				userTable.getItems().add(userToAdd);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch users from the database.");
			e.printStackTrace();
		}
	}

	/**
	 * Given a {@link TableView} with {@link User} as its generic object, this
	 * pushes the selected roles for the users in the TableView to the local H2
	 * database.
	 * 
	 * @param userTable TableView to update
	 */
	private void updateDatabaseRoles(TableView<User> userTable) {
		for (User user : userTable.getItems()) {
			if (!user.getRole().equals(StartCSE360.getDatabaseHelper().getUserRole(user.getUserName()))) {
				StartCSE360.getDatabaseHelper().updateUserRole(user.getUserName(), user.getRole());
			}
		}

		userTable.refresh();
		updateButton.setVisible(false);
	}

}