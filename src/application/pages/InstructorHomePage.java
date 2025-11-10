package application.pages;

import java.sql.ResultSet;
import java.sql.SQLException;

import application.StartCSE360;
import application.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class InstructorHomePage {
	public void show(Stage primaryStage){
		VBox layout = new VBox();

		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		TableView<User> userTable = new TableView<>();
		setupUserTable(userTable);

		layout.getChildren().addAll(userTable);
		Scene instructorScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(instructorScene);
		primaryStage.setTitle("Instructor Page");
	}
	
	private void setupUserTable(TableView<User> userTable) {
		// Create user column
		TableColumn<User, String> usernameCol = new TableColumn<>("Username");
		usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));

		// Create reviews column
		TableColumn<User, Void> reviewsCol = new TableColumn<>("Reviews");
		reviewsCol.setCellFactory(col -> new TableCell<User, Void>() {
			private final Button reviewsBtn = new Button("Reviews");
			
			{
				reviewsBtn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
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
		
		// Create accept/reject column
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
		            removeUserFromTable(user.getUserName(), userTable);
		            System.out.println("Accepted: " + user.getUserName());
		        });

		        rejectBtn.setOnAction(event -> {
		            User user = getTableView().getItems().get(getIndex());
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

		userTable.getColumns().addAll(usernameCol, reviewsCol, actionCol);
		userTable.setEditable(true);
		
		String query = "TRUNCATE TABLE PendingReviewers";

		try (Statement stmt = StartCSE360.getDatabaseHelper().getConnection().createStatement()) {
		    stmt.executeUpdate(query);
		    System.out.println("PendingReviewers table truncated.");
		} catch (SQLException e) {
		    e.printStackTrace();
		}

		String selectQuery = "SELECT * FROM cse360users";
		String insertQuery = "INSERT INTO PendingReviewers (userName) VALUES (?)";
		try (
		    Statement selectStmt = StartCSE360.getDatabaseHelper().getConnection().createStatement();
		    ResultSet rs = selectStmt.executeQuery(selectQuery);
		    PreparedStatement insertStmt = StartCSE360.getDatabaseHelper().getConnection().prepareStatement(insertQuery)
		) {
		    while (rs.next()) {
		        String userName = rs.getString("userName");
		        String password = rs.getString("password");
		        String role = rs.getString("role");

		        insertStmt.setString(1, userName);
		        insertStmt.executeUpdate();
		    }

		    System.out.println("All users inserted into PendingReviewers for testing.");
		} catch (SQLException e) {
		    System.err.println("Error inserting users into PendingReviewers.");
		    e.printStackTrace();
		}
		
		userTable.getItems().clear();

		String fetchPending = "SELECT * FROM PendingReviewers";
		try (
		    Statement stmt = StartCSE360.getDatabaseHelper().getConnection().createStatement();
		    ResultSet rs = stmt.executeQuery(fetchPending)
		) {
		    while (rs.next()) {
		        String userName = rs.getString("userName");

		        User user = StartCSE360.getDatabaseHelper().fetchUser(userName);
		        userTable.getItems().add(user);
		    }

		    System.out.println("Loaded PendingReviewers into table.");
		} catch (SQLException e) {
		    System.err.println("Failed to fetch from PendingReviewers.");
		    e.printStackTrace();
		}
	}
	
	private void removeUserFromTable(String userName, TableView<User> userTable)
	{
		StartCSE360.getDatabaseHelper().removeFromPendingReviewers(userName);
        for (User u : userTable.getItems()) {
            if (u.getUserName().equals(userName) && u != null) {
            	userTable.getItems().remove(u);
                break;
            }
        }
	}
}
