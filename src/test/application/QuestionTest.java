package test.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import application.obj.Answer;
import application.obj.Question;

/**
 * This class consists of methods created to test various parts of {@link Question} functionality.
 */
class QuestionTest {

	@Test
	void test() {
		int id = 1;
		String title = "Java Programming";
		String body = "Placeholder";
		String author = "Kyle";

		Question question = new Question(id, author, LocalDateTime.now(), title, body, new ArrayList<Answer>(),
				new ArrayList<String>());
		assertEquals(question.getId(), id);

		assertEquals(question.getTitle(), title);

		question.setTitle("New Title");
		assertNotEquals(question.getTitle(), title);

		assertEquals(question.getContent(), body);

		question.setContent("New body.");
		assertNotEquals(question.getContent(), body);

		assertEquals(question.getUserName(), author);

		// create question with valid title, body, and author
		assertDoesNotThrow(() -> {
			// Question question2 = new Question(2, "Valid title", "Valid body", "Valid
			// author");
		});
		// create question with invalid title
		assertThrows(IllegalArgumentException.class, () -> {
			// Question question2 = new Question(2, "", "Valid body", "Valid author");
		});
		// create question with invalid body
		assertThrows(IllegalArgumentException.class, () -> {
			// Question question2 = new Question(2, "Valid title", "", "Valid author");
		});
		// create question with invalid author
		assertThrows(IllegalArgumentException.class, () -> {
			// Question question2 = new Question(2, "Valid title", "Valid body", "");
		});
	}

}
