package testCases;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import database.Database;
import entityClasses.LessonLearned;
import entityClasses.LessonLearnedList;

/*******
 * <p> Title: ContributorTestbed Class </p>
 *
 * <p> Description: Semi-automated tests for the Contributor (Phase Two) user stories.  It follows
 * the same pattern as the PasswordEvaluationTestbed and the team's testCases class: every test
 * case has an ID, the requirement it checks, a short description, an expected result, and an
 * actual result.  Each test prints PASS or FAIL, and a summary is printed at the end. </p>
 *
 * <p> The test IDs (TC-C01 to TC-C46) and requirement IDs (R1 to R14) match the document
 * "Contributor Test Plan - TP2". </p>
 *
 * <p> How results are judged: each test asks "did the action succeed?"  Positive tests expect
 * success (valid input is accepted).  Negative tests expect failure (bad input is rejected).
 * A test that throws an exception is counted as FAIL and the exception is printed, so one
 * broken method can't stop the rest of the run. </p>
 *
 * <p> The tests are in three groups: </p>
 * <ul>
 *   <li> In-memory tests (validation and LessonLearnedList).  These need no database and can
 *        be run now. </li>
 *   <li> Database tests.  These only run when TEST_DB_IS_SAFE is true (see below). </li>
 *   <li> Manual tests.  These are printed as a checklist to perform through the GUI. </li>
 * </ul>
 *
 * <p> Merge note: this class is written against LessonLearned, LessonLearnedList, and the
 * matching Database methods as uploaded on 2026-10-24.  When the classes are renamed to
 * LessonsLearned and LessonsLearnedList, rename them here as well. </p>
 *
 * @author Pablo Pelaez Fundora
 *
 * @version 1.00		2026-10-24 First working version for TP2
 */
public class ContributorTestbed {

	/*
	 * Test database settings
	 */

	// A separate database, so the tests never touch real data.
	private static final String TEST_DB_URL = "jdbc:h2:~/LessonsLearnedTestDB";

	// SAFETY SWITCH.  Database.connectToDatabase() currently connects to DB_URL (the real
	// database) and ignores the URL given to the Database(String) constructor.  Leave this false
	// until that line is changed to use dbUrl; otherwise the tests write to the real database.
	private static final boolean TEST_DB_IS_SAFE = false;

	/*
	 * Counters for the summary
	 */
	private static int numPassed = 0;
	private static int numFailed = 0;
	private static int numPending = 0;
	private static int numManual = 0;

	private static Database db;									// Test database connection
	private static List<Integer> createdIds = new ArrayList<Integer>();	// Lessons to clean up


	/*****************************************************************************************
	 * Reporting helpers
	 ****************************************************************************************/

	/*****
	 * <p> Method: void check(String id, String req, String description, boolean expected,
	 * 		BooleanSupplier test) </p>
	 *
	 * <p> Description: Runs one automated test case and reports whether the actual result
	 * matched the expected result.  An exception inside the test counts as a FAIL. </p>
	 *
	 * @param id			the test case ID (e.g. "TC-C01")
	 * @param req			the requirement this test case checks (e.g. "R1")
	 * @param description	what the test does, in plain words
	 * @param expected		true if the action should succeed, false if it should be rejected
	 * @param test			the code that performs the action and returns whether it succeeded
	 */
	private static void check(String id, String req, String description,
			boolean expected, BooleanSupplier test) {
		System.out.println("\n" + id + " [" + req + "]: " + description);
		System.out.println("Expected: " + (expected ? "Success" : "Rejected"));

		boolean actual;
		try {
			actual = test.getAsBoolean();
		} catch (Exception e) {
			numFailed++;
			System.out.println("Actual:   Exception -> " + e);
			System.out.println("Result: *** FAIL ***");
			return;
		}

		System.out.println("Actual:   " + (actual ? "Success" : "Rejected"));
		if (actual == expected) {
			numPassed++;
			System.out.println("Result: PASS");
		} else {
			numFailed++;
			System.out.println("Result: *** FAIL ***");
		}
	}

