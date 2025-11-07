package application.obj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An object that represents user-submitted answers.
 */
public class Answer extends UserSubmission {

	private String content;
	private List<String> tags;

	public Answer(int id, String user, LocalDateTime created, String content, List<String> tags) {
		super(id, user, created);
		this.content = content;
		this.tags = tags;
	}

	/**
	 * Gets the content of the answer.
	 * 
	 * @return String - Content of the answer
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Gets the tags of the answer, for organizational and search functionality.
	 * 
	 * @return List<String> - Unmodifiable list of tags
	 */
	public List<String> getTags() {
		return Collections.unmodifiableList(this.tags);
	}

	/**
	 * Sets the content of the answer.
	 * 
	 * @param content New content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Sets the tags of the answer.
	 * 
	 * @param tags New set of tags
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * Adds new tags to the answer.
	 * 
	 * @param tags Tags to add
	 * @return true if tags were added; false otherwise
	 */
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

	/**
	 * Removes specific tags from the answer.
	 * 
	 * @param tags Tags to remove
	 * @return true if tags were found and removed; false otherwise
	 */
	public boolean removeTags(String... tags) {
		boolean result = false;
		for (String t : tags) {
			result = this.tags.remove(t);
		}
		return result;
	}

	/**
	 * Clears all tags from the answer.
	 */
	public void removeAllTags() {
		this.tags = new ArrayList<String>();
	}
}
