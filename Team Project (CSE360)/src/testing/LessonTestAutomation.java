package testing;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import lessonManager.LessonManager;

/*******
 * <p> Title: LessonTestAutomation Class </p>
 * 
 * <p> Description: Automated tests for the lesson learned CRUD and input validation operations.
 * Each test method matches one test case (TC-01 to TC-28) in Test Cases.pdf, in the same order,
 * and prints PASS or FAIL.  Like the other testbeds in this course, this is a plain Java
 * application: run it and read the console.
 * 
 * Design note: the tests use their own database (HW2TestDatabase) so they never touch the data
 * created through the application.  Every test starts by emptying the lesson table so no test
 * depends on another.  Expected error messages are written out in full here, not taken from
 * the validator, so a wrong message in the code is caught.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 */
public class LessonTestAutomation {
	
	private static final String TEST_DB_URL = "jdbc:h2:~/HW2TestDatabase";
	
	// The exact messages the tests expect
	private static final String TITLE_MSG = "Title must be between 1 and 100 characters.";
	private static final String DESC_MSG = "Description must be between 1 and 1000 characters.";
	private static final String CAT_MSG = 
			"Category must be one of: Setup, Coding, Testing, Teamwork, Other.";
	private static final String SEARCH_MSG = "Search text must be no more than 100 characters.";
	private static final String NOT_FOUND = "Lesson not found.";
	private static final String NO_LESSONS = "You have not added any lessons yet.";
	private static final String NO_MATCH = "No lessons match your search.";
	
	private static Database db;
	private static LessonManager mgr;
	private static int passed = 0;
	private static int failed = 0;
	
	/**
	 * Default constructor is not used.
	 */
	private LessonTestAutomation() {
	}
	
	/**********
	 * <p> Method: main(String[] args) </p>
	 * 
	 * <p> Description: Runs every test case and prints a summary. </p>
	 * 
	 * @param args not used
	 * 
	 * @throws Exception if the database cannot be opened
	 */
	public static void main(String[] args) throws Exception {
		db = new Database(TEST_DB_URL);
		db.connectToDatabase();
		mgr = new LessonManager(db);
		
		tc01(); tc02(); tc03(); tc04(); tc05(); tc06(); tc07(); tc08(); tc09();
		tc10(); tc11(); tc12(); tc13(); tc14(); tc15(); tc16();
		tc17(); tc18(); tc19(); tc20(); tc21();
		tc22(); tc23(); tc24(); tc25(); tc26(); tc27();
		tc28();
		
		db.deleteAllLessons();
		db.closeConnection();
		System.out.println("\nPassed: " + passed + "   Failed: " + failed);
	}
	
	/*-*******************************************************************************************
	
	Helpers
	
	*/
	
	// Print one result line and keep the totals
	private static void report(String id, String what, boolean ok) {
		if (ok) passed++; else failed++;
		System.out.println((ok ? "PASS " : "FAIL ") + id + "  " + what);
	}
	
	// Start a test from an empty lesson table
	private static void reset() { db.deleteAllLessons(); }
	
	// Create a valid lesson and return its id
	private static int add(String author, String title, String desc) {
		mgr.createLesson(author, title, desc, "Setup");
		return mgr.getLastCreatedId();
	}
	
	/*-*******************************************************************************************
	
	Test cases
	
	*/
	
	private static void tc01() {
		reset();
		String msg = mgr.createLesson("alice", "JavaFX build path", "Add the module path.", "Setup");
		LessonsLearned l = db.getLessonById(mgr.getLastCreatedId());
		report("TC-01", "Create a valid lesson", msg.isEmpty() && l != null && l.getId() > 0
				&& l.getAuthor().equals("alice") && l.getTitle().equals("JavaFX build path")
				&& l.getDescription().equals("Add the module path.")
				&& l.getCategory().equals("Setup")
				&& l.getCreatedAt() != null && l.getUpdatedAt() != null);
	}
	
	private static void tc02() {
		reset();
		String msg = mgr.createLesson("alice", "T".repeat(100), "D".repeat(1000), "Coding");
		report("TC-02", "Title of 100 and description of 1000 characters are accepted",
				msg.isEmpty() && db.getNumberOfLessons() == 1);
	}
	
	private static void tc03() {
		reset();
		String m1 = mgr.createLesson("alice", "", "desc", "Setup");
		String m2 = mgr.createLesson("alice", "   ", "desc", "Setup");
		report("TC-03", "Empty or spaces-only title is rejected", m1.equals(TITLE_MSG)
				&& m2.equals(TITLE_MSG) && db.getNumberOfLessons() == 0);
	}
	
