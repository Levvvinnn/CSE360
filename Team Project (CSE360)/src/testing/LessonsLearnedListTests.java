package testing;

import java.sql.Timestamp;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: LessonsLearnedListTests Class </p>
 * 
 * <p> Description: Automated tests for the LessonsLearnedList entity class: the complete
 * collection of lessons and the subsets built from it.  Each public test method checks one
 * requirement (LIST-01 to LIST-14), prints one PASS or FAIL line followed by the expected and
 * actual values, and the run ends with the totals.  Like LessonTestAutomation, this is a plain
 * Java application: run it and read the console. </p>
 * 
 * <p> Design note: LIST-01 to LIST-12 and LIST-14 build their lists in memory, so they test
 * LessonsLearnedList by itself and do not depend on the database.  LIST-13 is the one test that
 * uses a database, because its requirement is about the complete persistent collection; it uses
 * its own database (TP2ListTestDatabase) so it never touches the application's data or the
 * HW2 test database.  The test methods are public and documented so that each one appears in
 * the generated Javadoc and can be traced to its requirement. </p>
 * 
 * <p> Reading the output: a line starting with PASS means the actual values matched every
 * expected value listed on the next line; FAIL means at least one did not.  A run with
 * "Failed: 0" shows that every LIST requirement is satisfied. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Levin Shibu
 * 
 * @version 1.00		2026-10-25 Initial version for TP2
 */
public class LessonsLearnedListTests {
	
	private static final String TEST_DB_URL = "jdbc:h2:~/TP2ListTestDatabase";
	
	// The empty messages used by the tests (the same text LessonManager uses)
	private static final String NO_LESSONS = "You have not added any lessons yet.";
	private static final String NO_MATCH = "No lessons match your search.";
	
	private static int passed = 0;
	private static int failed = 0;
	
	/**
	 * Default constructor is not used.
	 */
	private LessonsLearnedListTests() {
	}
	
	/**********
	 * <p> Method: main(String[] args) </p>
	 * 
	 * <p> Description: Runs every test in requirement order and prints the totals. </p>
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("LessonsLearnedList tests\n");
		testList01EmptyList();
		testList02AddOneLesson();
		testList03AddNullIgnored();
		testList04ManyLessonsNoMaximum();
		testList05FindByIdExisting();
		testList06FindByIdNonexistent();
		testList07SubsetByAuthorMatches();
		testList08SubsetByAuthorNoMatch();
		testList09KeywordMatchesTitleIgnoringCase();
		testList10KeywordMatchesDescriptionIgnoringCase();
		testList11KeywordNoMatch();
		testList12CopyConstructorIndependent();
		testList13CompleteCollectionAllContributors();
		testList14KeywordDoesNotSearchAuthor();
		System.out.println("\nPassed: " + passed + "   Failed: " + failed);
	}
	
	/*-*******************************************************************************************
	
	Helpers
	
	*/
	
	// Print one result line, the expected and actual values, and keep the totals
	private static void report(String id, String what, boolean ok, String expected,
			String actual) {
		if (ok) passed++; else failed++;
		System.out.println((ok ? "PASS " : "FAIL ") + id + "  " + what);
		System.out.println("      Expected: " + expected);
		System.out.println("      Actual:   " + actual);
	}
	
