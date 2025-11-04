package application.obj;

import java.time.LocalDateTime;

/**
 * An object that represents user-submitted comments.
 */
public class Comment extends UserSubmission {

	private String content;
	private Question parent;

	public Comment(int id, String userName, LocalDateTime creationTime, String content, Question parent) {
		super(id, userName, creationTime);
		this.content = content;
		this.parent = parent;
	}

	/**
	 * Gets the content of the comment.
	 * 
	 * @return String
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Sets the content of the comment.
	 * 
	 * @param content New content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets the parent question of this comment.
	 * 
	 * @return Question
	 */
	public Question getParentQuestion() {
		return this.parent;
	}
}