	/*****
	 * <p> Method: void pending(String id, String req, String description, String reason) </p>
	 *
	 * <p> Description: Reports a test case that can't run yet, and why, so it still shows up
	 * in the output and the summary. </p>
	 *
	 * @param id			the test case ID
	 * @param req			the requirement this test case checks
	 * @param description	what the test will do
	 * @param reason		why it can't run yet
	 */
	private static void pending(String id, String req, String description, String reason) {
		numPending++;
		System.out.println("\n" + id + " [" + req + "]: " + description);
		System.out.println("Result: PENDING (" + reason + ")");
	}

	/*****
	 * <p> Method: void manual(String id, String req, String steps, String expected) </p>
	 *
	 * <p> Description: Prints one manual test as a checklist item to perform through the GUI. </p>
	 *
	 * @param id		the test case ID
	 * @param req		the requirement this test case checks
	 * @param steps		what the tester does
	 * @param expected	what the tester should see
	 */
	private static void manual(String id, String req, String steps, String expected) {
		numManual++;
		System.out.println("\n" + id + " [" + req + "] MANUAL");
		System.out.println("Do:     " + steps);
		System.out.println("Expect: " + expected);
	}


	/*****************************************************************************************
	 * Test data helpers
	 ****************************************************************************************/

	/*****
	 * <p> Method: LessonLearned newLesson(String author) </p>
	 *
	 * <p> Description: Builds a lesson with valid values in every required field. </p>
	 *
	 * @param author the username of the contributor
	 * @return a valid lesson that has not been saved
	 */
	private static LessonLearned newLesson(String author) {
		LessonLearned lesson = new LessonLearned("Test lesson title",
				"Test lesson description.", author);
		lesson.setRootCause("Test root cause.");
		lesson.setRecommendation("Test recommendation.");
		lesson.setKeywords("testing, junit");
		lesson.setCategory("Testing");
		return lesson;
	}

