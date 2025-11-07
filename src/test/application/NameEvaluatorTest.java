package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.NameEvaluator;

/**
 * This class consists of methods created to test various parts of name validation.
 */
public class NameEvaluatorTest {

	/**
	 * This test checks that empty names are properly rejected.
	 */
	@Test
	void tryEmptyName() {
		String r = NameEvaluator.evaluateName("");
		assertFalse(r.isEmpty(), "Blank names are not valid");
	}
	
	/**
	 * This test checks that excessively long names are properly rejected.
	 */
	@Test
	void tryTooLongName() {
		String r = NameEvaluator.evaluateName("pneumonoultramicroscopicsilicovolcanoconiosis");
		assertFalse(r.isEmpty(), "Name exceeds max length");
	}
	
	/**
	 * This test checks that a name with an invalid character is properly rejected.
	 */
	@Test
	void tryNameWithSpecialChar() {
		String r = NameEvaluator.evaluateName("Jo$hua");
		assertFalse(r.isEmpty(), "Name contains an invalid character");
	}
	
	/**
	 * This test checks that a name with a number is properly rejected.
	 */
	@Test
	void tryNameWithNumber() {
		String r = NameEvaluator.evaluateName("5teven");
		assertFalse(r.isEmpty(), "Name contains a number");
	}
	
	/**
	 * This test checks that a valid name is properly accepted.
	 */
	@Test
	void tryValidName() {
		String r = NameEvaluator.evaluateName("Kyle");
		assertTrue(r.isEmpty(), "Name is valid");
	}
}
