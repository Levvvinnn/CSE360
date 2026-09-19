package passwordPopUpWindow;

/*******
 * <p> Title: Model Class - the Password Recognizer. </p>
 *
 * <p> Description: This class implements the finite state machine defined by the
 * Password Recognizer UML state machine diagram.  It is a pure recognizer: it has
 * no dependency on JavaFX or on any View class, so it may be called from any page
 * in the application (e.g. guiFirstAdmin and guiNewAccount).
 *
 * The machine has a single working state (state 0) that consumes one character per
 * transition, and a single accepting state (state 1) that is reached when the input
 * is fully consumed.
 *
 *   State 0 transitions (self loops):
 *      A-Z                 [1] increment counter, set upperCase,    advance
 *      a-z                 [2] increment counter, set lowerCase,    advance
 *      0-9                 [3] increment counter, set numericChar,  advance
 *      special character   [4] increment counter, set specialChar,  advance
 *      other character     [5] set otherChar (the machine halts here; the counter
 *                              is NOT incremented and the character is NOT consumed)
 *
 *   State 0 -> State 1:
 *      input fully consumed [6] if 8 <= charCounter <= 32 set longEnough
 *                               if charCounter > 32       set tooLong
 *
 * Note that, per the diagram, over-length input is NOT rejected before the machine
 * runs.  The whole input is consumed and the length is judged by semantic action [6].
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 4.00 2026-09-17 Reimplemented from the UML state machine diagram and
 *                          decoupled from the View
 */

public class Model {

	/*-******************************************************************************
	 * Constants
	 */

	/** The smallest number of characters a valid password may contain. */
	public static final int MIN_PASSWORD_LENGTH = 8;

	/** The largest number of characters a valid password may contain. */
	public static final int MAX_PASSWORD_LENGTH = 32;

	/**
	 * The set of special characters recognized by transition [4], taken from the
	 * state machine diagram.  If the diagram's list is revised, change only this
	 * string; nothing else in the recognizer needs to be touched.
	 */
	public static final String SPECIAL_CHARACTERS =
			"~`!@#$%^&*()_-+{}[]\\:,.?/";

	/**
	 * When true the recognizer traces its progress to the console.  Leave this
	 * false in the GUI application; set it true when unit testing the FSM.
	 */
	public static boolean debug = false;

	/*-******************************************************************************
	 * Recognizer results
	 *
	 * These attributes are how the recognizer communicates its findings back to the
	 * caller.  They are refreshed at the start of every call to evaluatePassword.
	 */

	/** The error message produced by the last evaluation ("" when valid). */
	public static String passwordErrorMessage = "";

	/** The input string given to the last evaluation. */
	public static String passwordInput = "";

	/**
	 * The index of the character that caused the error, or -1 when the failure is
	 * not attributable to one specific character (e.g. a missing requirement).
	 */
	public static int passwordIndexofError = -1;

	/** The number of characters consumed by the finite state machine. */
	public static int charCounter = 0;

	/*
	 * The Boolean flags named by the state machine diagram.
	 */
	public static boolean foundUpperCase = false;		// 1. upperCase
	public static boolean foundLowerCase = false;		// 2. lowerCase
	public static boolean foundNumericDigit = false;	// 3. numericChar
	public static boolean foundSpecialChar = false;		// 4. specialChar
	public static boolean foundLongEnough = false;		// 5. longEnough
	public static boolean foundOtherChar = false;		// 6. otherChar
	public static boolean foundTooLong = false;			// 7. tooLong

	/*-******************************************************************************
	 * Finite state machine working storage
	 */

	private static String inputLine = "";
	private static char currentChar;
	private static int currentCharNdx;
	private static boolean running;

	/**
	 * Default constructor.  The recognizer is a collection of static methods, so
	 * there is no reason to instantiate it.
	 */
	public Model() {
	}

	/*-******************************************************************************
	 * The recognizer
	 */

