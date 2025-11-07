package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.PasswordEvaluator;

/**
 * This class consists of methods created to test various parts of password validation.
 */
public class PasswordEvaluatorTest {
	String upperCaseError = "Upper case; ";
	String lowerCaseError = "Lower case; ";
	String specialCharacterError = "Special character; ";
	String numericError = "Numeric digits; ";
	String lengthError = "Long Enough; ";
	String generalError = "conditions were not satisfied";

	/**
	 * This test ensures that empty passwords are properly rejected.
	 */
	@Test
	void testEmptyPassword() {
		String password = "";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.isEmpty(), "Passwords cannot be empty.");
	}

	/**
	 * This test checks that passwords without uppercase letters are being properly rejected.
	 */
	@Test
	void testUpperCaseRequirement() {
		String password = "Benjamin";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(upperCaseError), "Password must have an uppercase letter.");

		password = "karl";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(upperCaseError), "Password must have an uppercase letter.");

		password = "mIkU";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(upperCaseError), "Password must have an uppercase letter.");

		password = "albert";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(upperCaseError), "Password must have an uppercase letter.");
	}

	/**
	 * This test checks that passwords without lowercase letters are being properly rejected.
	 */
	@Test
	void testLowerCaseRequirement() {

		String password = "JOSH";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");

		password = "Jedidiah";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");

		password = "Richardson";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");

		password = "TYLER";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");
	}

	/**
	 * This test checks that passwords without special characters are being properly rejected.
	 */
	@Test
	void testSpecialCharacterRequirement() {

		String password = "Jeremy";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(specialCharacterError), "Password must have a special character.");

		password = "Liam!";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(specialCharacterError), "Password must have a special character.");

		password = "[Brittany?";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(specialCharacterError), "Password must have a special character.");

		password = "NAOMI";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(specialCharacterError), "Password must have a special character.");
	}

	/**
	 * This test checks that passwords without numbers are being properly rejected.
	 */
	@Test
	void testNumericDigitRequirement() {
		String password = "-eThAn.";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(numericError), "Password must have a numeric digit.");

		password = "_Stev-en2";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(numericError), "Password must have a numeric digit.");

		password = "[Dylan!]";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(numericError), "Password must have a numeric digit.");

		password = "(Sadie7/|";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(numericError), "Password must have a numeric digit.");
	}

	/**
	 * This test checks that passwords that are too short are being properly rejected.
	 */
	@Test
	void testLengthRequirement() {
		String password = "Val";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lengthError), "Passwords must have at least 8 characters.");

		password = "McDonald";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lengthError), "Passwords must have at least 8 characters.");

		password = "Junior";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lengthError), "Passwords must have at least 8 characters.");

		password = "shyguy123";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lengthError), "Passwords must have at least 8 characters.");
	}
}
