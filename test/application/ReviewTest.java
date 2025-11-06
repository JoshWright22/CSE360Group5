package application;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import application.obj.Answer;
import application.obj.Question;
import application.obj.Review;

class ReviewTest {

	private Question testQuestion;
	private Answer testAnswer;
	private LocalDateTime testDate;

	@BeforeEach
	void setUp() {
		testDate = LocalDateTime.now();
		testQuestion = new Question(1, "testUser", testDate, "Test Question", "Test Content", null, null);
		testAnswer = new Answer(1, "testUser", testDate, "Test Answer Content", null);
	}

	@Test
	void testValidQuestionReview() {
		Review review = new Review(1, "reviewer1", testDate, "Great question!", 5, testQuestion);
		assertEquals("Great question!", review.getContent());
		assertEquals(5, review.getRating());
		assertTrue(review.isQuestionReview());
		assertFalse(review.isAnswerReview());
	}

	@Test
	void testValidAnswerReview() {
		Review review = new Review(1, "reviewer1", testDate, "Helpful answer", 4, testAnswer);
		assertEquals("Helpful answer", review.getContent());
		assertEquals(4, review.getRating());
		assertTrue(review.isAnswerReview());
		assertFalse(review.isQuestionReview());
	}

	@Test
	void testInvalidRatingTooLow() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Review(1, "reviewer1", testDate, "Bad rating", 0, testQuestion);
		});
	}

	@Test
	void testInvalidRatingTooHigh() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Review(1, "reviewer1", testDate, "Bad rating", 6, testQuestion);
		});
	}

	@Test
	void testEmptyContent() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Review(1, "reviewer1", testDate, "", 3, testQuestion);
		});
	}

	@Test
	void testNullContent() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Review(1, "reviewer1", testDate, null, 3, testQuestion);
		});
	}

	@Test
	void testSetContent() {
		Review review = new Review(1, "reviewer1", testDate, "Original content", 3, testQuestion);
		review.setContent("Updated content");
		assertEquals("Updated content", review.getContent());
	}

	@Test
	void testSetInvalidContent() {
		Review review = new Review(1, "reviewer1", testDate, "Original content", 3, testQuestion);
		assertThrows(IllegalArgumentException.class, () -> {
			review.setContent("");
		});
	}

	@Test
	void testSetRating() {
		Review review = new Review(1, "reviewer1", testDate, "Content", 3, testQuestion);
		review.setRating(5);
		assertEquals(5, review.getRating());
	}

	@Test
	void testSetInvalidRating() {
		Review review = new Review(1, "reviewer1", testDate, "Content", 3, testQuestion);
		assertThrows(IllegalArgumentException.class, () -> {
			review.setRating(7);
		});
	}

	@Test
	void testGetReviewedQuestion() {
		Review review = new Review(1, "reviewer1", testDate, "Content", 3, testQuestion);
		assertEquals(testQuestion, review.getReviewedQuestion());
		assertNull(review.getReviewedAnswer());
	}

	@Test
	void testGetReviewedAnswer() {
		Review review = new Review(1, "reviewer1", testDate, "Content", 3, testAnswer);
		assertEquals(testAnswer, review.getReviewedAnswer());
		assertNull(review.getReviewedQuestion());
	}
}
