package application.obj;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String senderName;
    private String receiverName;
    private String content;
    private LocalDateTime sentTime;
    private boolean isRead;

    public Message(int id, String senderName, String receiverName, String content, LocalDateTime sentTime,
            boolean isRead) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.content = content;
        this.sentTime = sentTime;
        this.isRead = isRead;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public boolean isRead() {
        return isRead;
    }

    // setter for read status
    public void setRead(boolean read) {
        this.isRead = read;
    }
}
