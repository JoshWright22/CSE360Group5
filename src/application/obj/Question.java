package application.obj;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An object that represents a user-submitted question.
 */
public class Question extends UserSubmission {

	private String title;
	private String content;
	private List<String> tags;
	private List<Answer> answers;

	public Question(int id, String userName, LocalDateTime creationTime, String title, String content, List<Answer> answers,
			List<String> tags) {
		super(id, userName, creationTime);
		this.title = title;
		this.content = content;
		if (this.answers == null)
			this.answers = new ArrayList<Answer>();
		this.tags = tags;
	}

	/**
	 * Gets the title (or header) of the question.
	 * 
	 * @return String
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the content (or body) of the question.
	 * 
	 * @return String
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Gets an unmodifiable list of all marked answers to the question (may be
	 * empty).
	 * 
	 * @return unmodifiable list of answer objects
	 */
	public List<Answer> getAnswers() {
		return Collections.unmodifiableList(this.answers);
	}

	/**
	 * Sets the title of the question.
	 * 
	 * @param title New title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the content of the question.
	 * 
	 * @param content New content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Adds a particular answer to the question
	 * 
	 * @param answers Answer(s) to add
	 * @return true if the answer(s) were added; false otherwise
	 */
	public boolean addAnswers(Answer... answers) {
		boolean result = false;
		for (Answer a : answers) {
			if (this.answers.contains(a))
				continue;
			this.answers.add(a);
			result = true;
		}
		return result;
	}

	/**
	 * Sets the tags of the question.
	 * 
	 * @param tags New tags
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * Adds tags to the question.
	 * 
	 * @param tags Tag(s) to be added
	 * @return true if tag(s) were added; false otherwise
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
	 * Removes particular tags from the question.
	 * 
	 * @param tags Tag(s) to remove
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
	 * Removes all tags from the question.
	 */
	public void removeAllTags() {
		this.tags = new ArrayList<String>();
	}
}