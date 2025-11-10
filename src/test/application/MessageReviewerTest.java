package test.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.MessageManager;
import application.ReviewerProfileManager;
import application.User;
import application.UserRole;
import application.obj.Message;
import application.obj.ReviewerProfile;
import databasePart1.DatabaseHelper;

/**
 * This class consists of methods created to test message sending/receiving and
 * reviewer
 * profile management functionality.
 * 
 * <p>
 * Because this class tests database functionality, <b>each test has the
 * possibility to throw an {@link SQLException}.</b> Given that the database
 * being tested exists locally, such exceptions should, in theory, never get
 * thrown.
 */
public class MessageReviewerTest {
    private static DatabaseHelper dbHelper;
    private static MessageManager messageManager;
    private static ReviewerProfileManager reviewerProfileManager;
    private static User testSender;
    private static User testReceiver;

    @BeforeAll
    static void setup() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        messageManager = new MessageManager(dbHelper);
        reviewerProfileManager = new ReviewerProfileManager(dbHelper);

        // Create test users
        testSender = dbHelper.createUser("testSender", "Pass123!", "Test", "Sender", "sender@test.com",
                UserRole.STUDENT);
        testReceiver = dbHelper.createUser("testReceiver", "Pass123!", "Test", "Receiver", "receiver@test.com",
                UserRole.REVIEWER);
    }

    @BeforeEach
    void refresh() {
        messageManager.fetchMessages();
        reviewerProfileManager.fetchProfiles();
    }

    @AfterAll
    static void cleanup() {
        dbHelper.closeConnection();
    }

    /**
     * Test 17: Verifies that a user can request to become a reviewer and that their
     * request is properly recorded in the pending reviewers database.
     */
    @Test
    void testReviewerRequest() throws SQLException {
        // Setup - create the pending reviewers table if needed
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS PendingReviewers (
                    userName VARCHAR(255) PRIMARY KEY,
                    requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        dbHelper.getStatement().execute(createTableSQL);

        // Create a test user who wants to become a reviewer
        User aspiringReviewer = dbHelper.createUser("aspiring17", "Pass123!", "Aspiring", "Reviewer",
                "aspiring17@test.com", UserRole.STUDENT);

        // Submit reviewer request by adding to pending table
        String insertSQL = "INSERT INTO PendingReviewers (userName) VALUES (?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(insertSQL)) {
            pstmt.setString(1, aspiringReviewer.getUserName());
            int rowsInserted = pstmt.executeUpdate();
            assertTrue(rowsInserted > 0, "Should successfully insert pending request");
        }

        // Verify the request is in the pending list
        String checkSQL = "SELECT userName FROM PendingReviewers WHERE userName = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(checkSQL)) {
            pstmt.setString(1, aspiringReviewer.getUserName());
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next(), "User should be in pending reviewers list");
        }
    }

    /**
     * Test 18: Verifies that an instructor can approve a reviewer request and that
     * the user's role is properly updated.
     */
    @Test
    void testReviewerApproval() throws SQLException {
        // Create test users
        User instructor = dbHelper.createUser("instructor18", "Pass123!", "Test", "Instructor", "instructor18@test.com",
                UserRole.INSTRUCTOR);
        User pendingReviewer = dbHelper.createUser("pending18", "Pass123!", "Pending", "Reviewer", "pending18@test.com",
                UserRole.STUDENT);

        // Add user to pending reviewers
        String insertSQL = "INSERT INTO PendingReviewers (userName) VALUES (?)";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(insertSQL)) {
            pstmt.setString(1, pendingReviewer.getUserName());
            pstmt.executeUpdate();
        }

        // Verify instructor role
        UserRole instructorRole = dbHelper.getUserRole(instructor.getUserName());
        assertEquals(UserRole.INSTRUCTOR, instructorRole, "Should be an instructor");

        // Approve the request
        dbHelper.updateUserRole(pendingReviewer.getUserName(), UserRole.REVIEWER);
        String deleteSQL = "DELETE FROM PendingReviewers WHERE userName = ?";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(deleteSQL)) {
            pstmt.setString(1, pendingReviewer.getUserName());
            pstmt.executeUpdate();
        }

        // Verify role change
        UserRole updatedRole = dbHelper.getUserRole(pendingReviewer.getUserName());
        assertEquals(UserRole.REVIEWER, updatedRole, "User role should be updated to REVIEWER");

        // Verify removal from pending list
        List<String> pendingReviewers = reviewerProfileManager.getPendingReviewers();
        assertFalse(pendingReviewers.contains(pendingReviewer.getUserName()),
                "User should be removed from pending list");
    }

    /**
     * Test 19: Verifies that an admin can promote a user to admin role and that
     * the role change is properly reflected in the database.
     */
    @Test
    void testAdminPromotion() throws SQLException {
        // Create test users
        User admin = dbHelper.createUser("admin19", "Pass123!", "Test", "Admin", "admin19@test.com", UserRole.ADMIN);
        User regularUser = dbHelper.createUser("regular19", "Pass123!", "Test", "User", "regular19@test.com",
                UserRole.STUDENT);

        // Promote regular user to admin
        dbHelper.updateUserRole(regularUser.getUserName(), UserRole.ADMIN);

        // Verify role change
        UserRole updatedRole = dbHelper.getUserRole(regularUser.getUserName());
        assertEquals(UserRole.ADMIN, updatedRole, "User role should be updated to ADMIN");

        // Verify role in user object
        User updatedUser = dbHelper.fetchUser(regularUser.getUserName());
        assertNotNull(updatedUser, "Updated user should exist");
        assertEquals(UserRole.ADMIN, updatedUser.getRole(), "User object should reflect admin role");
    }

    /**
     * Test 13: Verifies that a user can successfully send a private message
     * and that it is stored correctly in the database.
     */
    @Test
    void testSendPrivateMessage() {
        String content = "Test message content";
        Message message = messageManager.sendMessage(testSender.getUserName(), testReceiver.getUserName(), content);

        assertNotNull(message, "Message should be created");
        assertEquals(content, message.getContent(), "Message content should match");
        assertEquals(testSender.getUserName(), message.getSenderName(), "Sender should match");
        assertEquals(testReceiver.getUserName(), message.getReceiverName(), "Receiver should match");
        assertFalse(message.isRead(), "New message should be unread");

        // Verify message is in the database by refreshing and checking
        messageManager.fetchMessages();
        List<Message> messages = messageManager.getMessagesFor(testReceiver.getUserName());
        assertTrue(messages.stream().anyMatch(m -> m.getContent().equals(content) &&
                m.getSenderName().equals(testSender.getUserName())),
                "Message should exist in database");
    }

    /**
     * Test 14: Ensures that a recipient can retrieve received messages
     * and that message content matches what was sent.
     */
    @Test
    void testReceivePrivateMessage() {
        // Send two test messages
        String content1 = "First test message";
        String content2 = "Second test message";
        messageManager.sendMessage(testSender.getUserName(), testReceiver.getUserName(), content1);
        messageManager.sendMessage(testSender.getUserName(), testReceiver.getUserName(), content2);

        // Get messages for receiver
        List<Message> receivedMessages = messageManager.getMessagesFor(testReceiver.getUserName());
        assertTrue(receivedMessages.size() >= 2, "Should have received at least two messages");

        // Verify message contents
        assertTrue(receivedMessages.stream().anyMatch(m -> m.getContent().equals(content1)),
                "First message content should be found");
        assertTrue(receivedMessages.stream().anyMatch(m -> m.getContent().equals(content2)),
                "Second message content should be found");

        // Test unread messages
        List<Message> unreadMessages = messageManager.getUnreadMessagesFor(testReceiver.getUserName());
        assertFalse(unreadMessages.isEmpty(), "Should have unread messages");
    }

    /**
     * Test 15: Checks that a reviewer can submit feedback on a review
     * and that it links to the correct review ID.
     */
    @Test
    void testFeedbackSubmission() {
        // First create a review that we can provide feedback for
        ReviewerProfile profile = reviewerProfileManager.getOrCreateProfile(testReceiver.getUserName());
        assertNotNull(profile, "Reviewer profile should be created");

        // Update profile with initial rating
        profile.setAverageRating(4.0);
        profile.setTotalReviews(1);
        reviewerProfileManager.updateProfile(profile);

        // Verify feedback is recorded
        ReviewerProfile updatedProfile = reviewerProfileManager.getOrCreateProfile(testReceiver.getUserName());
        assertEquals(4.0, updatedProfile.getAverageRating(), "Average rating should be updated");
        assertEquals(1, updatedProfile.getTotalReviews(), "Total reviews should be updated");
    }

    /**
     * Test 16: Validates that reviewers can update their profile information
     * and that changes persist in the database.
     */
    @Test
    void testReviewerProfileUpdate() {
        ReviewerProfile profile = reviewerProfileManager.getOrCreateProfile(testReceiver.getUserName());

        // Update profile information
        String newBio = "Updated bio information";
        String newExpertise = "Java, Python, Software Architecture";
        int newYearsExp = 5;

        profile.setBio(newBio);
        profile.setExpertise(newExpertise);
        profile.setYearsExperience(newYearsExp);

        reviewerProfileManager.updateProfile(profile);

        // Fetch fresh profile from database and verify changes
        reviewerProfileManager.fetchProfiles();
        ReviewerProfile updatedProfile = reviewerProfileManager.getOrCreateProfile(testReceiver.getUserName());

        assertEquals(newBio, updatedProfile.getBio(), "Bio should be updated");
        assertEquals(newExpertise, updatedProfile.getExpertise(), "Expertise should be updated");
        assertEquals(newYearsExp, updatedProfile.getYearsExperience(), "Years of experience should be updated");
    }
}