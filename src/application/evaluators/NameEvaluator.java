package application.evaluators;

/**
 * Used to evaluate and validate the input of a person's first or last name.
 */
public class NameEvaluator {

	public static String ERR_SPACE = "Name cannot contain a space";
	public static String ERR_NUM = "Name cannot contain a number";
	public static String ERR_CHAR = "Name cannot contain a symbol or special character";
	public static String ERR_TOO_LONG = "Name is too long";

	public static int MAX_LENGTH = 32;

	/**
	 * Evaluates a given String to check if it complies with contemporary
	 * conventions for first and last names in American English.
	 * 
	 * @param input String to input
	 * @return Empty String if the input String is a valid name
	 * @return String containing error message if input is invalid
	 */
	public static String evaluateName(String input) {

		String result = "";
		input = input.toLowerCase();

		// Check for length
		if (input.length() > MAX_LENGTH)
			result = ERR_TOO_LONG;
		
		// Check for the other conditions of a valid name
		if (input.matches(" "))
			result = ERR_SPACE;
		if (input.matches("[0-9]"))
			result = ERR_NUM;
		if (!input.matches("[a-zA-Z]+"))
			result = ERR_CHAR;
		
		return result;
	}
}
