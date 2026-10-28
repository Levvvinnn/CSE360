package guiLessons;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import lessonManager.LessonManager;

/*******
 * <p> Title: ControllerLessons Class. </p>
 * 
 * <p> Description: The Java/FX-based Lessons Learned page.  This class provides the controller
 * actions based on the user's use of the JavaFX GUI widgets defined by the View class.  Every
 * action passes the typed values to LessonManager, which validates them and uses the Database
 * class, and then shows the result or the error message on the page.  No validation rules are
 * written here, so the GUI and the automated tests use exactly the same code.  The data
 * that must persist between one user action and the next (the lessons currently shown in the
 * list, and which lesson, if any, is selected) is kept in ModelLessons, not here, so this
 * package follows the same Model/View/Controller structure as the rest of the Foundations GUI
 * packages.
 * 
 * The class has been written assuming that the View is the only class that invokes these
 * methods.  This is why each has been declared as "protected".  Do not change any of these
 * methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 * @version 1.01		2026-10-23 TP2: moved the page-state fields (the displayed list and the
 * 						selected lesson) out to the new ModelLessons class.
 */
public class ControllerLessons {
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerLessons() {
	}
	
	/** The LessonManager this page uses; created the first time it is needed. */
	private static LessonManager manager;
	
	/*-*******************************************************************************************

	Private helper methods
	
	*/
	
	/**
	 * <p> Method: getManager() </p>
	 * 
	 * <p> Description: Returns the shared LessonManager, creating it on first use so the
	 * database reference is certain to exist by the time it is needed. </p>
	 * 
	 * @return the LessonManager this page uses
	 */
	private static LessonManager getManager() {
		if (manager == null)
			manager = new LessonManager(applicationMain.FoundationsMain.database);
		return manager;
	}
	
	/**
	 * <p> Method: author() </p>
	 * 
	 * <p> Description: Returns the username of the contributor currently using this page. </p>
	 * 
	 * @return the logged-in user's username
	 */
	private static String author() {
		return ViewLessons.theUser.getUserName();
	}
	
	/**
	 * <p> Method: showStatus(String message, boolean isError) </p>
	 * 
	 * <p> Description: Shows a message under the form, styled red for an error or green for a
	 * success or informational message. </p>
	 * 
	 * @param message the text to display
	 * 
	 * @param isError true to style the message as an error (red), false for success (green)
	 */
	private static void showStatus(String message, boolean isError) {
		ViewLessons.label_Status.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
		ViewLessons.label_Status.setText(message);
	}
	
	/**
	 * <p> Method: showList(LessonsLearnedList list) </p>
	 * 
	 * <p> Description: Records the given set of lessons as the page's current list and puts a
	 * one-line summary of each lesson into the list widget on the page. </p>
	 * 
	 * @param list the set of lessons to display
	 */
	private static void showList(LessonsLearnedList list) {
		ModelLessons.currentList = list;
		List<String> rows = new ArrayList<String>();
		for (LessonsLearned l : list.getAll())
			rows.add("#" + l.getId() + "  " + l.getTitle() + "  [" + l.getCategory() + "]  by "
					+ l.getAuthor());
		ViewLessons.list_Lessons.setItems(FXCollections.observableArrayList(rows));
	}
	
	/**
	 * <p> Method: refreshList() </p>
	 * 
	 * <p> Description: Reloads the team-wide list of every lesson into the list widget,
	 * without changing the status line underneath it. </p>
	 */
	private static void refreshList() {
		showList(getManager().listAllLessons());
	}
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	*/
	
	/**********
	 * <p> Method: performCreate() </p>
	 * 
	 * <p> Description: Saves the lesson typed into the form as a new lesson. </p>
	 */
	protected static void performCreate() {
		String message = getManager().createLesson(author(), ViewLessons.text_Title.getText(),
				ViewLessons.text_Description.getText(),
				ViewLessons.combobox_Category.getValue());
		if (message.isEmpty()) {
			int id = getManager().getLastCreatedId();
			performClear();
			refreshList();
			showStatus("Lesson #" + id + " was saved.", false);
		} else
			showStatus(message, true);
	}
	
	/**********
	 * <p> Method: performShowAll() </p>
	 * 
	 * <p> Description: Shows every lesson currently recorded, from every contributor. </p>
	 */
	protected static void performShowAll() {
		LessonsLearnedList list = getManager().listAllLessons();
		showList(list);
		if (list.isEmpty()) showStatus(list.getStatusMessage(), false);
		else showStatus("Showing all " + list.size() + " lesson(s).", false);
	}
	
	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: Shows the subset of every lesson, from every contributor, whose title
	 * or description matches the search text. </p>
	 */
	protected static void performSearch() {
		LessonsLearnedList list = getManager().searchByKeyword(ViewLessons.text_Search.getText());
		if (list == null) {
			showStatus(getManager().getLastMessage(), true);
			return;
		}
		showList(list);
		if (list.isEmpty()) showStatus(list.getStatusMessage(), false);
		else showStatus(list.size() + " lesson(s) match your search.", false);
	}
	
