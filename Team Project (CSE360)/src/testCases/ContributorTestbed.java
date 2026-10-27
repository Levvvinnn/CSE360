package testCases;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ContributorTestbed Class </p>
 *
 * <p> Description: Semi-automated tests showing that the LessonsLearned and LessonsLearnedList
 * entity classes, and the Database methods that store them, satisfy the Contributor (Phase
 * Two) user stories.  It follows the pattern of the PasswordEvaluationTestbed: every test case
 * has an ID, the requirement it checks, an expected result, and an actual result, and prints
 * PASS or FAIL.  A summary is printed at the end. </p>
 *
 * <p> Organization: every test case is its own method, named after what it checks, with a
 * Javadoc comment giving its test ID, its requirement, the user story text it comes from, the
 * input, the expected result, and why a PASS shows the requirement is met.  The test IDs
 * (TC-C01 to TC-C46) and requirement IDs (R1 to R14) match the document "Contributor Test
 * Plan - TP2".  The manual tests in that plan are printed as a checklist at the end of the
 * run and are performed through the Contributor GUI. </p>
 *
 * <p> The Contributor user stories, numbered C1 to C4 in the order they appear in "Lessons
 * Learned System V1": </p>
 * <ul>
 *   <li> C1: A contributor adds lessons with core information, sees and manages them, and
 *        cannot alter fields another role has locked or see what other roles added. </li>
 *   <li> C2: A contributor sees a list of their own lessons, examines one, sees which fields
 *        are locked, and cannot delete a lesson that is locked or has a locked field. </li>
 *   <li> C3: A contributor adds experience (time and effort) information to a lesson. </li>
 *   <li> C4: A contributor sees only their own content, including a curator's edits of it,
 *        and never another contributor's submissions. </li>
 * </ul>
 *
 * <p> How results are judged: each test asks one question, "did the action succeed?"
 * Positive tests use valid input and expect Success.  Negative tests use bad input and expect
 * Rejected.  A test that throws an exception counts as FAIL and the exception is printed, so
 * one broken method cannot stop the rest of the run. </p>
 *
 * <p> Test groups: </p>
 * <ul>
 *   <li> Validation tests (R1, R2, R10) call LessonsLearned.validate(), which
 *        createLessonLearned() and updateLessonLearned() run before every save. </li>
 *   <li> List tests (R8, R11, R14) build a LessonsLearnedList in memory. </li>
 *   <li> Database tests (R1, R3 to R7, R10, R12 to R14) save and read lessons through the
 *        lessonLearnedDB table, using a separate test database. </li>
 * </ul>
 *
 * <p> Known failures: TC-C18, TC-C24, and TC-C25 fail because updateLessonLearned() and
 * deleteLessonLearned() do not yet check locks, and TC-C44 fails because keyword search does
 * not look at the keywords field.  These tests are correct; they pass once the code is fixed.
 * </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Pablo Pelaez Fundora
 *
 * @version 1.00		2026-10-24 First working version for TP2
 * @version 1.10		2026-10-25 One method per test; updated for the merged LessonsLearned and
 * 		LessonsLearnedList classes; database tests enabled; attribute sources and rationale
 */
public class ContributorTestbed {

	/*
	 * Test database settings
	 */

	/** A separate H2 database used only by these tests.
	 * <p> Source: Task 7 requires tests that can be run repeatedly without damaging data. </p>
	 * <p> Rationale: running against the real database would add test lessons to the
	 * application's data and could delete real lessons during cleanup. </p> */
	private static final String TEST_DB_URL = "jdbc:h2:~/LessonsLearnedTestDB";

	/** Safety switch for the database tests.
	 * <p> Source: a bug found while writing these tests, where connectToDatabase() ignored
	 * the URL passed to Database(String) and always opened the real database. </p>
	 * <p> Rationale: the bug is fixed, so this is true.  If it ever returns, setting this to
	 * false skips every database test instead of writing test lessons into real data. </p> */
	private static final boolean TEST_DB_IS_SAFE = true;

	/*
	 * Counters for the summary
	 */

	/** The number of automated tests whose actual result matched the expected result.
	 * <p> Source: the PasswordEvaluationTestbed pattern, which ends with a pass/fail count.
	 * </p>
	 * <p> Rationale: one total shows at a glance whether the requirements are met. </p> */
	private static int numPassed = 0;

	/** The number of automated tests whose actual result did not match.
	 * <p> Source: the PasswordEvaluationTestbed pattern. </p>
	 * <p> Rationale: any value above zero means a requirement is not yet met, or a test is
	 * wrong, and must be investigated before submission. </p> */
	private static int numFailed = 0;

