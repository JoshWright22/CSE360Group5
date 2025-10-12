package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import application.obj.Comment;
import application.obj.Question;
import databasePart1.DatabaseHelper;

/**
 * Acts as a manager class for dealing with Comment objects. This use of data hiding and encapsulation helps ensure parity
 * between data stored in the local cache and data stored on the MySQL database.
 */
public class CommentManager {

	private final DatabaseHelper database;
	private Set<Comment> commentSet = new HashSet<>();
	
	public CommentManager(DatabaseHelper database) {
		this.database = database;
	}
	
	/**
	 * Fetches all data from the Comments table.
	 */
	public void fetchComments() {
		String query = "SELECT * FROM Comments";
		try (ResultSet rs = this.database.getStatement().executeQuery(query)) {
			while (rs.next()) {
				// Collect required information to construct Comment object
				int id = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationDate = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				// Sadly, fetching questions is a bit more complicated
				Question q = StartCSE360.getQuestionManager().fetchQuestion(rs.getInt("parent"));
				
				// Construct comment and add it into local cache
				Comment c = new Comment(id, userName, creationDate, content, q);
				this.commentSet.add(c);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch all answers from the database.");
			e.printStackTrace();
		}
	}
	
}
