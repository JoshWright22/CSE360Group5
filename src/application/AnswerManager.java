package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.obj.Answer;
import databasePart1.DatabaseHelper;

public class AnswerManager {

	private final DatabaseHelper database;
	private final Set<Answer> answerSet = new HashSet<>();
	
	public AnswerManager(DatabaseHelper database) {
		this.database = database;
	}
	
	public void fetchAnswers() {
		String query = "SELECT * FROM Answers";
		try (ResultSet rs = this.database.getStatement().executeQuery(query)) {
			while (rs.next()) {
				// Collect required information to construct Answer object
				int id = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationDate = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				List<String> tags = Arrays.asList(rs.getString("tags").split(","));
				
				// Construct answer and add it into local cache
				Answer a = new Answer(id, userName, creationDate, content, tags);
				this.answerSet.add(a);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch all answers from the database.");
			e.printStackTrace();
		}
	}
	
	public Answer fetchAnswer(int id) {
		Answer result = null;
		
		String query = "SELECT * FROM Answers WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(0, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationTime = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				List<String> tags = Arrays.asList(rs.getString("tags").split(","));
				
				result = new Answer(i, userName, creationTime, content, tags);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch answer from database.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Set<Answer> getAnswerSet() {
		return Collections.unmodifiableSet(this.answerSet);
	}
}
