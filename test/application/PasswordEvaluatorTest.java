package application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordEvaluatorTest {

	String upperCaseError = "Upper case; ";
	String lowerCaseError = "Lower case; ";
	String specialCharacterError = "Special character; ";
	String numericError = "Numeric digits; ";
	String lengthError = "Long Enough; ";
	String generalError = "conditions were not satisfied";
	
	@Test
	void testEmptyPassword() 
	{
		String password = "";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.isEmpty(), "Passwords cannot be empty.");
	}
	
	@Test
	void testUpperCaseRequirement() 
	{
		String password = "John";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(upperCaseError), "Passwords must have an uppercase letter.");
		
		password = "tim";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(upperCaseError), "Passwords must have an uppercase letter.");
		
		password = "aleXanDer";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(upperCaseError), "Passwords must have an uppercase letter.");
		
		password = "thomas";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(upperCaseError), "Passwords must have an uppercase letter.");
	}
	
	@Test
	void testLowerCaseRequirement() 
	{
		String password = "KEVIN";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");
		
		password = "Curtis";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");
		
		password = "jOEl";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");
		
		password = "TAYLOR";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lowerCaseError), "Passwords must have a lowercase letter.");
	}
	
	@Test
	void testSpecialCharacterRequirement() 
	{
		String password = "jeremy";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(specialCharacterError), "Passwords must have a special character.");
		
		password = "Lucas!";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(specialCharacterError), "Passwords must have a special character.");
		
		password = "[Melissa:";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(specialCharacterError), "Passwords must have a special character.");
		
		password = "LARA";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(specialCharacterError), "Passwords must have a special character.");
	}
	
	@Test
	void testNumericDigitRequirement() 
	{
		String password = "-joSef!";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(numericError), "Passwords must have a numeric digit.");
		
		password = "_Tra-vis2";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(numericError), "Passwords must have a numeric digit.");
		
		password = "{April?}";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(numericError), "Passwords must have a numeric digit.");
		
		password = "|Elijah7/|";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(numericError), "Passwords must have a numeric digit.");
	}
	
	@Test
	void testLengthRequirement() 
	{
		String password = "Cal";
		String error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lengthError), "Passwords must have at least 8 characters.");
		
		password = "Mckenzie";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lengthError), "Passwords must have at least 8 characters.");
		
		password = "vin";
		error = PasswordEvaluator.evaluatePassword(password);
		assertTrue(error.contains(lengthError), "Passwords must have at least 8 characters.");
		
		password = "Luke1234";
		error = PasswordEvaluator.evaluatePassword(password);
		assertFalse(error.contains(lengthError), "Passwords must have at least 8 characters.");
	}
	
}
