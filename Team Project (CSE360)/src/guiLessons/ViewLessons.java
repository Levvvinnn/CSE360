package guiLessons;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import lessonValidator.LessonValidator;

/*******
 * <p> Title: ViewLessons Class. </p>
 * 
 * <p> Description: The Java/FX-based Lessons Learned page.  This class provides the JavaFX GUI
 * widgets that let a logged-in user create, view, search, update and delete lessons learned.  Every user
 * can see and search the full team-wide list; only the author of a lesson, or an Admin, may
 * update or delete it.  It follows the same singleton View pattern as the other pages: all access
 * starts with the static method displayLessons, which instantiates the class once and then
 * refreshes the parts that change on each use.  The page contains no validation or database
 * logic; the Controller calls LessonManager, which performs both.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-10-05 Initial version for HW2
 */
public class ViewLessons {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	/** The width, in pixels, the page's widgets were laid out for. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	/** The height, in pixels, the page's widgets were laid out for. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// GUI Area 1: the purpose of the page and whose account is being used
	/** Shows the page's title: "Lessons Learned" or "Lessons Learned (Admin)". */
	protected static Label label_PageTitle = new Label();
	/** Shows which user is currently logged in. */
	protected static Label label_UserDetails = new Label();
	
	// This is a separator and it is used to partition the GUI for various tasks
	/** Separates GUI Area 1 from the rest of the page. */
	private static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	// GUI Area 2 (left): the lesson form used to create, view, update and delete one lesson
	/** Labels the title field. */
	protected static Label label_Title = new Label("Title (1 to 100 characters)");
	/** Holds the title typed or loaded for the current lesson. */
	protected static TextField text_Title = new TextField();
	/** Labels the description field. */
	protected static Label label_Description = new Label("Description (1 to 1000 characters)");
	/** Holds the description typed or loaded for the current lesson. */
	protected static TextArea text_Description = new TextArea();
	/** Labels the category field. */
	protected static Label label_Category = new Label("Category");
	/** Holds the category chosen for the current lesson, from LessonValidator.CATEGORIES. */
	protected static ComboBox <String> combobox_Category = new ComboBox <String>();
	/** Saves the form as a new lesson. */
	protected static Button button_Create = new Button("Create");
	/** Saves changes to the selected lesson. */
	protected static Button button_Update = new Button("Update");
	/** Deletes the selected lesson, after confirmation. */
	protected static Button button_Delete = new Button("Delete");
	/** Empties the form and forgets which lesson was selected. */
	protected static Button button_Clear = new Button("Clear");
	/** Shows the result of the most recent action: red for an error, green for success. */
	protected static Label label_Status = new Label();
	/** Confirms "Are you sure?" before a lesson is permanently deleted. */
	protected static Alert alertConfirmDelete = new Alert(AlertType.CONFIRMATION);
	
	// GUI Area 3 (right): search and the list of lessons
	/** Labels the keyword search field. */
	protected static Label label_Search = new Label("Search all lessons");
	/** Holds the keyword text for a team-wide search. */
	protected static TextField text_Search = new TextField();
	/** Runs a team-wide keyword search using the text in text_Search. */
	protected static Button button_Search = new Button("Search");
	/** Shows every lesson in the system, from every contributor. */
	protected static Button button_ShowAll = new Button("Show All");
	/** Filters the team-wide list down to one category when chosen. */
	protected static ComboBox <String> combobox_SearchCategory = new ComboBox <String>();
	/** Shows the lessons currently in view: one line per lesson. */
	protected static ListView <String> list_Lessons = new ListView <String>();
	
	// This is a separator and it is used to partition the GUI for various tasks
	/** Separates GUI Area 3 from GUI Area 4. */
	private static Line line_Separator4 = new Line(20, 525, width-20, 525);
	
	// GUI Area 4: navigation
	/** Returns to the page that opened this one (Admin Home or Role1 Home). */
	protected static Button button_Back = new Button("Back to Home");
	/** Logs out and returns to the login page. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the execution of the program. */
	protected static Button button_Quit = new Button("Quit");
	
	// This is the end of the GUI objects for the page.
	
	/** Used to determine if instantiation is needed. */
	private static ViewLessons theView;
	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	/** The Pane that holds all the GUI widgets. */
	private static Pane theRootPane;
	/** The current logged in User. */
	protected static User theUser;
	/** True when opened from the Admin Home page, letting an Admin change any lesson. */
	protected static boolean theIsAdmin = false;
	/** The shared Scene each invocation populates. */
	private static Scene theLessonsScene;
	
	/*-*******************************************************************************************

	Constructors
	
	*/
	
	/**********
	 * <p> Method: displayLessons(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Lessons Learned page to be displayed.  It sets up the shared attributes, instantiates
	 * the class if needed, clears the form, shows the contributor's lessons, and displays the
	 * page. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 */
	public static void displayLessons(Stage ps, User user) {
		displayLessons(ps, user, false);
	}
	
