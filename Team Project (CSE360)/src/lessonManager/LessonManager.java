package lessonManager;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import lessonValidator.LessonValidator;

/*******
 * <p> Title: LessonManager Class </p>
 * 
 * <p> Description: This class performs the Create, Read, Update and Delete operations on lessons
 * learned for one contributor.  Each operation first validates the input with LessonValidator,
 * then calls the Database class.  Keeping this logic in one class with no JavaFX code lets the
 * GUI pages and the automated tests use exactly the same code path.
 * 
 * Design note: a contributor can only see and change their own lessons.  A lesson that belongs
 * to someone else is reported as "Lesson not found." so the system does not reveal whether that
 * lesson exists.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 */
public class LessonManager {
	
	/** Message used when a lesson does not exist or belongs to someone else. */
	public static final String MSG_NOT_FOUND = "Lesson not found.";
	
	/** Message used when a contributor has no lessons. */
	public static final String MSG_NO_LESSONS = "You have not added any lessons yet.";
	
	/** Message used when a search finds nothing. */
	public static final String MSG_NO_MATCH = "No lessons match your search.";
	
	/** Message used when the contributor does not confirm a delete. */
	public static final String MSG_DELETE_CANCELLED = 
			"Delete cancelled. The lesson was not removed.";
	
	/** Message used when the database could not save a lesson. */
	public static final String MSG_SAVE_FAILED = "The lesson could not be saved. Try again.";
	
	private final Database database;			// The database this manager works with
	private int lastCreatedId = -1;			// The id of the lesson most recently created
	private String lastMessage = "";		// The message produced by the most recent operation
	
	/*****
	 * <p> Method: LessonManager(Database database) </p>
	 * 
	 * <p> Description: Establishes a manager that uses the specified database. </p>
	 * 
	 * @param database an already connected Database object
	 */
	public LessonManager(Database database) {
		this.database = database;
	}
	
	/*****
	 * <p> Method: int getLastCreatedId() </p>
	 * @return the id of the lesson most recently created, or -1 if none has been created
	 */
	public int getLastCreatedId() { return lastCreatedId; }
	
	/*****
	 * <p> Method: String getLastMessage() </p>
	 * @return the message from the most recent operation, or an empty string if it succeeded
	 */
	public String getLastMessage() { return lastMessage; }
	
	// Private helper: returns the lesson only if it exists and belongs to this author
	private LessonsLearned findOwned(String author, int id) {
		LessonsLearned lesson = database.getLessonById(id);
		if (lesson == null || !lesson.getAuthor().equals(author)) return null;
		return lesson;
	}
	
	// Private helper: validates all three core fields and returns the first message found
	private String validateCoreInformation(String title, String description, String category) {
		String message = LessonValidator.validateTitle(title);
		if (!message.isEmpty()) return message;
		message = LessonValidator.validateDescription(description);
		if (!message.isEmpty()) return message;
		return LessonValidator.validateCategory(category);
	}
	
	/**********
	 * <p> Method: String createLesson(String author, String title, String description,
	 * 		String category) </p>
	 * 
	 * <p> Description: Validate the core information and, if it is acceptable, save a new
	 * lesson.  Nothing is saved when validation fails. </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @param title the title of the lesson
	 * 
	 * @param description the description of the lesson
	 * 
	 * @param category the category of the lesson
	 * 
	 * @return an empty string if the lesson was saved, else the error message
	 */
	public String createLesson(String author, String title, String description, String category) {
		lastMessage = validateCoreInformation(title, description, category);
		if (!lastMessage.isEmpty()) return lastMessage;
		
		lastCreatedId = database.insertLesson(new LessonsLearned(author, title, description,
				category));
		if (lastCreatedId < 0) lastMessage = MSG_SAVE_FAILED;
		return lastMessage;
	}
	