	private static void tc04() {
		reset();
		String m = mgr.createLesson("alice", "T".repeat(101), "desc", "Setup");
		report("TC-04", "101-character title is rejected", m.equals(TITLE_MSG)
				&& db.getNumberOfLessons() == 0);
	}
	
	private static void tc05() {
		reset();
		String m1 = mgr.createLesson("alice", "title", "", "Setup");
		String m2 = mgr.createLesson("alice", "title", "   ", "Setup");
		report("TC-05", "Empty or spaces-only description is rejected", m1.equals(DESC_MSG)
				&& m2.equals(DESC_MSG) && db.getNumberOfLessons() == 0);
	}
	
	private static void tc06() {
		reset();
		String m = mgr.createLesson("alice", "title", "D".repeat(1001), "Setup");
		report("TC-06", "1001-character description is rejected", m.equals(DESC_MSG)
				&& db.getNumberOfLessons() == 0);
	}
	
	private static void tc07() {
		reset();
		String m = mgr.createLesson("alice", "title", "desc", "");
		report("TC-07", "Empty category is rejected", m.equals(CAT_MSG)
				&& db.getNumberOfLessons() == 0);
	}
	
	private static void tc08() {
		reset();
		String m = mgr.createLesson("alice", "title", "desc", "Cooking");
		report("TC-08", "Category not on the list is rejected", m.equals(CAT_MSG)
				&& db.getNumberOfLessons() == 0);
	}
	
	private static void tc09() {
		reset();
		boolean ok = true;
		for (String c : new String[] {"Setup", "Coding", "Testing", "Teamwork", "Other"})
			ok &= mgr.createLesson("alice", "title " + c, "desc", c).isEmpty();
		report("TC-09", "All five allowed categories are accepted", ok
				&& db.getNumberOfLessons() == 5);
	}
	
	private static void tc10() {
		reset();
		add("alice", "A1", "d"); add("alice", "A2", "d"); add("alice", "A3", "d");
		add("bob", "B1", "d"); add("bob", "B2", "d");
		LessonsLearnedList list = mgr.listMyLessons("alice");
		boolean ok = list.size() == 3;
		for (LessonsLearned l : list.getAll())
			ok &= l.getAuthor().equals("alice") && l.getTitle() != null && l.getCategory() != null
					&& l.getCreatedAt() != null;
		report("TC-10", "List returns only the contributor's own lessons", ok);
	}
	
	private static void tc11() {
		reset();
		LessonsLearnedList list = mgr.listMyLessons("carol");
		report("TC-11", "List with no lessons is empty and shows the message", list.isEmpty()
				&& list.getStatusMessage().equals(NO_LESSONS));
	}
	
	private static void tc12() {
		reset();
		add("alice", "Only one", "d");
		report("TC-12", "List with one lesson", mgr.listMyLessons("alice").size() == 1);
	}
	
	private static void tc13() {
		reset();
		for (int i = 0; i < 1000; i++) add("alice", "Lesson " + i, "d");
		report("TC-13", "List with 1000 lessons returns all of them",
				mgr.listMyLessons("alice").size() == 1000);
	}
	
	private static void tc14() {
		reset();
		int id = add("alice", "Standup notes", "Keep them short.");
		LessonsLearned l = mgr.getLesson("alice", id);
		report("TC-14", "View one lesson by id", l != null && l.getId() == id
				&& l.getTitle().equals("Standup notes")
				&& l.getDescription().equals("Keep them short.") && l.getCategory().equals("Setup")
				&& l.getAuthor().equals("alice") && l.getCreatedAt() != null
				&& l.getUpdatedAt() != null);
	}
	
	private static void tc15() {
		reset();
		LessonsLearned l = mgr.getLesson("alice", 9999);
		report("TC-15", "Unknown id gives \"Lesson not found.\"", l == null
				&& mgr.getLastMessage().equals(NOT_FOUND));
	}
	
	private static void tc16() {
		reset();
		int id = add("bob", "Bob's lesson", "private");
		LessonsLearned l = mgr.getLesson("alice", id);
		report("TC-16", "Another contributor's lesson gives \"Lesson not found.\"", l == null
				&& mgr.getLastMessage().equals(NOT_FOUND));
	}
	
	private static void tc17() {
		reset();
		add("alice", "JavaFX build path", "d");
		add("alice", "H2 file location", "d");
		add("alice", "Standup notes", "d");
		LessonsLearnedList list = mgr.searchMyLessons("alice", "javafx");
		report("TC-17", "Search matches a title, ignoring case", list != null && list.size() == 1
				&& list.get(0).getTitle().equals("JavaFX build path"));
	}
	
	private static void tc18() {
		reset();
		add("alice", "H2 file location", "The database file is in the home folder.");
		add("alice", "Standup notes", "Keep them short.");
		LessonsLearnedList list = mgr.searchMyLessons("alice", "database");
		report("TC-18", "Search matches a description", list != null && list.size() == 1
				&& list.get(0).getTitle().equals("H2 file location"));
	}
	
