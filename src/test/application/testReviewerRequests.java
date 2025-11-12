package test.application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import application.StartCSE360;

class testReviewerRequests {
	private String testUserName1 = "Tim123";
	private String testUserName2 = "Kate78";

	@Test
	void testInserteviewerRequest() {
		StartCSE360.getDatabaseHelper().insertIntoPendingReviewers(testUserName1);
		assertTrue(isUserInPendingReviewers(testUserName1), "User should exist in Pending Reviewers.");
		StartCSE360.getDatabaseHelper().insertIntoPendingReviewers(testUserName2);
		assertTrue(isUserInPendingReviewers(testUserName2), "User should exist in Pending Reviewers.");
	}

	@Test
	void testRemoveReviewerRequest()
	{
		StartCSE360.getDatabaseHelper().removeFromPendingReviewers(testUserName1);
		assertFalse(isUserInPendingReviewers(testUserName1), "User should not exist in Pending Reviewers.");
		assertTrue(isUserInPendingReviewers(testUserName2), "User should exist in Pending Reviewers.");
		
		StartCSE360.getDatabaseHelper().removeFromPendingReviewers(testUserName2);
		assertFalse(isUserInPendingReviewers(testUserName2), "User should not exist in Pending Reviewers.");
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
