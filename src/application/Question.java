package application;

import java.time.LocalDateTime;

public class Question {
    private int id;
    private String studentId;
    private String title;
    private String body;
    private LocalDateTime created;

    public Question(int id, String studentId, String title, String body, LocalDateTime created) {
        this.id = id;
        this.studentId = studentId;
        this.title = title;
        this.body = body;
        this.created = created;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public LocalDateTime getCreated() { return created; }

    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " by " + studentId;
    }
}