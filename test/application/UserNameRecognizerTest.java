package application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserNameRecognizerTest {

	String firstCharacterError = "A UserName must start with A-Z or a-z.";
	String minCharacterError = "A UserName must have at least 4 characters.";
	String maxCharacterError = "A UserName must have no more than 16 character.";
	String invalidCharacterError = "A UserName character may only contain the characters A-Z, a-z, 0-9.";
	String specialCharacterError =  "A UserName character after a hyphen, underscore, or period must be A-Z, a-z, 0-9.";
	
	@Test
	void testEmptyUserName() 
	{
		String userName = "";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.isEmpty(), "");
		
		userName = "Abcd";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.isEmpty(), "");
	}
	
	@Test
	void testFirstCharacterIsAlphabetical() 
	{
		String userName = "John";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(firstCharacterError), firstCharacterError);
		
		userName = "9ohn";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(firstCharacterError), firstCharacterError);
		
		userName = "-ohn";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(firstCharacterError), firstCharacterError);
	}
	
	@Test
	void testSpecialCharactersAreNotConsecitve() 
	{
		String userName = "Ti.m";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(specialCharacterError), specialCharacterError);
		
		userName = "T_im";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(specialCharacterError), specialCharacterError);
		
		userName = "Ti._m";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(specialCharacterError), specialCharacterError);
		
		userName = "t.i.-m";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(specialCharacterError), specialCharacterError);
	}
	
	@Test
	void testMinimumLength() 
	{
		String userName = "Lara";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(minCharacterError), minCharacterError);
		
		userName = "Lar";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(minCharacterError), minCharacterError);
		
		userName = "Cd";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(minCharacterError), minCharacterError);	
	}
	
	@Test
	void testMaximumLength() 
	{
		String userName = "Christopher12345";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(maxCharacterError), maxCharacterError);
		
		userName = "Christopher123456";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(maxCharacterError), maxCharacterError);
	}
	
	@Test
	void testInvalidCharacters() 
	{
		String userName = "Alex[12]";
		String error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(invalidCharacterError), invalidCharacterError);
		
		userName = "Alex&*12";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(invalidCharacterError), invalidCharacterError);
		
		userName = "Alex#$";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertTrue(error.contains(invalidCharacterError), invalidCharacterError);
		
		userName = "A-l.e_x";
		error = UserNameRecognizer.checkForValidUserName(userName);
		assertFalse(error.contains(invalidCharacterError), invalidCharacterError);

	}
	
	
}
