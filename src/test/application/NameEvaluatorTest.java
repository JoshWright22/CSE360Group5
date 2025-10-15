package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.NameEvaluator;

public class NameEvaluatorTest {

	@Test
	void tryEmptyName() {
		String r = NameEvaluator.evaluateName("");
		assertFalse(r.isEmpty(), "Blank names are not valid");
	}
	
	@Test
	void tryTooLongName() {
		String r = NameEvaluator.evaluateName("pneumonoultramicroscopicsilicovolcanoconiosis");
		assertFalse(r.isEmpty(), "Name exceeds max length");
	}
	
	@Test
	void tryNameWithSpecialChar() {
		String r = NameEvaluator.evaluateName("Jo$hua");
		assertFalse(r.isEmpty(), "Name contains an invalid character");
	}
	
	@Test
	void tryNameWithNumber() {
		String r = NameEvaluator.evaluateName("5teven");
		assertFalse(r.isEmpty(), "Name contains a number");
	}
	
	@Test
	void tryValidName() {
		String r = NameEvaluator.evaluateName("Kyle");
		assertTrue(r.isEmpty(), "Name is valid");
	}
}
