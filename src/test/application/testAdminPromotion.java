package test.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.StartCSE360;
import application.UserRole;

class testAdminPromotion {
	static private String testUserName1 = "Carl646";
	static private String testUserName2 = "Jade2005";

	@BeforeAll
    static void init() {
		StartCSE360.getDatabaseHelper().createUser(testUserName1, "Password", UserRole.STUDENT);
		StartCSE360.getDatabaseHelper().createUser(testUserName2, "Password", UserRole.STUDENT);
    }
	
	@Test
	void testPromoteAdmin() {
		StartCSE360.getDatabaseHelper().updateUserRole(testUserName1, UserRole.ADMIN);
		assertTrue(StartCSE360.getDatabaseHelper().getUserRole(testUserName1) == UserRole.ADMIN, "User should be an admin.");
	}
	
	@Test
	void testPromoteInstructor() {
		StartCSE360.getDatabaseHelper().updateUserRole(testUserName2, UserRole.INSTRUCTOR);
		assertTrue(StartCSE360.getDatabaseHelper().getUserRole(testUserName2) == UserRole.INSTRUCTOR, "User should be an instructor.");
	}

	@Test
	void testPromoteStaff() {
		StartCSE360.getDatabaseHelper().updateUserRole(testUserName1, UserRole.STAFF);
		assertTrue(StartCSE360.getDatabaseHelper().getUserRole(testUserName1) == UserRole.STAFF, "User should be staff.");
	}
	
	@Test
	void testPromoteReviewer() {
		StartCSE360.getDatabaseHelper().updateUserRole(testUserName1, UserRole.REVIEWER);
		assertTrue(StartCSE360.getDatabaseHelper().getUserRole(testUserName1) == UserRole.REVIEWER, "User should be a reviewer.");
	}
	
	@AfterAll
	static void resetRoles()
	{
		StartCSE360.getDatabaseHelper().updateUserRole(testUserName1, UserRole.STUDENT);
		StartCSE360.getDatabaseHelper().updateUserRole(testUserName1, UserRole.STUDENT);
	}
}
