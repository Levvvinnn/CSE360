package testCases;

import userNameRecognizer.UserNameRecognizer;

/**
 * Test cases for the UserNameRecognizer FSM implementation (v1.02).
 *
 * These are the same 16 cases (TC-U01 - TC-U16) from the Username Recognizer
 * test case table, run against the actual checkForValidUserName() method.
 *
 * NOTE: TC-U01 ("abc") and TC-U08 ("a") were marked Valid in the original
 * table, which was written against the general state-machine rules only.
 * The actual implementation also enforces a 4-32 character length range, so
 * those two are Invalid here (too short) rather than Valid. Every other
 * case's expected result is unchanged.
 */
public class testCases {

	private static int numPassed = 0;
	private static int numFailed = 0;

	/**
	 * Runs a single named test case against the UserNameRecognizer and reports
	 * whether the actual result matched the expected result.
	 *
	 * @param id			The test case ID from the test case table (e.g. "TC-U01")
	 * @param input			The UserName candidate to test
	 * @param expectedValid	true if the input is expected to be a valid UserName
	 */
	private static void performUserNameTestCase(String id, String input, boolean expectedValid) {
		String result = UserNameRecognizer.checkForValidUserName(input);
		boolean actualValid = result.isEmpty();

		System.out.println("\n" + id + ": \"" + input + "\"");
		System.out.println("Expected: " + (expectedValid ? "Valid" : "Invalid"));
		System.out.println("Actual:   " + (actualValid ? "Valid" : "Invalid -> " + result.trim()));

		if (actualValid == expectedValid) {
			numPassed++;
			System.out.println("Result: PASS");
		} else {
			numFailed++;
			System.out.println("Result: *** FAIL ***");
		}
	}
	
	/**
	 * Runs a single named test case against Model.evaluatePassword() and
	 * reports whether the actual result matched the expected result.
	 *
	 * @param id			The test case ID (e.g. "TC-P01")
	 * @param input			The password candidate to test (may be null)
	 * @param expectedValid	true if the input is expected to be a valid password
	 */
	public static void performPasswordTestCase(String id, String input, boolean expectedValid) {
		
		
		
		String result = passwordPopUpWindow.Model.evaluatePassword(input);
		boolean actualValid = result.isEmpty();
 
		System.out.println("\n" + id + ": " + (input == null ? "null" : "\"" + input + "\""));
		System.out.println("Expected: " + (expectedValid ? "Valid" : "Invalid"));
		System.out.println("Actual:   " + (actualValid ? "Valid" : "Invalid -> " + result.trim()));
 
		if (actualValid == expectedValid) {
			numPassed++;
			System.out.println("Result: PASS");
		} else {
			numFailed++;
			System.out.println("Result: *** FAIL ***");
		}

		
	}
	
	// Builds a string of length totalLength starting with 'a' followed by 'b's.
	// Used for exact-length boundary tests without relying on String.repeat().
	private static String buildOfLength(int totalLength) {
		StringBuilder sb = new StringBuilder();
		sb.append('a');
		for (int i = 1; i < totalLength; i++) {
			sb.append('b');
		}
		return sb.toString();
	}

	

	private static void runUserNameTestCases() {
		 
		performUserNameTestCase("TC-U01", "abc",         false);	// Too short (3 chars, min is 4)
		performUserNameTestCase("TC-U02", "abc123",       true);	// Letters followed by digits
		performUserNameTestCase("TC-U03", "a1b2c3",       true);	// Mixed alphanumeric
		performUserNameTestCase("TC-U04", "user-name",    true);	// Single dash between two UNChars
		performUserNameTestCase("TC-U05", "user_name",    true);	// Single underscore between two UNChars
		performUserNameTestCase("TC-U06", "user.name",    true);	// Single period between two UNChars
		performUserNameTestCase("TC-U07", "user&name",    true);	// Single ampersand between two UNChars
		performUserNameTestCase("TC-U08", "a",            false);	// Too short (1 char, min is 4)
		performUserNameTestCase("TC-U09", "1username",    false);	// Must start with a letter, not a digit
		performUserNameTestCase("TC-U10", "-username",    false);	// Must start with a letter, not a separator
		performUserNameTestCase("TC-U11", "user--name",   false);	// Two consecutive separators not allowed
		performUserNameTestCase("TC-U12", "user.-name",   false);	// Two different separators back-to-back
		performUserNameTestCase("TC-U13", "username-",    false);	// Cannot end on a separator
		performUserNameTestCase("TC-U14", "",             false);	// Empty input
		performUserNameTestCase("TC-U15", "user name",    false);	// Space is not a valid character
		performUserNameTestCase("TC-U16", "user@name",    false);	// '@' is not a valid character
	}
 
	private static void runPasswordTestCases() {
 
		performPasswordTestCase("TC-P01", "Passw0rd!",        true);	// All 4 classes, 9 chars
		performPasswordTestCase("TC-P02", "Aa1!Aa1!",         true);	// All 4 classes, boundary min length (8)
		performPasswordTestCase("TC-P03", "Aa1!Aa1",          false);	// All 4 classes but only 7 chars (too short)
		performPasswordTestCase("TC-P04", buildAllClassesOfLength(32), true);	// All 4 classes, boundary max length (32)
		performPasswordTestCase("TC-P05", buildAllClassesOfLength(33), false);	// Exceeds max length (33) - rejected before FSM runs
		performPasswordTestCase("TC-P06", "",                 false);	// Empty password
		performPasswordTestCase("TC-P07", "alllowercase",     false);	// Only lower case letters (missing upper/digit/special)
		performPasswordTestCase("TC-P08", "ALLUPPERCASE",     false);	// Only upper case letters (missing lower/digit/special)
		performPasswordTestCase("TC-P09", "12345678",         false);	// Only digits (missing upper/lower/special)
		performPasswordTestCase("TC-P10", "!@#$%^&*",         false);	// Only special characters (missing upper/lower/digit)
		performPasswordTestCase("TC-P11", "Password!",        false);	// Missing only a numeric digit
		performPasswordTestCase("TC-P12", "Passw0rd !",       false);	// Contains a space (invalid character)
		performPasswordTestCase("TC-P13", "Passw\u00E9rd1!",  false);	// Contains 'é' (invalid character, non-ASCII)
		performPasswordTestCase("TC-P14", null,               false);	// Null input
		performPasswordTestCase("TC-P15", "Aa1;\"Bb2",        true);	// All 4 classes using the expanded special set (';' and '"')
	}
 
	// Builds a password of exactly totalLength characters that contains all four
	// required classes (upper, lower, digit, special), used for the length
	// boundary test cases.
	private static String buildAllClassesOfLength(int totalLength) {
		StringBuilder sb = new StringBuilder("Aa1!");
		for (int i = sb.length(); i < totalLength; i++) {
			sb.append('a');
		}
		return sb.toString();
	}
 
	public static void main(String[] args) {
 
		runUserNameTestCases();
		runPasswordTestCases();
 
		// ---------- Summary ----------
		System.out.println("\n----------------------------------------");
		System.out.println("Total test cases: " + (numPassed + numFailed));
		System.out.println("Passed: " + numPassed);
		System.out.println("Failed: " + numFailed);
	}

}