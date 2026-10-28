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
 * Design note: every logged-in user can see and search every lesson in the system (team-wide),
 * but only the lesson's own author, or an Admin, may update or delete it.  A lesson that does
 * not exist is reported as "Lesson not found."; a lesson that exists but belongs to someone
 * else, when a non-admin tries to change it, is reported with MSG_NOT_OWNER instead.</p>
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
	
	/** Message used when nobody has recorded any lesson yet. */
	public static final String MSG_NO_LESSONS_ANY = "No lessons have been recorded yet.";
	
	/** Message used when a non-admin tries to change a lesson someone else created. */
	public static final String MSG_NOT_OWNER = 
			"You can only update or delete lessons you created.";
	
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
	 * <p> Method: LessonsLearnedList listMyLessons(String author) </p>
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
	 * <p> Method: LessonsLearned getLesson(String author, int id) </p>
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
	 * <p> Method: LessonsLearnedList searchMyLessons(String author, String keyword) </p>
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
	
	/**********
	 * <p> Method: LessonLearnedList listAllLessons() </p>
	 * 
	 * <p> Description: Get every lesson currently recorded, from every contributor.  This is
	 * what the "Show All" action on the page now uses so a logged-in user can review everything
	 * the team has learned, not just their own lessons. </p>
	 * 
	 * @return every lesson in the system (the list may be empty)
	 */
	public LessonsLearnedList listAllLessons() {
		LessonsLearnedList list = new LessonsLearnedList(MSG_NO_LESSONS_ANY);
		for (LessonsLearned lesson : database.getAllLessons()) list.add(lesson);
		lastMessage = list.getStatusMessage();
		return list;
	}
	
	/**********
	 * <p> Method: LessonLearnedList searchByKeyword(String keyword) </p>
	 * 
	 * <p> Description: Get the subset of every lesson, from every contributor, that matches the
	 * keyword.  Empty search text returns every lesson. </p>
	 * 
	 * @param keyword the text to look for in titles and descriptions
	 * 
	 * @return the matching lessons (the list may be empty), or null if the search text is
	 * 		invalid; in that case getLastMessage explains why
	 */
	public LessonsLearnedList searchByKeyword(String keyword) {
		lastMessage = LessonValidator.validateSearchText(keyword);
		if (!lastMessage.isEmpty()) return null;
		
		String text = (keyword == null) ? "" : keyword.trim();
		if (text.isEmpty()) return listAllLessons();
		
		LessonsLearnedList list = new LessonsLearnedList(MSG_NO_MATCH);
		for (LessonsLearned lesson : database.searchLessonsByKeyword(text)) list.add(lesson);
		lastMessage = list.getStatusMessage();
		return list;
	}
	
	/**********
	 * <p> Method: LessonLearnedList searchByCategory(String category) </p>
	 * 
	 * <p> Description: Get the subset of every lesson, from every contributor, whose category
	 * matches exactly, ignoring case. </p>
	 * 
	 * @param category the category to match; must be one of LessonValidator.CATEGORIES
	 * 
	 * @return the matching lessons (the list may be empty), or null if the category is
	 * 		invalid; in that case getLastMessage explains why
	 */
	public LessonsLearnedList searchByCategory(String category) {
		lastMessage = LessonValidator.validateCategory(category);
		if (!lastMessage.isEmpty()) return null;
		
		LessonsLearnedList list = new LessonsLearnedList(MSG_NO_MATCH);
		for (LessonsLearned lesson : database.getLessonsByCategory(category)) list.add(lesson);
		lastMessage = list.getStatusMessage();
		return list;
	}
	
	/**********
	 * <p> Method: LessonLearned getLessonById(int id) </p>
	 * 
	 * <p> Description: Get one lesson by id regardless of who created it.  Used so any
	 * logged-in user can open the details of a lesson they see in the team-wide list; the
	 * ownership check still happens separately, at update or delete time. </p>
	 * 
	 * @param id the id of the lesson
	 * 
	 * @return the lesson, or null (with the message "Lesson not found.") if it does not exist
	 */
	public LessonsLearned getLessonById(int id) {
		LessonsLearned lesson = database.getLessonById(id);
		lastMessage = (lesson == null) ? MSG_NOT_FOUND : "";
		return lesson;
	}
	
	/**********
	 * <p> Method: String updateLesson(String currentUser, int id, String title,
	 * 		String description, String category, boolean isAdmin) </p>
	 * 
	 * <p> Description: Change any lesson after validating the new values.  A non-admin may
	 * only change a lesson they created; an admin may change any lesson.  The lesson is left
	 * unchanged when validation or the ownership check fails. </p>
	 * 
	 * @param currentUser the username of the logged-in user making the change
	 * 
	 * @param id the id of the lesson
	 * 
	 * @param title the new title
	 * 
	 * @param description the new description
	 * 
	 * @param category the new category
	 * 
	 * @param isAdmin true when the logged-in user is an Admin, who may change any lesson
	 * 
	 * @return an empty string if the lesson was updated, else the error message
	 */
	public String updateLesson(String currentUser, int id, String title, String description,
			String category, boolean isAdmin) {
		LessonsLearned existing = database.getLessonById(id);
		if (existing == null) {
			lastMessage = MSG_NOT_FOUND;
			return lastMessage;
		}
		if (!isAdmin && !existing.getAuthor().equals(currentUser)) {
			lastMessage = MSG_NOT_OWNER;
			return lastMessage;
		}
		lastMessage = validateCoreInformation(title, description, category);
		if (!lastMessage.isEmpty()) return lastMessage;
		
		LessonsLearned cleaned = new LessonsLearned(existing.getAuthor(), title, description,
				category);
		if (!database.updateLesson(id, cleaned.getTitle(), cleaned.getDescription(),
				cleaned.getCategory()))
			lastMessage = MSG_SAVE_FAILED;
		return lastMessage;
	}
	
	/**********
	 * <p> Method: String deleteLesson(String currentUser, int id, String answer,
	 * 		boolean isAdmin) </p>
	 * 
	 * <p> Description: Delete any lesson after the user confirms.  A non-admin may only delete
	 * a lesson they created; an admin may delete any lesson.  The lesson is removed only when
	 * the answer to "Are you sure?" is exactly "Yes". </p>
	 * 
	 * @param currentUser the username of the logged-in user requesting the delete
	 * 
	 * @param id the id of the lesson
	 * 
	 * @param answer the user's answer to the confirmation question
	 * 
	 * @param isAdmin true when the logged-in user is an Admin, who may delete any lesson
	 * 
	 * @return an empty string if the lesson was removed, else the message explaining why not
	 */
	public String deleteLesson(String currentUser, int id, String answer, boolean isAdmin) {
		LessonsLearned existing = database.getLessonById(id);
		if (existing == null) {
			lastMessage = MSG_NOT_FOUND;
			return lastMessage;
		}
		if (!isAdmin && !existing.getAuthor().equals(currentUser)) {
			lastMessage = MSG_NOT_OWNER;
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