	/**********
	 * <p> Method: LessonLearnedList listMyLessons(String author) </p>
	 * 
	 * <p> Description: Get every lesson the contributor has added. </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @return the contributor's lessons (the list may be empty)
	 */
	public LessonsLearnedList listMyLessons(String author) {
		LessonsLearnedList list = new LessonsLearnedList(MSG_NO_LESSONS);
		for (LessonsLearned lesson : database.getLessonsByAuthor(author)) list.add(lesson);
		lastMessage = list.getStatusMessage();
		return list;
	}
	
	/**********
	 * <p> Method: LessonLearned getLesson(String author, int id) </p>
	 * 
	 * <p> Description: Get one of the contributor's lessons. </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @param id the id of the lesson
	 * 
	 * @return the lesson, or null (with the message "Lesson not found.") if it does not exist
	 * 		or belongs to someone else
	 */
	public LessonsLearned getLesson(String author, int id) {
		LessonsLearned lesson = findOwned(author, id);
		lastMessage = (lesson == null) ? MSG_NOT_FOUND : "";
		return lesson;
	}
	
	/**********
	 * <p> Method: LessonLearnedList searchMyLessons(String author, String keyword) </p>
	 * 
	 * <p> Description: Get the subset of the contributor's lessons that match the keyword.
	 * Empty search text returns all of the contributor's lessons. </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @param keyword the text to look for in titles and descriptions
	 * 
	 * @return the matching lessons (the list may be empty), or null if the search text is
	 * 		invalid; in that case getLastMessage explains why
	 */
	public LessonsLearnedList searchMyLessons(String author, String keyword) {
		lastMessage = LessonValidator.validateSearchText(keyword);
		if (!lastMessage.isEmpty()) return null;
		
		String text = (keyword == null) ? "" : keyword.trim();
		if (text.isEmpty()) return listMyLessons(author);
		
		LessonsLearnedList list = new LessonsLearnedList(MSG_NO_MATCH);
		for (LessonsLearned lesson : database.searchLessonsByAuthor(author, text)) list.add(lesson);
		lastMessage = list.getStatusMessage();
		return list;
	}
	
	/**********
	 * <p> Method: String updateLesson(String author, int id, String title, String description,
	 * 		String category) </p>
	 * 
	 * <p> Description: Change one of the contributor's lessons after validating the new values.
	 * The lesson is left unchanged when validation fails. </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @param id the id of the lesson
	 * 
	 * @param title the new title
	 * 
	 * @param description the new description
	 * 
	 * @param category the new category
	 * 
	 * @return an empty string if the lesson was updated, else the error message
	 */
	public String updateLesson(String author, int id, String title, String description,
			String category) {
		if (findOwned(author, id) == null) {
			lastMessage = MSG_NOT_FOUND;
			return lastMessage;
		}
		lastMessage = validateCoreInformation(title, description, category);
		if (!lastMessage.isEmpty()) return lastMessage;
		
		LessonsLearned cleaned = new LessonsLearned(author, title, description, category);
		if (!database.updateLesson(id, cleaned.getTitle(), cleaned.getDescription(),
				cleaned.getCategory()))
			lastMessage = MSG_SAVE_FAILED;
		return lastMessage;
	}
	
	/**********
	 * <p> Method: String deleteLesson(String author, int id, String answer) </p>
	 * 
	 * <p> Description: Delete one of the contributor's lessons.  The lesson is removed only
	 * when the answer to "Are you sure?" is exactly "Yes". </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @param id the id of the lesson
	 * 
	 * @param answer the contributor's answer to the confirmation question
	 * 
	 * @return an empty string if the lesson was removed, else the message explaining why not
	 */
	public String deleteLesson(String author, int id, String answer) {
		if (findOwned(author, id) == null) {
			lastMessage = MSG_NOT_FOUND;
			return lastMessage;
		}
		if (!"Yes".equals(answer)) {
			lastMessage = MSG_DELETE_CANCELLED;
			return lastMessage;
		}
		lastMessage = database.deleteLesson(id) ? "" : MSG_NOT_FOUND;
		return lastMessage;
	}
}
