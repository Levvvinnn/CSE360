package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonLearnedList Class </p>
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
	 * <p> Method: LessonLearnedList(String emptyMessage) </p>
	 * 
	 * <p> Description: Establishes an empty list. </p>
	 * 
	 * @param emptyMessage the message that explains why the list is empty
	 */
	public LessonsLearnedList(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}
	
	/*****
	 * <p> Method: void add(LessonLearned lesson) </p>
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
	 * <p> Method: LessonLearned get(int index) </p>
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
}
