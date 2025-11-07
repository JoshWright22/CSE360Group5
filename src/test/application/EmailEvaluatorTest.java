package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.EmailEvaluator;

/**
 * This class consists of methods created to test various parts of database
 * functionality. It is automated via JUnit.
 */
public class EmailEvaluatorTest {

	/**
	 * This test checks that an empty String is properly rejected.
	 */
	@Test
	void tryEmptyEmail() {
		String result = EmailEvaluator.evaluateEmail("");
		assertFalse(result.isEmpty(), "A blank email is not valid");
	}

	/**
	 * This test checks that an email missing a top-level domain (such as .com) is
	 * properly rejected.
	 */
	@Test
	void tryMissingDomain() {
		String r = EmailEvaluator.evaluateEmail("adamsmith@gmail");
		assertFalse(r.isEmpty(), "Email is missing a domain name");
	}

	/**
	 * This test checks that an email has the '@' symbol, rejecting emails that
	 * don't.
	 */
	@Test
	void tryNotAnEmail() {
		String r = EmailEvaluator.evaluateEmail("google.com");
		assertFalse(r.isEmpty(), "Input is not consistent with typical email formatting (no @ symbol found)");
	}

	/**
	 * This test checks that an email is rejected if it contains whitespace
	 * characters.
	 */
	@Test
	void tryInvalidCharacter() {
		String r = EmailEvaluator.evaluateEmail("nice code@aol.com");
		assertFalse(r.isEmpty(), "Email contains an invalid character (whitespace)");
	}

	/**
	 * This test checks that a valid email is properly accepted.
	 */
	@Test
	void tryValidEmail() {
		String r = EmailEvaluator.evaluateEmail("adamsmith@gmail.com");
		assertTrue(r.isEmpty(), "Email is valid");
	}

	/**
	 * This test checks that a valid email that contains sub-domains is properly
	 * accepted.
	 */
	@Test
	void tryEmailWithSubdomain() {
		String r = EmailEvaluator.evaluateEmail("admin@mail.somewebsite.com");
		assertTrue(r.isEmpty(), "Email is valid");
	}
}