	/*****
	 * <p> Method: String text(int length) </p>
	 *
	 * <p> Description: Builds a string of exactly the given length, used for the maximum
	 * length boundary tests. </p>
	 *
	 * @param length the number of characters
	 * @return a string of that many 'a' characters
	 */
	private static String text(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) sb.append('a');
		return sb.toString();
	}

	/*****
	 * <p> Method: LessonLearned saved(String author) </p>
	 *
	 * <p> Description: Saves a new valid lesson to the test database and remembers its id so
	 * it is deleted at the end of the run. </p>
	 *
	 * @param author the username of the contributor
	 * @return the saved lesson, with its database id filled in
	 */
	private static LessonLearned saved(String author) {
		LessonLearned lesson = newLesson(author);
		if (!db.createLessonLearned(lesson))
			throw new IllegalStateException("setup failed: could not save a valid lesson");
		createdIds.add(lesson.getId());
		return lesson;
	}


	/*****************************************************************************************
	 * In-memory tests (no database needed)
	 ****************************************************************************************/

	/*****
	 * <p> Method: void runValidationTests() </p>
	 *
	 * <p> Description: R1 and R2.  Checks LessonLearned.validate() for blank required fields
	 * and for every field's maximum length (exactly at the limit, and one over it).  An empty
	 * string from validate() means the lesson is accepted. </p>
	 */
	private static void runValidationTests() {
		System.out.println("\n========== R1, R2: Required fields and length limits ==========");

		check("TC-C02", "R1", "Create a lesson with the title left blank", false, () ->
			new LessonLearned("", "Description", "contributorA").validate().isEmpty());

		check("TC-C03", "R1", "Create a lesson whose title is only spaces", false, () ->
			new LessonLearned("   ", "Description", "contributorA").validate().isEmpty());

		check("TC-C04", "R1", "Create a lesson with the description left blank", false, () ->
			new LessonLearned("Title", "", "contributorA").validate().isEmpty());

		check("TC-C05", "R1", "Create a lesson with no author", false, () ->
			new LessonLearned("Title", "Description", "").validate().isEmpty());

		check("TC-C06", "R2", "Fill every field to its exact maximum length", true, () -> {
			LessonLearned l = new LessonLearned(text(100), text(2000), "contributorA");
			l.setRootCause(text(2000));
			l.setRecommendation(text(2000));
			l.setKeywords(text(1000));
			l.setCategory(text(100));
			return l.validate().isEmpty();
		});

		check("TC-C07", "R2", "Enter a title one character too long (101)", false, () ->
			new LessonLearned(text(101), "Description", "contributorA").validate().isEmpty());

		check("TC-C08", "R2", "Enter a description one character too long (2001)", false, () ->
			new LessonLearned("Title", text(2001), "contributorA").validate().isEmpty());

		check("TC-C09", "R2", "Enter a root cause one character too long (2001)", false, () -> {
			LessonLearned l = newLesson("contributorA");
			l.setRootCause(text(2001));
			return l.validate().isEmpty();
		});

		check("TC-C10", "R2", "Enter a recommendation one character too long (2001)", false, () -> {
			LessonLearned l = newLesson("contributorA");
			l.setRecommendation(text(2001));
			return l.validate().isEmpty();
		});

		check("TC-C11", "R2", "Enter keywords one character too long (1001)", false, () -> {
			LessonLearned l = newLesson("contributorA");
			l.setKeywords(text(1001));
			return l.validate().isEmpty();
		});

		check("TC-C12", "R2", "Enter a category one character too long (101)", false, () -> {
			LessonLearned l = newLesson("contributorA");
			l.setCategory(text(101));
			return l.validate().isEmpty();
		});

		check("TC-C31", "R10", "Add time and effort info that is 256 characters long", false, () -> {
			LessonLearned l = newLesson("contributorA");
			l.setTimeEffort(text(256));
			return l.validate().isEmpty();
		});
	}

	/*****
	 * <p> Method: void runLessonListTests() </p>
	 *
	 * <p> Description: R14.  Checks LessonLearnedList search (createSubset) and lookup
	 * (findById) using three lessons built in memory.  Only lesson 1 contains the word
	 * "database", and the word "branching" appears only in lesson 3's keywords. </p>
	 */
	private static void runLessonListTests() {
		System.out.println("\n========== R14: Lesson list and keyword search ==========");

		LessonLearned l1 = new LessonLearned(1, "Back up the database",
				"Restore failed because the database backup was never tested.", "contributorA");
		LessonLearned l2 = new LessonLearned(2, "UI froze on large lists",
				"Loading every row at once froze the screen.", "contributorA");
		LessonLearned l3 = new LessonLearned(3, "Merge conflicts before the deadline",
				"Two people edited the same file the night before.", "contributorB");
		l3.setKeywords("git, branching");

		LessonLearnedList list = new LessonLearnedList();
		list.addLesson(l1);
		list.addLesson(l2);
		list.addLesson(l3);

		check("TC-C40", "R14", "Search for a word in some lessons (\"database\")", true, () -> {
			LessonLearnedList result = list.createSubset("database");
			return result.size() == 1 && result.findById(1) != null;
		});

		check("TC-C41", "R14", "Search for a word in no lesson (\"zzzz\")", false, () ->
			list.createSubset("zzzz").size() > 0);

		check("TC-C42", "R14", "Search for \"DATABASE\" in capitals", true, () -> {
			LessonLearnedList result = list.createSubset("DATABASE");
			return result.size() == 1 && result.findById(1) != null;
		});

		check("TC-C43", "R14", "Search with an empty search box", true, () ->
			list.createSubset("").size() == 3);

		// Expected to fail on the current code: createSubset() does not search keywords.
		check("TC-C44", "R14", "Search for a word only in a lesson's keywords (\"branching\")",
				true, () -> list.createSubset("branching").findById(3) != null);

		check("TC-C45", "R14", "Look up a lesson by an ID that exists", true, () ->
			list.findById(2) != null);

		check("TC-C46", "R14", "Look up a lesson by an ID that doesn't exist", false, () ->
			list.findById(99999) != null);
	}


	/*****************************************************************************************
	 * Database tests
	 ****************************************************************************************/

	/*****
	 * <p> Method: void runDatabaseTests() </p>
	 *
	 * <p> Description: R1, R3 to R7, R10, R12, R13, and R14 tests that need the database.
	 * Locks and curator edits are set up directly through LessonLearned and
	 * updateLessonLearned(), because there is no curator screen yet. </p>
	 */
	private static void runDatabaseTests() {
		System.out.println("\n========== Database tests (R1, R3-R7, R10, R12-R14) ==========");

		// ----- R1: Create -----
		check("TC-C01", "R1", "Create a lesson with all required fields filled in", true, () -> {
			LessonLearned l = newLesson("contributorA");
			boolean ok = db.createLessonLearned(l);
			if (ok) createdIds.add(l.getId());
			return ok && l.getId() > 0;
		});

		// ----- R3: Persistence -----
		check("TC-C13", "R3", "Save, close the database, reopen it, and read the lesson back",
				true, () -> {
			LessonLearned l = newLesson("contributorA");
			l.setTimeEffort("3 hours, 2 people");
			l.setFieldLocked("title", true);
			l.setEffortVisible(false);
			if (!db.createLessonLearned(l)) return false;
			createdIds.add(l.getId());

			db.closeConnection();
			connect();

			LessonLearned r = db.getLessonLearned(l.getId());
			return r != null
					&& r.getTitle().equals(l.getTitle())
					&& r.getLessonText().equals(l.getLessonText())
					&& r.getAuthor().equals(l.getAuthor())
					&& r.getRootCause().equals(l.getRootCause())
					&& r.getRecommendation().equals(l.getRecommendation())
					&& r.getKeywords().equals(l.getKeywords())
					&& r.getTimeEffort().equals(l.getTimeEffort())
					&& r.getCategory().equals(l.getCategory())
					&& r.isFieldLocked("title")
					&& !r.getEffortVisible();
		});

		check("TC-C14", "R3", "Ask for a lesson ID that doesn't exist", false, () ->
			db.getLessonLearned(999999) != null);

		// ----- R4: Update -----
		check("TC-C15", "R4", "Change a lesson's description and save it", true, () -> {
			LessonLearned l = saved("contributorA");
			l.setLessonText("Updated description.");
			return db.updateLessonLearned(l)
					&& db.getLessonLearned(l.getId()).getLessonText().equals("Updated description.");
		});

		check("TC-C16", "R4", "Change a lesson's title to blank and save it", false, () -> {
			LessonLearned l = saved("contributorA");
			l.setTitle("");
			boolean accepted = db.updateLessonLearned(l);
			boolean titleChanged = db.getLessonLearned(l.getId()).getTitle().isEmpty();
			return accepted || titleChanged;
		});

		check("TC-C17", "R4", "Try to update a lesson that was never saved", false, () ->
			db.updateLessonLearned(newLesson("contributorA")));

		// ----- R5: Locked fields -----
		// Expected to fail on the current code: updateLessonLearned() does not check locks.
		check("TC-C18", "R5", "Lock the title, then have the contributor change it", false, () -> {
			LessonLearned l = saved("contributorA");
			l.setFieldLocked("title", true);			// Curator locks the title
			db.updateLessonLearned(l);
			l.setTitle("Contributor changed the title");	// Contributor tries to change it
			db.updateLessonLearned(l);
			return db.getLessonLearned(l.getId()).getTitle()
					.equals("Contributor changed the title");
		});

		check("TC-C19", "R5", "Lock the title, then have the contributor change the description",
				true, () -> {
			LessonLearned l = saved("contributorA");
			l.setFieldLocked("title", true);
			db.updateLessonLearned(l);
			l.setLessonText("Contributor changed the description.");
			return db.updateLessonLearned(l)
					&& db.getLessonLearned(l.getId()).getLessonText()
						.equals("Contributor changed the description.");
		});

		// ----- R6: Delete -----
		check("TC-C21", "R6", "Delete an unlocked lesson", true, () -> {
			LessonLearned l = saved("contributorA");
			return db.deleteLessonLearned(l.getId()) && db.getLessonLearned(l.getId()) == null;
		});

		check("TC-C22", "R6", "Delete a lesson that doesn't exist", false, () ->
			db.deleteLessonLearned(999999));

		// ----- R7: Locked lessons can't be deleted -----
		// Expected to fail on the current code: deleteLessonLearned() does not check locks.
		check("TC-C24", "R7", "Lock a whole lesson, then have the contributor delete it", false, () -> {
			LessonLearned l = saved("contributorA");
			l.setIsLocked(true);
			db.updateLessonLearned(l);
			return db.deleteLessonLearned(l.getId());
		});

		// Expected to fail on the current code: deleteLessonLearned() does not check locks.
		check("TC-C25", "R7", "Lock one field, then have the contributor delete the lesson",
				false, () -> {
			LessonLearned l = saved("contributorA");
			l.setFieldLocked("title", true);
			db.updateLessonLearned(l);
			return db.deleteLessonLearned(l.getId());
		});

		// ----- R10: Experience information -----
		check("TC-C30", "R10", "Add time and effort info (255 characters) to a lesson", true, () -> {
			LessonLearned l = saved("contributorA");
			l.setTimeEffort(text(255));
			return db.updateLessonLearned(l)
					&& db.getLessonLearned(l.getId()).getTimeEffort().length() == 255;
		});

		check("TC-C32", "R10", "Add time and effort info to a lesson that doesn't exist",
				false, () -> {
			LessonLearned l = new LessonLearned(999999, "Title", "Description", "contributorA");
			l.setTimeEffort("2 hours");
			return db.updateLessonLearned(l);
		});

		// ----- R12: Effort visibility flag -----
		check("TC-C35", "R12", "Turn effort visibility off, save, and read it back", true, () -> {
			LessonLearned l = saved("contributorA");
			l.setEffortVisible(false);
			db.updateLessonLearned(l);
			return !db.getLessonLearned(l.getId()).getEffortVisible();
		});

		// ----- R13: Curator edits are visible to the contributor -----
		check("TC-C38", "R13", "A curator changes the description, then the contributor reads it",
				true, () -> {
			LessonLearned l = saved("contributorA");
			LessonLearned curatorCopy = db.getLessonLearned(l.getId());	// Curator's view
			curatorCopy.setLessonText("Curator's clearer description.");
			db.updateLessonLearned(curatorCopy);
			return db.getLessonLearned(l.getId()).getLessonText()		// Contributor's view
					.equals("Curator's clearer description.");
		});

		// ----- R14: List all lessons -----
		check("TC-C39", "R14", "Ask for the list of all lessons", true, () -> {
			LessonLearned a = saved("contributorA");
			LessonLearned b = saved("contributorB");
			LessonLearnedList all = db.getAllLessonsLearned();
			return all.findById(a.getId()) != null && all.findById(b.getId()) != null;
		});
	}

	/*****
	 * <p> Method: void runPendingTests() </p>
	 *
	 * <p> Description: R8 and R11 tests that need a method the code does not have yet: a way
	 * to get only one contributor's lessons, and a read that checks who is asking. </p>
	 */
	private static void runPendingTests() {
		System.out.println("\n========== R8, R11: Waiting on new methods ==========");
		String reason = "needs a get-lessons-by-author method for LessonLearned";
		pending("TC-C26", "R8", "A adds 3 lessons, B adds 2; A's list shows exactly A's 3", reason);
		pending("TC-C33", "R11", "B's lessons don't appear in A's list", reason);
		pending("TC-C34", "R11", "A asks for one of B's lessons by its ID; access is denied",
				"needs a read method that checks the requesting user");
	}

	/*****
	 * <p> Method: void printManualTests() </p>
	 *
	 * <p> Description: Prints the manual tests from the test plan as a checklist.  These are
	 * performed by hand through the Contributor GUI and shown in the testing screencast. </p>
	 */
	private static void printManualTests() {
		System.out.println("\n========== Manual tests (perform through the GUI) ==========");
		manual("TC-C20", "R5", "Open a lesson with a locked title on the contributor screen.",
				"The title box can't be edited and is marked as locked.");
		manual("TC-C23", "R6", "Click Delete, then answer \"No\" at the confirmation.",
				"The lesson is kept. (Only if the team adds a confirmation.)");
		manual("TC-C27", "R8", "Open the list as a contributor with no lessons.",
				"A \"no lessons\" message appears and nothing crashes.");
		manual("TC-C28", "R9", "Select a lesson in the list and click View.",
				"The detail screen opens for that lesson.");
		manual("TC-C29", "R9", "Click View without selecting a lesson.",
				"A message asks the user to select one, and nothing opens.");
		manual("TC-C36", "R12", "Turn effort visibility off and open the lesson as the contributor.",
				"The time and effort info is not shown.");
		manual("TC-C37", "R12", "Turn effort visibility on and open the lesson as the contributor.",
				"The time and effort info is shown.");
	}


	/*****************************************************************************************
	 * Setup, cleanup, and main
	 ****************************************************************************************/

	/*****
	 * <p> Method: void connect() </p>
	 *
	 * <p> Description: Opens the test database. </p>
	 */
	private static void connect() {
		db = new Database(TEST_DB_URL);
		try {
			db.connectToDatabase();
		} catch (SQLException e) {
			throw new IllegalStateException("Could not open the test database: " + e);
		}
	}

	/*****
	 * <p> Method: void cleanUp() </p>
	 *
	 * <p> Description: Deletes every lesson these tests created.  Locks are removed first, so
	 * cleanup still works after the lock checks are added to delete. </p>
	 */
	private static void cleanUp() {
		for (int id : createdIds) {
			LessonLearned l = db.getLessonLearned(id);
			if (l == null) continue;
			l.setIsLocked(false);
			l.setLockedFields(null);
			db.updateLessonLearned(l);
			db.deleteLessonLearned(id);
		}
	}

	/*****
	 * <p> Method: void main(String[] args) </p>
	 *
	 * <p> Description: Runs every group of tests and prints the summary. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		runValidationTests();
		runLessonListTests();

		String[][] dbTests = {
			{"TC-C01", "R1"}, {"TC-C13", "R3"}, {"TC-C14", "R3"}, {"TC-C15", "R4"},
			{"TC-C16", "R4"}, {"TC-C17", "R4"}, {"TC-C18", "R5"}, {"TC-C19", "R5"},
			{"TC-C21", "R6"}, {"TC-C22", "R6"}, {"TC-C24", "R7"}, {"TC-C25", "R7"},
			{"TC-C30", "R10"}, {"TC-C32", "R10"}, {"TC-C35", "R12"}, {"TC-C38", "R13"},
			{"TC-C39", "R14"}};

		if (TEST_DB_IS_SAFE) {
			connect();
			try {
				runDatabaseTests();
			} finally {
				cleanUp();
				db.closeConnection();
			}
		} else {
			System.out.println("\n========== Database tests skipped ==========");
			for (String[] t : dbTests)
				pending(t[0], t[1], "database test",
						"TEST_DB_IS_SAFE is false; fix connectToDatabase() first");
		}

		runPendingTests();
		printManualTests();

		// ---------- Summary ----------
		System.out.println("\n----------------------------------------");
		System.out.println("Automated test cases run: " + (numPassed + numFailed));
		System.out.println("Passed:  " + numPassed);
		System.out.println("Failed:  " + numFailed);
		System.out.println("Pending: " + numPending);
		System.out.println("Manual:  " + numManual + " (perform through the GUI)");
	}
}
