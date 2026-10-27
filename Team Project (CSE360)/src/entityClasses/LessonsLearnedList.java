package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonsLearnedList Class </p>
 * 
 * <p> Description: This class holds any group of lessons learned.  The same class is used for
 * the complete collection of lessons (every lesson stored in the lessonDB table, as returned by
 * Database.getAllLessons) and for any subset of that collection: one contributor's lessons, or
 * the lessons that match a search keyword.  A subset is always a new LessonsLearnedList, so the
 * collection it was taken from is never changed by building it. </p>
 * 
 * <p> Storage: the lessons are kept in an ArrayList.  The TP2 requirements say a collection or
 * subset may hold zero, one, or many lessons with no arbitrary maximum, and an ArrayList grows
 * as needed, keeps the order the lessons were added (the database returns them oldest first),
 * and gives direct access by position, which the Contributor page uses when the user selects a
 * row in its list.  A fixed-size array would have forced an artificial upper limit. </p>
 * 
 * <p> Empty message: the list also carries the message to show when it is empty, so the user
 * interface does not need to know which operation produced it ("You have not added any lessons
 * yet." for a contributor's lessons, "No lessons match your search." for a search). </p>
 * 
 * <p> Subset rules: the subset operations follow the rules already used by the rest of the
 * system so a subset built here gives the same result as the database query for the same
 * request. </p>
 * <ul>
 * <li> subsetByAuthor keeps the lessons whose author is exactly the given username, which is
 * how Database.getLessonsByAuthor and LessonManager decide which lessons a contributor owns.
 * </li>
 * <li> subsetByKeyword keeps the lessons whose title or description contains the keyword,
 * ignoring case, which matches Database.searchLessonsByAuthor and test cases TC-17 and TC-18.
 * The author and category are deliberately not searched.  Empty search text keeps every lesson,
 * which matches TC-20. </li>
 * </ul>
 * 
 * <p> How this supports the Contributor functions: the Contributor "Show All" and "Search"
 * actions display a LessonsLearnedList, the empty message explains an empty result, and
 * findById lets the page locate the lesson behind a selected row.  The class depends only on
 * the public getters of LessonsLearned, so attributes added to that class later can be used by
 * new subset methods here without changing the existing ones. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author Levin Shibu
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 * @version 1.01		2026-10-25 TP2: copy constructor, null guard, findById, and the
 * 		author and keyword subset operations
 */
public class LessonsLearnedList {
	
	/*
	 * These are the private attributes for this list object
	 */
	private final List<LessonsLearned> lessons = new ArrayList<LessonsLearned>();
	private final String emptyMessage;		// Shown when the list has no elements
	
	/*****
	 * <p> Method: LessonsLearnedList(String emptyMessage) </p>
	 * 
	 * <p> Description: Establishes an empty list. </p>
	 * 
	 * @param emptyMessage the message that explains why the list is empty
	 */
	public LessonsLearnedList(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}
	
	/*****
	 * <p> Method: LessonsLearnedList(LessonsLearnedList source) </p>
	 * 
	 * <p> Description: Establishes a new list holding the same lessons, in the same order, and
	 * the same empty message as the source list.  The new list has its own ArrayList, so adding
	 * to either list later does not change the other.  The lesson objects themselves are shared,
	 * not duplicated, because both lists describe the same stored lessons. </p>
	 * 
	 * @param source the list to copy; a null source produces an empty list with no message
	 */
	public LessonsLearnedList(LessonsLearnedList source) {
		this.emptyMessage = (source == null) ? "" : source.emptyMessage;
		if (source != null) lessons.addAll(source.lessons);
	}
	
	/*****
	 * <p> Method: LessonsLearnedList() </p>
	 * 
	 * <p> Description: Establishes an empty list with no empty-list message.  Merged in from
	 * the duplicate list class this one replaces, for callers that build a list without a
	 * message. </p>
	 */
	public LessonsLearnedList() {
		this.emptyMessage = "";
	}
	
	/*****
	 * <p> Method: LessonsLearnedList(List source) </p>
	 * 
	 * <p> Description: Establishes a list holding a copy of the supplied lessons. </p>
	 * 
	 * @param source the lessons to copy into this list; null produces an empty list
	 */
	public LessonsLearnedList(List<LessonsLearned> source) {
		this.emptyMessage = "";
		if (source != null) lessons.addAll(source);
	}
	
	/*****
	 * <p> Method: void add(LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Adds a lesson to the end of the list.  A null lesson is ignored so that
	 * every element of the list can be used without a null check. </p>
	 * 
	 * @param lesson the lesson to add to the end of the list
	 */
	public void add(LessonsLearned lesson) {
		if (lesson != null) lessons.add(lesson);
	}
	
	/*****
	 * <p> Method: int size() </p>
	 * @return the number of lessons in the list
	 */
	public int size() { return lessons.size(); }
	
	/*****
	 * <p> Method: boolean isEmpty() </p>
	 * @return true if the list has no lessons, else false
	 */
	public boolean isEmpty() { return lessons.isEmpty(); }
	
	/*****
	 * <p> Method: LessonsLearned get(int index) </p>
	 * @param index the position in the list (starting at 0)
	 * @return the lesson at that position
	 */
	public LessonsLearned get(int index) { return lessons.get(index); }
	
	/*****
	 * <p> Method: List getAll() </p>
	 * @return a read-only view of all the lessons in the list
	 */
	public List<LessonsLearned> getAll() { return Collections.unmodifiableList(lessons); }
	
	/*****
	 * <p> Method: String getStatusMessage() </p>
	 * @return the empty-list message if the list is empty, else an empty string
	 */
	public String getStatusMessage() { return lessons.isEmpty() ? emptyMessage : ""; }
	
	/*****
	 * <p> Method: LessonsLearned findById(int id) </p>
	 * 
	 * <p> Description: Finds the lesson in this list with the given database id. </p>
	 * 
	 * @param id the database id of the lesson
	 * 
	 * @return the lesson, or null if no lesson in this list has that id
	 */
	public LessonsLearned findById(int id) {
		for (LessonsLearned lesson : lessons)
			if (lesson.getId() == id) return lesson;
		return null;
	}
	
	/*****
	 * <p> Method: LessonsLearnedList subsetByAuthor(String author, String emptyMessage) </p>
	 * 
	 * <p> Description: Builds the subset of this list written by one contributor.  The author
	 * must match the username exactly, the same rule the database uses to decide ownership.
	 * </p>
	 * 
	 * @param author the username of the contributor
	 * 
	 * @param emptyMessage the message the subset shows if it is empty
	 * 
	 * @return a new list with the matching lessons, in their original order (it may be empty)
	 */
	public LessonsLearnedList subsetByAuthor(String author, String emptyMessage) {
		LessonsLearnedList subset = new LessonsLearnedList(emptyMessage);
		if (author == null) return subset;
		for (LessonsLearned lesson : lessons)
			if (author.equals(lesson.getAuthor())) subset.add(lesson);
		return subset;
	}
	
	/*****
	 * <p> Method: LessonsLearnedList subsetByKeyword(String keyword, String emptyMessage) </p>
	 * 
	 * <p> Description: Builds the subset of this list whose title or description contains the
	 * keyword, ignoring case and the spaces at the ends of the keyword.  Empty search text keeps
	 * every lesson.  The length limit on search text is checked by LessonValidator before a
	 * search is made, so it is not repeated here. </p>
	 * 
	 * @param keyword the text to look for in titles and descriptions
	 * 
	 * @param emptyMessage the message the subset shows if it is empty
	 * 
	 * @return a new list with the matching lessons, in their original order (it may be empty)
	 */
	public LessonsLearnedList subsetByKeyword(String keyword, String emptyMessage) {
		LessonsLearnedList subset = new LessonsLearnedList(emptyMessage);
		String text = (keyword == null) ? "" : keyword.trim().toLowerCase();
		for (LessonsLearned lesson : lessons)
			if (text.isEmpty() || lesson.getTitle().toLowerCase().contains(text)
					|| lesson.getDescription().toLowerCase().contains(text))
				subset.add(lesson);
		return subset;
	}
	
	/*****
	 * <p> Method: void addLesson(LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Adds a lesson to the end of the list, ignoring null. </p>
	 * 
	 * @param lesson the lesson to add
	 */
	public void addLesson(LessonsLearned lesson) {
		if (lesson != null) lessons.add(lesson);
	}
	
	/*****
	 * <p> Method: boolean removeById(int id) </p>
	 * 
	 * @param id the database id of the lesson to remove
	 * 
	 * @return true if a lesson was removed, else false
	 */
	public boolean removeById(int id) {
		for (int i = 0; i < lessons.size(); i++) {
			if (lessons.get(i).getId() == id) {
				lessons.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/*****
	 * <p> Method: LessonsLearnedList createSubset(String searchText) </p>
	 * 
	 * <p> Description: Builds a new list holding only the lessons whose title, description,
	 * or author contains the search text, ignoring case.  Null or blank search text returns
	 * every lesson.  The empty-list message is carried over to the subset. </p>
	 * 
	 * @param searchText the text to look for
	 * 
	 * @return a new list containing only the matching lessons
	 */
	public LessonsLearnedList createSubset(String searchText) {
		LessonsLearnedList subset = new LessonsLearnedList(emptyMessage);
		if (searchText == null || searchText.trim().isEmpty()) {
			subset.lessons.addAll(lessons);
			return subset;
		}
		String search = searchText.trim().toLowerCase();
		for (LessonsLearned lesson : lessons) {
			if (lesson.getTitle().toLowerCase().contains(search)
					|| lesson.getDescription().toLowerCase().contains(search)
					|| lesson.getAuthor().toLowerCase().contains(search)) {
				subset.lessons.add(lesson);
			}
		}
		return subset;
	}
	
	/*****
	 * <p> Method: List getLessons() </p>
	 * @return a copy of the lessons currently in the list
	 */
	public List<LessonsLearned> getLessons() {
		return new ArrayList<LessonsLearned>(lessons);
	}
}
