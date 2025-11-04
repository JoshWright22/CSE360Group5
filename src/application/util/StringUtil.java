package application.util;

public class StringUtil {
	
	private StringUtil() {} // Prevent accidental construction
	
	/**
	 * Measures the similarity between two Strings using the common Levenshtein distance function.
	 * 
	 * @param first		Initial string
	 * @param second	Second string to compare with
	 * @return			0.00 if no similarities are found; 1.00 if parameters are identical
	 */
	public static double getSimilarity(String first, String second) {
		String longString = first;
		String shortString = second;
		// Ensure that the longer String always has the greater length
		if (longString.length() < second.length()) {
			longString = second;
			shortString = first;
		}
		
		int longLength = longString.length();
		if (longLength == 0) return 1.0; // Strings have identical length
		return (longLength - StringUtil.getLevenshteinDistance(longString, shortString)) / (double) longLength;
	}
	
	/**
	 * Gets the Levenshtein distance between two Strings. It tries to measure the distance between two Strings
	 * by the amount of character changes required to turn one String into another.
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int getLevenshteinDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		int[] costs = new int[s2.length() + 1];
		
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					costs[j] = j;
				} else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1)) newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0) costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
}

