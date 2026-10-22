package lessonValidator;

/*******
 * <p> Title: LessonValidator Class </p>
 * 
 * <p> Description: Input validation for the lesson learned fields.  Each method follows the same
 * pattern as the username and password recognizers: it returns an empty string when the input is
 * acceptable, or a message that names the field and states the rule when it is not.  The class
 * has no JavaFX code, so it can be tested without starting the GUI.  The GUI classes call these
 * methods and contain no validation logic of their own. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 */
public class LessonValidator {
	
	/** The longest title allowed. */
	public static final int TITLE_MAX = 100;
	
	/** The longest description allowed. */
	public static final int DESCRIPTION_MAX = 1000;
	
	/** The longest search text allowed. */
	public static final int SEARCH_MAX = 100;
	
	/** The categories a lesson may have. */
	public static final String[] CATEGORIES = {"Setup", "Coding", "Testing", "Teamwork", "Other"};
	
	/** The message for a bad title. */
	public static final String TITLE_MESSAGE = 
			"Title must be between 1 and " + TITLE_MAX + " characters.";
	
	/** The message for a bad description. */
	public static final String DESCRIPTION_MESSAGE = 
			"Description must be between 1 and " + DESCRIPTION_MAX + " characters.";
	
	/** The message for a bad category. */
	public static final String CATEGORY_MESSAGE = 
			"Category must be one of: " + String.join(", ", CATEGORIES) + ".";
	
	/** The message for search text that is too long. */
	public static final String SEARCH_MESSAGE = 
			"Search text must be no more than " + SEARCH_MAX + " characters.";
	
	/**
	 * Default constructor is not used.
	 */
	private LessonValidator() {
	}
	
	/**********
	 * <p> Method: String validateTitle(String title) </p>
	 * 
	 * <p> Description: Spaces at the ends are ignored, so a title of only spaces is empty. </p>
	 * 
	 * @param title the text to check
	 * 
	 * @return an empty string if the title is acceptable, else the error message
	 */
	public static String validateTitle(String title) {
		int length = (title == null) ? 0 : title.trim().length();
		return (length >= 1 && length <= TITLE_MAX) ? "" : TITLE_MESSAGE;
	}
	
	/**********
	 * <p> Method: String validateDescription(String description) </p>
	 * 
	 * <p> Description: Spaces at the ends are ignored, so a description of only spaces is empty.
	 * </p>
	 * 
	 * @param description the text to check
	 * 
	 * @return an empty string if the description is acceptable, else the error message
	 */
	public static String validateDescription(String description) {
		int length = (description == null) ? 0 : description.trim().length();
		return (length >= 1 && length <= DESCRIPTION_MAX) ? "" : DESCRIPTION_MESSAGE;
	}
	
	/**********
	 * <p> Method: String validateCategory(String category) </p>
	 * 
	 * <p> Description: The category must match one of the allowed categories exactly. </p>
	 * 
	 * @param category the text to check
	 * 
	 * @return an empty string if the category is allowed, else the error message
	 */
	public static String validateCategory(String category) {
		if (category != null) {
			String c = category.trim();
			for (String allowed : CATEGORIES)
				if (allowed.equals(c)) return "";
		}
		return CATEGORY_MESSAGE;
	}
	
	/**********
	 * <p> Method: String validateSearchText(String text) </p>
	 * 
	 * <p> Description: Search text is optional, so empty text is acceptable.  Only the length is
	 * checked. </p>
	 * 
	 * @param text the text to check
	 * 
	 * @return an empty string if the search text is acceptable, else the error message
	 */
	public static String validateSearchText(String text) {
		int length = (text == null) ? 0 : text.trim().length();
		return (length <= SEARCH_MAX) ? "" : SEARCH_MESSAGE;
	}
}
