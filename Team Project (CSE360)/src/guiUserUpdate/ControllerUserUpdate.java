package guiUserUpdate;

import entityClasses.User;
import javafx.stage.Stage;
import database.Database;

/**********
 * <p> Title: ControllerUserUpdate Class</p>
 * 
 * <p> Description: This static class supports the actions initiated by the ViewUserUpdate
 * class.  In addition to navigating to the user's home page, it now validates and applies a
 * change to the user's password.
 * 
 * All validation is performed here rather than in the View.  The password is checked by the
 * Password Recognizer (passwordPopUpWindow.Model).  Note that the recognizer must be referred
 * to by its fully qualified name because this package declares a class of its own named
 * Model.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01		2026-09-17 Added a password update action
 */

public class ControllerUserUpdate {

	/*-********************************************************************************************

	The Controller attributes for this page
	
	**********************************************************************************************/

	// Reference for the application's database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor is not used.
	 */
	public ControllerUserUpdate() {
	}

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	**********************************************************************************************/

	/**********
	 * <p> Method: protected updatePassword(User theUser, String password1, String password2) </p>
	 * 
	 * <p> Description: Validates and applies a change to the user's password.  The two copies of
	 * the password must be identical and the password must satisfy every requirement enforced by
	 * the Password Recognizer.</p>
	 * 
	 * @param theUser the user whose password is being changed
	 * 
	 * @param password1 the proposed new password
	 * 
	 * @param password2 the confirmation copy of the proposed new password
	 * 
	 * @return an empty string on success, otherwise a message explaining the rejection
	 */
	protected static String updatePassword(User theUser, String password1, String password2) {

		if (password1 == null || password2 == null) {
			return "The password cannot be empty.";
		}

		// The two copies must match before the password is examined any further
		if (password1.compareTo(password2) != 0) {
			return "The two passwords must match. Please try again.";
		}

		// Make sure the password satisfies the Password Recognizer's FSM rules.  The fully
		// qualified name is required because this package also declares a class named Model.
		String passwordError = passwordPopUpWindow.Model.evaluatePassword(password1);
		if (!passwordError.isEmpty()) {
			return passwordError;
		}

		if (password1.equals(theUser.getPassword())) {
			return "The new password is the same as the current password.";
		}

		// Apply the change to the database first; only then update the in-memory copy
		if (!theDatabase.updatePassword(theUser.getUserName(), password1)) {
			return "The database was unable to change the password. Please try again.";
		}

		theUser.setPassword(password1);

		theDatabase.getUserAccountDetails(theUser.getUserName());

		return "";
	}

	/**********
	 * <p> Method: public goToUserHomePage(Stage theStage, User theUser) </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the button to
	 * proceed to the user's home page.
	 * 
	 * @param theStage specifies the JavaFX Stage for next next GUI page and it's methods
	 * 
	 * @param theUser specifies the user so we go to the right page and so the right information
	 */
	protected static void goToUserHomePage(Stage theStage, User theUser) {
		
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
			break;
		case 3:
			guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
			break;
		default: 
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + 
					theRole);
			System.exit(0);
		}
 	}
}