	/**********
	 * <p> Method: evaluatePassword(String input) </p>
	 *
	 * <p> Description: Runs the password recognizer over the supplied input and
	 * reports the result.  All of the public result attributes above are set by
	 * this method. </p>
	 *
	 * @param input the password to be evaluated
	 *
	 * @return an empty string when the password satisfies every requirement,
	 *         otherwise a message describing what is wrong
	 */
	public static String evaluatePassword(String input) {

		/*
		 * Semantic action [0]: set the character counter to zero, set all of the
		 * Boolean flags to False, and set currentChar to the first input character.
		 */
		passwordErrorMessage = "";
		passwordIndexofError = -1;
		charCounter = 0;

		foundUpperCase = false;
		foundLowerCase = false;
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundLongEnough = false;
		foundOtherChar = false;
		foundTooLong = false;

		if (input == null) {
			passwordInput = "";
			inputLine = "";
			passwordIndexofError = 0;
			passwordErrorMessage = "*** Error *** The password cannot be null!";
			return passwordErrorMessage;
		}

		passwordInput = input;
		inputLine = input;
		currentCharNdx = 0;

		if (input.isEmpty()) {
			passwordIndexofError = 0;
			passwordErrorMessage = "*** Error *** The password is empty!";
			return passwordErrorMessage;
		}

		currentChar = input.charAt(0);

		/*
		 * State 0: consume one character per iteration until either the input is
		 * fully consumed or an unrecognized character halts the machine.
		 */
		running = true;

		while (running) {

			displayInputState();

			if (currentChar >= 'A' && currentChar <= 'Z') {

				// Transition [1]: an upper case letter
				charCounter++;
				foundUpperCase = true;
				moveToNextCharacter();

			} else if (currentChar >= 'a' && currentChar <= 'z') {

				// Transition [2]: a lower case letter
				charCounter++;
				foundLowerCase = true;
				moveToNextCharacter();

			} else if (currentChar >= '0' && currentChar <= '9') {

				// Transition [3]: a numeric digit
				charCounter++;
				foundNumericDigit = true;
				moveToNextCharacter();

			} else if (SPECIAL_CHARACTERS.indexOf(currentChar) >= 0) {

				// Transition [4]: a special character
				charCounter++;
				foundSpecialChar = true;
				moveToNextCharacter();

			} else {

				/*
				 * Transition [5]: any other character.  The diagram neither
				 * increments the counter nor advances the input here, so the
				 * machine simply halts in a non-accepting state.
				 */
				foundOtherChar = true;
				passwordIndexofError = currentCharNdx;
				running = false;
			}
		}

		/*
		 * Semantic action [6], performed on the transition from state 0 to the
		 * accepting state.  It is only reached when the input was fully consumed,
		 * so it is skipped when the machine halted on an invalid character.
		 */
		if (!foundOtherChar) {

			if (charCounter >= MIN_PASSWORD_LENGTH
					&& charCounter <= MAX_PASSWORD_LENGTH) {
				foundLongEnough = true;
			}

			if (charCounter > MAX_PASSWORD_LENGTH) {
				foundTooLong = true;
			}
		}

		passwordErrorMessage = buildErrorMessage();

		return passwordErrorMessage;
	}

	/**********
	 * <p> Method: isValid(String input) </p>
	 *
	 * <p> Description: A convenience wrapper for callers that only need a yes or
	 * no answer.  The public result attributes are still updated, so the caller
	 * may inspect the individual flags afterwards. </p>
	 *
	 * @param input the password to be evaluated
	 *
	 * @return true when the password satisfies every requirement
	 */
	public static boolean isValid(String input) {
		return evaluatePassword(input).isEmpty();
	}

	/*-******************************************************************************
	 * Private helper methods
	 */

	/*
	 * Advances the machine to the next input character.  When the input has been
	 * fully consumed the machine stops so semantic action [6] can be performed.
	 */
	private static void moveToNextCharacter() {

		currentCharNdx++;

		if (currentCharNdx >= inputLine.length()) {
			running = false;
		} else {
			currentChar = inputLine.charAt(currentCharNdx);
		}
	}

	/*
	 * Assembles the message describing everything that is wrong with the input.
	 * An invalid character and an over-length password are reported on their own
	 * because neither can be fixed by adding more character classes.
	 */
	private static String buildErrorMessage() {

		if (foundOtherChar) {
			return "*** Error *** An invalid character was found at position "
					+ (passwordIndexofError + 1)
					+ " of the password!";
		}

		if (foundTooLong) {
			passwordIndexofError = MAX_PASSWORD_LENGTH;
			return "*** Error *** The password is "
					+ charCounter
					+ " characters long; the maximum is "
					+ MAX_PASSWORD_LENGTH
					+ " characters!";
		}

		String errMessage = "";

		if (!foundUpperCase) {
			errMessage += "Upper case; ";
		}

		if (!foundLowerCase) {
			errMessage += "Lower case; ";
		}

		if (!foundNumericDigit) {
			errMessage += "Numeric digit; ";
		}

		if (!foundSpecialChar) {
			errMessage += "Special character; ";
		}

		if (!foundLongEnough) {
			errMessage += "At least " + MIN_PASSWORD_LENGTH + " characters; ";
		}

		if (errMessage.isEmpty()) {
			return "";
		}

		return "*** Error *** " + errMessage + "conditions were not satisfied";
	}

	/*
	 * Traces the state of the machine.  Does nothing unless debug is turned on.
	 */
	private static void displayInputState() {

		if (!debug) {
			return;
		}

		System.out.println(inputLine);

		if (currentCharNdx <= inputLine.length()) {
			System.out.println(inputLine.substring(0, currentCharNdx) + "?");
		}

		System.out.println(
				"The password size: " + inputLine.length()
				+ "  |  The currentCharNdx: " + currentCharNdx
				+ "  |  The charCounter: " + charCounter
				+ "  |  The currentChar: \"" + currentChar + "\"");
	}
}