package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.obj.Answer;
import application.obj.Question;
import databasePart1.DatabaseHelper;

/**
 * Acts as a manager class for dealing with Answer objects. This use of data hiding and encapsulation helps ensure parity
 * between data stored in the local cache and data stored on the MySQL database.
 */
public class AnswerManager {

	private final DatabaseHelper database;
	private final Set<Answer> answerSet = new HashSet<>();
	
	public AnswerManager(DatabaseHelper database) {
		this.database = database;
	}
	
	/**
	 * Attempts to fetch all data from the Answers table.
	 */
	public void fetchAnswers() {
		String query = "SELECT * FROM Answers";
		try (ResultSet rs = this.database.getStatement().executeQuery(query)) {
			
			answerSet.clear();
			
			while (rs.next()) {
				// Collect required information to construct Answer object
				int id = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationDate = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				//List<String> tags = Arrays.asList(rs.getString("tags").split(","));
				
				// Construct answer and add it into local cache
				Answer a = new Answer(id, userName, creationDate, content, null);
				this.answerSet.add(a);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch all answers from the database.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetches a particular answer from the database given the answer's ID.
	 * 
	 * @param id	ID of answer whose data will be fetched
	 * @return		Answer object constructed from database data
	 */
	public Answer fetchAnswer(int id) {
		Answer result = null;
		
		String query = "SELECT * FROM Answers WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationTime = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				//List<String> tags = Arrays.asList(rs.getString("tags").split(","));
				
				result = new Answer(i, userName, creationTime, content, null);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch answer from database.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Gets an unmodifiable reference to the set of answers stored in the local cache.
	 * 
	 * @return	Unmodifiable set containing local answers
	 */
	public Set<Answer> getAnswerSet() {
		return Collections.unmodifiableSet(this.answerSet);
	}
	
	/**
	 * Creates a new Answer before inserting it into the Answers table and local
	 * cache.
	 * 
	 * @param userName     Name of user who posted the answer
	 * @param creationTime When the answer was created
	 * @param content      User-submitted answer body/content
	 * @return new Answer object
	 */
	public Answer createNewAnswer(String userName, LocalDateTime creationDate, String content) {
		String query = "INSERT INTO Answers (userName, creationDate, content) VALUES (?, ?, ?)";
		int id = -1;
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, userName);
			stmt.setString(2, creationDate.toString());
			stmt.setString(3, content);
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();
			if (results.next())
				id = results.getInt(1);
		} catch (SQLException e) {
			System.err.println("Failed to create a new answer.");
			e.printStackTrace();
		}

		Answer a = new Answer(id, userName, creationDate, content, null);
		this.answerSet.add(a);
		return a;
	}

}
