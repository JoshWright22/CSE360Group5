package application.pages;

import application.StartCSE360;
import application.User;
import application.obj.Review;
import java.util.Set;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserReviewsPage {
	public void show(Stage primaryStage, User user) {
		//Get reviews for specific user from the reviewManager
	    Set<Review> reviewsSet = StartCSE360.getReviewManager().getReviewsByUser(user.getUserName());
	    //Create an observableList from the reviews
	    ObservableList<Review> reviewsList = FXCollections.observableArrayList(reviewsSet);

	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

	    //If user has no reviews, display no reviews message
	    if (reviewsList.isEmpty()) {
	        Label noReviewsLabel = new Label("No reviews yet");
	        noReviewsLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic; -fx-font-size: 13px;");
	        layout.getChildren().add(noReviewsLabel);   
	    } 
	    //Otherwise show reviews
	    else {
	        ListView<Review> reviewsListView = new ListView<>(reviewsList);

	        //Create ListView elements and styling
	        reviewsListView.setCellFactory(listView -> new ListCell<Review>() {
	            private final Label ratingLabel = new Label();
	            private final Label reviewContent = new Label();
	            private final Label reviewAuthor = new Label();
	            private final HBox reviewBox = new HBox(10);

	            {
	                reviewBox.setStyle("""
	                    -fx-padding: 8;
	                    -fx-background-color: white;
	                    -fx-border-color: #e0e0e0;
	                    -fx-border-radius: 5;
	                    -fx-background-radius: 5;
	                """);
	                reviewBox.setSpacing(15);

	                ratingLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");
	                reviewContent.setWrapText(true);
	                reviewContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
	                reviewAuthor.setStyle("-fx-font-style: italic; -fx-text-fill: #666666; -fx-font-size: 12px;");

	                reviewBox.getChildren().addAll(ratingLabel, reviewContent, reviewAuthor);
	            }

	            @Override
	            protected void updateItem(Review review, boolean empty) {
	                super.updateItem(review, empty);
	                if (empty || review == null) {
	                    setGraphic(null);
	                } else {
	                    ratingLabel.setText("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
	                    reviewContent.setText(review.getContent());
	                    reviewAuthor.setText("- " + review.getUserName());
	                    setGraphic(reviewBox);
	                }
	            }
	        });

	        reviewsListView.setStyle("""
	            -fx-selection-bar: transparent;
	            -fx-selection-bar-non-focused: transparent;
	        """);

	        layout.getChildren().add(reviewsListView);
	    }

	    // Back button
	    Button backBtn = new Button("Go back");
	    backBtn.setOnAction(a -> {
	        InstructorHomePage instructorHomePage = new InstructorHomePage();
	        instructorHomePage.show(primaryStage);
	    });
	    layout.getChildren().add(backBtn);

	    Scene userScene = new Scene(layout, 800, 400);
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Reviews Page");
	}
}
