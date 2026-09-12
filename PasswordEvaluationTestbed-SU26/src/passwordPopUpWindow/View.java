package passwordPopUpWindow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import passwordEvaluationTestbedMain.PasswordEvaluationGUITestbed;

/*******
 * <p> Title: View Class - establishes the Graphics User interface, presents information to the
 * user, and accept information from the user.</p>
 *
 * <p> Description: This View class is a major component of a Model View Controller (MVC)
 * application design that provides the user with Graphical User Interface with JavaFX
 * widgets as opposed to a command line interface.
 *
 * In this case the GUI consists of numerous widgets to show the user where to enter the password,
 * where any errors are located, and a set of requirements for a valid password and whether or not
 * they have been satisfied
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 3.00 2026-09-11 Updated for TP1 password length validation
 */

public class View {

	/*
	 * The following private objects are GUI widgets that the view manages.
	 */

	/*
	 * A Label for the text input field and the text input field.
	 */
	static private Label label_Password =
			new Label("Enter the password here");

	static protected TextField text_Password =
			new TextField();

	/*
	 * Feedback labels to show the user where the error is located.
	 */
	static protected Label label_errPassword =
			new Label();

	static protected Label noInputFound =
			new Label();

	static private TextFlow errPassword;

	static protected Text errPasswordPart1 =
			new Text();

	static protected Text errPasswordPart2 =
			new Text();

	static protected Label errPasswordPart3 =
			new Label();

	/*
	 * Feedback labels with text and color to specify which
	 * requirements have been satisfied.
	 */
	static protected Label validPassword =
			new Label();

	static protected Label label_Requirements =
			new Label(
					"A valid password must satisfy the following requirements:");

	static protected Label label_UpperCase =
			new Label();

	static protected Label label_LowerCase =
			new Label();

	static protected Label label_NumericDigit =
			new Label();

	static protected Label label_SpecialChar =
			new Label();

	static protected Label label_LongEnough =
			new Label();

	/*
	 * Button to finish the process.
	 */
	static protected Button button_Finish =
			new Button();

	/*******
	 * <p> Title: View - Default Constructor </p>
	 *
	 * <p> Description: This constructor does not perform any special function
	 * for this application. </p>
	 */
	public View() {
		// No special actions required
	}

	/*******
	 * <p> Title: View - Static Public "Constructor" </p>
	 *
	 * <p> Description: This method creates the View singleton.
	 *
	 * @param theRoot Specifies the Pane on which the GUI should be built.
	 */
	static public void setupView(Pane theRoot) {
		theView = new View(theRoot);
	}

	/*******
	 * <p> Title: View - Private Constructor </p>
	 *
	 * <p> Description: This constructor creates the GUI elements and
	 * places them on the supplied Pane.
	 *
	 * @param theRoot Specifies the Pane on which the GUI widgets should be added.
	 */
	private View(Pane theRoot) {
		view(theRoot);
	}

	/*******
	 * <p> Title: View - Static Public View Method </p>
	 *
	 * <p> Description: Creates and initializes all GUI components and places
	 * them into the supplied JavaFX Pane.
	 *
	 * @param theRoot Specifies the Pane on which the GUI widgets are placed.
	 */
	public static void view(Pane theRoot) {

		double windowWidth =
				PasswordEvaluationGUITestbed.WINDOW_WIDTH;

		/*
		 * Label the password input field.
		 */
		setupLabelWidget(
				label_Password,
				10,
				10,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		/*
		 * Password input field.
		 */
		setupTextWidget(
				text_Password,
				10,
				30,
				"Arial",
				18,
				windowWidth - 20,
				Pos.BASELINE_LEFT,
				true);

		/*
		 * Whenever the password changes, evaluate it.
		 */
		text_Password.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					Model.updatePassword();
				});

