package test.application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.StartCSE360;
import application.UserRole;

class testInstructorApproval {
	static private String testUserName1 = "Carl646";
	static private String testUserName2 = "Jade2005";

	@BeforeAll
    static void init() {
		StartCSE360.getDatabaseHelper().createUser(testUserName1, "Password", UserRole.STUDENT);
		StartCSE360.getDatabaseHelper().createUser(testUserName2, "Password", UserRole.STUDENT);
    }
	
	@Test
	void testApproval() {
		StartCSE360.getDatabaseHelper().insertIntoPendingReviewers(testUserName1);
		assertTrue(isUserInPendingReviewers(testUserName1), "User should exist in Pending Reviewers.");
		
		StartCSE360.getDatabaseHelper().approvePendingReviewer(testUserName1);
		assertFalse(isUserInPendingReviewers(testUserName1), "User should not exist in Pending Reviewers.");
		
		UserRole role = StartCSE360.getDatabaseHelper().getUserRole(testUserName1);
		assertTrue(role == UserRole.REVIEWER, "User should be a Reviewer.");
	}
	
	@Test
	void testRejection() {
		StartCSE360.getDatabaseHelper().insertIntoPendingReviewers(testUserName2);
		assertTrue(isUserInPendingReviewers(testUserName2), "User should exist in Pending Reviewers.");
		
		StartCSE360.getDatabaseHelper().removeFromPendingReviewers(testUserName2);
		assertFalse(isUserInPendingReviewers(testUserName2), "User should not exist in Pending Reviewers.");
		
		UserRole role = StartCSE360.getDatabaseHelper().getUserRole(testUserName2);
		assertFalse(role == UserRole.REVIEWER, "User should not be a Reviewer.");
	}

	private boolean isUserInPendingReviewers(String userName) {
		Connection connection = StartCSE360.getDatabaseHelper().getConnection();
	    if (connection == null) {
	        System.err.println("Connection not established. Cannot check pending reviewers.");
	        return false;
	    }

	    String sql = "SELECT 1 FROM PendingReviewers WHERE userName = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setString(1, userName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            return rs.next(); // true if at least one match
	        }
	    } catch (SQLException e) {
	        System.err.println("Error checking user in PendingReviewers.");
	        e.printStackTrace();
	        return false;
	    }
	}
}
