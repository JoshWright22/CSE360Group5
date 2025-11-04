package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.EmailEvaluator;

public class EmailEvaluatorTest {

	@Test
	void tryEmptyEmail() {
		String result = EmailEvaluator.evaluateEmail("");
		assertFalse(result.isEmpty(), "A blank email is not valid");
	}

	@Test
	void tryMissingDomain() {
		String r = EmailEvaluator.evaluateEmail("adamsmith@gmail");
		assertFalse(r.isEmpty(), "Email is missing a domain name");
	}

	@Test
	void tryNotAnEmail() {
		String r = EmailEvaluator.evaluateEmail("google.com");
		assertFalse(r.isEmpty(), "Input is not consistent with typical email formatting (no @ symbol found)");
	}

	@Test
	void tryInvalidCharacter() {
		String r = EmailEvaluator.evaluateEmail("nice code@aol.com");
		assertFalse(r.isEmpty(), "Email contains an invalid character (whitespace)");
	}

	@Test
	void tryValidEmail() {
		String r = EmailEvaluator.evaluateEmail("adamsmith@gmail.com");
		assertTrue(r.isEmpty(), "Email is valid");
	}

	@Test
	void tryEmailWithSubdomain() {
		String r = EmailEvaluator.evaluateEmail("admin@mail.somewebsite.com");
		assertTrue(r.isEmpty(), "Email is valid");
	}
}
