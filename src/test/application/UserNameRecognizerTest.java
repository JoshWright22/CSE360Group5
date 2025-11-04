package test.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.eval.UserNameRecognizer;

public class UserNameRecognizerTest {
	String firstCharacterError = "A UserName must start with A-Z or a-z.";
	String minCharacterError = "A UserName must have at least 4 characters.";
	String maxCharacterError = "A UserName must have no more than 16 character.";
	String invalidCharacterError = "A UserName character may only contain the characters A-Z, a-z, 0-9.";
	String specialCharacterError = "A UserName character after a period, underscore, or dash must be A-Z, a-z, or 0-9.";

	@Test
	void testEmptyUserName() {
		String userName = "";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.isEmpty(), "");

		userName = "Stuvy";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.isEmpty(), "");
	}

	@Test
	void testFirstCharacterIsAlphabetical() {
		String userName = "Justin";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(firstCharacterError), firstCharacterError);

		userName = "8ustin";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(firstCharacterError), firstCharacterError);

		userName = "-7ustin";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(firstCharacterError), firstCharacterError);
	}

	@Test
	void testSpecialCharactersAreNotConsecutive() {
		String userName = "Be.th";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(specialCharacterError), specialCharacterError);

		userName = "Bet_h";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(specialCharacterError), specialCharacterError);

		userName = "Be--th";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(specialCharacterError), specialCharacterError);

		userName = "B.e.-th";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(specialCharacterError), specialCharacterError);
	}

	@Test
	void testMinLength() {
		String userName = "Tamara";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(minCharacterError), minCharacterError);

		userName = "Tam";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(minCharacterError), minCharacterError);

		userName = "Vi";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(minCharacterError), minCharacterError);
	}

	@Test
	void testMaxLength() {
		String userName = "Christopher12345";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(maxCharacterError), maxCharacterError);

		userName = "Christopher456789";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(maxCharacterError), maxCharacterError);
	}

	@Test
	void testInvalidCharacters() {
		String userName = "Dave[]12";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(invalidCharacterError), invalidCharacterError);

		userName = "Fall&|52";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(invalidCharacterError), invalidCharacterError);

		userName = "A13x!5";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(invalidCharacterError), invalidCharacterError);

		userName = "B-a.r.r_y";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(invalidCharacterError), invalidCharacterError);
	}
}
