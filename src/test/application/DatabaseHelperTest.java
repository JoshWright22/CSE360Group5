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
 * 
 */
public class DatabaseHelperTest {
	private static DatabaseHelper dbHelper;

	@BeforeAll
	static void setup() throws SQLException {
		dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
	}

	@AfterAll
	static void exit() {
		dbHelper.closeConnection();
	}

	@Test
	void testDatabaseStartsEmpty() throws SQLException {
		// Assuming database is cleared before testing
		boolean isEmpty = dbHelper.isDatabaseEmpty();
		assertTrue(isEmpty, "Database should start empty");
	}

	@Test
	void testRegisterUser() throws SQLException {
		User user = dbHelper.createUser("testUser1", "Password123!", UserRole.STUDENT);
		assertTrue(dbHelper.doesUserExist(user.getUserName()), "User should exist after registration");
		assertEquals(UserRole.STUDENT, dbHelper.getUserRole(user.getUserName()), "User role should be 'student'");
	}

	@Test
	void testLoginSuccess() throws SQLException {
		User user = dbHelper.createUser("testUser2", "Password123!", UserRole.STUDENT);
		assertTrue(dbHelper.login(user), "Login should succeed with correct credentials");
	}

	@Test
	void testLoginFail() throws SQLException {
		User user = new User("nonExistent", "nopass", null, null, null, UserRole.STUDENT);
		assertFalse(dbHelper.login(user), "Login should fail for non-existent user");
	}

	@Test
	void testDuplicateUserRegistration() throws SQLException {
		User user = dbHelper.createUser("copiedUser", "Password123!", UserRole.STUDENT);
		
		assertFalse(user == null, "User was successfully registered");
		
		User dupeUser = dbHelper.createUser("copiedUser", "Password123!", UserRole.STUDENT);
		
		assertTrue(dupeUser == null, "Attempt to register a duplicate user was caught");
	}
}
