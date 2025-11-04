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

	/**
	 * Constructs a new User object, taking every field as a parameter.
	 * 
	 * @param userName  Username of the new user
	 * @param password  Password of the new user
	 * @param firstName New user's first name
	 * @param lastName  New user's password
	 * @param email     New user's email address as a String
	 * @param role      UserRole to use, determining user's access level
	 */
	public User(String userName, String password, String firstName, String lastName, String email, UserRole role) {
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
	}

	public User(String userName, String password, UserRole role) {
		this.userName = userName;
		this.password = password;
		this.role = role;
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

	public void setPassword(String newPassword) {
		this.password = newPassword;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String newEmail) {
		this.email = newEmail;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String newFirstName) {
		this.firstName = newFirstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String newLastName) {
		this.lastName = newLastName;
	}
}
