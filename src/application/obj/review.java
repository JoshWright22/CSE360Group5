package application.obj;

import java.time.LocalDateTime;

/**
 * An object that represents a reviewer's evaluation of a question or answer.
 * Reviews can be of either questions or answers, but not both simultaneously.
 */

public class Review extends UserSubmission {

	private String content;
	private int rating;
	private Question reviewedQuestion;
	private Answer reviewedAnswer;
	
	/**
	 * Constructs a Review for a Question.
	 * 
	 * @param id Review ID
	 * @param userName Name of reviewer
	 * @param creationDate When the review was created
	 * @param content Review content/body
	 * @param rating Rating value (1-5)
	 * @param reviewedQuestion The question being reviewed
	 */
  
	public Review(int id, String userName, LocalDateTime creationDate, String content, int rating, Question reviewedQuestion) {
		super(id, userName, creationDate);
		if (rating < 1 || rating > 5)
			throw new IllegalArgumentException("Rating must be between 1 and 5");
		if (content == null || content.trim().isEmpty())
			throw new IllegalArgumentException("Review content cannot be empty");
		
		this.content = content;
		this.rating = rating;
		this.reviewedQuestion = reviewedQuestion;
		this.reviewedAnswer = null;
	}
	
	/**
	 * Constructs a Review for an Answer.
	 * 
	 * @param id Review ID
	 * @param userName Name of reviewer
	 * @param creationDate When the review was created
	 * @param content Review content/body
	 * @param rating Rating value (1-5)
	 * @param reviewedAnswer The answer being reviewed
	 */
  
	public Review(int id, String userName, LocalDateTime creationDate, String content, int rating, Answer reviewedAnswer) {
		super(id, userName, creationDate);
		if (rating < 1 || rating > 5)
			throw new IllegalArgumentException("Rating must be between 1 and 5");
		if (content == null || content.trim().isEmpty())
			throw new IllegalArgumentException("Review content cannot be empty/NULL");
		
		this.content = content;
		this.rating = rating;
		this.reviewedAnswer = reviewedAnswer;
		this.reviewedQuestion = null;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		if (content == null || content.trim().isEmpty())
			throw new IllegalArgumentException("Review content cannot be empty/NULL");
		this.content = content;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	public void setRating(int rating) {
		if (rating < 1 || rating > 5)
			throw new IllegalArgumentException("Rating must be between 1 and 5");
		this.rating = rating;
	}
	
	public Question getReviewedQuestion() {
		return this.reviewedQuestion;
	}
	
	public Answer getReviewedAnswer() {
		return this.reviewedAnswer;
	}
	
	public boolean isQuestionReview() {
		return this.reviewedQuestion != null;
	}
	
	public boolean isAnswerReview() {
		return this.reviewedAnswer != null;
	}
}
