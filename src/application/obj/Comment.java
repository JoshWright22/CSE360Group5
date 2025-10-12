package application.obj;

import java.time.LocalDateTime;

public class Comment extends UserSubmission {

	private String content;
	private Question parent;
	
	public Comment(int id, String userName, LocalDateTime creationTime, String content, Question parent) {
		super(id, userName, creationTime);
		this.content = content;
		this.parent = parent;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Question getParentQuestion() {
		return this.parent;
	}
}
