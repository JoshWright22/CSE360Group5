package application.eval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to evaluate/validate the input of an email address.
 */
public class EmailEvaluator {

	public static final String ERR_INVALID = "Email address is invalid";
	public static final String ERR_TOO_LONG = "Email address is too long";

	public static final int MAX_LENGTH = 64;

	/**
	 * Evaluates a given String input to determine whether it conforms to typical
	 * expectations for an email address, as long as the input is under a certain
	 * character limit.
	 * 
	 * @param input String to evaluate
	 * @return Empty String if validation passes
	 * @return String containing error message if validation fails
	 */
	public static String evaluateEmail(String input) {
		String result = "";
		input = input.toLowerCase();

		// Check for length
		if (input.length() > MAX_LENGTH)
			result = ERR_TOO_LONG;

		// Check for conformity to email address format
		Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		if (!matcher.matches())
			result = ERR_INVALID;

		return result;
	}
}
