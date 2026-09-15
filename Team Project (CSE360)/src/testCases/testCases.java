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
	private static void performTestCaseUsername(String id, String input, boolean expectedValid) {
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
		
		
		
		
		
	}

	public static void main(String[] args) {

		performTestCaseUsername("TC-U01", "abc",         false);	// Too short (3 chars, min is 4)
		performTestCaseUsername("TC-U02", "abc123",       true);	// Letters followed by digits
		performTestCaseUsername("TC-U03", "a1b2c3",       true);	// Mixed alphanumeric
		performTestCaseUsername("TC-U04", "user-name",    true);	// Single dash between two UNChars
		performTestCaseUsername("TC-U05", "user_name",    true);	// Single underscore between two UNChars
		performTestCaseUsername("TC-U06", "user.name",    true);	// Single period between two UNChars
		performTestCaseUsername("TC-U07", "user&name",    true);	// Single ampersand between two UNChars
		performTestCaseUsername("TC-U08", "a",            false);	// Too short (1 char, min is 4)
		performTestCaseUsername("TC-U09", "1username",    false);	// Must start with a letter, not a digit
		performTestCaseUsername("TC-U10", "-username",    false);	// Must start with a letter, not a separator
		performTestCaseUsername("TC-U11", "user--name",   false);	// Two consecutive separators not allowed
		performTestCaseUsername("TC-U12", "user.-name",   false);	// Two different separators back-to-back
		performTestCaseUsername("TC-U13", "username-",    false);	// Cannot end on a separator
		performTestCaseUsername("TC-U14", "",             false);	// Empty input
		performTestCaseUsername("TC-U15", "user name",    false);	// Space is not a valid character
		performTestCaseUsername("TC-U16", "user@name",    false);	// '@' is not a valid character

		// ---------- Summary ----------
		System.out.println("\n----------------------------------------");
		System.out.println("Total test cases: " + (numPassed + numFailed));
		System.out.println("Passed: " + numPassed);
		System.out.println("Failed: " + numFailed);
	}
}