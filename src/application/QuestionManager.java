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

/**
 * Acts as a manager class for dealing with Answer objects. This use of data
 * hiding and encapsulation helps ensure parity between data stored in the local
 * cache and data stored on the MySQL database.
 */
public class QuestionManager {

	// For database access
	private final DatabaseHelper database;

	private final Set<Question> questionSet = new HashSet<>();

	public QuestionManager(DatabaseHelper database) {
		this.database = database;
	}

	/**
	 * Fetches all data from the Questions table.
	 */
	public void fetchQuestions() {
		String query = "SELECT * FROM Questions";
		try (ResultSet rs = this.database.getStatement().executeQuery(query)) {

			questionSet.clear();

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
					if (!s.isEmpty()) {
						int answerID = Integer.parseInt(s);
						Answer a = StartCSE360.getAnswerManager().fetchAnswer(answerID);
						answers.add(a);
					}

				}

				List<String> tags = Arrays.asList(rs.getString("tags").split(","));

				// Construct question and add it into local cache
				Question q = new Question(id, userName, creationDate, title, content, answers, tags);
				this.questionSet.add(q);
				// Add answers to question
				for (Answer a : answers)
					q.addAnswers(a);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch all questions from the database.");
			e.printStackTrace();
		}
	}

	/**
	 * Fetches a particular question from the Questions table.
	 * 
	 * @param id id of question to fetch
	 * @return Question object
	 */
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
					if (!s.isEmpty()) {
						Answer a = StartCSE360.getAnswerManager().fetchAnswer(Integer.parseInt(s));
						answers.add(a);
					}
				}
				List<String> tags = Arrays.asList(rs.getString("tags").split(","));

				// Construct the Question object from the retrieved & processed data
				result = new Question(i, userName, creationTime, title, content, answers, tags);
				// Add the fetched question to the local cache if necessary
				if (!this.questionSet.contains(result))
					this.questionSet.add(result);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch answer from database.");
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Gets an unmodifiable reference to the set of questions stored in the local
	 * cache.
	 * 
	 * @return Unmodifiable set containing local questions
	 */
	public Set<Question> getQuestionSet() {
		return Collections.unmodifiableSet(this.questionSet);
	}

	/**
	 * Creates a new question before inserting it into the Questions table and local
	 * cache.
	 * 
	 * @param userName     Name of user who asked the question
	 * @param creationTime When the question was created
	 * @param title        User-submitted question title
	 * @param content      User-submitted question body/content
	 * @param tags         Tags for categorization and search purposes
	 * @return new Question object
	 */
	public Question createNewQuestion(String userName, LocalDateTime creationDate, String title, String content,
			List<String> tags) {

		if (tags == null)
			tags = new ArrayList<>();

		// Split title into searchable tags
		for (String s : title.trim().split("\\s+")) {
			s = s.replaceAll("[^a-zA-Z0-9]", "");
			s = s.toLowerCase();
			if (!s.isEmpty() && !tags.contains(s)) {
				tags.add(s);
			}
		}

		String query = "INSERT INTO Questions (userName, creationDate, title, content, answers, tags) VALUES (?, ?, ?, ?, ?, ?)";
		int id = -1;
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, userName);
			stmt.setString(2, creationDate.toString());
			stmt.setString(3, title);
			stmt.setString(4, content);
			stmt.setString(5, "");
			stmt.setString(6, String.join(",", tags));
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();
			if (results.next())
				id = results.getInt(1);
		} catch (SQLException e) {
			System.err.println("Failed to create a new question.");
			e.printStackTrace();
		}

		Question q = new Question(id, userName, creationDate, title, content, null, tags);
		this.questionSet.add(q);
		return q;
	}

	/**
	 * Deletes a particular question from the database and the local cache.
	 * 
	 * @param q Question to delete
	 */
	public void deleteQuestion(Question q) {
		String query = "DELETE FROM Questions WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, q.getId());
			stmt.executeUpdate();
			questionSet.remove(q);
		} catch (SQLException e) {
			System.out.println("Failed to delete a question from the database.");
			e.printStackTrace();
		}
	}

	/**
	 * Adds an answer to a question and updates the database
	 * 
	 * @param q question to update
	 * @param a answer to insert
	 */
	public void addAnswerToQuestion(Question q, Answer a) {
		// Update the question
		q.addAnswers(a);

		// Fetch the current CSV string from the database
		String query = "SELECT answers FROM Questions WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, q.getId());
			ResultSet rs = stmt.executeQuery();
			String answerIds = "";
			if (rs.next()) {
				answerIds = rs.getString("answers");
			}

			// Append the new answer ID
			String updatedCsv = (answerIds == null || answerIds.isEmpty()) ? String.valueOf(a.getId())
					: answerIds + "," + a.getId();

			// Update the database
			String updateQuery = "UPDATE Questions SET answers = ? WHERE id = ?";
			try (PreparedStatement updateStmt = this.database.getConnection().prepareStatement(updateQuery)) {
				updateStmt.setString(1, updatedCsv);
				updateStmt.setInt(2, q.getId());
				updateStmt.executeUpdate();
			}

		} catch (SQLException e) {
			System.err.println("Failed to update question answers in the database.");
			e.printStackTrace();
		}
	}

	/**
	 * Searches all locally cached questions by tag. This also checks tags of
	 * answers belonging to the question.
	 *
	 * @param queryTag The tag string to search for
	 * @return A set of Question objects that contain the tag
	 */
	public Set<Question> searchByTag(String queryTag) {
		Set<Question> results = new HashSet<>();
		String tagLower = queryTag.toLowerCase();

		for (Question q : questionSet) {
			for (String tag : q.getTags()) {
				if (tag.toLowerCase().contains(tagLower)) {
					results.add(q);
					break;
				}
			}
		}
		return results;
	}
}
