package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import application.obj.Message;
import databasePart1.DatabaseHelper;

/**
 * Manages private messages between users (students and reviewers). Handles
 * sending, receiving, and retrieving messages with database persistence.
 */
public class MessageManager {
	private final DatabaseHelper database;
	private final Set<Message> messageSet = new HashSet<>();

	public MessageManager(DatabaseHelper database) {
		this.database = database;
	}

	/**
	 * Fetches all messages from the database.
	 */
	public void fetchMessages() {
		String query = "SELECT * FROM Messages";
		try (ResultSet rs = this.database.getStatement().executeQuery(query)) {
			messageSet.clear();

			while (rs.next()) {
				int id = rs.getInt("id");
				String senderName = rs.getString("senderName");
				String receiverName = rs.getString("receiverName");
				String content = rs.getString("content");
				LocalDateTime sentTime = LocalDateTime.parse(rs.getString("sentTime"));
				boolean isRead = rs.getBoolean("isRead");

				Message m = new Message(id, senderName, receiverName, content, sentTime, isRead);
				this.messageSet.add(m);
			}
		} catch (SQLException e) {
			System.err.println("Failed to fetch messages from database.");
			e.printStackTrace();
		}
	}

	/**
	 * Sends a new message and persists it to the database.
	 * 
	 * @param senderName   Name of message sender
	 * @param receiverName Name of message receiver
	 * @param content      Message content
	 * @return Message object
	 */
	public Message sendMessage(String senderName, String receiverName, String content) {
		String query = "INSERT INTO Messages (senderName, receiverName, content, sentTime, isRead) VALUES (?, ?, ?, ?, ?)";
		int id = -1;

		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, senderName);
			stmt.setString(2, receiverName);
			stmt.setString(3, content);
			stmt.setString(4, LocalDateTime.now().toString());
			stmt.setBoolean(5, false);
			stmt.executeUpdate();

			ResultSet results = stmt.getGeneratedKeys();
			if (results.next())
				id = results.getInt(1);
		} catch (SQLException e) {
			System.err.println("Failed to send message.");
			e.printStackTrace();
		}

		Message m = new Message(id, senderName, receiverName, content, LocalDateTime.now(), false);
		this.messageSet.add(m);
		return m;
	}

	/**
	 * Gets all messages for a specific user (both sent and received).
	 * 
	 * @param userName User to fetch messages for
	 * @return List of messages involving this user
	 */
	public List<Message> getMessagesFor(String userName) {
		return messageSet.stream().filter(m -> m.getReceiverName().equals(userName) || m.getSenderName().equals(userName))
				.collect(Collectors.toList());
	}

	/**
	 * Gets all unread messages received by a user.
	 * 
	 * @param userName Recipient user
	 * @return List of unread messages
	 */
	public List<Message> getUnreadMessagesFor(String userName) {
		return messageSet.stream().filter(m -> m.getReceiverName().equals(userName) && !m.isRead())
				.collect(Collectors.toList());
	}

	/**
	 * Gets conversation between two users.
	 * 
	 * @param user1 First user
	 * @param user2 Second user
	 * @return List of messages between the two users
	 */
	public List<Message> getConversation(String user1, String user2) {
		return messageSet.stream()
				.filter(m -> (m.getSenderName().equals(user1) && m.getReceiverName().equals(user2))
						|| (m.getSenderName().equals(user2) && m.getReceiverName().equals(user1)))
				.sorted((a, b) -> a.getSentTime().compareTo(b.getSentTime())).collect(Collectors.toList());
	}

	/**
	 * Marks a message as read in the database.
	 * 
	 * @param messageId ID of message to mark as read
	 */
	public void markMessageAsRead(int messageId) {
		String query = "UPDATE Messages SET isRead = TRUE WHERE id = ?";
		try (PreparedStatement stmt = this.database.getConnection().prepareStatement(query)) {
			stmt.setInt(1, messageId);
			stmt.executeUpdate();

			// Update local cache
			for (Message m : messageSet) {
				if (m.getId() == messageId) {
					m.setRead(true);
					break;
				}
			}
		} catch (SQLException e) {
			System.err.println("Failed to mark message as read.");
			e.printStackTrace();
		}
	}

	/**
	 * Gets an unmodifiable reference to all messages.
	 * 
	 * @return Unmodifiable set of all messages
	 */
	public Set<Message> getMessageSet() {
		return Collections.unmodifiableSet(this.messageSet);
	}
}