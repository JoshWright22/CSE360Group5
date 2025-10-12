package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.obj.Answer;
import application.obj.Question;
import databasePart1.DatabaseHelper;

public class QuestionManager {
	
	// For database access
	private final DatabaseHelper database;
	
	private final Set<Question> questionSet = new HashSet<>();
	
	public QuestionManager(DatabaseHelper database) {
		this.database = database;
	}
	
	public Set<Question> getQuestionSet() {
		return Collections.unmodifiableSet(this.questionSet);
	}
	
	public void fetchQuestions() {
		String query = "SELECT * FROM Questions";
		try (ResultSet rs = this.database.getStatement().executeQuery(query)) {
			while (rs.next()) {
				// Collect required information to construct Question object
				int id = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationDate = LocalDateTime.parse(rs.getString("creationDate"));
				String title = rs.getString("title");
				String content = rs.getString("content");
				// Sadly, fetching answers is a bit more complicated
				String answerIds = rs.getString("answers");
				List<Answer> answers = new ArrayList<>();
				for (String s : answerIds.split(",")) {
					Answer a = StartCSE360.getAnswerManager().fetchAnswer(Integer.parseInt(s));
					answers.add(a);
				}
				
				List<String> tags = Arrays.asList(rs.getString("tags").split(","));
				
				// Construct question and add it into local cache
				Question q = new Question(id, userName, creationDate, title, content, answers, tags);
				this.questionSet.add(q);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch all questions from the database.");
			e.printStackTrace();
		}
	}
	
	public Question fetchQuestion(int id) {
		Question result = null;
		
		String query = "SELECT * FROM Questions WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(0, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationTime = LocalDateTime.parse(rs.getString("creationDate"));
				String title = rs.getString("title");
				String content = rs.getString("content");
				// Do the over-complicated way of fetching the answers
				String answerIds = rs.getString("answers");
				List<Answer> answers = new ArrayList<>();
				for (String s : answerIds.split(",")) {
					Answer a = StartCSE360.getAnswerManager().fetchAnswer(Integer.parseInt(s));
					answers.add(a);
				}
				List<String> tags = Arrays.asList(rs.getString("tags").split(","));
				
				result = new Question(i, userName, creationTime, title, content, answers, tags);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch answer from database.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Question createNewQuestion(String userName, LocalDateTime creationTime, String title, String content, List<String> tags) {
		String query = "INSERT INTO Questions (userName, creationTime, title, content, answers, tags) VALUES (?, ?, ?, ?, ?, ?)";
		int id = -1;
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(0, userName);
			stmt.setString(1, creationTime.toString());
			stmt.setString(2, title);
			stmt.setString(3, content);
			stmt.setString(4, "");
			stmt.setString(5, String.join(",", tags));
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();
			if (results.next())
				id = results.getInt(1);
		} catch (SQLException e) {
			System.err.println("Failed to create a new question.");
			e.printStackTrace();
		}
		
		Question q = new Question(id, userName, creationTime, title, content, null, tags);
		this.questionSet.add(q);
		return q;
	}
	
	public void deleteQuestion(Question q) {
		String query = "DELETE FROM Questions WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, q.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Failed to delete a question from the database.");
			e.printStackTrace();
		}
	}
}