	// Build a lesson as it would be read back from the database, with a known id
	private static LessonsLearned lesson(int id, String author, String title, String description,
			String category) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		return new LessonsLearned(id, author, title, description, category, now, now);
	}
	
	// A small collection from two contributors, used by the subset tests
	private static LessonsLearnedList sampleCollection() {
		LessonsLearnedList all = new LessonsLearnedList(NO_LESSONS);
		all.add(lesson(1, "alice", "JavaFX module path setup",
				"Add the JavaFX lib folder to the module path before running.", "Setup"));
		all.add(lesson(2, "bob", "H2 database file location",
				"The H2 database file is created in the user's home folder.", "Setup"));
		all.add(lesson(3, "alice", "Write the test before the fix",
				"A failing test showed the off-by-one error in the search loop.", "Testing"));
		all.add(lesson(4, "carol", "Agree on stand-up times",
				"Pick one stand-up time that works for every time zone.", "Teamwork"));
		return all;
	}
	
	/*-*******************************************************************************************
	
	Test cases
	
	*/
	
	/**********
	 * <p> Method: testList01EmptyList() </p>
	 * 
	 * <p> Requirement LIST-01: an empty collection is a valid list (a contributor may have zero
	 * lessons). </p>
	 * 
	 * <p> Approach: create a list and add nothing; check size, isEmpty, getAll and the empty
	 * message. </p>
	 * 
	 * <p> PASS means the list reports size 0, isEmpty true, an empty getAll, and the empty
	 * message is shown.  FAIL means an empty list is not handled correctly. </p>
	 */
	public static void testList01EmptyList() {
		LessonsLearnedList list = new LessonsLearnedList(NO_LESSONS);
		boolean ok = list.size() == 0 && list.isEmpty() && list.getAll().isEmpty()
				&& list.getStatusMessage().equals(NO_LESSONS);
		report("LIST-01", "Empty list is valid", ok,
				"size=0, isEmpty=true, message=\"" + NO_LESSONS + "\"",
				"size=" + list.size() + ", isEmpty=" + list.isEmpty() + ", message=\""
						+ list.getStatusMessage() + "\"");
	}
	
	/**********
	 * <p> Method: testList02AddOneLesson() </p>
	 * 
	 * <p> Requirement LIST-02: a list can hold exactly one lesson. </p>
	 * 
	 * <p> Approach: add one valid lesson to an empty list and read it back with get(0). </p>
	 * 
	 * <p> PASS means the size is 1, the lesson at position 0 is the one added, and no empty
	 * message is shown.  FAIL means a one-lesson list is not handled correctly. </p>
	 */
	public static void testList02AddOneLesson() {
		LessonsLearnedList list = new LessonsLearnedList(NO_LESSONS);
		LessonsLearned only = lesson(1, "alice", "JavaFX module path setup",
				"Add the JavaFX lib folder to the module path before running.", "Setup");
		list.add(only);
		boolean ok = list.size() == 1 && !list.isEmpty() && list.get(0) == only
				&& list.getStatusMessage().isEmpty();
		report("LIST-02", "Adding one valid lesson produces size 1", ok,
				"size=1, get(0)=\"JavaFX module path setup\", message=\"\"",
				"size=" + list.size() + ", get(0)=\""
						+ (list.size() > 0 ? list.get(0).getTitle() : "none") + "\", message=\""
						+ list.getStatusMessage() + "\"");
	}
	
	/**********
	 * <p> Method: testList03AddNullIgnored() </p>
	 * 
	 * <p> Requirement LIST-03 (negative): a null lesson is never added, so every element of a
	 * list is a real lesson. </p>
	 * 
	 * <p> Approach: add null to an empty list, then add null to a list that holds one lesson.
	 * </p>
	 * 
	 * <p> PASS means both sizes are unchanged (0 and 1) and the empty list still shows its empty
	 * message.  FAIL means a null element was stored. </p>
	 */
	public static void testList03AddNullIgnored() {
		LessonsLearnedList empty = new LessonsLearnedList(NO_LESSONS);
		empty.add(null);
		LessonsLearnedList one = new LessonsLearnedList(NO_LESSONS);
		one.add(lesson(1, "alice", "JavaFX module path setup", "Add the lib folder.", "Setup"));
		one.add(null);
		boolean ok = empty.size() == 0 && empty.getStatusMessage().equals(NO_LESSONS)
				&& one.size() == 1;
		report("LIST-03", "Adding null does not add an element", ok,
				"empty list size=0, one-lesson list size=1",
				"empty list size=" + empty.size() + ", one-lesson list size=" + one.size());
	}
	
	/**********
	 * <p> Method: testList04ManyLessonsNoMaximum() </p>
	 * 
	 * <p> Requirement LIST-04: a list can hold many lessons with no fixed artificial maximum.
	 * </p>
	 * 
	 * <p> Approach: add 10,000 lessons (ten times the 1,000 used by TC-13) and check the size
	 * and that the first and last lessons are in the order they were added. </p>
	 * 
	 * <p> PASS means all 10,000 are kept in order.  FAIL means the list dropped lessons or has
	 * a limit. </p>
	 */
	public static void testList04ManyLessonsNoMaximum() {
		final int count = 10000;
		LessonsLearnedList list = new LessonsLearnedList(NO_LESSONS);
		for (int i = 1; i <= count; i++)
			list.add(lesson(i, "alice", "Sprint " + i + " retrospective note",
					"What went well and what to change in sprint " + i + ".", "Teamwork"));
		boolean ok = list.size() == count && list.get(0).getId() == 1
				&& list.get(count - 1).getId() == count;
		report("LIST-04", "A list can contain many lessons without a fixed maximum", ok,
				"size=" + count + ", first id=1, last id=" + count,
				"size=" + list.size() + ", first id=" + list.get(0).getId() + ", last id="
						+ list.get(list.size() - 1).getId());
	}
	
	/**********
	 * <p> Method: testList05FindByIdExisting() </p>
	 * 
	 * <p> Requirement LIST-05: findById finds a lesson that is in the list. </p>
	 * 
	 * <p> Approach: search the sample collection for id 3. </p>
	 * 
	 * <p> PASS means the lesson returned has id 3 and the expected title.  FAIL means the wrong
	 * lesson, or no lesson, was returned. </p>
	 */
	public static void testList05FindByIdExisting() {
		LessonsLearned found = sampleCollection().findById(3);
		boolean ok = found != null && found.getId() == 3
				&& found.getTitle().equals("Write the test before the fix");
		report("LIST-05", "findById finds an existing lesson", ok,
				"id=3, title=\"Write the test before the fix\"",
				(found == null) ? "null" : "id=" + found.getId() + ", title=\""
						+ found.getTitle() + "\"");
	}
	
	/**********
	 * <p> Method: testList06FindByIdNonexistent() </p>
	 * 
	 * <p> Requirement LIST-06 (negative): findById reports a missing lesson instead of failing.
	 * </p>
	 * 
	 * <p> Approach: search the sample collection for id 9999, and an empty list for id 1. </p>
	 * 
	 * <p> PASS means both searches return null without an exception.  FAIL means a lesson was
	 * returned that is not in the list. </p>
	 */
	public static void testList06FindByIdNonexistent() {
		LessonsLearned fromSample = sampleCollection().findById(9999);
		LessonsLearned fromEmpty = new LessonsLearnedList(NO_LESSONS).findById(1);
		boolean ok = fromSample == null && fromEmpty == null;
		report("LIST-06", "findById handles a nonexistent id", ok,
				"null for id 9999, null for an empty list",
				fromSample + " for id 9999, " + fromEmpty + " for an empty list");
	}
	
	/**********
	 * <p> Method: testList07SubsetByAuthorMatches() </p>
	 * 
	 * <p> Requirement LIST-07: subsetByAuthor returns only the given contributor's lessons, in
	 * their original order, and leaves the complete collection unchanged. </p>
	 * 
	 * <p> Approach: take alice's subset (ids 1 and 3) and carol's subset (id 4) of the sample
	 * collection of four lessons. </p>
	 * 
	 * <p> PASS means alice gets ids 1 and 3 (many), carol gets id 4 (one), and the collection
	 * still has 4 lessons.  FAIL means a lesson was missed, a lesson from another contributor
	 * was included, or the collection was changed. </p>
	 */
	public static void testList07SubsetByAuthorMatches() {
		LessonsLearnedList all = sampleCollection();
		LessonsLearnedList alice = all.subsetByAuthor("alice", NO_LESSONS);
		LessonsLearnedList carol = all.subsetByAuthor("carol", NO_LESSONS);
		boolean ok = alice.size() == 2 && alice.get(0).getId() == 1 && alice.get(1).getId() == 3
				&& carol.size() == 1 && carol.get(0).getId() == 4 && all.size() == 4;
		report("LIST-07", "subsetByAuthor returns matching lessons", ok,
				"alice ids=[1, 3], carol ids=[4], collection size=4",
				"alice ids=" + ids(alice) + ", carol ids=" + ids(carol) + ", collection size="
						+ all.size());
	}
	
	/**********
	 * <p> Method: testList08SubsetByAuthorNoMatch() </p>
	 * 
	 * <p> Requirement LIST-08 (negative): subsetByAuthor returns an empty subset, with its
	 * message, when the contributor has no lessons. </p>
	 * 
	 * <p> Approach: ask the sample collection for "dave" (no lessons), for "Alice" (wrong case;
	 * usernames must match exactly), and for null. </p>
	 * 
	 * <p> PASS means all three subsets are empty and show the empty message.  FAIL means a
	 * lesson was returned for a contributor who did not write it. </p>
	 */
	public static void testList08SubsetByAuthorNoMatch() {
		LessonsLearnedList all = sampleCollection();
		LessonsLearnedList dave = all.subsetByAuthor("dave", NO_LESSONS);
		LessonsLearnedList wrongCase = all.subsetByAuthor("Alice", NO_LESSONS);
		LessonsLearnedList none = all.subsetByAuthor(null, NO_LESSONS);
		boolean ok = dave.isEmpty() && wrongCase.isEmpty() && none.isEmpty()
				&& dave.getStatusMessage().equals(NO_LESSONS);
		report("LIST-08", "subsetByAuthor returns an empty subset when nothing matches", ok,
				"dave=0, Alice=0, null=0, message=\"" + NO_LESSONS + "\"",
				"dave=" + dave.size() + ", Alice=" + wrongCase.size() + ", null=" + none.size()
						+ ", message=\"" + dave.getStatusMessage() + "\"");
	}
	
	/**********
	 * <p> Method: testList09KeywordMatchesTitleIgnoringCase() </p>
	 * 
	 * <p> Requirement LIST-09: subsetByKeyword finds a keyword in a title, ignoring case (the
	 * same rule as TC-17). </p>
	 * 
	 * <p> Approach: search the sample collection for "JAVAFX", which appears only in the title
	 * of lesson 1 and only in a different case. </p>
	 * 
	 * <p> PASS means the subset is exactly lesson 1.  FAIL means the case was not ignored or the
	 * title was not searched. </p>
	 */
	public static void testList09KeywordMatchesTitleIgnoringCase() {
		LessonsLearnedList result = sampleCollection().subsetByKeyword("JAVAFX", NO_MATCH);
		boolean ok = result.size() == 1 && result.get(0).getId() == 1;
		report("LIST-09", "subsetByKeyword matches a title, ignoring case", ok,
				"ids=[1]", "ids=" + ids(result));
	}
	
	/**********
	 * <p> Method: testList10KeywordMatchesDescriptionIgnoringCase() </p>
	 * 
	 * <p> Requirement LIST-10: subsetByKeyword finds a keyword in a description, ignoring case
	 * (the same rule as TC-18). </p>
	 * 
	 * <p> Approach: search for "Off-By-One", which appears only in the description of lesson 3,
	 * in lower case. </p>
	 * 
	 * <p> PASS means the subset is exactly lesson 3.  FAIL means the case was not ignored or the
	 * description was not searched. </p>
	 */
	public static void testList10KeywordMatchesDescriptionIgnoringCase() {
		LessonsLearnedList result = sampleCollection().subsetByKeyword("Off-By-One", NO_MATCH);
		boolean ok = result.size() == 1 && result.get(0).getId() == 3;
		report("LIST-10", "subsetByKeyword matches a description, ignoring case", ok,
				"ids=[3]", "ids=" + ids(result));
	}
	
	/**********
	 * <p> Method: testList11KeywordNoMatch() </p>
	 * 
	 * <p> Requirement LIST-11 (negative): subsetByKeyword returns an empty subset, with the
	 * "No lessons match your search." message, when nothing matches (the same rule as TC-19).
	 * </p>
	 * 
	 * <p> Approach: search the sample collection for "kubernetes", which is in no lesson. </p>
	 * 
	 * <p> PASS means the subset is empty and shows the no-match message.  FAIL means a lesson
	 * was returned or the message is wrong. </p>
	 */
	public static void testList11KeywordNoMatch() {
		LessonsLearnedList result = sampleCollection().subsetByKeyword("kubernetes", NO_MATCH);
		boolean ok = result.isEmpty() && result.getStatusMessage().equals(NO_MATCH);
		report("LIST-11", "subsetByKeyword returns an empty subset when nothing matches", ok,
				"size=0, message=\"" + NO_MATCH + "\"",
				"size=" + result.size() + ", message=\"" + result.getStatusMessage() + "\"");
	}
	
	/**********
	 * <p> Method: testList12CopyConstructorIndependent() </p>
	 * 
	 * <p> Requirement LIST-12: the copy constructor produces the same lessons, in the same
	 * order, with the same empty message, but in its own list, so later changes to one list do
	 * not change the other. </p>
	 * 
	 * <p> Approach: copy the sample collection, add a lesson to the copy, then add a different
	 * lesson to the original. </p>
	 * 
	 * <p> PASS means the copy started with ids [1, 2, 3, 4], each list then has 5 lessons, and
	 * each has only its own fifth lesson.  FAIL means the two lists share the same structure.
	 * </p>
	 */
	public static void testList12CopyConstructorIndependent() {
		LessonsLearnedList original = sampleCollection();
		LessonsLearnedList copy = new LessonsLearnedList(original);
		String copiedIds = ids(copy);
		copy.add(lesson(5, "bob", "Pull before you push",
				"Run git pull before every push to avoid merge conflicts.", "Coding"));
		original.add(lesson(6, "carol", "Name branches by feature",
				"Use the feature name in the branch name so its purpose is clear.", "Teamwork"));
		boolean ok = copiedIds.equals("[1, 2, 3, 4]") && copy.size() == 5 && original.size() == 5
				&& copy.findById(5) != null && copy.findById(6) == null
				&& original.findById(6) != null && original.findById(5) == null;
		report("LIST-12", "Copy constructor copies the lessons into its own list", ok,
				"copy=[1, 2, 3, 4]; after adds copy=[1, 2, 3, 4, 5], original=[1, 2, 3, 4, 6]",
				"copy=" + copiedIds + "; after adds copy=" + ids(copy) + ", original="
						+ ids(original));
	}
	
	/**********
	 * <p> Method: testList13CompleteCollectionAllContributors() </p>
	 * 
	 * <p> Requirement LIST-13: the complete persistent collection holds the lessons of every
	 * contributor, and subsets can be built from it. </p>
	 * 
	 * <p> Approach: using the TP2ListTestDatabase, store three lessons from two contributors,
	 * close and reopen the database, load Database.getAllLessons into a LessonsLearnedList, and
	 * take bob's subset. </p>
	 * 
	 * <p> PASS means the collection read after reopening has all 3 lessons, from both
	 * contributors, oldest first, and bob's subset has 1.  FAIL means lessons were lost, a
	 * contributor's lessons were left out, or the database could not be used. </p>
	 */
	public static void testList13CompleteCollectionAllContributors() {
		String actual;
		boolean ok;
		Database db = new Database(TEST_DB_URL);
		try {
			db.connectToDatabase();
			db.deleteAllLessons();
			db.insertLesson(new LessonsLearned("alice", "JavaFX module path setup",
					"Add the JavaFX lib folder to the module path before running.", "Setup"));
			db.insertLesson(new LessonsLearned("bob", "H2 database file location",
					"The H2 database file is created in the user's home folder.", "Setup"));
			db.insertLesson(new LessonsLearned("alice", "Write the test before the fix",
					"A failing test showed the off-by-one error in the search loop.", "Testing"));
			db.closeConnection();								// Simulate closing the program
	
			db = new Database(TEST_DB_URL);						// Open the same database again
			db.connectToDatabase();
			LessonsLearnedList all = new LessonsLearnedList(NO_LESSONS);
			for (LessonsLearned l : db.getAllLessons()) all.add(l);
			LessonsLearnedList bob = all.subsetByAuthor("bob", NO_LESSONS);
			ok = all.size() == 3 && all.get(0).getAuthor().equals("alice")
					&& all.get(1).getAuthor().equals("bob")
					&& all.get(2).getTitle().equals("Write the test before the fix")
					&& bob.size() == 1;
			actual = "collection size=" + all.size() + ", authors=" + authors(all)
					+ ", bob subset size=" + bob.size();
			db.deleteAllLessons();
		} catch (Exception e) {
			ok = false;
			actual = "exception: " + e.getMessage();
		} finally {
			db.closeConnection();
		}
		report("LIST-13", "Complete persistent collection includes every contributor", ok,
				"collection size=3, authors=[alice, bob, alice], bob subset size=1", actual);
	}
	
	/**********
	 * <p> Method: testList14KeywordDoesNotSearchAuthor() </p>
	 * 
	 * <p> Requirement LIST-14 (negative): subsetByKeyword searches only the title and the
	 * description, so it gives the same result as the database search used by the Contributor
	 * page.  The author is not searched. </p>
	 * 
	 * <p> Approach: search the sample collection for "carol", a username that appears in no
	 * title or description, and for "   " (spaces only), which counts as empty search text. </p>
	 * 
	 * <p> PASS means "carol" finds nothing and the spaces-only search keeps all 4 lessons (the
	 * same rule as TC-20).  FAIL means the author was searched or empty text was mishandled. </p>
	 */
	public static void testList14KeywordDoesNotSearchAuthor() {
		LessonsLearnedList all = sampleCollection();
		LessonsLearnedList byName = all.subsetByKeyword("carol", NO_MATCH);
		LessonsLearnedList blank = all.subsetByKeyword("   ", NO_MATCH);
		boolean ok = byName.isEmpty() && blank.size() == 4;
		report("LIST-14", "subsetByKeyword does not search the author; blank keeps all", ok,
				"\"carol\" size=0, blank size=4",
				"\"carol\" size=" + byName.size() + ", blank size=" + blank.size());
	}
	
	// The ids in a list, for the printed results
	private static String ids(LessonsLearnedList list) {
		StringBuilder s = new StringBuilder("[");
		for (int i = 0; i < list.size(); i++)
			s.append(i == 0 ? "" : ", ").append(list.get(i).getId());
		return s.append("]").toString();
	}
	
	// The authors in a list, for the printed results
	private static String authors(LessonsLearnedList list) {
		StringBuilder s = new StringBuilder("[");
		for (int i = 0; i < list.size(); i++)
			s.append(i == 0 ? "" : ", ").append(list.get(i).getAuthor());
		return s.append("]").toString();
	}
}
