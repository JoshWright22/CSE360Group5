package application.pages;

import application.StartCSE360;
import application.User;

import application.obj.*;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class UserHomePage {	
	
	public void show(Stage primaryStage)
	{
		showScene(primaryStage);
	}
	
    private void showScene(Stage primaryStage)
    {	
    	User currentUser = StartCSE360.getCurrentUser();    			
    	
    	Label userLabel;
    	//welcome label
    	if(currentUser != null)
    		userLabel = new Label("Hello, " + currentUser.getUserName() + "!");
    	else
    		userLabel = new Label("Hello, Anonymous!");
    	
    	
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        //creates a box to contain all questions
        VBox questionsBox = new VBox(5);
        populateQuestions(questionsBox, primaryStage);
        
        //creates scroll pane for questions
        ScrollPane questionsScrollPane = new ScrollPane(questionsBox);
        questionsScrollPane.setFitToWidth(true);
        questionsScrollPane.setContent(questionsBox);
        
        //creates a box and text area to submit questions
        VBox postBox = new VBox(10);
        Label titleLabel = new Label("Title: ");
        Label bodyLabel = new Label("Body: ");
        TextArea titleTextArea = new TextArea();
        TextArea bodyTextArea = new TextArea();
        Button postButton = new Button("Post question");
        
        titleTextArea.setPrefHeight(2);
        
        //creates new question on press of postButton
        postButton.setOnAction(e -> {
        	Question question = StartCSE360.getQuestionManager().createNewQuestion(currentUser.getUserName(), LocalDateTime.now(), titleTextArea.getText(), bodyTextArea.getText(), new ArrayList<String>());
        	addQuestionLabel(questionsBox, question, primaryStage);
        });
        
        postBox.getChildren().addAll(titleLabel, titleTextArea, bodyLabel, bodyTextArea, postButton);
        
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: top-center; -fx-padding: 20;");
        layout.getChildren().addAll(userLabel, questionsScrollPane, new Separator(), postBox);
        
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
        questionsBox.prefHeightProperty().bind(userScene.heightProperty().multiply(0.75));
        postBox.prefHeightProperty().bind(userScene.heightProperty().multiply(0.25));
    }
    private void setQuestionLabelStyle(Label questionLabel)
    {
    	questionLabel.setStyle(
    			"-fx-font-size: 18px;" +
    		    "-fx-background-color: #f0f0f0; " +
    		    "-fx-padding: 10; " +
    		    "-fx-background-radius: 5; " +
    		    "-fx-border-color: transparent; " +
    		    "-fx-border-radius: 5;"
    		);

    		questionLabel.setOnMouseEntered(f ->
    		questionLabel.setStyle(
    		    	"-fx-font-size: 18px;" +
    		        "-fx-background-color: #f0f0f0; " +
    		        "-fx-padding: 10; " +
    		        "-fx-background-radius: 5; " +
    		        "-fx-border-color: #ADD0F2; " +
    		        "-fx-border-radius: 5;"
    		 ));

    		questionLabel.setOnMouseExited(f ->
    		questionLabel.setStyle(
    		    	"-fx-font-size: 18px;" +
    		        "-fx-background-color: #f0f0f0; " +
    		        "-fx-padding: 10; " +
    		        "-fx-background-radius: 5; " +
    		        "-fx-border-color: transparent; " +
    		        "-fx-border-radius: 5;"
    		 ));
    }
    private void addQuestionLabel(VBox questionsBox, Question question, Stage primaryStage)
    {
    	Label questionLabel = new Label(question.getTitle());
		questionLabel.setWrapText(true);
		questionLabel.setMaxWidth(Double.MAX_VALUE);
		questionLabel.setPrefHeight(50);

		setQuestionLabelStyle(questionLabel);
	
		questionLabel.setOnMouseClicked(f -> {
			QuestionPage questionPage = new QuestionPage();
			questionPage.show(primaryStage, this, question);
			});
		
		questionsBox.getChildren().add(questionLabel);
    }
    private void populateQuestions(VBox questionsBox, Stage primaryStage) 
    {
        questionsBox.getChildren().clear(); // remove old entries

        for (Question question : StartCSE360.getQuestionManager().getQuestionSet()) 
        {
        	addQuestionLabel(questionsBox, question, primaryStage);
        }
    }
}