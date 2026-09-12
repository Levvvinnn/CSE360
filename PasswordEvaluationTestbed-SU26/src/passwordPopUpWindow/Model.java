package passwordPopUpWindow;

import javafx.scene.paint.Color;

/*******
 * <p> Title: Model Class - establishes the required GUI data and the computations.
 * </p>
 *
 * <p> Description: This Model class evaluates a password using the requirements
 * specified by the password recognizer. It also performs input-length validation
 * before processing the password.
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 3.00 2026-09-11 Updated for TP1 password length validation
 */

public class Model {

	/*
	 * Password length limits.
	 *
	 * The password must contain at least 8 characters and no more than 64
	 * characters. The maximum length is checked before the finite-state
	 * password evaluation begins.
	 */
	public static final int MIN_PASSWORD_LENGTH = 8;
	public static final int MAX_PASSWORD_LENGTH = 32;

	/*******
	 * <p> Title: updatePassword - Protected Method </p>
	 *
	 * <p> Description: This method is called whenever the user changes the
	 * password. It evaluates the current password and updates the GUI.
	 */
	protected static void updatePassword() {

		View.resetAssessments();

		String password = View.text_Password.getText();

		if (password.isEmpty()) {

			View.errPasswordPart1.setText("");
			View.errPasswordPart2.setText("");
			View.errPasswordPart3.setText("");
			View.noInputFound.setText("No input text found!");
			View.validPassword.setText("");

			View.button_Finish.setDisable(true);

		} else {

			String errMessage = evaluatePassword(password);

			updateFlags();

			if (!errMessage.equals("")) {

				System.out.println(errMessage);

				View.noInputFound.setText("");

				/*
				 * Prevent substring errors when the error index is outside
				 * the normal character-processing range.
				 */
				int errorIndex = passwordIndexofError;

				if (errorIndex < 0) {
					errorIndex = 0;
				}

				if (errorIndex > password.length()) {
					errorIndex = password.length();
				}

				View.errPasswordPart1.setText(
						password.substring(0, errorIndex));

				View.errPasswordPart2.setText("\u21EB");

				View.errPasswordPart3.setText(
						"The red arrow points at the character causing the error!");

				View.validPassword.setTextFill(Color.RED);
				View.validPassword.setText(
						"Failure! The password is not valid.");

				View.button_Finish.setDisable(true);

			} else {

				System.out.println(
						"Success! The password satisfies the requirements.");

				View.errPasswordPart1.setText("");
				View.errPasswordPart2.setText("");
				View.errPasswordPart3.setText("");

				View.validPassword.setTextFill(Color.GREEN);
				View.validPassword.setText(
						"Success! The password satisfies the requirements.");

				View.button_Finish.setDisable(false);
			}
		}
	}

	/*
	 * Attributes used by the password recognizer to communicate the
	 * evaluation results.
	 */
	public static String passwordErrorMessage = "";
	public static String passwordInput = "";
	public static int passwordIndexofError = -1;

	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;

	private static String inputLine = "";
	private static char currentChar;
	private static int currentCharNdx;
	private static boolean running;

	/*
	 * Displays the current state of password evaluation.
	 */
	private static void displayInputState() {

		System.out.println(inputLine);

		if (currentCharNdx <= inputLine.length()) {
			System.out.println(
					inputLine.substring(0, currentCharNdx) + "?");
		}

		System.out.println(
				"The password size: " + inputLine.length()
				+ "  |  The currentCharNdx: " + currentCharNdx
				+ "  |  The currentChar: \"" + currentChar + "\"");
	}

