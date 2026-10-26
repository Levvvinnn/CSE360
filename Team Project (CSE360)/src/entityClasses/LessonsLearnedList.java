package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonsLearnedList Class </p>
 * 
 * <p> Description: This class holds any group of lessons learned: every lesson a contributor has
 * added, or just the subset returned by a search.  The group may be empty, hold one lesson, or
 * hold any number of lessons; the class sets no upper limit because it is backed by an
 * ArrayList.  The list also carries the message to show when it is empty, so the user
 * interface does not need to know which operation produced it. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 */
public class LessonsLearnedList {
	
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
	 * @param lesson the lesson to add to the end of the list
	 */
	public void add(LessonsLearned lesson) { lessons.add(lesson); }
	
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
	
	/*****
	 * <p> Method: LessonsLearned findById(int id) </p>
	 * @param id the database id to look for
	 * @return the matching lesson, or null if it is not in this list
	 */
	public LessonsLearned findById(int id) {
		for (LessonsLearned lesson : lessons) {
			if (lesson.getId() == id) return lesson;
		}
		return null;
	}
}
