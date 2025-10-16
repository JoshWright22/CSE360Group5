package application.obj;

import java.time.LocalDateTime;

/**
 * A parent class that is extended by the Question, Answer, and Comment classes.
 */
public class UserSubmission {

	protected final int id;
	protected final String userName;
	protected final LocalDateTime creationDate;
	
	public UserSubmission(int id, String userName, LocalDateTime creationDate) {

		this.id = id;
		this.userName = userName;
		this.creationDate = creationDate;
	}

	/**
	 * Gets the ID of the UserSubmission.
	 * 
	 * @return	int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the name of the user who made the UserSubmission.
	 * 
	 * @return	String
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * Gets the creation date/time of the UserSubmission.
	 * 
	 * @return	LocalDateTime
	 */
	public LocalDateTime getCreationDate() {
		return this.creationDate;
	}
}