	/*
	 * Updates the GUI indicators for each password requirement.
	 */
	private static void updateFlags() {

		if (foundUpperCase) {
			View.label_UpperCase.setText(
					"At least one upper case letter - Satisfied");
			View.label_UpperCase.setTextFill(Color.GREEN);
		}

		if (foundLowerCase) {
			View.label_LowerCase.setText(
					"At least one lower case letter - Satisfied");
			View.label_LowerCase.setTextFill(Color.GREEN);
		}

		if (foundNumericDigit) {
			View.label_NumericDigit.setText(
					"At least one numeric digit - Satisfied");
			View.label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (foundSpecialChar) {
			View.label_SpecialChar.setText(
					"At least one special character - Satisfied");
			View.label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (foundLongEnough) {
			View.label_LongEnough.setText(
					"At least 8 characters - Satisfied");
			View.label_LongEnough.setTextFill(Color.GREEN);
		}
	}

	/**********
	 * <p> Title: evaluatePassword - Public Method </p>
	 *
	 * <p> Description: Evaluates the supplied password. Input length is checked
	 * before the finite-state-machine processing begins.
	 *
	 * @param input password to evaluate
	 * @return empty string when valid, otherwise an error message
	 */
	public static String evaluatePassword(String input) {

		/*
		 * Reset all evaluation state before processing the new password.
		 */
		passwordErrorMessage = "";
		passwordIndexofError = 0;

		foundUpperCase = false;
		foundLowerCase = false;
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundLongEnough = false;

		/*
		 * Null input is rejected before any other processing.
		 */
		if (input == null) {
			passwordInput = "";
			inputLine = "";

			return "*** Error *** The password cannot be null!";
		}

		/*
		 * Empty password validation.
		 */
		if (input.length() == 0) {
			passwordInput = input;
			inputLine = input;

			return "*** Error *** The password is empty!";
		}

		/*
		 * Maximum-length validation MUST happen before the FSM processes
		 * the password. This protects the application from unnecessarily
		 * processing excessively large textual input.
		 */
		if (input.length() > MAX_PASSWORD_LENGTH) {

			passwordInput = input;
			inputLine = input;

			passwordIndexofError = MAX_PASSWORD_LENGTH;

			return "*** Error *** The password exceeds the maximum length of "
					+ MAX_PASSWORD_LENGTH + " characters!";
		}

		/*
		 * Store the valid-size input.
		 */
		passwordInput = input;
		inputLine = input;
		currentCharNdx = 0;

		currentChar = input.charAt(0);

		/*
		 * Start the finite-state-machine processing.
		 */
		running = true;

		while (running) {

			displayInputState();

			/*
			 * Uppercase letter
			 */
			if (currentChar >= 'A' && currentChar <= 'Z') {

				System.out.println("Upper case letter found");
				foundUpperCase = true;

			/*
			 * Lowercase letter
			 */
			} else if (currentChar >= 'a' && currentChar <= 'z') {

				System.out.println("Lower case letter found");
				foundLowerCase = true;

			/*
			 * Numeric digit
			 */
			} else if (currentChar >= '0' && currentChar <= '9') {

				System.out.println("Digit found");
				foundNumericDigit = true;

			/*
			 * Special character
			 */
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/"
					.indexOf(currentChar) >= 0) {

				System.out.println("Special character found");
				foundSpecialChar = true;

			/*
			 * Any other character is invalid.
			 */
			} else {

				passwordIndexofError = currentCharNdx;

				return "*** Error *** An invalid character has been found!";
			}

			/*
			 * Eight or more characters satisfies the minimum-length
			 * requirement.
			 */
			if (currentCharNdx >= MIN_PASSWORD_LENGTH - 1) {

				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}

			/*
			 * Move to the next character.
			 */
			currentCharNdx++;

			if (currentCharNdx >= inputLine.length()) {

				running = false;

			} else {

				currentChar = input.charAt(currentCharNdx);
			}

			System.out.println();
		}

		/*
		 * Construct an error message containing all requirements that
		 * were not satisfied.
		 */
		String errMessage = "";

		if (!foundUpperCase) {
			errMessage += "Upper case; ";
		}

		if (!foundLowerCase) {
			errMessage += "Lower case; ";
		}

		if (!foundNumericDigit) {
			errMessage += "Numeric digits; ";
		}

		if (!foundSpecialChar) {
			errMessage += "Special character; ";
		}

		if (!foundLongEnough) {
			errMessage += "At least 8 characters; ";
		}

		/*
		 * Empty error message means the password satisfies all
		 * requirements.
		 */
		if (errMessage.equals("")) {
			return "";
		}

		passwordIndexofError = currentCharNdx;

		return errMessage + "conditions were not satisfied";
	}
}
 