package test.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.User;
import application.UserRole;
import databasePart1.DatabaseHelper;

/**
 * This class consists of methods created to test various parts of database
 * functionality. It is automated via JUnit.
 * 
 * <p>
 * Because this class tests database functionality, <b>each test has the
 * possibility to throw an {@link SQLException}.</b> Given that the database
 * being tested exists locally, such exceptions should, in theory, never get
 * thrown.
 */
public class DatabaseHelperTest {
	private static DatabaseHelper dbHelper;

	/**
	 * Before tests can be run, a connection to the SQL database must be established
	 * 
	 * @throws SQLException
	 */
	@BeforeAll
	static void setup() throws SQLException {
		dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
	}

	/**
	 * In line with good practices, opened resources should be closed once they are
	 * no longer in use.
	 */
	@AfterAll
	static void exit() {
		dbHelper.closeConnection();
	}

	/**
	 * This test is to ensure that the database starts empty and that the
	 * {@link DatabaseHelper#isDatabaseEmpty()} method will accurately report that
	 * the database is empty.
	 * 
	 * <p>
	 * <b>This test only functions properly if the database is cleared prior to the
	 * test running.
	 * 
	 * @throws SQLException
	 */
	@Test
	void testDatabaseStartsEmpty() throws SQLException {
		// Assuming database is cleared before testing
		boolean isEmpty = dbHelper.isDatabaseEmpty();
		assertTrue(isEmpty, "Database should start empty");
	}

	/**
	 * This test does three things.
	 * 
	 * <p>
	 * First, it uses {@link DatabaseHelper#createUser(String, String, UserRole)} to
	 * create a new {@link User} object before storing it in both the local cache
	 * and database.
	 * 
	 * <p>
	 * Secondly, the test checks that {@link DatabaseHelper#doesUserExist(String)}
	 * correctly finds the newly-created User object in the database.
	 * 
	 * <p>
	 * Thirdly, and lastly, the test checks that
	 * {@link DatabaseHelper#getUserRole(String)} fetches the appropriate
	 * {@link UserRole} for the newly-created User. This function is used to
	 * validate user logins.
	 * 
	 * @throws SQLException
	 */
	@Test
	void testRegisterUser() throws SQLException {
		User user = dbHelper.createUser("testUser1", "Password123!", UserRole.STUDENT);
		assertTrue(dbHelper.doesUserExist(user.getUserName()), "User should exist after registration");
		assertEquals(UserRole.STUDENT, dbHelper.getUserRole(user.getUserName()), "User role should be 'student'");
	}

	/**
	 * This test does two things.
	 * 
	 * <p>
	 * First, it uses {@link DatabaseHelper#createUser(String, String, UserRole)} to
	 * create a new {@link User} object before storing it in both the local cache
	 * and database.
	 * 
	 * <p>
	 * Secondly, it attempts to log into the program as the new User object via
	 * {@link DatabaseHelper#login(User)}, which it expects to be successful.
	 * 
	 * @throws SQLException
	 */
	@Test
	void testLoginSuccess() throws SQLException {
		User user = dbHelper.createUser("testUser2", "Password123!", UserRole.STUDENT);
		assertTrue(dbHelper.login(user), "Login should succeed with correct credentials");
	}

	/**
	 * This test does two things.
	 * 
	 * <p>
	 * First, it creates a new {@link User} object directly — which critically does
	 * <i>not</i> push the new User into the database.
	 * 
	 * <p>
	 * Secondly, it attempts to log into the program as the new user. Since
	 * {@link DatabaseHelper#login(User)} references the database to validate login
	 * attempts, this particular login attempt is doomed to fail, as is expected.
	 * 
	 * @throws SQLException
	 */
	@Test
	void testLoginFail() throws SQLException {
		User user = new User("nonExistent", "nopass", null, null, null, UserRole.STUDENT);
		assertFalse(dbHelper.login(user), "Login should fail for non-existent user");
	}

	/**
	 * This test checks that creates a {@link User} in the appropriate manner before
	 * attempting to create an identical User in the same manner. The expected
	 * behavior is that the duplicate User will not be created.
	 * 
	 * @throws SQLException
	 */
	@Test
	void testDuplicateUserRegistration() throws SQLException {
		User user = dbHelper.createUser("copiedUser", "Password123!", UserRole.STUDENT);
		assertFalse(user == null, "User was successfully registered");
		User dupeUser = dbHelper.createUser("copiedUser", "Password123!", UserRole.STUDENT);
		assertTrue(dupeUser == null, "Attempt to register a duplicate user was caught");
	}
}