		/*
		 * Error message for empty input.
		 */
		setupLabelWidget(
				noInputFound,
				10,
				80,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		noInputFound.setText(
				"No input text found!");

		noInputFound.setTextFill(
				Color.RED);

		/*
		 * General password error message.
		 */
		label_errPassword.setTextFill(
				Color.RED);

		label_errPassword.setAlignment(
				Pos.BASELINE_RIGHT);

		setupLabelWidget(
				label_errPassword,
				22,
				96,
				"Arial",
				14,
				windowWidth - 160,
				Pos.BASELINE_LEFT);

		/*
		 * Error message components.
		 */
		errPasswordPart1.setFill(
				Color.BLACK);

		errPasswordPart1.setFont(
				Font.font(
						"Arial",
						FontPosture.REGULAR,
						18));

		errPasswordPart2.setFill(
				Color.RED);

		errPasswordPart2.setFont(
				Font.font(
						"Arial",
						FontPosture.REGULAR,
						24));

		errPassword =
				new TextFlow(
						errPasswordPart1,
						errPasswordPart2);

		errPassword.setMinWidth(
				windowWidth - 10);

		errPassword.setLayoutX(22);

		errPassword.setLayoutY(70);

		setupLabelWidget(
				errPasswordPart3,
				20,
				110,
				"Arial",
				14,
				200,
				Pos.BASELINE_LEFT);

		/*
		 * Password requirements heading.
		 */
		setupLabelWidget(
				label_Requirements,
				10,
				140,
				"Arial",
				16,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		/*
		 * Uppercase requirement.
		 */
		setupLabelWidget(
				label_UpperCase,
				30,
				180,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		/*
		 * Lowercase requirement.
		 */
		setupLabelWidget(
				label_LowerCase,
				30,
				210,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		/*
		 * Numeric requirement.
		 */
		setupLabelWidget(
				label_NumericDigit,
				30,
				240,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		/*
		 * Special character requirement.
		 */
		setupLabelWidget(
				label_SpecialChar,
				30,
				270,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		/*
		 * Password length requirement.
		 *
		 * The password must contain at least 8 characters
		 * and no more than  characters.
		 */
		setupLabelWidget(
				label_LongEnough,
				30,
				300,
				"Arial",
				14,
				windowWidth - 10,
				Pos.BASELINE_LEFT);

		resetAssessments();

		/*
		 * Valid password message.
		 */
		validPassword.setTextFill(
				Color.GREEN);

		validPassword.setAlignment(
				Pos.BASELINE_RIGHT);

		setupLabelWidget(
				validPassword,
				10,
				340,
				"Arial",
				18,
				windowWidth - 160,
				Pos.BASELINE_LEFT);

		/*
		 * Finish button.
		 */
		setupButtonWidget(
				button_Finish,
				"Finish and Save The Password",
				(windowWidth - 250) / 2,
				380,
				"Arial",
				14,
				250,
				Pos.BASELINE_CENTER);

		button_Finish.setDisable(true);

		button_Finish.setOnAction(
				new EventHandler<>() {

					public void handle(ActionEvent event) {

						Controller.handleButtonPress();
					}
				});

		/*
		 * Add GUI widgets to the root pane.
		 */
		theRoot.getChildren().addAll(
				label_Password,
				text_Password,
				noInputFound,
				label_errPassword,
				errPassword,
				errPasswordPart3,
				validPassword,
				label_Requirements,
				label_UpperCase,
				label_LowerCase,
				label_NumericDigit,
				label_SpecialChar,
				label_LongEnough,
				button_Finish);
	}

	/*******
	 * <p> Title: View - Public Method that returns a reference
	 * to the View singleton object. </p>
	 *
	 * @return The View singleton object.
	 */
	static public View getView() {
		return theView;
	}

	/*******
	 * <p> Title: resetAssessments </p>
	 *
	 * <p> Description: Resets the password requirement indicators
	 * to their initial state.
	 */
	static protected void resetAssessments() {

		label_UpperCase.setText(
				"At least one upper case letter - Not yet satisfied");

		label_UpperCase.setTextFill(
				Color.RED);

		label_LowerCase.setText(
				"At least one lower case letter - Not yet satisfied");

		label_LowerCase.setTextFill(
				Color.RED);

		label_NumericDigit.setText(
				"At least one numeric digit - Not yet satisfied");

		label_NumericDigit.setTextFill(
				Color.RED);

		label_SpecialChar.setText(
				"At least one special character - Not yet satisfied");

		label_SpecialChar.setTextFill(
				Color.RED);

		/*
		 * UPDATED:
		 * The password must be between 8 and 32 characters.
		 */
		label_LongEnough.setText(
				"At least 8 characters and no more than 32 characters "
				+ "- Not yet satisfied");

		label_LongEnough.setTextFill(
				Color.RED);
	}

	/*
	 * Private method used to initialize a Label.
	 */
	static private void setupLabelWidget(
			Label l,
			double x,
			double y,
			String ff,
			double f,
			double w,
			Pos p) {

		l.setLayoutX(x);

		l.setLayoutY(y);

		l.setFont(
				Font.font(ff, f));

		l.setMinWidth(w);

		l.setAlignment(p);
	}

	/*
	 * Private method used to initialize a TextField.
	 */
	static private void setupTextWidget(
			TextField t,
			double x,
			double y,
			String ff,
			double f,
			double w,
			Pos p,
			boolean e) {

		t.setFont(
				Font.font(ff, f));

		t.setMinWidth(w);

		t.setMaxWidth(w);

		t.setAlignment(p);

		t.setLayoutX(x);

		t.setLayoutY(y);

		t.setEditable(e);
	}

	/*
	 * Private method used to initialize a Button.
	 */
	static private void setupButtonWidget(
			Button b,
			String s,
			double x,
			double y,
			String ff,
			double f,
			double w,
			Pos p) {

		b.setText(s);

		b.setFont(
				Font.font(ff, f));

		b.setMinWidth(w);

		b.setMaxWidth(w);

		b.setAlignment(p);

		b.setLayoutX(x);

		b.setLayoutY(y);
	}

	/*
	 * Reference to the View singleton.
	 */
	static private View theView;
}
