package application.obj;

import java.time.LocalDateTime;

/**
 * An object that represents private messages.
 */
public class Message {
	
    private int id;
    private String senderName;
    private String receiverName;
    private String content;
    private LocalDateTime sentTime;
    private boolean isRead;

    /**
     * Constructs a new message.
     * 
     * @param id				Message ID
     * @param senderName		Sender username
     * @param receiverName		Recipient username
     * @param content			Message contents
     * @param sentTime			{@link LocalDateTime} object representing when the message was sent
     * @param isRead			Whether the recipient has opened the message
     */
    public Message(int id, String senderName, String receiverName, String content, LocalDateTime sentTime,
            boolean isRead) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.content = content;
        this.sentTime = sentTime;
        this.isRead = isRead;
    }

    /**
     * Fetches the ID of the message.
     * 
     * @return int - Message ID
     */
    public int getId() {
        return id;
    }

    /**
     * Fetches the username of the message sender.
     * 
     * @return String - Username of message sender
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Fetches the username of the message recipient.
     * 
     * @return String - Username of message recipient
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * Fetches the content of the message.
     * 
     * @return String - Message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Fetches the creation time of the message.
     * 
     * @return {@link LocalDateTime} - Time at which the message was sent
      */
    public LocalDateTime getSentTime() {
        return sentTime;
    }

    /**
     * Fetches whether the message has been opened by the recipient.
     * 
     * @return true if the message has been opened; false otherwise
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets whether the message has been opened by the recipient.
     * 
     * @param read
     */
    public void setRead(boolean read) {
        this.isRead = read;
    }
}
