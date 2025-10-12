package application.obj;

import java.time.LocalDateTime;

public class UserSubmission {

	protected int id;
	protected String userName;
	protected LocalDateTime creationDate;
	
	public UserSubmission(int id, String userName, LocalDateTime creationDate) {
		this.id = id;
		this.userName = userName;
		this.creationDate = creationDate;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public LocalDateTime getCreationDate() {
		return this.creationDate;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	
}
