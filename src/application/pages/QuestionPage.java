package application.pages;

import application.StartCSE360;
import application.obj.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.*;

/*
 * QuestionPage represents the user interface for a given question
 * It displays the question and its corresponding answers
 */

public class QuestionPage {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	
	public void show(Stage primaryStage, UserHomePage userHomePage, Question question)
	{
		List<Answer> answers = question.getAnswers();
		
		//creates an HBox that holds the back and delete buttons for questions
		HBox removeBox = createRemoveBox(primaryStage, userHomePage, question, answers);
		
		//creates an HBox that holds the title
		HBox titleBox = createTitleBox(primaryStage, userHomePage, question, answers);
		
		//creates an HBox that holds the author and date
		HBox headerBox = createHeaderBox(primaryStage, userHomePage, question, answers);
		
		//creates an HBox that holds the body
		VBox bodyBox = createBodyBox(primaryStage, userHomePage, question, answers);
		
		//creates a VBox where all answers are listed
		VBox answerBox = new VBox(15);
		answerBox.setPrefHeight(200);
		
		//creates scroll pane for answers
        ScrollPane answersScrollPane = new ScrollPane(answerBox);
        answersScrollPane.setFitToWidth(true);
        answersScrollPane.setContent(answerBox);
        
        //populate answers if any exist
		populateAnswerBox(answerBox, question, answers);
		
		//create text area and button to submit answers
		TextArea answerTextArea = new TextArea();
		Button postButton = new Button("Submit Answer");
		postButton.setOnMouseClicked(e -> {
			
			Answer answer = StartCSE360.getAnswerManager().createNewAnswer(StartCSE360.getCurrentUser().getUserName(), LocalDateTime.now(), answerTextArea.getText());
			question.addAnswers(answer);
			addAnswerLabel(answerBox, answer);
		});
		
		
		VBox layout = new VBox();
		layout.setStyle("-fx-alignment: top-center; -fx-padding: 20;");
		layout.getChildren().addAll(removeBox, titleBox, bodyBox, headerBox, new Separator(), answersScrollPane, answerTextArea, postButton);
		
		primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
	}
	private void addAnswerLabel(VBox answerBox, Answer answer)
	{
		HBox box = new HBox();
		Label answerLabel = new Label(answer.getContent());
		Label authorLabel = new Label(answer.getUserName());
		Label dateLabel = new Label(answer.getCreationDate().format(FORMATTER));
		
		answerLabel.setWrapText(true);

		Separator separator1 = new Separator();
		separator1.setOrientation(Orientation.VERTICAL);
		Separator separator2 = new Separator();
		separator2.setOrientation(Orientation.VERTICAL);
		
		box.getChildren().addAll(answerLabel, separator1, authorLabel, separator2, dateLabel);
		
		answerBox.getChildren().addAll(box);
	}
	private void populateAnswerBox(VBox answerBox, Question question, List<Answer> answers)
	{
		answerBox.getChildren().clear();
		for(Answer answer : question.getAnswers())
		{
			addAnswerLabel(answerBox, answer);
		}
	}
	private HBox createRemoveBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers)
	{
		HBox removeBox = new HBox(6);
		Button backButton = new Button("<-");
		backButton.setOnMouseClicked(e -> {
			userHomePage.show(primaryStage);
		});
		removeBox.getChildren().add(backButton);
		removeBox.setAlignment(Pos.TOP_RIGHT);
		if(StartCSE360.getCurrentUser().equals(question.getUserName()))
		{
			Button removeButton = new Button("X");
			removeButton.setAlignment(Pos.BASELINE_RIGHT);
			removeButton.setOnMouseClicked(e -> {
				userHomePage.show(primaryStage);
				StartCSE360.getQuestionManager().deleteQuestion(question);
			});
			removeBox.getChildren().add(removeButton);
		}
		return removeBox;
	}
	private HBox createTitleBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers)
	{
		HBox titleBox = new HBox(6);
		titleBox.setAlignment(Pos.TOP_CENTER);
		Label titleLabel = new Label(question.getTitle());
		titleLabel.setStyle("-fx-font-size: 32px;");
		titleBox.getChildren().addAll(titleLabel);
		return titleBox;
	}
	private HBox createHeaderBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers)
	{
		HBox headerBox = new HBox(6);
		headerBox.setAlignment(Pos.TOP_RIGHT);
		headerBox.setPrefWidth(Double.MAX_VALUE);
		Label authorLabel = new Label(question.getUserName());
		Label dateLabel = new Label(question.getCreationDate().format(FORMATTER));
		Separator headerSeparator = new Separator();
		headerSeparator.setOrientation(Orientation.VERTICAL);
		headerBox.getChildren().addAll(authorLabel, headerSeparator, dateLabel);
		return headerBox;
	}
	private VBox createBodyBox(Stage primaryStage, UserHomePage userHomePage, Question question, List<Answer> answers)
	{
		VBox bodyBox = new VBox();
		bodyBox.setAlignment(Pos.TOP_CENTER); 
		Label bodyLabel = new Label(question.getContent());
		bodyLabel.setStyle("-fx-font-size: 14px;");
		bodyLabel.setWrapText(true);
		bodyBox.getChildren().addAll(bodyLabel);
		return bodyBox;
	}
}
