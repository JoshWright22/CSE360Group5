package application.obj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question extends UserSubmission {

	private String title;
	private String content;
	private List<String> tags;
	private List<Answer> answers;

	public Question(int id, String userName, LocalDateTime creationTime, String title, String content,
			List<Answer> answers, List<String> tags) {
		super(id, userName, creationTime);
		this.title = title;
		this.content = content;
		if (this.answers == null) this.answers = new ArrayList<Answer>();
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
	
	public List<Answer> getAnswers() {
		return Collections.unmodifiableList(this.answers);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public boolean addAnswers(Answer... answers) {
		boolean result = false;
		for (Answer a : answers) {
			if (this.answers.contains(a)) continue;
			this.answers.add(a);
			result = true;
		}
		return result;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean addTags(String... tags) {
		boolean result = false;
		for (String t : tags) {
			// Skip over duplicate tags
			if (this.tags.contains(t))
				continue;
			this.tags.add(t);
			result = true;
		}
		return result;
	}

	public boolean removeTags(String... tags) {
		boolean result = false;
		for (String t : tags) {
			result = this.tags.remove(t);
		}
		return result;
	}

	public void removeAllTags() {
		this.tags = new ArrayList<String>();
	}
}