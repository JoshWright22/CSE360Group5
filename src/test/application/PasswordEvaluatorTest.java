package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.PasswordEvaluator;

public class PasswordEvaluatorTest {
	String upperCaseError = "Upper case; ";
	String lowerCaseError = "Lower case; ";
	String specialCharacterError = "Special character; ";
	String numericError = "Numeric digits; ";
	String lengthError = "Long enough; ";
	String generalError = "conditions were not satisfied";

	@Test
	void testEmptyPassword() {
		String password = "";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.isEmpty(), "Passwords cannot be empty.");
	}

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