	private static void tc19() {
		reset();
		add("alice", "JavaFX build path", "d");
		LessonsLearnedList list = mgr.searchMyLessons("alice", "zebra");
		report("TC-19", "Search with no match gives an empty subset and the message",
				list != null && list.isEmpty() && list.getStatusMessage().equals(NO_MATCH));
	}
	
	private static void tc20() {
		reset();
		add("alice", "One", "d"); add("alice", "Two", "d"); add("alice", "Three", "d");
		add("bob", "Not alice's", "d");
		LessonsLearnedList list = mgr.searchMyLessons("alice", "");
		report("TC-20", "Empty search text returns all of the contributor's lessons",
				list != null && list.size() == 3);
	}
	
	private static void tc21() {
		reset();
		LessonsLearnedList list = mgr.searchMyLessons("alice", "k".repeat(101));
		report("TC-21", "101-character search text is rejected", list == null
				&& mgr.getLastMessage().equals(SEARCH_MSG));
	}
	
	private static void tc22() throws Exception {
		reset();
		int id = add("alice", "Old title", "Old description");
		LessonsLearned before = db.getLessonById(id);
		Thread.sleep(50);		// Make sure the clock moves so the updated time can be compared
		String msg = mgr.updateLesson("alice", id, "New title", "New description", "Testing");
		LessonsLearned after = db.getLessonById(id);
		report("TC-22", "Update stores new values and changes only the updated time",
				msg.isEmpty() && after.getTitle().equals("New title")
				&& after.getDescription().equals("New description")
				&& after.getCategory().equals("Testing")
				&& after.getUpdatedAt().after(before.getUpdatedAt())
				&& after.getCreatedAt().equals(before.getCreatedAt())
				&& after.getAuthor().equals("alice") && after.getId() == id);
	}
	
	private static void tc23() {
		reset();
		int id = add("alice", "Keep title", "Keep description");
		String m1 = mgr.updateLesson("alice", id, "", "new", "Coding");
		String m2 = mgr.updateLesson("alice", id, "new", "D".repeat(1001), "Coding");
		String m3 = mgr.updateLesson("alice", id, "new", "new", "Cooking");
		LessonsLearned l = db.getLessonById(id);
		report("TC-23", "Update with invalid values is rejected and nothing changes",
				m1.equals(TITLE_MSG) && m2.equals(DESC_MSG) && m3.equals(CAT_MSG)
				&& l.getTitle().equals("Keep title")
				&& l.getDescription().equals("Keep description") && l.getCategory().equals("Setup"));
	}
	
	private static void tc24() {
		reset();
		String m = mgr.updateLesson("alice", 9999, "title", "desc", "Setup");
		report("TC-24", "Update of an unknown id gives \"Lesson not found.\"", m.equals(NOT_FOUND));
	}
	
	private static void tc25() {
		reset();
		int id = add("alice", "Delete me", "d");
		String m = mgr.deleteLesson("alice", id, "Yes");
		LessonsLearned l = mgr.getLesson("alice", id);
		report("TC-25", "Delete confirmed with Yes removes the lesson", m.isEmpty() && l == null
				&& mgr.getLastMessage().equals(NOT_FOUND));
	}
	
	private static void tc26() {
		reset();
		int id = add("alice", "Keep me", "d");
		String m1 = mgr.deleteLesson("alice", id, "No");
		String m2 = mgr.deleteLesson("alice", id, "maybe");
		LessonsLearned l = db.getLessonById(id);
		report("TC-26", "Delete answered No (or anything but Yes) keeps the lesson",
				!m1.isEmpty() && !m2.isEmpty() && l != null && l.getTitle().equals("Keep me"));
	}
	
	private static void tc27() {
		reset();
		String m = mgr.deleteLesson("alice", 9999, "Yes");
		report("TC-27", "Delete of an unknown id gives \"Lesson not found.\"", m.equals(NOT_FOUND));
	}
	
	private static void tc28() {
		reset();
		add("alice", "Persist one", "d");
		add("alice", "Persist two", "d");
		db.closeConnection();									// Simulate closing the program
		db = new Database(TEST_DB_URL);							// Open the same database again
		try { db.connectToDatabase(); } catch (Exception e) { e.printStackTrace(); }
		mgr = new LessonManager(db);
		LessonsLearnedList list = mgr.listMyLessons("alice");
		report("TC-28", "Lessons are still there after the database is closed and reopened",
				list.size() == 2 && list.get(0).getTitle().equals("Persist one")
				&& list.get(1).getTitle().equals("Persist two"));
	}
}
