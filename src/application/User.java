package application;

/**
 * The User class represents a user entity in the system. It contains the user's
 * details such as userName, password, and role.
 */
public class User {
	
	// PHASE 0 FIELDS
	private String userName;
	private String password;
	private UserRole role;
	
	// PHASE 1 FIELDS
	private String firstName;
	private String lastName;
	private String email;

	// Constructor to initialize a new User object with userName, password, and role.
	public User(String userName, String password, UserRole role) {
		this.userName = userName;
		this.password = password;
		this.role = role;
	}
	
	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;
		this.role = null;
	}
	
	public String getUserName() {
		return this.userName;
	}

	public String getPassword() {
		return this.password;
	}

	public UserRole getRole() {
		return this.role;
	}
	
	public void setRole(UserRole newRole) {
		this.role = newRole;
	}
	
	// PHASE 1 METHODS
	
	public void setUserName(String newUserName) {
		this.userName = newUserName;
		// TODO: Push to database
	}
	
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String newEmail) {
		this.email = newEmail;
	}
}
