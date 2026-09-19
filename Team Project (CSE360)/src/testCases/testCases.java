package testCases;

import userNameRecognizer.UserNameRecognizer;
import emailAddressTestbed.EmailAddressRecognizer; 

/**
 * Test cases for the UserNameRecognizer FSM implementation (v1.50).
 *
 * These are the same 16 cases (TC-U01 - TC-U16) from the Username Recognizer
 * test case table, run against the actual checkForValidUserName() method.
 *
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
	/**
	 * Runs a single named test case against the EmailAddressRecognizer and reports
	 * whether the actual result matched the expected result.
	 *
	 * @param id			The test case ID from the test case table (e.g. "TC-E01")
	 * @param input			The UserName candidate to test
	 * @param expectedValid	true if the input is expected to be a valid UserName
	 */
	private static void performEmailTestCase(String id, String input, boolean expectedValid) {
		String result = EmailAddressRecognizer.checkEmailAddress(input);
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
		performPasswordTestCase("TC-P14", "Aa1:Bb2,",        true);	// All 4 classes, length exactly 8 (combined boundary + completeness case)
	}
	
	private static void runEmailTestCases() {
		performEmailTestCase("TC-E01", "user@example.com", true);
		performEmailTestCase("TC-E02", "first.last@example.com", true);
		performEmailTestCase("TC-E03", "user@my-site.com", true);
		performEmailTestCase("TC-E04", "user@mail.example.co.uk", true);
		performEmailTestCase("TC-E05", "a@b.co", true);
		performEmailTestCase("TC-E06", "userexample.com", false);
		performEmailTestCase("TC-E07", ".user@example.com", false);
		performEmailTestCase("TC-E08", "user..name@example.com", false);
		performEmailTestCase("TC-E09", "user.@example.com", false);
		performEmailTestCase("TC-E10", "@example.com", false);
		performEmailTestCase("TC-E11", "user@", false);
		performEmailTestCase("TC-E12", "user@.com", false);
		performEmailTestCase("TC-E13", "user@example.com.", false);
		performEmailTestCase("TC-E14", "user@example.com-", false);
		performEmailTestCase("TC-E15", "user@exa--mple.com", false);
		performEmailTestCase("TC-E16", "user@example..com", false);
		performEmailTestCase("TC-E17", "us er@example.com", false);
		performEmailTestCase("TC-E18", "user@exam ple.com", false);
		performEmailTestCase("TC-E19", buildAllClassesOfLength(255) + "@email.com", false);
	}
 
	// Builds a string of exactly totalLength characters that contains all four
	// required password character classes (upper, lower, digit, special), used
	// for the password length boundary test cases (TC-P04, TC-P05) and reused
	// as harmless filler for the oversized email local-part case (TC-E19).

	
	private static String buildAllClassesOfLength(int totalLength) {
		StringBuilder sb = new StringBuilder("Aa1!"); // one of each required class
		for (int i = sb.length(); i < totalLength; i++) {
			sb.append('a');
		}
		return sb.toString();
	}
 
	public static void main(String[] args) {
 
		runUserNameTestCases();
		runPasswordTestCases();
		runEmailTestCases();
 
		// ---------- Summary ----------
		System.out.println("\n----------------------------------------");
		System.out.println("Total test cases: " + (numPassed + numFailed));
		System.out.println("Passed: " + numPassed);
		System.out.println("Failed: " + numFailed);
	}

}