	/** The number of tests that cannot run yet.
	 * <p> Source: the test plan includes tests for methods that do not exist yet. </p>
	 * <p> Rationale: counting them separately keeps them from being mistaken for passes or
	 * failures, and keeps them visible until they can run. </p> */
	private static int numPending = 0;

	/** The number of manual tests printed as a checklist.
	 * <p> Source: Task 6 requires both semi-automated and manual tests. </p>
	 * <p> Rationale: some requirements are about what the screen shows and can only be
	 * checked by a person using the GUI; listing them here keeps every test in one run. </p> */
	private static int numManual = 0;

	/** The test database connection used by the database tests.
	 * <p> Source: the Database class from the Foundations code. </p>
	 * <p> Rationale: the tests call the same methods the application uses, so a PASS shows
	 * the application's own code works, not a copy of it. </p> */
	private static Database db;

	/** The ids of the lessons the database tests create.
	 * <p> Rationale: cleanUp() deletes exactly these lessons at the end of a run, so every
	 * run starts from the same state and nothing else in the test database is touched. </p> */
	private static List<Integer> createdIds = new ArrayList<Integer>();

	
	/*****
	 * <p> Method: ContributorTestbed() </p>
	 *
	 * <p> Description: Not used.  The testbed is run through main() and all of its methods
	 * are static, so no object of this class is ever created. </p>
	 */
	private ContributorTestbed() {
	}
	/*****************************************************************************************
	 * Reporting helpers
	 ****************************************************************************************/

	/*****
	 * <p> Method: void check(String id, String req, String description, boolean expected,
	 * 		BooleanSupplier test) </p>
	 *
	 * <p> Description: Runs one automated test case and reports whether the actual result
	 * matched the expected result.  This is the same report format as the
	 * PasswordEvaluationTestbed.  An exception inside the test counts as a FAIL. </p>
	 *
	 * @param id			the test case ID, for example TC-C01
	 * @param req			the requirement the test case checks, for example R1
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
	 * <p> Description: Reports a test case that cannot run yet, and why, so it still appears
	 * in the output and is counted in the summary. </p>
	 *
	 * @param id			the test case ID
	 * @param req			the requirement the test case checks
	 * @param description	what the test will do
	 * @param reason		why it cannot run yet
	 */
	private static void pending(String id, String req, String description, String reason) {
		numPending++;
		System.out.println("\n" + id + " [" + req + "]: " + description);
		System.out.println("Result: PENDING (" + reason + ")");
	}

	/*****
	 * <p> Method: void manual(String id, String req, String steps, String expected) </p>
	 *
	 * <p> Description: Prints one manual test as a checklist item, to be performed through the
	 * Contributor GUI and shown in the testing screencast. </p>
	 *
	 * @param id		the test case ID
	 * @param req		the requirement the test case checks
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
	 * <p> Method: LessonsLearned newLesson(String author) </p>
	 *
	 * <p> Description: Builds a lesson with valid values in every field, as a contributor
	 * would enter it.  Tests change one field at a time from this starting point, so a
	 * rejection can only be caused by the field under test. </p>
	 *
	 * @param author the username of the contributor
	 * @return a valid lesson that has not been saved
	 */
	private static LessonsLearned newLesson(String author) {
		LessonsLearned lesson = new LessonsLearned(author, "Test lesson title",
				"Test lesson description.", "Testing");
		lesson.setRootCause("Test root cause.");
		lesson.setRecommendation("Test recommendation.");
		lesson.setKeywords("testing, junit");
		return lesson;
	}

