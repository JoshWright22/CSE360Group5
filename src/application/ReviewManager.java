package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import application.obj.Answer;
import application.obj.Question;
import application.obj.Review;
import databasePart1.DatabaseHelper;

/**
 * Acts as a manager class for dealing with Review objects. This use of data hiding and encapsulation helps ensure parity
 * between data stored in the local cache and data stored on the MySQL database.
 */
public class ReviewManager {

	private final DatabaseHelper database;
	private final Set<Review> reviewSet = new HashSet<>();
	public ReviewManager(DatabaseHelper database) {
		this.database = database;
	}
	
	/**
	 * Attempts to fetch all data from the Reviews table.
	 */
	public void fetchReviews() {
		// Ensure database connection is available
		if (this.database.getConnection() == null) {
			try {
				this.database.connectToDatabase();
			} catch (SQLException e) {
				System.err.println("Failed to connect to database when fetching reviews: " + e.getMessage());
				return;
			}
		}

		String query = "SELECT * FROM Reviews";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query);
			ResultSet rs = stmt.executeQuery()) {
			
			reviewSet.clear();
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationDate = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				int rating = rs.getInt("rating");
				Integer questionId = rs.getObject("questionId", Integer.class);
				Integer answerId = rs.getObject("answerId", Integer.class);
				Review r = null;

				try {
					if (questionId != null) {
						Question q = StartCSE360.getQuestionManager().fetchQuestion(questionId);
						if (q != null) {
							r = new Review(id, userName, creationDate, content, rating, q);
						}
					} else if (answerId != null) {
						Answer a = StartCSE360.getAnswerManager().fetchAnswer(answerId);
						if (a != null) {
							r = new Review(id, userName, creationDate, content, rating, a);
						}
					}
					
					if (r != null) {
						this.reviewSet.add(r);
					} else {
						System.err.println("Failed to create review with ID " + id + ": associated question/answer not found");
					}
				} catch (Exception e) {
					System.err.println("Error processing review with ID " + id + ": " + e.getMessage());
				}
			}
		} catch (SQLException e) {
			System.err.println("Failed to get all reviews from the database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetches a particular review from the database given the review's ID.
	 * 
	 * @param id ID of review whose data will be fetched
	 * @return Review object constructed from database data
	 */
	public Review fetchReview(int id) {
		Review result = null;
		
		String query = "SELECT * FROM Reviews WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("id");
				String userName = rs.getString("userName");
				LocalDateTime creationTime = LocalDateTime.parse(rs.getString("creationDate"));
				String content = rs.getString("content");
				int rating = rs.getInt("rating");
				Integer questionId = rs.getObject("questionId", Integer.class);
				Integer answerId = rs.getObject("answerId", Integer.class);
				
				if (questionId != null) {
					Question q = StartCSE360.getQuestionManager().fetchQuestion(questionId);
					result = new Review(i, userName, creationTime, content, rating, q);
				} else {
					Answer a = StartCSE360.getAnswerManager().fetchAnswer(answerId);
					result = new Review(i, userName, creationTime, content, rating, a);
				}
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch review from database.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Gets an unmodifiable reference to the set of reviews stored in the local cache.
	 * 
	 * @return Unmodifiable set containing local reviews
	 */
	public Set<Review> getReviewSet() {
		return Collections.unmodifiableSet(this.reviewSet);
	}
	
	/**
	 * Gets all reviews written by a specific reviewer.
	 * 
	 * @param userName Name of the reviewer
	 * @return Set of reviews by the specified user
	 */
	public Set<Review> getReviewsByUser(String userName) {
		return reviewSet.stream()
				.filter(r -> r.getUserName().equals(userName))
				.collect(Collectors.toSet());
	}
	
	/**
	 * Gets all reviews for a specific question.
	 * 
	 * @param questionId ID of the question
	 * @return Set of reviews for the specified question
	 */
	public Set<Review> getReviewsForQuestion(int questionId) {
		return reviewSet.stream()
				.filter(r -> r.isQuestionReview() && r.getReviewedQuestion().getId() == questionId)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Gets all reviews for a specific answer.
	 * 
	 * @param answerId ID of the answer
	 * @return Set of reviews for the specified answer
	 */
	public Set<Review> getReviewsForAnswer(int answerId) {
		return reviewSet.stream()
				.filter(r -> r.isAnswerReview() && r.getReviewedAnswer().getId() == answerId)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Creates a new Review for a Question before inserting it into the Reviews table and local cache.
	 * 
	 * @param userName Name of reviewer
	 * @param creationDate When the review was created
	 * @param content Review content
	 * @param rating Rating value (1-5)
	 * @param question Question being reviewed
	 * @return new Review object
	 */
	public Review createNewQuestionReview(String userName, LocalDateTime creationDate, String content, int rating, Question question) {
		// Check if database is connected, if not try to reconnect
		if (this.database.getConnection() == null) {
			try {
				this.database.connectToDatabase();
			} catch (SQLException e) {
				System.err.println("Failed to connect to database: " + e.getMessage());
				return null;
			}
		}

		String query = "INSERT INTO Reviews (userName, creationDate, content, rating, questionId, answerId) VALUES (?, ?, ?, ?, ?, NULL)";
		int id = -1;
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, userName);
			stmt.setString(2, creationDate.toString());
			stmt.setString(3, content);
			stmt.setInt(4, rating);
			stmt.setInt(5, question.getId());
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();
			if (results.next()) {
				id = results.getInt(1);
			} else {
				System.err.println("Failed to get generated ID for new review");
				return null;
			}
		} catch (SQLException e) {
			System.err.println("Failed to create a new question review: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		Review r = new Review(id, userName, creationDate, content, rating, question);
		this.reviewSet.add(r);
		return r;
	}
  
	/**
	 * Creates a new Review for an Answer before inserting it into the Reviews table and local cache.
	 * 
	 * @param userName Name of reviewer
	 * @param creationDate When the review was created
	 * @param content Review content
	 * @param rating Rating value (1-5)
	 * @param answer Answer being reviewed
	 * @return new Review object
	 */
	public Review createNewAnswerReview(String userName, LocalDateTime creationDate, String content, int rating, Answer answer) {
		// Check if database is connected, if not try to reconnect
		if (this.database.getConnection() == null) {
			try {
				this.database.connectToDatabase();
			} catch (SQLException e) {
				System.err.println("Failed to connect to database: " + e.getMessage());
				return null;
			}
		}

		String query = "INSERT INTO Reviews (userName, creationDate, content, rating, questionId, answerId) VALUES (?, ?, ?, ?, NULL, ?)";
		int id = -1;
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, userName);
			stmt.setString(2, creationDate.toString());
			stmt.setString(3, content);
			stmt.setInt(4, rating);
			stmt.setInt(5, answer.getId());
			stmt.executeUpdate();
			ResultSet results = stmt.getGeneratedKeys();
			if (results.next()) {
				id = results.getInt(1);
			} else {
				System.err.println("Failed to get generated ID for new review");
				return null;
			}
		} catch (SQLException e) {
			System.err.println("Failed to create a new answer review: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		Review r = new Review(id, userName, creationDate, content, rating, answer);
		this.reviewSet.add(r);
		return r;
	}
	
	/**
	 * Updates an existing review's content and rating in the database.
	 * 
	 * @param review Review to update
	 */
	public void updateReview(Review review) {
		String query = "UPDATE Reviews SET content = ?, rating = ? WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setString(1, review.getContent());
			stmt.setInt(2, review.getRating());
			stmt.setInt(3, review.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to update review in database.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes a review from the database and local cache.
	 * 
	 * @param review Review to delete
	 */
	public void deleteReview(Review review) {
		String query = "DELETE FROM Reviews WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, review.getId());
			stmt.executeUpdate();
			reviewSet.remove(review);
		} catch (SQLException e) {
			System.err.println("Failed to delete review from database.");
			e.printStackTrace();
		}
	}
}
