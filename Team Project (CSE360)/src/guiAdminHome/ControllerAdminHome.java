package guiAdminHome;

import java.util.List;
import java.util.Optional;
import emailAddressTestbed.EmailAddressRecognizer;
import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword() {

	    // Get the user selected by the Admin
	    String selectedUser =
	            (String) ViewAdminHome.combobox_SelectUserToDelete.getValue();

	    // Make sure a user was selected
	    if (selectedUser == null ||
	            selectedUser.compareTo("<Select a User>") == 0) {

	        ViewAdminHome.alertNoUserSelected.setTitle("*** WARNING ***");
	        ViewAdminHome.alertNoUserSelected.setHeaderText(
	                "One-Time Password Issue");
	        ViewAdminHome.alertNoUserSelected.setContentText(
	                "Please select a user before setting a one-time password.");
	        ViewAdminHome.alertNoUserSelected.showAndWait();

	        return;
	    }

	    // Do not allow the Admin to reset their own password
	    if (selectedUser.compareTo(ViewAdminHome.theUser.getUserName()) == 0) {

	        ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText(
	                "One-Time Password Issue");
	        ViewAdminHome.alertNotImplemented.setContentText(
	                "You cannot set a one-time password for your own account.");
	        ViewAdminHome.alertNotImplemented.showAndWait();

	        return;
	    }

	    // Ask the Admin to enter the new one-time password
	    javafx.scene.control.TextInputDialog passwordDialog =
	            new javafx.scene.control.TextInputDialog();

	    passwordDialog.setTitle("Set One-Time Password");
	    passwordDialog.setHeaderText(
	            "Set a One-Time Password for: " + selectedUser);
	    passwordDialog.setContentText(
	            "Enter the temporary password:");

	    java.util.Optional<String> result =
	            passwordDialog.showAndWait();

	    // Admin cancelled the dialog
	    if (!result.isPresent()) {
	        return;
	    }

	    String oneTimePassword = result.get();

	    // Check that something was entered
	    if (oneTimePassword == null ||
	            oneTimePassword.trim().length() == 0) {

	        ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText(
	                "One-Time Password Issue");
	        ViewAdminHome.alertNotImplemented.setContentText(
	                "The one-time password cannot be empty.");
	        ViewAdminHome.alertNotImplemented.showAndWait();

	        return;
	    }

	    // Check the password length before using it
	    if (oneTimePassword.length() > 64) {

	        ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText(
	                "One-Time Password Issue");
	        ViewAdminHome.alertNotImplemented.setContentText(
	                "The one-time password cannot be longer than 64 characters.");
	        ViewAdminHome.alertNotImplemented.showAndWait();

	        return;
	    }

	    // Save the one-time password in the database
	    if (theDatabase.setOneTimePassword(selectedUser, oneTimePassword)) {

	        System.out.println(
	                "One-time password successfully set for user: "
	                + selectedUser);

	        ViewAdminHome.alertNotImplemented.setTitle(
	                "One-Time Password Set");
	        ViewAdminHome.alertNotImplemented.setHeaderText(
	                "Success");
	        ViewAdminHome.alertNotImplemented.setContentText(
	                "A one-time password was successfully set for "
	                + selectedUser + ".");
	        ViewAdminHome.alertNotImplemented.showAndWait();

	    } else {

	        ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText(
	                "One-Time Password Issue");
	        ViewAdminHome.alertNotImplemented.setContentText(
	                "The one-time password could not be set.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that lets an Admin delete a selected user from the
	 * system.  The Admin must first choose a user from the ComboBox.  An Admin cannot delete
	 * their own account.  A confirmed deletion removes that user from the database so they
	 * can no longer log in.  Any response other than Yes cancels with no change. </p>
	 */
	protected static void deleteUser() {
		// Fetch which user the Admin selected from the ComboBox on the Admin Home page
		String selectedUser = (String) ViewAdminHome.combobox_SelectUserToDelete.getValue();
		
		// If no actual user has been selected, explain the issue and stop here
		if (selectedUser == null || selectedUser.compareTo("<Select a User>") == 0) {
			ViewAdminHome.alertNoUserSelected.setTitle("*** WARNING ***");
			ViewAdminHome.alertNoUserSelected.setHeaderText("Delete User Issue");
			ViewAdminHome.alertNoUserSelected.setContentText(
					"Please select a user from the list before clicking Delete a User.");
			ViewAdminHome.alertNoUserSelected.showAndWait();
			return;
		}
		
		// An Admin is not allowed to delete the account they are currently using.  Doing so
		// would leave them without an account while still logged in as that Admin.
		if (selectedUser.compareTo(ViewAdminHome.theUser.getUserName()) == 0) {
			ViewAdminHome.alertCannotDeleteSelf.setTitle("*** WARNING ***");
			ViewAdminHome.alertCannotDeleteSelf.setHeaderText("Delete User Issue");
			ViewAdminHome.alertCannotDeleteSelf.setContentText(
					"You cannot delete your own account. The currently logged-in Admin " +
					"must remain in the system. Select a different user.");
			ViewAdminHome.alertCannotDeleteSelf.showAndWait();
			return;
		}
		
		// Ask the Admin to confirm.  Only a "Yes" answer proceeds with the deletion.
		// Any other response (No, or closing the dialog) cancels with no change.
		ViewAdminHome.alertConfirmDelete.setTitle("Confirm Delete");
		ViewAdminHome.alertConfirmDelete.setHeaderText("Delete User");
		ViewAdminHome.alertConfirmDelete.setContentText(
				"Are you sure? This will permanently remove user \"" + selectedUser +
				"\" from the system.");
		ViewAdminHome.alertConfirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> result = ViewAdminHome.alertConfirmDelete.showAndWait();
		
		if (result.isPresent() && result.get() == ButtonType.YES) {
			// The Admin confirmed, so remove the user from the database
			if (theDatabase.deleteUser(selectedUser)) {
				// Refresh the ComboBox and the user count so the GUI matches the database
				List<String> userList = theDatabase.getUserList();
				ViewAdminHome.combobox_SelectUserToDelete.setItems(
						FXCollections.observableArrayList(userList));
				ViewAdminHome.combobox_SelectUserToDelete.getSelectionModel().select(0);
				ViewAdminHome.label_NumberOfUsers.setText("Number of users: " +
						theDatabase.getNumberOfUsers());
			}
		}
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that displays the username, full name, email address,
	 * and assigned roles for every user currently in the system.  The information is shown in
	 * a scrollable, read-only dialog since the number of users is not bounded. </p>
	 */
	protected static void listUsers() {
		// Fetch every user record currently in the database
		List<User> allUsers = theDatabase.getAllUsers();

		if (allUsers.isEmpty()) {
			ViewAdminHome.alertNotImplemented.setTitle("List All Users");
			ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
			ViewAdminHome.alertNotImplemented.setContentText(
					"There are no users currently in the system.");
			ViewAdminHome.alertNotImplemented.showAndWait();
			return;
		}

		// Build one block of text per user with their username, full name, email address, and
		// the roles they currently have assigned
		StringBuilder sb = new StringBuilder();
		for (User user : allUsers) {
			sb.append("Username: ").append(user.getUserName()).append("\n");
			sb.append("Name: ").append(buildFullName(user)).append("\n");
			sb.append("Email: ").append(
					(user.getEmailAddress() == null || user.getEmailAddress().isEmpty())
							? "(none)" : user.getEmailAddress()).append("\n");
			sb.append("Roles: ").append(buildRoleList(user)).append("\n");
			sb.append("------------------------------------------------------------\n");
		}

		ViewAdminHome.textarea_UserList.setText(sb.toString());
		ViewAdminHome.alertListUsers.setHeaderText("All Users (" + allUsers.size() + ")");
		ViewAdminHome.alertListUsers.showAndWait();
	}

	/**********
	 * <p> 
	 * 
	 * Title: buildFullName () Method. </p>
	 * 
	 * <p> Description: Protected helper method that assembles a user's full name from their
	 * first, middle, and last name attributes, skipping any that have not been set. </p>
	 * 
	 * @param user	specifies the user whose full name is being assembled
	 * 
	 * @return a single String with the user's first, middle, and last name separated by spaces,
	 * 			or "(not set)" if none of those fields have been populated
	 */
	private static String buildFullName(User user) {
		StringBuilder name = new StringBuilder();
		if (user.getFirstName() != null && !user.getFirstName().isEmpty())
			name.append(user.getFirstName());
		if (user.getMiddleName() != null && !user.getMiddleName().isEmpty())
			name.append(name.length() > 0 ? " " : "").append(user.getMiddleName());
		if (user.getLastName() != null && !user.getLastName().isEmpty())
			name.append(name.length() > 0 ? " " : "").append(user.getLastName());
		return name.length() > 0 ? name.toString() : "(not set)";
	}

	/**********
	 * <p> 
	 * 
	 * Title: buildRoleList () Method. </p>
	 * 
	 * <p> Description: Protected helper method that assembles a comma-separated list of the
	 * roles a user currently has assigned. </p>
	 * 
	 * @param user	specifies the user whose assigned roles are being listed
	 * 
	 * @return a comma-separated String of this user's roles (e.g., "Admin, Contributor"), or
	 * 			"(none)" if the user has no roles assigned
	 */
	private static String buildRoleList(User user) {
		StringBuilder roles = new StringBuilder();
		if (user.getAdminRole())
			roles.append("Admin");
		if (user.getNewRole1())
			roles.append(roles.length() > 0 ? ", " : "").append("Contributor");
		if (user.getNewRole2())
			roles.append(roles.length() > 0 ? ", " : "").append("Viewer");
		return roles.length() > 0 ? roles.toString() : "(none)";
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		String emailError = EmailAddressRecognizer.checkEmailAddress(emailAddress);
	    if (!emailError.isEmpty()) {
	        ViewAdminHome.alertEmailError.setContentText(emailError.trim());
	        ViewAdminHome.alertEmailError.showAndWait();
	        return true;
	    }
	    return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