	/**********
	 * <p> Method: performSearchByCategory() </p>
	 * 
	 * <p> Description: Shows the subset of every lesson, from every contributor, whose
	 * category matches the one chosen in the category search ComboBox. </p>
	 */
	protected static void performSearchByCategory() {
		String category = ViewLessons.combobox_SearchCategory.getValue();
		if (category == null) {
			showStatus("Choose a category to search by.", true);
			return;
		}
		LessonsLearnedList list = getManager().searchByCategory(category);
		if (list == null) {
			showStatus(getManager().getLastMessage(), true);
			return;
		}
		showList(list);
		if (list.isEmpty()) showStatus(list.getStatusMessage(), false);
		else showStatus(list.size() + " lesson(s) in category \"" + category + "\".", false);
	}
	
	/**********
	 * <p> Method: performSelect(int index) </p>
	 * 
	 * <p> Description: Loads the lesson chosen in the list into the form so it can be read, and
	 * changed or deleted if the current user is allowed to. Any lesson in the team-wide list
	 * can be viewed; only its author (or an Admin) can change or delete it. </p>
	 * 
	 * @param index the position chosen in the list (-1 when nothing is chosen)
	 */
	protected static void performSelect(int index) {
		if (ModelLessons.currentList == null || index < 0
				|| index >= ModelLessons.currentList.size()) return;
		LessonsLearned lesson = getManager().getLessonById(
				ModelLessons.currentList.get(index).getId());
		if (lesson == null) {
			showStatus(getManager().getLastMessage(), true);
			return;
		}
		ModelLessons.selectedLessonId = lesson.getId();
		ModelLessons.selectedLessonAuthor = lesson.getAuthor();
		ViewLessons.text_Title.setText(lesson.getTitle());
		ViewLessons.text_Description.setText(lesson.getDescription());
		ViewLessons.combobox_Category.setValue(lesson.getCategory());
		showStatus("Lesson #" + lesson.getId() + "   Author: " + lesson.getAuthor()
				+ "   Created: " + lesson.getCreatedAt() + "   Updated: " + lesson.getUpdatedAt(),
				false);
	}
	
	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: Saves the changes made in the form to the selected lesson. </p>
	 */
	protected static void performUpdate() {
		if (ModelLessons.selectedLessonId < 0) {
			showStatus("Select a lesson from the list first.", true);
			return;
		}
		String message = getManager().updateLesson(author(), ModelLessons.selectedLessonId,
				ViewLessons.text_Title.getText(), ViewLessons.text_Description.getText(),
				ViewLessons.combobox_Category.getValue(), ViewLessons.theIsAdmin);
		if (message.isEmpty()) {
			int id = ModelLessons.selectedLessonId;
			refreshList();
			ModelLessons.selectedLessonId = id;		// Keep the lesson selected for further changes
			showStatus("Lesson #" + id + " was updated.", false);
		} else
			showStatus(message, true);
	}
	
	/**********
	 * <p> Method: performDelete() </p>
	 * 
	 * <p> Description: Deletes the selected lesson after the contributor answers "Yes" to an
	 * "Are you sure?" question.  Any other answer leaves the lesson unchanged. </p>
	 */
	protected static void performDelete() {
		if (ModelLessons.selectedLessonId < 0) {
			showStatus("Select a lesson from the list first.", true);
			return;
		}
		ViewLessons.alertConfirmDelete.setContentText("Are you sure? This will permanently "
				+ "remove lesson #" + ModelLessons.selectedLessonId + ".");
		ViewLessons.alertConfirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> result = ViewLessons.alertConfirmDelete.showAndWait();
		String answer = (result.isPresent() && result.get() == ButtonType.YES) ? "Yes" : "No";
		
		int id = ModelLessons.selectedLessonId;
		String message = getManager().deleteLesson(author(), id, answer, ViewLessons.theIsAdmin);
		if (message.isEmpty()) {
			performClear();
			refreshList();
			showStatus("Lesson #" + id + " was deleted.", false);
		} else
			showStatus(message, true);
	}
	
	/**********
	 * <p> Method: performClear() </p>
	 * 
	 * <p> Description: Empties the form and forgets which lesson was selected. </p>
	 */
	protected static void performClear() {
		ModelLessons.selectedLessonId = -1;
		ModelLessons.selectedLessonAuthor = null;
		ViewLessons.text_Title.setText("");
		ViewLessons.text_Description.setText("");
		ViewLessons.combobox_Category.setValue(null);
		ViewLessons.list_Lessons.getSelectionModel().clearSelection();
		ViewLessons.label_Status.setText("");
	}
	
	/**********
	 * <p> Method: performBack() </p>
	 * 
	 * <p> Description: Returns to the page that opened this one: Admin Home when this page was
	 * opened in admin mode, else the Role1 (Contributor) Home page. </p>
	 */
	protected static void performBack() {
		if (ViewLessons.theIsAdmin)
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewLessons.theStage, ViewLessons.theUser);
		else
			guiRole1.ViewRole1Home.displayRole1Home(ViewLessons.theStage, ViewLessons.theUser);
	}
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: Logs out and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewLessons.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: Terminates the execution of the program. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
