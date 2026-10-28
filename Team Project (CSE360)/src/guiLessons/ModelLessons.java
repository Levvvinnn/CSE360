package guiLessons;

import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ModelLessons Class. </p>
 * 
 * <p> Description: The Lessons Learned page Model.  Unlike most of the stub Model classes
 * elsewhere in this Foundations application, this page genuinely has data to track between
 * user actions: which set of lessons is currently being displayed (either every lesson, from
 * every contributor, or the subset returned by the most recent keyword or category search),
 * and which specific lesson, if any, is currently selected for viewing, updating, or
 * deleting. </p>
 * 
 * <p> Copyright: Ojas Patil © 2026 </p>
 * 
 * @author Ojas Patil
 * 
 * @version 1.00		2026-10-23 Initial version for TP2 — added so the guiLessons package
 * 							follows the same Model/View/Controller structure as the rest of the
 * 							Foundations GUI packages.  This page state previously lived directly
 * 							in ControllerLessons; it has been moved here to align with the MVC
 * 							pattern used throughout the provided Foundations code.
 */
public class ModelLessons {

	/**
	 * Default constructor is not used.
	 */
	public ModelLessons() {
	}

	/**
	 * The set of lessons currently being shown in the list widget.  This is either every
	 * lesson in the system (after "Show All") or the subset matching the most recent keyword
	 * or category search.  It may legitimately be empty.
	 */
	protected static LessonsLearnedList currentList = null;

	/**
	 * The id of the lesson currently selected in the list, for viewing, updating, or deleting;
	 * -1 means no lesson is currently selected (i.e., the form is set up to create a new one).
	 */
	protected static int selectedLessonId = -1;

	/**
	 * The username of the author of the currently selected lesson, if any; null when no lesson
	 * is selected.  Lets the page show who wrote the selected lesson.
	 */
	protected static String selectedLessonAuthor = null;
}
