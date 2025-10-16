package test.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import application.obj.Question;
import application.obj.Answer;

class QuestionTest {
	
	@Test
	void test() 
	{
		int id = 1;
		String title = "Java Programming";
		String body = "Placeholder";
		String author = "Kyle";
		
		Question question = new Question(id, author, LocalDateTime.now(), title, body, new ArrayList<Answer>(), new ArrayList<String>());
		assertEquals(question.getId(), id);
		
		assertEquals(question.getTitle(), title);
		
		question.setTitle("New Title");
		assertNotEquals(question.getTitle(), title);
		
		assertEquals(question.getContent(), body);
		
		question.setContent("New body.");
		assertNotEquals(question.getContent(), body);
		
		assertEquals(question.getUserName(), author);
		
		//create question with valid title, body, and author
		assertDoesNotThrow(() -> {
			//Question question2 = new Question(2, "Valid title", "Valid body", "Valid author");
		});
		//create question with invalid title
		assertThrows(IllegalArgumentException.class, () -> {
			//Question question2 = new Question(2, "", "Valid body", "Valid author");
		});
		//create question with invalid body
		assertThrows(IllegalArgumentException.class, () -> {
			//Question question2 = new Question(2, "Valid title", "", "Valid author");
		});
		//create question with invalid author
		assertThrows(IllegalArgumentException.class, () -> {
			//Question question2 = new Question(2, "Valid title", "Valid body", "");
		});
	}

}
