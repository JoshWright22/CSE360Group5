package application.obj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Answer extends UserSubmission {

	private String content;
	private List<String> tags;

	public Answer(int id, String user, LocalDateTime created, String content, List<String> tags) {
		super(id, user, created);
		this.content = content;
		this.tags = tags;
	}

	public String getContent() {
		return this.content;
	}

	public List<String> getTags() {
		return Collections.unmodifiableList(this.tags);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setContent(String content) {
		this.content = content;
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