	/*****
	 * <p> Method: String text(int length) </p>
	 *
	 * <p> Description: Builds a string of exactly the given length, for the boundary tests
	 * that check each field at its maximum length and one character over it. </p>
	 *
	 * @param length the number of characters
	 * @return a string of that many letter a characters
	 */
	private static String text(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) sb.append('a');
		return sb.toString();
	}

	/*****
	 * <p> Method: LessonsLearned saved(String author) </p>
	 *
	 * <p> Description: Saves a new valid lesson to the test database and records its id so it
	 * is deleted at the end of the run.  Used as setup by tests that need an existing lesson.
	 * </p>
	 *
	 * @param author the username of the contributor
	 * @return the saved lesson, with its database id filled in
	 */
	private static LessonsLearned saved(String author) {
		LessonsLearned lesson = newLesson(author);
		if (!db.createLessonLearned(lesson))
			throw new IllegalStateException("setup failed: could not save a valid lesson");
		createdIds.add(lesson.getId());
		return lesson;
	}

	/*****
	 * <p> Method: LessonsLearnedList sampleList() </p>
	 *
	 * <p> Description: Builds the in-memory list used by the list and search tests.  Only
	 * lesson 1 contains the word database, the word branching appears only in lesson 3's
	 * keywords, and contributorA wrote lessons 1 and 2 while contributorB wrote lesson 3. </p>
	 *
	 * @return a list of three lessons with ids 1, 2, and 3
	 */
	private static LessonsLearnedList sampleList() {
		LessonsLearned l1 = new LessonsLearned("contributorA", "Back up the database",
				"Restore failed because the database backup was never tested.", "Data");
		l1.setId(1);
		LessonsLearned l2 = new LessonsLearned("contributorA", "UI froze on large lists",
				"Loading every row at once froze the screen.", "GUI");
		l2.setId(2);
		LessonsLearned l3 = new LessonsLearned("contributorB", "Merge conflicts before the deadline",
				"Two people edited the same file the night before.", "Process");
		l3.setId(3);
		l3.setKeywords("git, branching");

		LessonsLearnedList list = new LessonsLearnedList("No lessons match your search.");
		list.add(l1);
		list.add(l2);
		list.add(l3);
		return list;
	}


	/*****************************************************************************************
	 * R1, R2, R10: Validation tests (no database needed)
	 ****************************************************************************************/

	/*****
	 * <p> TC-C02 [R1, Negative]: Create a lesson with the title left blank. </p>
	 *
	 * <p> Story C1: "I can add new lessons learned to the system, specifying core
	 * information."  The title is core information, so a lesson without one is incomplete. </p>
	 *
	 * <p> Expected: Rejected.  A PASS shows validate() refuses a lesson with no title, so
	 * createLessonLearned() never saves one. </p>
	 */
	private static void testBlankTitle() {
		check("TC-C02", "R1", "Create a lesson with the title left blank", false, () ->
			new LessonsLearned("contributorA", "", "Description", "Testing").validate().isEmpty());
	}

	/*****
	 * <p> TC-C03 [R1, Negative]: Create a lesson whose title is only spaces. </p>
	 *
	 * <p> Story C1: core information is required.  A title of spaces looks filled in on the
	 * screen but holds no information. </p>
	 *
	 * <p> Expected: Rejected.  A PASS shows the setters trim the spaces, so the title counts as
	 * blank and is refused like TC-C02. </p>
	 */
	private static void testSpacesOnlyTitle() {
		check("TC-C03", "R1", "Create a lesson whose title is only spaces", false, () ->
			new LessonsLearned("contributorA", "   ", "Description", "Testing").validate().isEmpty());
	}

	/*****
	 * <p> TC-C04 [R1, Negative]: Create a lesson with the description left blank. </p>
	 *
	 * <p> Story C1: the description is core information; without it the lesson teaches
	 * nothing. </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testBlankDescription() {
		check("TC-C04", "R1", "Create a lesson with the description left blank", false, () ->
			new LessonsLearned("contributorA", "Title", "", "Testing").validate().isEmpty());
	}

	/*****
	 * <p> TC-C05 [R1, Negative]: Create a lesson with no author. </p>
	 *
	 * <p> Stories C2 and C4 depend on knowing who wrote each lesson: a contributor sees only
	 * their own lessons.  A lesson with no author could never be shown to anyone. </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testBlankAuthor() {
		check("TC-C05", "R1", "Create a lesson with no author", false, () ->
			new LessonsLearned("", "Title", "Description", "Testing").validate().isEmpty());
	}

	/*****
	 * <p> TC-C06 [R2, Positive]: Fill every field to its exact maximum length. </p>
	 *
	 * <p> Limits come from the lessonLearnedDB columns: title 100, description 2000, root cause
	 * 2000, recommendation 2000, keywords 1000, category 100. </p>
	 *
	 * <p> Expected: Success.  A PASS shows the limits are inclusive, so a contributor can use
	 * the full space each field allows. </p>
	 */
	private static void testAllFieldsAtMax() {
		check("TC-C06", "R2", "Fill every field to its exact maximum length", true, () -> {
			LessonsLearned l = new LessonsLearned("contributorA", text(100), text(2000), text(100));
			l.setRootCause(text(2000));
			l.setRecommendation(text(2000));
			l.setKeywords(text(1000));
			return l.validate().isEmpty();
		});
	}

	/*****
	 * <p> TC-C07 [R2, Negative]: Enter a title one character too long (101). </p>
	 *
	 * <p> Expected: Rejected.  Together with TC-C06, a PASS shows the title limit is exactly
	 * 100, so the database column can never overflow. </p>
	 */
	private static void testTitleOverMax() {
		check("TC-C07", "R2", "Enter a title one character too long (101)", false, () ->
			new LessonsLearned("contributorA", text(101), "Description", "Testing")
				.validate().isEmpty());
	}

	/*****
	 * <p> TC-C08 [R2, Negative]: Enter a description one character too long (2001). </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testDescriptionOverMax() {
		check("TC-C08", "R2", "Enter a description one character too long (2001)", false, () ->
			new LessonsLearned("contributorA", "Title", text(2001), "Testing")
				.validate().isEmpty());
	}

	/*****
	 * <p> TC-C09 [R2, Negative]: Enter a root cause one character too long (2001). </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testRootCauseOverMax() {
		check("TC-C09", "R2", "Enter a root cause one character too long (2001)", false, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setRootCause(text(2001));			// Only this field is invalid
			return l.validate().isEmpty();
		});
	}

	/*****
	 * <p> TC-C10 [R2, Negative]: Enter a recommendation one character too long (2001). </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testRecommendationOverMax() {
		check("TC-C10", "R2", "Enter a recommendation one character too long (2001)", false, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setRecommendation(text(2001));		// Only this field is invalid
			return l.validate().isEmpty();
		});
	}

	/*****
	 * <p> TC-C11 [R2, Negative]: Enter keywords one character too long (1001). </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testKeywordsOverMax() {
		check("TC-C11", "R2", "Enter keywords one character too long (1001)", false, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setKeywords(text(1001));			// Only this field is invalid
			return l.validate().isEmpty();
		});
	}

	/*****
	 * <p> TC-C12 [R2, Negative]: Enter a category one character too long (101). </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testCategoryOverMax() {
		check("TC-C12", "R2", "Enter a category one character too long (101)", false, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setCategory(text(101));				// Only this field is invalid
			return l.validate().isEmpty();
		});
	}

	/*****
	 * <p> TC-C31 [R10, Negative]: Add time and effort information that is 256 characters
	 * long. </p>
	 *
	 * <p> Story C3: "I can add experience information (e.g., time and effort data) to an
	 * existing lesson."  The timeEffort column holds at most 255 characters. </p>
	 *
	 * <p> Expected: Rejected. </p>
	 */
	private static void testTimeEffortOverMax() {
		check("TC-C31", "R10", "Add time and effort info that is 256 characters long", false, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setTimeEffort(text(256));			// Only this field is invalid
			return l.validate().isEmpty();
		});
	}


	/*****************************************************************************************
	 * R8, R11, R14: List tests (no database needed)
	 ****************************************************************************************/

	/*****
	 * <p> TC-C26 [R8, Positive]: A contributor's list shows exactly their own lessons. </p>
	 *
	 * <p> Story C2: "I can see a list of all the lessons learned I have added to the system."
	 * </p>
	 *
	 * <p> Input: the sample list, where contributorA wrote lessons 1 and 2.  Expected: Success,
	 * meaning subsetByAuthor returns exactly lessons 1 and 2. </p>
	 */
	private static void testListShowsOwnLessons() {
		check("TC-C26", "R8", "contributorA's list shows exactly A's 2 lessons", true, () -> {
			LessonsLearnedList mine = sampleList().subsetByAuthor("contributorA",
					"You have not added any lessons yet.");
			return mine.size() == 2 && mine.findById(1) != null && mine.findById(2) != null;
		});
	}

	/*****
	 * <p> TC-C33 [R11, Negative]: Another contributor's lessons do not appear in the list. </p>
	 *
	 * <p> Story C4: "I am unable to see submissions from other contributors." </p>
	 *
	 * <p> Input: the sample list, where contributorB wrote lesson 3.  Expected: Rejected,
	 * meaning lesson 3 is not in contributorA's subset. </p>
	 */
	private static void testOtherContributorsHidden() {
		check("TC-C33", "R11", "contributorB's lesson does not appear in A's list", false, () ->
			sampleList().subsetByAuthor("contributorA", "").findById(3) != null);
	}

	/*****
	 * <p> TC-C40 [R14, Positive]: Search for a word that appears in some lessons. </p>
	 *
	 * <p> Task 3 requires subsets of all lessons, such as the lessons containing a keyword.
	 * </p>
	 *
	 * <p> Input: database, which appears only in lesson 1.  Expected: Success, meaning the
	 * result holds lesson 1 and nothing else. </p>
	 */
	private static void testKeywordMatch() {
		check("TC-C40", "R14", "Search for a word in some lessons (\"database\")", true, () -> {
			LessonsLearnedList result = sampleList().subsetByKeyword("database", "");
			return result.size() == 1 && result.findById(1) != null;
		});
	}

	/*****
	 * <p> TC-C41 [R14, Negative]: Search for a word that appears in no lesson. </p>
	 *
	 * <p> Expected: Rejected, meaning the result is empty, and the list's empty message is
	 * there for the GUI to show. </p>
	 */
	private static void testKeywordNoMatch() {
		check("TC-C41", "R14", "Search for a word in no lesson (\"zzzz\")", false, () -> {
			LessonsLearnedList result = sampleList().subsetByKeyword("zzzz",
					"No lessons match your search.");
			// Found anything, or lost the message the GUI needs for an empty result
			return result.size() > 0 || result.getStatusMessage().isEmpty();
		});
	}

	/*****
	 * <p> TC-C42 [R14, Positive]: Search in capitals. </p>
	 *
	 * <p> Expected: Success, meaning DATABASE finds the same lesson as database.  A contributor
	 * should not have to guess the capitalization used in a lesson. </p>
	 */
	private static void testKeywordIgnoresCase() {
		check("TC-C42", "R14", "Search for \"DATABASE\" in capitals", true, () -> {
			LessonsLearnedList result = sampleList().subsetByKeyword("DATABASE", "");
			return result.size() == 1 && result.findById(1) != null;
		});
	}

	/*****
	 * <p> TC-C43 [R14, Positive]: Search with an empty search box. </p>
	 *
	 * <p> Expected: Success, meaning all three lessons are returned, so clearing the search
	 * shows the whole list again. </p>
	 */
	private static void testBlankSearchReturnsAll() {
		check("TC-C43", "R14", "Search with an empty search box", true, () ->
			sampleList().subsetByKeyword("", "").size() == 3);
	}

	/*****
	 * <p> TC-C44 [R14, Positive]: Search for a word that only appears in a lesson's keywords.
	 * </p>
	 *
	 * <p> The keywords field exists so that lessons can be found by search.  Input: branching,
	 * which appears only in lesson 3's keywords.  Expected: Success, meaning lesson 3 is found.
	 * </p>
	 *
	 * <p> Known failure: subsetByKeyword() searches only the title and description, so this
	 * test fails until the keywords field is added to the search. </p>
	 */
	private static void testSearchKeywordsField() {
		check("TC-C44", "R14", "Search for a word only in a lesson's keywords (\"branching\")",
				true, () -> sampleList().subsetByKeyword("branching", "").findById(3) != null);
	}

	/*****
	 * <p> TC-C45 [R14, Positive]: Look up a lesson in the list by an ID that exists. </p>
	 *
	 * <p> Story C2: the contributor uses the list "to specify which lesson learned to examine
	 * more closely."  The GUI uses findById to get the lesson behind the selected row. </p>
	 *
	 * <p> Expected: Success. </p>
	 */
	private static void testFindByIdExists() {
		check("TC-C45", "R14", "Look up a lesson by an ID that exists", true, () ->
			sampleList().findById(2) != null);
	}

	/*****
	 * <p> TC-C46 [R14, Negative]: Look up a lesson in the list by an ID that does not exist.
	 * </p>
	 *
	 * <p> Expected: Rejected, meaning null is returned and nothing crashes. </p>
	 */
	private static void testFindByIdMissing() {
		check("TC-C46", "R14", "Look up a lesson by an ID that doesn't exist", false, () ->
			sampleList().findById(99999) != null);
	}


	/*****************************************************************************************
	 * R1, R3 to R7, R10, R12 to R14: Database tests
	 ****************************************************************************************/

	/*****
	 * <p> TC-C01 [R1, Positive]: Create a lesson with every field filled in correctly. </p>
	 *
	 * <p> Story C1: "I can add new lessons learned to the system... Once a lesson has been
	 * added to the system, I can see and manage it." </p>
	 *
	 * <p> Expected: Success, meaning createLessonLearned() returns true and the lesson gets a
	 * database id greater than zero. </p>
	 */
	private static void testCreateValidLesson() {
		check("TC-C01", "R1", "Create a lesson with all required fields filled in", true, () -> {
			LessonsLearned l = newLesson("contributorA");
			boolean ok = db.createLessonLearned(l);
			if (ok) createdIds.add(l.getId());		// So cleanUp() removes it
			return ok && l.getId() > 0;
		});
	}

	/*****
	 * <p> TC-C13 [R3, Positive]: Save a lesson, close the database, reopen it, and read the
	 * lesson back. </p>
	 *
	 * <p> Task 3 requires a persistent class: lessons must survive the program closing. </p>
	 *
	 * <p> Expected: Success, meaning every field comes back exactly as saved, including the
	 * locked field and the effort visibility setting a curator controls. </p>
	 */
	private static void testLessonSurvivesRestart() {
		check("TC-C13", "R3", "Save, close the database, reopen it, and read the lesson back",
				true, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setTimeEffort("3 hours, 2 people");
			l.setFieldLocked("title", true);
			l.setEffortVisible(false);
			if (!db.createLessonLearned(l)) return false;
			createdIds.add(l.getId());

			db.closeConnection();					// Simulate the program closing
			connect();								// and starting again

			LessonsLearned r = db.getLessonLearned(l.getId());
			return r != null
					&& r.getTitle().equals(l.getTitle())
					&& r.getDescription().equals(l.getDescription())
					&& r.getAuthor().equals(l.getAuthor())
					&& r.getCategory().equals(l.getCategory())
					&& r.getRootCause().equals(l.getRootCause())
					&& r.getRecommendation().equals(l.getRecommendation())
					&& r.getKeywords().equals(l.getKeywords())
					&& r.getTimeEffort().equals(l.getTimeEffort())
					&& r.isFieldLocked("title")
					&& !r.getEffortVisible();
		});
	}

	/*****
	 * <p> TC-C14 [R3, Negative]: Ask for a lesson ID that does not exist. </p>
	 *
	 * <p> Expected: Rejected, meaning getLessonLearned() returns null instead of crashing or
	 * returning the wrong lesson. </p>
	 */
	private static void testReadMissingLesson() {
		check("TC-C14", "R3", "Ask for a lesson ID that doesn't exist", false, () ->
			db.getLessonLearned(999999) != null);
	}

	/*****
	 * <p> TC-C15 [R4, Positive]: Change a lesson's description and save it. </p>
	 *
	 * <p> Story C1: "I can see and manage it."  Managing a lesson includes changing it. </p>
	 *
	 * <p> Expected: Success, meaning the new description is what the database returns. </p>
	 */
	private static void testUpdateDescription() {
		check("TC-C15", "R4", "Change a lesson's description and save it", true, () -> {
			LessonsLearned l = saved("contributorA");
			l.setDescription("Updated description.");
			return db.updateLessonLearned(l)
					&& db.getLessonLearned(l.getId()).getDescription()
						.equals("Updated description.");
		});
	}

	/*****
	 * <p> TC-C16 [R4, Negative]: Change a lesson's title to blank and save it. </p>
	 *
	 * <p> Expected: Rejected, meaning the update is refused and the old title is still in the
	 * database.  Updates are validated the same way as new lessons. </p>
	 */
	private static void testUpdateToInvalidValue() {
		check("TC-C16", "R4", "Change a lesson's title to blank and save it", false, () -> {
			LessonsLearned l = saved("contributorA");
			l.setTitle("");
			boolean accepted = db.updateLessonLearned(l);
			boolean titleChanged = db.getLessonLearned(l.getId()).getTitle().isEmpty();
			return accepted || titleChanged;		// Either one means the bad value got in
		});
	}

	/*****
	 * <p> TC-C17 [R4, Negative]: Try to update a lesson that was never saved. </p>
	 *
	 * <p> Expected: Rejected.  A lesson with no database id has nothing to update. </p>
	 */
	private static void testUpdateUnsavedLesson() {
		check("TC-C17", "R4", "Try to update a lesson that was never saved", false, () ->
			db.updateLessonLearned(newLesson("contributorA")));
	}

	/*****
	 * <p> TC-C18 [R5, Negative]: Lock the title, then have the contributor change it. </p>
	 *
	 * <p> Story C1: "Other roles may lock one or more fields, so I am unable to alter them."
	 * There is no curator screen yet, so the test locks the field directly, as a curator
	 * would, before the contributor's change. </p>
	 *
	 * <p> Expected: Rejected, meaning the title in the database is unchanged. </p>
	 *
	 * <p> Known failure: updateLessonLearned() does not check locked fields yet. </p>
	 */
	private static void testEditLockedField() {
		check("TC-C18", "R5", "Lock the title, then have the contributor change it", false, () -> {
			LessonsLearned l = saved("contributorA");
			l.setFieldLocked("title", true);			// Curator locks the title
			db.updateLessonLearned(l);
			l.setTitle("Contributor changed the title");	// Contributor tries to change it
			db.updateLessonLearned(l);
			return db.getLessonLearned(l.getId()).getTitle()
					.equals("Contributor changed the title");
		});
	}

	/*****
	 * <p> TC-C19 [R5, Positive]: Lock the title, then have the contributor change the
	 * description. </p>
	 *
	 * <p> Story C1 locks only the fields another role chose.  The rest of the lesson stays
	 * editable. </p>
	 *
	 * <p> Expected: Success, meaning one locked field does not block changes to the others.
	 * </p>
	 */
	private static void testEditUnlockedFieldOfPartlyLockedLesson() {
		check("TC-C19", "R5", "Lock the title, then have the contributor change the description",
				true, () -> {
			LessonsLearned l = saved("contributorA");
			l.setFieldLocked("title", true);			// Curator locks only the title
			db.updateLessonLearned(l);
			l.setDescription("Contributor changed the description.");
			return db.updateLessonLearned(l)
					&& db.getLessonLearned(l.getId()).getDescription()
						.equals("Contributor changed the description.");
		});
	}

	/*****
	 * <p> TC-C21 [R6, Positive]: Delete an unlocked lesson. </p>
	 *
	 * <p> Story C1: "I can see and manage it."  Managing includes deleting a lesson nobody has
	 * locked. </p>
	 *
	 * <p> Expected: Success, meaning the delete returns true and the lesson can no longer be
	 * read. </p>
	 */
	private static void testDeleteUnlockedLesson() {
		check("TC-C21", "R6", "Delete an unlocked lesson", true, () -> {
			LessonsLearned l = saved("contributorA");
			return db.deleteLessonLearned(l.getId()) && db.getLessonLearned(l.getId()) == null;
		});
	}

	/*****
	 * <p> TC-C22 [R6, Negative]: Delete a lesson that does not exist. </p>
	 *
	 * <p> Expected: Rejected, meaning false is returned and nothing else is deleted. </p>
	 */
	private static void testDeleteMissingLesson() {
		check("TC-C22", "R6", "Delete a lesson that doesn't exist", false, () ->
			db.deleteLessonLearned(999999));
	}

	/*****
	 * <p> TC-C24 [R7, Negative]: Lock a whole lesson, then have the contributor delete it.
	 * </p>
	 *
	 * <p> Story C2: "When a lesson I created is locked or has a locked field, I am unable to
	 * delete that lesson." </p>
	 *
	 * <p> Expected: Rejected.  Known failure: deleteLessonLearned() does not check locks yet.
	 * </p>
	 */
	private static void testDeleteLockedLesson() {
		check("TC-C24", "R7", "Lock a whole lesson, then have the contributor delete it", false, () -> {
			LessonsLearned l = saved("contributorA");
			l.setIsLocked(true);						// Curator locks the lesson
			db.updateLessonLearned(l);
			return db.deleteLessonLearned(l.getId());	// Contributor tries to delete it
		});
	}

	/*****
	 * <p> TC-C25 [R7, Negative]: Lock one field, then have the contributor delete the lesson.
	 * </p>
	 *
	 * <p> Story C2: a lesson that "has a locked field" cannot be deleted either. </p>
	 *
	 * <p> Expected: Rejected.  Known failure: deleteLessonLearned() does not check locks yet.
	 * </p>
	 */
	private static void testDeleteLessonWithLockedField() {
		check("TC-C25", "R7", "Lock one field, then have the contributor delete the lesson",
				false, () -> {
			LessonsLearned l = saved("contributorA");
			l.setFieldLocked("title", true);			// Curator locks one field
			db.updateLessonLearned(l);
			return db.deleteLessonLearned(l.getId());	// Contributor tries to delete it
		});
	}

	/*****
	 * <p> TC-C30 [R10, Positive]: Add time and effort information to a saved lesson. </p>
	 *
	 * <p> Story C3: "I can add experience information (e.g., time and effort data) to an
	 * existing lesson."  Input: 255 characters, the maximum. </p>
	 *
	 * <p> Expected: Success, meaning all 255 characters are stored with the lesson. </p>
	 */
	private static void testAddExperienceInfo() {
		check("TC-C30", "R10", "Add time and effort info (255 characters) to a lesson", true, () -> {
			LessonsLearned l = saved("contributorA");
			l.setTimeEffort(text(255));
			return db.updateLessonLearned(l)
					&& db.getLessonLearned(l.getId()).getTimeEffort().length() == 255;
		});
	}

	/*****
	 * <p> TC-C32 [R10, Negative]: Add time and effort information to a lesson that does not
	 * exist. </p>
	 *
	 * <p> Story C3 says experience information is added to an existing lesson. </p>
	 *
	 * <p> Expected: Rejected, meaning no row is changed. </p>
	 */
	private static void testExperienceOnMissingLesson() {
		check("TC-C32", "R10", "Add time and effort info to a lesson that doesn't exist",
				false, () -> {
			LessonsLearned l = newLesson("contributorA");
			l.setId(999999);							// An id no lesson has
			l.setTimeEffort("2 hours");
			return db.updateLessonLearned(l);
		});
	}

	/*****
	 * <p> TC-C35 [R12, Positive]: Turn effort visibility off, save, and read it back. </p>
	 *
	 * <p> Curator epic: "I can make the statistically useful time and effort information I add
	 * visible or not to the contributor."  The setting must be stored so the Contributor
	 * screen can honor it (manual tests TC-C36 and TC-C37). </p>
	 *
	 * <p> Expected: Success, meaning the setting reads back as off. </p>
	 */
	private static void testEffortVisibilitySaved() {
		check("TC-C35", "R12", "Turn effort visibility off, save, and read it back", true, () -> {
			LessonsLearned l = saved("contributorA");
			l.setEffortVisible(false);
			db.updateLessonLearned(l);
			return !db.getLessonLearned(l.getId()).getEffortVisible();
		});
	}

	/*****
	 * <p> TC-C38 [R13, Positive]: A curator changes the description, then the contributor
	 * reads the lesson. </p>
	 *
	 * <p> Story C4: "I can only see the content I have added or the content that I have added
	 * after a curator has edited it." </p>
	 *
	 * <p> Expected: Success, meaning the contributor sees the curator's version. </p>
	 */
	private static void testSeesCuratorEdit() {
		check("TC-C38", "R13", "A curator changes the description, then the contributor reads it",
				true, () -> {
			LessonsLearned l = saved("contributorA");
			LessonsLearned curatorCopy = db.getLessonLearned(l.getId());	// Curator's view
			curatorCopy.setDescription("Curator's clearer description.");
			db.updateLessonLearned(curatorCopy);
			return db.getLessonLearned(l.getId()).getDescription()			// Contributor's view
					.equals("Curator's clearer description.");
		});
	}

	/*****
	 * <p> TC-C39 [R14, Positive]: Ask the database for the list of all lessons. </p>
	 *
	 * <p> Task 3 requires "a persistent list of all the lessons learned in the system." </p>
	 *
	 * <p> Expected: Success, meaning lessons from two different contributors are both in the
	 * list returned by getAllLessonsLearned(). </p>
	 */
	private static void testListAllLessons() {
		check("TC-C39", "R14", "Ask for the list of all lessons", true, () -> {
			LessonsLearned a = saved("contributorA");
			LessonsLearned b = saved("contributorB");
			LessonsLearnedList all = db.getAllLessonsLearned();
			return all.findById(a.getId()) != null && all.findById(b.getId()) != null;
		});
	}


	/*****************************************************************************************
	 * Pending and manual tests
	 ****************************************************************************************/

	/*****
	 * <p> TC-C34 [R11, Negative]: A contributor asks for another contributor's lesson by its
	 * ID. </p>
	 *
	 * <p> Story C4: "I am unable to see submissions from other contributors."  Expected:
	 * Rejected.  Pending until there is a read method that knows which user is asking. </p>
	 */
	private static void testOpenOtherContributorsLesson() {
		pending("TC-C34", "R11", "A asks for one of B's lessons by its ID; access is denied",
				"needs a read method that checks the requesting user");
	}

	/*****
	 * <p> Method: void printManualTests() </p>
	 *
	 * <p> Description: Prints the manual tests from the test plan as a checklist.  These check
	 * what the Contributor screens show and are performed by hand through the GUI. </p>
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
	 * <p> Description: Opens the test database at TEST_DB_URL. </p>
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
	 * <p> Description: Deletes every lesson the database tests created, so each run starts
	 * from the same state.  Locks are removed first, so cleanup still works after the lock
	 * checks are added to delete. </p>
	 */
	private static void cleanUp() {
		for (int id : createdIds) {
			LessonsLearned l = db.getLessonLearned(id);
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
	 * <p> Description: Runs every test in the order of the test plan's groups, prints the
	 * manual checklist, and prints the summary. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("========== R1, R2, R10: Required fields and length limits ==========");
		testBlankTitle();
		testSpacesOnlyTitle();
		testBlankDescription();
		testBlankAuthor();
		testAllFieldsAtMax();
		testTitleOverMax();
		testDescriptionOverMax();
		testRootCauseOverMax();
		testRecommendationOverMax();
		testKeywordsOverMax();
		testCategoryOverMax();
		testTimeEffortOverMax();

		System.out.println("\n========== R8, R11, R14: Lesson lists and keyword search ==========");
		testListShowsOwnLessons();
		testOtherContributorsHidden();
		testKeywordMatch();
		testKeywordNoMatch();
		testKeywordIgnoresCase();
		testBlankSearchReturnsAll();
		testSearchKeywordsField();
		testFindByIdExists();
		testFindByIdMissing();

		System.out.println("\n========== Database tests (R1, R3-R7, R10, R12-R14) ==========");
		if (TEST_DB_IS_SAFE) {
			connect();
			try {
				testCreateValidLesson();
				testLessonSurvivesRestart();
				testReadMissingLesson();
				testUpdateDescription();
				testUpdateToInvalidValue();
				testUpdateUnsavedLesson();
				testEditLockedField();
				testEditUnlockedFieldOfPartlyLockedLesson();
				testDeleteUnlockedLesson();
				testDeleteMissingLesson();
				testDeleteLockedLesson();
				testDeleteLessonWithLockedField();
				testAddExperienceInfo();
				testExperienceOnMissingLesson();
				testEffortVisibilitySaved();
				testSeesCuratorEdit();
				testListAllLessons();
			} finally {
				cleanUp();
				db.closeConnection();
			}
		} else {
			pending("TC-C01 to TC-C39", "R1-R14", "17 database tests",
					"TEST_DB_IS_SAFE is false");
		}

		System.out.println("\n========== Waiting on new methods ==========");
		testOpenOtherContributorsLesson();

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