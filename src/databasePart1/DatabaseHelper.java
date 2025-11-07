package databasePart1;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import application.User;
import application.UserRole;

/**
 * The DatabaseHelper class is responsible for managing the connection to the
 * database, performing operations such as user registration, login validation,
 * and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;
	// PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		// Create the users table
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "email VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);

		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE)";
		statement.execute(invitationCodesTable);
		
		// Create the questions table
		String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "creationDate VARCHAR(255), "
				+ "title VARCHAR(255), "
				+ "content TEXT, "
				+ "answers VARCHAR(255), "
				+ "tags TEXT)";
		statement.execute(questionsTable);
		
		// Create the answers table
		String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "creationDate VARCHAR(255), "
				+ "content TEXT)";
		statement.execute(answersTable);
		
		// Create the comments table
		String commentsTable = "CREATE TABLE IF NOT EXISTS Comments ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "creationDate VARCHAR(255), "
				+ "content TEXT, "
				+ "tags TEXT, "
				+ "parentId INT)";
		statement.execute(commentsTable);
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT * FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (!resultSet.first()) return true;
		else return false;
	}
	
	public Statement getStatement() {
		return this.statement;
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	// PHASE 1 
	
	/**
	 * Create a user by populating all fields.
	 * 
	 * @param userName
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param role
	 * @return	new User
	 */
	public User createUser(String userName, String password, String firstName, String lastName, String email, UserRole role) {
		if (this.doesUserExist(userName)) {
			System.err.println("Attempted to create a user with a duplicate username.");
			return null;
		}
		
		String query = "INSERT INTO cse360users (userName, password, firstName, lastName, email, role) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, userName);
			stmt.setString(2, password);
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			stmt.setString(5, email);
			stmt.setString(6, role.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to register a user into the database.");
			e.printStackTrace();
		}
		
		return new User(userName, password, firstName, lastName, email, role);
	}
	
	/**
	 * Create a new user using only strictly-necessary details.
	 * 
	 * @param userName
	 * @param password
	 * @param role
	 * @return	new User
	 */
	public User createUser(String userName, String password, UserRole role) {
		return this.createUser(userName, password, "", "", "", role);
	}

	public void registerUser(User user) {
		
		String query = "INSERT INTO cse360users (userName, password, firstName, lastName, email, role) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, user.getUserName());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFirstName());
			stmt.setString(4, user.getLastName());
			stmt.setString(5, user.getEmail());
			stmt.setString(6, user.getRole().toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to register a user into the database.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Validates a user's login.
	 * 
	 * @param user
	 * @return	true if validation succeeds; false otherwise
	 * @throws SQLException
	 */
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole().toString());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	/**
	 * Attempts to fetch a user from the database given their userName.
	 * 
	 * @param userName
	 * @return	User object with matching username if found; NULL otherwise
	 */
	public User fetchUser(String userName) {
		String query = "SELECT * FROM cse360users WHERE userName = ?";
		User user = null;
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, userName);
			stmt.executeQuery();
			ResultSet rs = stmt.getResultSet();
			if (rs.next()) {
				user = new User(rs.getString("userName"), rs.getString("password"),
								rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"),
								UserRole.valueOf(rs.getString("role")));
			} 
		} catch (SQLException e) {
			System.err.println("Failed to fetch a user from the database.");
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Checks if a user already exists in the database based on their UserName.
	 * 
	 * @param userName
	 * @return	true if the user exists; false otherwise
	 */
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	/**
	 * Retrieves the role of a user from the database using their UserName.
	 * 
	 * @param userName
	 * @return
	 */
	public UserRole getUserRole(String userName) {
		String query = "SELECT role FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return UserRole.valueOf(rs.getString("role").toUpperCase()); // Return the role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no user exists or an error occurs
	}
	
	public void updateUserRole(String userName, UserRole newRole){
	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newRole.toString());
	        pstmt.setString(2, userName);

	        int rowsUpdated = pstmt.executeUpdate();
	        if (rowsUpdated == 0) {
	            System.out.println("No user found with username: " + userName);
	        } else {
	            System.out.println("Updated role for user: " + userName + " to " + newRole);
	        }
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
		String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Mark the code as used
				markInvitationCodeAsUsed(code);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

}
