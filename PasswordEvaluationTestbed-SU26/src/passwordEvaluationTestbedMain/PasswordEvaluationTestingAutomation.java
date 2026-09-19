package passwordEvaluationTestbedMain;

/*******
 * <p> Title: PasswordEvaluationTestingAutomation Class. </p>
 *
 * <p> Description: A Java demonstration for semi-automated tests </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 3.00 2026-09-11 Updated for TP1 password validation testing
 */

public class PasswordEvaluationTestingAutomation {

	static int numPassed = 0;
	static int numFailed = 0;

	public static void main(String[] args) {

		System.out.println(
				"____________________________________________________________");

		System.out.println("\nTesting Automation");

		/*
		 * Positive test:
		 * Contains uppercase, lowercase, digit, special character,
		 * and at least 8 characters.
		 */
		performTestCase(1, "Aa!15678", true);

		/*
		 * Negative test:
		 * Too short.
		 */
		performTestCase(2, "A!", false);

		/*
		 * Negative test:
		 * Missing uppercase character.
		 */
		performTestCase(3, "aa!123456", false);

		/*
		 * Negative test:
		 * Missing lowercase character.
		 */
		performTestCase(4, "AA!123456", false);

		/*
		 * Negative test:
		 * Missing numeric digit.
		 */
		performTestCase(5, "Aa!abcdef", false);

		/*
		 * Negative test:
		 * Missing special character.
		 */
		performTestCase(6, "Aa123456", false);

		/*
		 * Negative test:
		 * Empty password.
		 */
		performTestCase(7, "", false);

		/*
		 * Positive boundary test:
		 * Exactly 64 characters.
		 */
		performTestCase(8, createPassword(64), true);

		/*
		 * Negative boundary test:
		 * 65 characters exceeds the maximum allowed length.
		 */
		performTestCase(9, createPassword(65), false);

		/*
		 * Negative test:
		 * Invalid whitespace character.
		 */
		performTestCase(10, "Aa!123 456", false);

		System.out.println(
				"____________________________________________________________");

		System.out.println();

		System.out.println(
				"Number of tests passed: " + numPassed);

		System.out.println(
				"Number of tests failed: " + numFailed);

		System.out.println();

		if (numFailed == 0) {
			System.out.println("ALL TESTS PASSED.");
		} else {
			System.out.println("SOME TESTS FAILED.");
		}
	}

	/*
	 * Creates a password of exactly the requested length.
	 *
	 * The generated password contains:
	 * - uppercase character
	 * - lowercase character
	 * - numeric digit
	 * - special character
	 *
	 * This allows boundary tests to focus specifically on password length.
	 */
	private static String createPassword(int length) {

		StringBuilder password = new StringBuilder();

		password.append("Aa!1");

		while (password.length() < length) {
			password.append("a1");
		}

		return password.substring(0, length);
	}

	/*
	 * Executes one password test case.
	 */
	private static void performTestCase(
			int testCase,
			String inputText,
			boolean expectedPass) {

		System.out.println(
				"____________________________________________________________");

		System.out.println(
				"\nTest case: " + testCase);

		System.out.println(
				"Input length: " + inputText.length());

		System.out.println(
				"Input: \"" + inputText + "\"");

		System.out.println(
				"____________________________________________________________");

		System.out.println(
				"\nFinite state machine execution trace:");

		/*
		 * Call the SAME password recognizer used by the GUI.
		 */
		String resultText =
				passwordPopUpWindow.Model.evaluatePassword(inputText);

		System.out.println();

		boolean actualPass = resultText.equals("");

		if (actualPass == expectedPass) {

			System.out.println(
					"***Success*** Test case " + testCase + " passed.");

			numPassed++;

		} else {

			System.out.println(
					"***Failure*** Test case " + testCase + " failed.");

			System.out.println(
					"Expected valid: " + expectedPass);

			System.out.println(
					"Actual valid: " + actualPass);

			if (!actualPass) {
				System.out.println(
						"Error message: " + resultText);
			}

			numFailed++;
		}

		displayEvaluation();
	}

	/*
	 * Displays the individual password requirements.
	 */
	private static void displayEvaluation() {

		if (passwordPopUpWindow.Model.foundUpperCase) {
			System.out.println(
					"At least one upper case letter - Satisfied");
		} else {
			System.out.println(
					"At least one upper case letter - Not Satisfied");
		}

		if (passwordPopUpWindow.Model.foundLowerCase) {
			System.out.println(
					"At least one lower case letter - Satisfied");
		} else {
			System.out.println(
					"At least one lower case letter - Not Satisfied");
		}

		if (passwordPopUpWindow.Model.foundNumericDigit) {
			System.out.println(
					"At least one digit - Satisfied");
		} else {
			System.out.println(
					"At least one digit - Not Satisfied");
		}

		if (passwordPopUpWindow.Model.foundSpecialChar) {
			System.out.println(
					"At least one special character - Satisfied");
		} else {
			System.out.println(
					"At least one special character - Not Satisfied");
		}

		if (passwordPopUpWindow.Model.foundLongEnough) {
			System.out.println(
					"At least 8 characters - Satisfied");
		} else {
			System.out.println(
					"At least 8 characters - Not Satisfied");
		}
	}
}
