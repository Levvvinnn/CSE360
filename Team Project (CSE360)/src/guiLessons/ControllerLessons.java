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
 * written here, so the GUI and the automated tests use exactly the same code.
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
 */
public class ControllerLessons {
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerLessons() {
	}
	
	private static LessonManager manager;					// Created the first time it is needed
	private static LessonsLearnedList currentList = null;	// The lessons now shown in the list
	private static int selectedLessonId = -1;				// -1 means no lesson is selected
	
	/*-*******************************************************************************************

	Private helper methods
	
	*/
	
	// The manager is created on first use so the database reference is certain to exist
	private static LessonManager getManager() {
		if (manager == null)
			manager = new LessonManager(applicationMain.FoundationsMain.database);
		return manager;
	}
	
	// The username of the contributor using this page
	private static String author() {
		return ViewLessons.theUser.getUserName();
	}
	
	// Show a message under the form: red for an error, green for a success or information
	private static void showStatus(String message, boolean isError) {
		ViewLessons.label_Status.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
		ViewLessons.label_Status.setText(message);
	}
	
	// Put a set of lessons into the list on the page
	private static void showList(LessonsLearnedList list) {
		currentList = list;
		List<String> rows = new ArrayList<String>();
		for (LessonsLearned l : list.getAll())
			rows.add("#" + l.getId() + "  " + l.getTitle() + "  [" + l.getCategory() + "]");
		ViewLessons.list_Lessons.setItems(FXCollections.observableArrayList(rows));
	}
	
	// Reload all of this contributor's lessons into the list without changing the status line
	private static void refreshList() {
		showList(getManager().listMyLessons(author()));
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
	 * <p> Description: Shows every lesson this contributor has added. </p>
	 */
	protected static void performShowAll() {
		LessonsLearnedList list = getManager().listMyLessons(author());
		showList(list);
		if (list.isEmpty()) showStatus(list.getStatusMessage(), false);
		else showStatus("Showing all " + list.size() + " of your lessons.", false);
	}
	
	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: Shows the subset of this contributor's lessons that match the search
	 * text. </p>
	 */
	protected static void performSearch() {
		LessonsLearnedList list = getManager().searchMyLessons(author(),
				ViewLessons.text_Search.getText());
		if (list == null) {
			showStatus(getManager().getLastMessage(), true);
			return;
		}
		showList(list);
		if (list.isEmpty()) showStatus(list.getStatusMessage(), false);
		else showStatus(list.size() + " lesson(s) match your search.", false);
	}
	
	/**********
	 * <p> Method: performSelect(int index) </p>
	 * 
	 * <p> Description: Loads the lesson chosen in the list into the form so it can be read,
	 * changed or deleted. </p>
	 * 
	 * @param index the position chosen in the list (-1 when nothing is chosen)
	 */
	protected static void performSelect(int index) {
		if (currentList == null || index < 0 || index >= currentList.size()) return;
		LessonsLearned lesson = getManager().getLesson(author(), currentList.get(index).getId());
		if (lesson == null) {
			showStatus(getManager().getLastMessage(), true);
			return;
		}
		selectedLessonId = lesson.getId();
		ViewLessons.text_Title.setText(lesson.getTitle());
		ViewLessons.text_Description.setText(lesson.getDescription());
		ViewLessons.combobox_Category.setValue(lesson.getCategory());
		showStatus("Lesson #" + lesson.getId() + "   Created: " + lesson.getCreatedAt()
				+ "   Updated: " + lesson.getUpdatedAt(), false);
	}
	
	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: Saves the changes made in the form to the selected lesson. </p>
	 */
	protected static void performUpdate() {
		if (selectedLessonId < 0) {
			showStatus("Select a lesson from the list first.", true);
			return;
		}
		String message = getManager().updateLesson(author(), selectedLessonId,
				ViewLessons.text_Title.getText(), ViewLessons.text_Description.getText(),
				ViewLessons.combobox_Category.getValue());
		if (message.isEmpty()) {
			int id = selectedLessonId;
			refreshList();
			selectedLessonId = id;		// Keep the lesson selected for further changes
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
		if (selectedLessonId < 0) {
			showStatus("Select a lesson from the list first.", true);
			return;
		}
		ViewLessons.alertConfirmDelete.setContentText("Are you sure? This will permanently "
				+ "remove lesson #" + selectedLessonId + ".");
		ViewLessons.alertConfirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> result = ViewLessons.alertConfirmDelete.showAndWait();
		String answer = (result.isPresent() && result.get() == ButtonType.YES) ? "Yes" : "No";
		
		int id = selectedLessonId;
		String message = getManager().deleteLesson(author(), id, answer);
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
		selectedLessonId = -1;
		ViewLessons.text_Title.setText("");
		ViewLessons.text_Description.setText("");
		ViewLessons.combobox_Category.setValue(null);
		ViewLessons.list_Lessons.getSelectionModel().clearSelection();
		ViewLessons.label_Status.setText("");
	}
	
	/**********
	 * <p> Method: performBack() </p>
	 * 
	 * <p> Description: Returns to the Role1 Home page. </p>
	 */
	protected static void performBack() {
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