	/**********
	 * <p> Method: displayLessons(Stage ps, User user, boolean isAdmin) </p>
	 * 
	 * <p> Description: This is the entry point used when an Admin opens this page from Admin
	 * Home.  It behaves exactly like displayLessons(Stage, User) except that it lets the Admin
	 * update or delete any lesson, not just lessons they created themselves. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 * @param isAdmin true when this page is being opened by an Admin
	 */
	public static void displayLessons(Stage ps, User user, boolean isAdmin) {
		theStage = ps;
		theUser = user;
		theIsAdmin = isAdmin;
		
		if (theView == null) theView = new ViewLessons();
		
		label_PageTitle.setText(theIsAdmin ? "Lessons Learned (Admin)" : "Lessons Learned");
		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerLessons.performClear();
		ControllerLessons.performShowAll();
		
		theStage.setTitle("CSE 360 HW2: Lessons Learned");
		theStage.setScene(theLessonsScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewLessons() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * It is a singleton and is only performed once.</p>
	 */
	private ViewLessons() {
		
		theRootPane = new Pane();
		theLessonsScene = new Scene(theRootPane, width, height);

		// TP2: make this page scale proportionally when its window is resized,
		// instead of staying pinned at its original size.
		guiTools.ResponsiveLayout.makeResponsive(theRootPane, theLessonsScene, width, height);
		
		// GUI Area 1
		label_PageTitle.setText("Lessons Learned");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		// GUI Area 2 (left): the form
		setupLabelUI(label_Title, "Arial", 16, 370, Pos.BASELINE_LEFT, 20, 105);
		setupTextUI(text_Title, "Arial", 16, 370, Pos.BASELINE_LEFT, 20, 130, true);
		
		setupLabelUI(label_Description, "Arial", 16, 370, Pos.BASELINE_LEFT, 20, 170);
		text_Description.setFont(Font.font("Arial", 16));
		text_Description.setWrapText(true);
		text_Description.setMinSize(370, 130);
		text_Description.setMaxSize(370, 130);
		text_Description.setLayoutX(20);
		text_Description.setLayoutY(195);
		
		setupLabelUI(label_Category, "Arial", 16, 90, Pos.BASELINE_LEFT, 20, 335);
		combobox_Category.setStyle("-fx-font: 16 Dialog;");
		combobox_Category.setMinWidth(200);
		combobox_Category.setLayoutX(110);
		combobox_Category.setLayoutY(330);
		combobox_Category.setPromptText("Choose a category");
		combobox_Category.setItems(FXCollections.observableArrayList(LessonValidator.CATEGORIES));
		
		setupButtonUI(button_Create, "Dialog", 16, 90, Pos.CENTER, 20, 375);
		button_Create.setOnAction((_) -> {ControllerLessons.performCreate(); });
		setupButtonUI(button_Update, "Dialog", 16, 90, Pos.CENTER, 120, 375);
		button_Update.setOnAction((_) -> {ControllerLessons.performUpdate(); });
		setupButtonUI(button_Delete, "Dialog", 16, 90, Pos.CENTER, 220, 375);
		button_Delete.setOnAction((_) -> {ControllerLessons.performDelete(); });
		setupButtonUI(button_Clear, "Dialog", 16, 90, Pos.CENTER, 320, 375);
		button_Clear.setOnAction((_) -> {ControllerLessons.performClear(); });
		
		// The status line shows success messages and error messages from the validator
		setupLabelUI(label_Status, "Arial", 15, 390, Pos.TOP_LEFT, 20, 425);
		label_Status.setWrapText(true);
		label_Status.setMaxWidth(390);
		label_Status.setMinHeight(90);
		
		alertConfirmDelete.setTitle("Confirm Delete");
		alertConfirmDelete.setHeaderText("Delete Lesson");
		
		// GUI Area 3 (right): search and list
		setupLabelUI(label_Search, "Arial", 16, 350, Pos.BASELINE_LEFT, 430, 105);
		setupTextUI(text_Search, "Arial", 16, 350, Pos.BASELINE_LEFT, 430, 130, true);
		setupButtonUI(button_Search, "Dialog", 16, 100, Pos.CENTER, 430, 170);
		button_Search.setOnAction((_) -> {ControllerLessons.performSearch(); });
		setupButtonUI(button_ShowAll, "Dialog", 16, 100, Pos.CENTER, 540, 170);
		button_ShowAll.setOnAction((_) -> {ControllerLessons.performShowAll(); });
		
		combobox_SearchCategory.setStyle("-fx-font: 14 Dialog;");
		combobox_SearchCategory.setMinWidth(130);
		combobox_SearchCategory.setLayoutX(650);
		combobox_SearchCategory.setLayoutY(170);
		combobox_SearchCategory.setPromptText("By Category");
		combobox_SearchCategory.setItems(FXCollections.observableArrayList(LessonValidator.CATEGORIES));
		combobox_SearchCategory.setOnAction((_) -> {ControllerLessons.performSearchByCategory(); });
		
		list_Lessons.setLayoutX(430);
		list_Lessons.setLayoutY(210);
		list_Lessons.setMinSize(350, 305);
		list_Lessons.setMaxSize(350, 305);
		list_Lessons.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, newIndex) -> ControllerLessons.performSelect(newIndex.intValue()));
		
		// GUI Area 4
		setupButtonUI(button_Back, "Dialog", 18, 200, Pos.CENTER, 20, 540);
		button_Back.setOnAction((_) -> {ControllerLessons.performBack(); });
		setupButtonUI(button_Logout, "Dialog", 18, 200, Pos.CENTER, 240, 540);
		button_Logout.setOnAction((_) -> {ControllerLessons.performLogout(); });
		setupButtonUI(button_Quit, "Dialog", 18, 200, Pos.CENTER, 460, 540);
		button_Quit.setOnAction((_) -> {ControllerLessons.performQuit(); });
		
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Separator1,
			label_Title, text_Title, label_Description, text_Description,
			label_Category, combobox_Category,
			button_Create, button_Update, button_Delete, button_Clear, label_Status,
			label_Search, text_Search, button_Search, button_ShowAll, combobox_SearchCategory,
			list_Lessons, line_Separator4, button_Back, button_Logout, button_Quit);
	}
	
	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text input field
	 * 
	 * @param t		The TextField object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the TextField
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		Is this TextField user editable?
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y,
			boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}
