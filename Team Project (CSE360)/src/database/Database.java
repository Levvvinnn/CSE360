package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: Database Class. </p>
 *
 * <p> Description: This is an in-memory database built on H2. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 2.00 2025-04-29
 * @version 2.01 2025-12-17
 */
public class Database {

	// JDBC driver name and database URL
		static final String JDBC_DRIVER = "org.h2.Driver";
		static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

		// Database credentials
		static final String USER = "sa";
		static final String PASS = "";

		// Shared variables
		private Connection connection = null;
		private Statement statement = null;
		private String dbUrl = DB_URL;

		// Currently logged-in user information
		private String currentUsername;
		private String currentPassword;
		private String currentFirstName;
		private String currentMiddleName;
		private String currentLastName;
		private String currentPreferredFirstName;
		private String currentEmailAddress;
		private boolean currentAdminRole;
		private boolean currentNewRole1;
		private boolean currentNewRole2;

	/*******
	 * Default constructor.
	 */
	public Database() {
	}

	/*******
	 * Establishes the connection to the H2 database.
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER);

			connection = DriverManager.getConnection(dbUrl, USER, PASS);
			statement = connection.createStatement();

			// Uncomment this if you want to completely reset the database.
			// statement.execute("DROP ALL OBJECTS");

			createTables();

		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	public Database (String dbUrl) {
	    this.dbUrl = dbUrl;
	}

	/*******
	 * Creates the database tables.
	 */
	private void createTables() throws SQLException {

		String userTable =
				"CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "oneTimePassword BOOL DEFAULT FALSE, "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";

		statement.execute(userTable);

		/*
		 * This allows an existing FoundationDatabase to gain the new
		 * oneTimePassword column without requiring the whole database
		 * to be deleted.
		 */
		try {
			statement.execute(
					"ALTER TABLE userDB ADD COLUMN IF NOT EXISTS "
					+ "oneTimePassword BOOL DEFAULT FALSE");
		} catch (SQLException e) {
			// Column may already exist.
		}

		String invitationCodesTable =
				"CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "emailAddress VARCHAR(255), "
				+ "role VARCHAR(10))";

		statement.execute(invitationCodesTable);

		/*
		 * This table stores the lessons learned for HW2.
		 * The table is created only if it does not already exist so
		 * previously stored lessons remain available between executions.
		 */
		String lessonLearnedTable =
				"CREATE TABLE IF NOT EXISTS lessonLearnedDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(100) NOT NULL, "
				+ "lessonText VARCHAR(2000) NOT NULL, "
				+ "author VARCHAR(255) NOT NULL, "
				+ "rootCause VARCHAR(2000) DEFAULT '', "
				+ "recommendation VARCHAR(2000) DEFAULT '', "
				+ "keywords VARCHAR(1000) DEFAULT '', "
				+ "timeEffort VARCHAR(255) DEFAULT '', "
				+ "category VARCHAR(100) DEFAULT '', "
				+ "isLocked BOOLEAN DEFAULT FALSE, "
				+ "lockedFields VARCHAR(4000) DEFAULT '', "
				+ "effortVisible BOOLEAN DEFAULT TRUE)";

		statement.execute(lessonLearnedTable);

		/* Restored during PR review: main's lesson methods (insertLesson,
		 * updateLesson, deleteLesson, getLessonById, getLessonsByAuthor,
		 * searchLessonsByAuthor, getNumberOfLessons, deleteAllLessons) all query
		 * lessonDB.  Creating lessonLearnedDB does not create lessonDB, so without
		 * this statement every one of those methods fails at runtime with
		 * "table not found". */
		String lessonTable = "CREATE TABLE IF NOT EXISTS lessonDB ("
		        + "id INT AUTO_INCREMENT PRIMARY KEY, "
		        + "title VARCHAR(100), "
		        + "description VARCHAR(1000), "
		        + "category VARCHAR(20), "
		        + "author VARCHAR(255), "
		        + "createdAt TIMESTAMP, "
		        + "updatedAt TIMESTAMP)";
		statement.execute(lessonTable);

		/* TP2 keyword search: lessonDB also stores the Keywords attribute so the Contributor
		 * search can look in it.  Added with ALTER so existing databases keep their lessons;
		 * existing rows get an empty string. */
		statement.execute("ALTER TABLE lessonDB ADD COLUMN IF NOT EXISTS keywords VARCHAR(1000) DEFAULT ''");

		// Add epic columns to existing databases without deleting saved lessons.
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS rootCause VARCHAR(2000) DEFAULT ''");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS recommendation VARCHAR(2000) DEFAULT ''");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS keywords VARCHAR(1000) DEFAULT ''");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS timeEffort VARCHAR(255) DEFAULT ''");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS category VARCHAR(100) DEFAULT ''");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS isLocked BOOLEAN DEFAULT FALSE");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS lockedFields VARCHAR(4000) DEFAULT ''");
		statement.execute("ALTER TABLE lessonLearnedDB ADD COLUMN IF NOT EXISTS effortVisible BOOLEAN DEFAULT TRUE");
	}

	/*******
	 * Returns true if there are no users in the database.
	 *
	 * @return true if the database contains no users; false otherwise
	 */
	public boolean isDatabaseEmpty() {

		String query = "SELECT COUNT(*) AS count FROM userDB";

		try {
			ResultSet resultSet = statement.executeQuery(query);

			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}

		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	/*******
	 * Returns the number of users in the database.
	 *
	 * @return the number of users currently in the database
	 */
	public int getNumberOfUsers() {

		String query = "SELECT COUNT(*) AS count FROM userDB";

		try {
			ResultSet resultSet = statement.executeQuery(query);

			if (resultSet.next()) {
				return resultSet.getInt("count");
			}

		} catch (SQLException e) {
			return 0;
		}

		return 0;
	}

	/*******
	 * Registers a new user.
	 */
	public void register(User user) throws SQLException {

		String insertUser =
				"INSERT INTO userDB "
				+ "(userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, "
				+ "adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt =
				connection.prepareStatement(insertUser)) {

			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);

			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);

			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);

			currentMiddleName = user.getMiddleName();
			pstmt.setString(4, currentMiddleName);

			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);

			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);

			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);

			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);

			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);

			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);

			pstmt.executeUpdate();
		}
	}

	/*******
	 * Returns a list of all usernames.
	 *
	 * @return the list of usernames in the database
	 */
	public List<String> getUserList() {

		List<String> userList = new ArrayList<String>();

		userList.add("<Select a User>");

		String query = "SELECT userName FROM userDB";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}

		} catch (SQLException e) {
			return null;
		}

		return userList;
	}

	/*******
	 * <p> Method: List<User> getAllUsers() </p>
	 *
	 * <p> Description: Generate a List of User objects, one for each user record currently in the
	 * userDB table. Used by the Admin "List All Users" function to display the username, full name,
	 * email address, and assigned roles for every user in the system. </p>
	 *
	 * @return a list of User objects, one per row in the userDB table. The list is empty (never
	 * 			null) if there are no users or if a database error occurs.
	 *
	 */
	public List<User> getAllUsers() {

		List<User> allUsers = new ArrayList<User>();
		String query = "SELECT * FROM userDB";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				User user = new User(
						rs.getString("userName"),
						rs.getString("password"),
						rs.getString("firstName"),
						rs.getString("middleName"),
						rs.getString("lastName"),
						rs.getString("preferredFirstName"),
						rs.getString("emailAddress"),
						rs.getBoolean("adminRole"),
						rs.getBoolean("newRole1"),
						rs.getBoolean("newRole2"));

				allUsers.add(user);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allUsers;
	}

	/*******
	 * <p> Method: boolean loginAdmin(User user) </p>
	 *
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 *
	 * @param user specifies the specific user that should be logged in playing the Admin role.
	 *
	 * @return true if the specified user has been logged in as an Admin else false.
	 *
	 */
	public boolean loginAdmin(User user) {

		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query =
				"SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());

			ResultSet rs = pstmt.executeQuery();

			return rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * <p> Method: boolean loginRole1(User user) </p>
	 *
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 *
	 * @param user specifies the specific user that should be logged in playing the Student role.
	 *
	 * @return true if the specified user has been logged in as an Student else false.
	 *
	 */
	public boolean loginRole1(User user) {

		// Validates a student user's login credentials.
		String query =
				"SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());

			ResultSet rs = pstmt.executeQuery();

			return rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Validates a Role 2 user's login.
	 */
	public boolean loginRole2(User user) {

		String query =
				"SELECT * FROM userDB "
				+ "WHERE userName = ? "
				+ "AND password = ? "
				+ "AND newRole2 = TRUE";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());

			ResultSet rs = pstmt.executeQuery();

			return rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Checks whether a username already exists.
	 */
	public boolean doesUserExist(String userName) {

		String query =
				"SELECT COUNT(*) FROM userDB WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, userName);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Gets the number of roles for a user.
	 */
	public int getNumberOfRoles(User user) {

		int numberOfRoles = 0;

		if (user.getAdminRole())
			numberOfRoles++;

		if (user.getNewRole1())
			numberOfRoles++;

		if (user.getNewRole2())
			numberOfRoles++;

		return numberOfRoles;
	}

	/*******
	 * Generates an invitation code.
	 */
	public String generateInvitationCode(
			String emailAddress, String role) {

		String code =
				UUID.randomUUID().toString().substring(0, 6);

		String query =
				"INSERT INTO InvitationCodes "
				+ "(code, emailaddress, role) "
				+ "VALUES (?, ?, ?)";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, role);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	/*******
	 * Gets the number of outstanding invitations.
	 *
	 * @return the number of outstanding invitations
	 */
	public int getNumberOfInvitations() {

		String query =
				"SELECT COUNT(*) AS count FROM InvitationCodes";

		try {

			ResultSet resultSet =
					statement.executeQuery(query);

			if (resultSet.next()) {
				return resultSet.getInt("count");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/*******
	 * Checks whether an email address has already been used.
	 */
	public boolean emailaddressHasBeenUsed(
			String emailAddress) {

		String query =
				"SELECT COUNT(*) AS count "
				+ "FROM InvitationCodes "
				+ "WHERE emailAddress = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, emailAddress);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("count") > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Gets the role associated with an invitation code.
	 */
	public String getRoleGivenAnInvitationCode(
			String code) {

		String query =
				"SELECT * FROM InvitationCodes "
				+ "WHERE code = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, code);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("role");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	/*******
	 * Gets the email address associated with an invitation code.
	 */
	public String getEmailAddressUsingCode(String code) {

		String query =
				"SELECT emailAddress "
				+ "FROM InvitationCodes "
				+ "WHERE code = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, code);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("emailAddress");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	/*******
	 * Removes an invitation after it is used.
	 */
	public void removeInvitationAfterUse(String code) {

		String query =
				"SELECT COUNT(*) AS count "
				+ "FROM InvitationCodes "
				+ "WHERE code = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, code);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				int counter = rs.getInt(1);

				if (counter > 0) {

					query =
							"DELETE FROM InvitationCodes "
							+ "WHERE code = ?";

					try (PreparedStatement pstmt2 =
							connection.prepareStatement(query)) {

						pstmt2.setString(1, code);
						pstmt2.executeUpdate();
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * Deletes a user.
	 */
	public boolean deleteUser(String username) {

		String query =
				"DELETE FROM userDB WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			int rowsDeleted =
					pstmt.executeUpdate();

			return rowsDeleted > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Gets a user's first name.
	 */
	public String getFirstName(String username) {

		String query =
				"SELECT firstName FROM userDB "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("firstName");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*******
	 * Updates a user's first name.
	 */
	public void updateFirstName(
			String username, String firstName) {

		String query =
				"UPDATE userDB SET firstName = ? "
				+ "WHERE username = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, firstName);
			pstmt.setString(2, username);

			pstmt.executeUpdate();

			currentFirstName = firstName;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * Gets a user's middle name.
	 */
	public String getMiddleName(String username) {

		String query =
				"SELECT middleName FROM userDB "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("middleName");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*******
	 * Updates a user's middle name.
	 */
	public void updateMiddleName(
			String username, String middleName) {

		String query =
				"UPDATE userDB SET middleName = ? "
				+ "WHERE username = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, middleName);
			pstmt.setString(2, username);

			pstmt.executeUpdate();

			currentMiddleName = middleName;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * Gets a user's last name.
	 */
	public String getLastName(String username) {

		String query =
				"SELECT lastName FROM userDB "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("lastName");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*******
	 * Updates a user's last name.
	 */
	public void updateLastName(
			String username, String lastName) {

		String query =
				"UPDATE userDB SET lastName = ? "
				+ "WHERE username = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, lastName);
			pstmt.setString(2, username);

			pstmt.executeUpdate();

			currentLastName = lastName;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * Gets a user's preferred first name.
	 */
	public String getPreferredFirstName(String username) {

		String query =
				"SELECT preferredFirstName "
				+ "FROM userDB WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("preferredFirstName");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*******
	 * Updates a user's preferred first name.
	 */
	public void updatePreferredFirstName(
			String username,
			String preferredFirstName) {

		String query =
				"UPDATE userDB SET preferredFirstName = ? "
				+ "WHERE username = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, preferredFirstName);
			pstmt.setString(2, username);

			pstmt.executeUpdate();

			currentPreferredFirstName =
					preferredFirstName;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * Gets a user's email address.
	 */
	public String getEmailAddress(String username) {

		String query =
				"SELECT emailAddress FROM userDB "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("emailAddress");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*******
	 * Updates a user's email address.
	 */
	public void updateEmailAddress(
			String username, String emailAddress) {

		String query =
				"UPDATE userDB SET emailAddress = ? "
				+ "WHERE username = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, emailAddress);
			pstmt.setString(2, username);

			pstmt.executeUpdate();

			currentEmailAddress = emailAddress;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**********
	 * Updates a user's password.
	 */
	public boolean updatePassword(
			String userName,
			String newPassword) {

		String query =
				"UPDATE cse360users SET password = ? "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, newPassword);
			pstmt.setString(2, userName);

			return pstmt.executeUpdate() == 1;

		} catch (SQLException e) {
			System.err.println(
					"*** ERROR *** updatePassword: "
					+ e.getMessage());

			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * Gets all account information for a user.
	 */
	public boolean getUserAccountDetails(String username) {

		String query =
				"SELECT * FROM userDB "
				+ "WHERE username = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				return false;
			}

			currentUsername =
					rs.getString("userName");

			currentPassword =
					rs.getString("password");

			currentFirstName =
					rs.getString("firstName");

			currentMiddleName =
					rs.getString("middleName");

			currentLastName =
					rs.getString("lastName");

			currentPreferredFirstName =
					rs.getString("preferredFirstName");

			currentEmailAddress =
					rs.getString("emailAddress");

			currentAdminRole =
					rs.getBoolean("adminRole");

			currentNewRole1 =
					rs.getBoolean("newRole1");

			currentNewRole2 =
					rs.getBoolean("newRole2");

			return true;

		} catch (SQLException e) {
			return false;
		}
	}

	/*******
	 * Updates a user's role.
	 */
	public boolean updateUserRole(
			String username,
			String role,
			String value) {

		if (role.compareTo("Admin") == 0) {

			String query =
					"UPDATE userDB SET adminRole = ? "
					+ "WHERE username = ?";

			try (PreparedStatement pstmt =
					connection.prepareStatement(query)) {

				pstmt.setString(1, value);
				pstmt.setString(2, username);

				pstmt.executeUpdate();

				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;

				return true;

			} catch (SQLException e) {
				return false;
			}
		}

		if (role.compareTo("Contributor") == 0) {

			String query =
					"UPDATE userDB SET newRole1 = ? "
					+ "WHERE username = ?";

			try (PreparedStatement pstmt =
					connection.prepareStatement(query)) {

				pstmt.setString(1, value);
				pstmt.setString(2, username);

				pstmt.executeUpdate();

				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;

				return true;

			} catch (SQLException e) {
				return false;
			}
		}

		if (role.compareTo("Viewer") == 0) {

			String query =
					"UPDATE userDB SET newRole2 = ? "
					+ "WHERE username = ?";

			try (PreparedStatement pstmt =
					connection.prepareStatement(query)) {

				pstmt.setString(1, value);
				pstmt.setString(2, username);

				pstmt.executeUpdate();

				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;

				return true;

			} catch (SQLException e) {
				return false;
			}
		}

		return false;
	}

	/**************************************************************
	 * ONE-TIME PASSWORD
	 **************************************************************/

	/*******
	 * Sets a one-time password for a specified user.
	 *
	 * The password is stored in the normal password field and
	 * the oneTimePassword flag is set to TRUE.
	 *
	 * @param username the user receiving the one-time password
	 * @param oneTimePassword the temporary password
	 *
	 * @return true if the update was successful
	 */
	public boolean setOneTimePassword(
			String username,
			String oneTimePassword) {

		// Validate the username before using it.
		if (username == null ||
				username.length() == 0) {
			return false;
		}

		// Validate the password before using it.
		if (oneTimePassword == null ||
				oneTimePassword.length() == 0) {
			return false;
		}

		// Prevent excessively large input.
		if (oneTimePassword.length() > 64) {
			return false;
		}

		String query =
				"UPDATE userDB "
				+ "SET password = ?, oneTimePassword = TRUE "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, oneTimePassword);
			pstmt.setString(2, username);

			int rowsUpdated =
					pstmt.executeUpdate();

			return rowsUpdated > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Returns whether a user's password is currently a
	 * one-time password.
	 *
	 * @param username the username being checked
	 *
	 * @return true if the user's password is marked as one-time
	 */
	public boolean isOneTimePassword(String username) {

		String query =
				"SELECT oneTimePassword "
				+ "FROM userDB WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getBoolean("oneTimePassword");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*******
	 * Clears the one-time password flag after the user
	 * successfully changes their password.
	 *
	 * @param username the username whose flag should be cleared
	 *
	 * @return true if the database row was updated
	 */
	public boolean clearOneTimePassword(String username) {

		String query =
				"UPDATE userDB "
				+ "SET oneTimePassword = FALSE "
				+ "WHERE userName = ?";

		try (PreparedStatement pstmt =
				connection.prepareStatement(query)) {

			pstmt.setString(1, username);

			int rowsUpdated =
					pstmt.executeUpdate();

			return rowsUpdated > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**************************************************************
	 * HW2 LESSONS LEARNED CRUD
	 **************************************************************/

	/**
	 * Creates a lesson learned after validating its content.
	 *
	 * @param lesson lesson to insert
	 * @return true if the lesson was inserted successfully
	 */
	public boolean createLessonLearned(LessonsLearned lesson) {
		if (lesson == null || !lesson.validate().isEmpty()) return false;

		String query = "INSERT INTO lessonLearnedDB "
				+ "(title, lessonText, author, rootCause, recommendation, "
				+ "keywords, timeEffort, category, isLocked, lockedFields, effortVisible) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(
				query, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, lesson.getTitle());
			pstmt.setString(2, lesson.getDescription());
			pstmt.setString(3, lesson.getAuthor());
			pstmt.setString(4, lesson.getRootCause());
			pstmt.setString(5, lesson.getRecommendation());
			pstmt.setString(6, lesson.getKeywords());
			pstmt.setString(7, lesson.getTimeEffort());
			pstmt.setString(8, lesson.getCategory());
			pstmt.setBoolean(9, lesson.getIsLocked());
			pstmt.setString(10, String.join(",", lesson.getLockedFields()));
			pstmt.setBoolean(11, lesson.getEffortVisible());

			if (pstmt.executeUpdate() != 1) return false;
			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				if (keys.next()) lesson.setId(keys.getInt(1));
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Reads one lesson learned by its database id.
	 *
	 * @param id database id
	 * @return matching lesson, or null if no lesson is found
	 */
	public LessonsLearned getLessonLearned(int id) {
		String query = "SELECT * FROM lessonLearnedDB WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) return lessonLearnedFromRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns all lessons in ascending id order.
	 *
	 * @return a list wrapper containing all stored lessons
	 */
	public LessonsLearnedList getAllLessonsLearned() {
		List<LessonsLearned> lessons = new ArrayList<LessonsLearned>();
		String query = "SELECT * FROM lessonLearnedDB ORDER BY id";
		try (PreparedStatement pstmt = connection.prepareStatement(query);
				ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) lessons.add(lessonLearnedFromRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new LessonsLearnedList(lessons);
	}
	/*******
	 * <p> Method: int insertLesson(LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Creates a new row in the lesson table using the lesson parameter.  The
	 * database generates the id; this method sets both timestamps to the current time.  The
	 * lesson's keywords are stored too, so they can be searched. </p>
	 * 
	 * @param lesson specifies the lesson to be added to the database.
	 * 
	 * @return the generated id of the new lesson, or -1 if the lesson could not be saved
	 * 
	 */
	// Create: add a lesson learned
	public int insertLesson(LessonsLearned lesson) {
		String query = "INSERT INTO lessonDB (title, description, category, author, "
				+ "createdAt, updatedAt, keywords) VALUES (?, ?, ?, ?, ?, ?, ?)";
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try (PreparedStatement pstmt = connection.prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, lesson.getTitle());
			pstmt.setString(2, lesson.getDescription());
			pstmt.setString(3, lesson.getCategory());
			pstmt.setString(4, lesson.getAuthor());
			pstmt.setTimestamp(5, now);
			pstmt.setTimestamp(6, now);
			pstmt.setString(7, lesson.getKeywords());
			pstmt.executeUpdate();
			ResultSet keys = pstmt.getGeneratedKeys();
			if (keys.next()) return keys.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Updates an existing lesson learned after validation.
	 *
	 * @param lesson lesson containing updated values and a valid id
	 * @return true if exactly one row was updated
	 */
	public boolean updateLessonLearned(LessonsLearned lesson) {
		if (lesson == null || lesson.getId() <= 0
				|| !lesson.validate().isEmpty()) return false;

		String query = "UPDATE lessonLearnedDB SET title = ?, lessonText = ?, "
				+ "author = ?, rootCause = ?, recommendation = ?, keywords = ?, "
				+ "timeEffort = ?, category = ?, isLocked = ?, lockedFields = ?, "
				+ "effortVisible = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lesson.getTitle());
			pstmt.setString(2, lesson.getDescription());
			pstmt.setString(3, lesson.getAuthor());
			pstmt.setString(4, lesson.getRootCause());
			pstmt.setString(5, lesson.getRecommendation());
			pstmt.setString(6, lesson.getKeywords());
			pstmt.setString(7, lesson.getTimeEffort());
			pstmt.setString(8, lesson.getCategory());
			pstmt.setBoolean(9, lesson.getIsLocked());
			pstmt.setString(10, String.join(",", lesson.getLockedFields()));
			pstmt.setBoolean(11, lesson.getEffortVisible());
			pstmt.setInt(12, lesson.getId());
			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Deletes a lesson learned by database id.
	 *
	 * @param id database id
	 * @return true if exactly one row was deleted
	 */
	public boolean deleteLessonLearned(int id) {
		if (id <= 0) return false;
		String query = "DELETE FROM lessonLearnedDB WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Builds a lesson entity from the current database row.
	 *
	 * @param rs result set positioned at a lesson row
	 * @return the populated lesson
	 * @throws SQLException if a column cannot be read
	 */
	private LessonsLearned lessonLearnedFromRow(ResultSet rs) throws SQLException {
		LessonsLearned lesson = new LessonsLearned(
				rs.getString("author"), rs.getString("title"),
				rs.getString("lessonText"), rs.getString("category"));
		lesson.setId(rs.getInt("id"));
		lesson.setRootCause(rs.getString("rootCause"));
		lesson.setRecommendation(rs.getString("recommendation"));
		lesson.setKeywords(rs.getString("keywords"));
		lesson.setTimeEffort(rs.getString("timeEffort"));
		lesson.setCategory(rs.getString("category"));
		lesson.setIsLocked(rs.getBoolean("isLocked"));
		String storedLockedFields = rs.getString("lockedFields");
		if (storedLockedFields != null && !storedLockedFields.isEmpty()) {
			java.util.Set<String> fields = new java.util.HashSet<String>();
			for (String field : storedLockedFields.split(",")) {
				if (!field.trim().isEmpty()) fields.add(field.trim());
			}
			lesson.setLockedFields(fields);
		}
		lesson.setEffortVisible(rs.getBoolean("effortVisible"));
		return lesson;
	}

	/*******
	 * <p> Method: LessonsLearned lessonFromRow(ResultSet rs) </p>
	 *
	 * <p> Description: Builds a LessonsLearned entity from the current row of the
	 * lessonDB result set.  Restored during PR review: the lessonLearnedDB mapper
	 * above had taken this method's name, which left getLessonById,
	 * getLessonsByAuthor and searchLessonsByAuthor without a row mapper. </p>
	 *
	 * @param rs result set positioned at a lessonDB row
	 *
	 * @return the populated LessonsLearned entity
	 *
	 * @throws SQLException if a column cannot be read
	 */
	private LessonsLearned lessonFromRow(ResultSet rs) throws SQLException {
		LessonsLearned lesson = new LessonsLearned(rs.getInt("id"), rs.getString("author"),
				rs.getString("title"), rs.getString("description"), rs.getString("category"),
				rs.getTimestamp("createdAt"), rs.getTimestamp("updatedAt"));
		lesson.setKeywords(rs.getString("keywords"));		// null becomes "" in setKeywords
		return lesson;
	}

	/*******
	 * <p> Method: LessonsLearned getLessonById(int id) </p>
	 * 
	 * <p> Description: Get one lesson given its id.</p>
	 * 
	 * @param id is the id of the lesson
	 * 
	 * @return the lesson, or null if there is no lesson with that id
	 * 
	 */
	// Read: get one lesson learned
	public LessonsLearned getLessonById(int id) {
		String query = "SELECT * FROM lessonDB WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return lessonFromRow(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/*******
	 * <p> Method: List getLessonsByAuthor(String author) </p>
	 * 
	 * <p> Description: Get every lesson added by one contributor, oldest first.</p>
	 * 
	 * @param author is the username of the contributor
	 * 
	 * @return a list of that contributor's lessons (empty if there are none)
	 * 
	 */
	// Read: get all of one contributor's lessons learned
	public List<LessonsLearned> getLessonsByAuthor(String author) {
		List<LessonsLearned> result = new ArrayList<LessonsLearned>();
		String query = "SELECT * FROM lessonDB WHERE author = ? ORDER BY id";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, author);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) result.add(lessonFromRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	/*******
	 * <p> Method: List getAllLessons() </p>
	 * 
	 * <p> Description: Get the complete collection of lessons in the lesson table, from every
	 * contributor, oldest first.  The result can be placed in a LessonsLearnedList, from which
	 * author and keyword subsets can be built.</p>
	 * 
	 * @return a list of every stored lesson (empty if there are none)
	 * 
	 */
	// Read: get the complete collection of lessons learned
	public List<LessonsLearned> getAllLessons() {
		List<LessonsLearned> result = new ArrayList<LessonsLearned>();
		String query = "SELECT * FROM lessonDB ORDER BY id";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) result.add(lessonFromRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}


	/*******
	 * <p> Method: List searchLessonsByAuthor(String author, String keyword) </p>
	 * 
	 * <p> Description: Get the subset of one contributor's lessons whose title, description or
	 * keywords contain the keyword, ignoring case.  These are the same fields that
	 * LessonsLearnedList.subsetByKeyword searches, so both searches give the same result; the
	 * author and category are not searched.  The characters % and _ in the keyword are treated
	 * as ordinary text, not as SQL wildcards.</p>
	 * 
	 * @param author is the username of the contributor
	 * 
	 * @param keyword is the text to look for
	 * 
	 * @return a list of the matching lessons (empty if there are none)
	 * 
	 */
	// Read: get the subset of lessons learned that match a keyword
	public List<LessonsLearned> searchLessonsByAuthor(String author, String keyword) {
		List<LessonsLearned> result = new ArrayList<LessonsLearned>();
		String pattern = "%" + keyword.toLowerCase().replace("\\", "\\\\").replace("%", "\\%")
				.replace("_", "\\_") + "%";
		String query = "SELECT * FROM lessonDB WHERE author = ? AND "
				+ "(LOWER(title) LIKE ? ESCAPE '\\' OR LOWER(description) LIKE ? ESCAPE '\\' "
				+ "OR LOWER(keywords) LIKE ? ESCAPE '\\') "
				+ "ORDER BY id";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, author);
			pstmt.setString(2, pattern);
			pstmt.setString(3, pattern);
			pstmt.setString(4, pattern);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) result.add(lessonFromRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	/*******
	 * <p> Method: boolean updateLesson(int id, String title, String description,
	 * 		String category) </p>
	 * 
	 * <p> Description: Change the title, description and category of a lesson and set its updated
	 * timestamp to the current time.  The id, author and created timestamp do not change.</p>
	 * 
	 * @param id is the id of the lesson
	 * 
	 * @param title is the new title
	 * 
	 * @param description is the new description
	 * 
	 * @param category is the new category
	 * 
	 * @return true if a lesson was updated, else false
	 * 
	 */
	// Update: change a lesson learned
	public boolean updateLesson(int id, String title, String description, String category) {
		String query = "UPDATE lessonDB SET title = ?, description = ?, category = ?, "
				+ "updatedAt = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, title);
			pstmt.setString(2, description);
			pstmt.setString(3, category);
			pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(5, id);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/*******
	 * <p> Method: boolean deleteLesson(int id) </p>
	 * 
	 * <p> Description: Remove a lesson from the lesson table given its id.</p>
	 * 
	 * @param id is the id of the lesson
	 * 
	 * @return true if a lesson was deleted, else false
	 * 
	 */
	// Delete: remove a lesson learned
	public boolean deleteLesson(int id) {
		String query = "DELETE FROM lessonDB WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	/*******
	 * <p> Method: int getNumberOfLessons() </p>
	 * 
	 * <p> Description: Returns the number of lessons currently in the lesson table.</p>
	 * 
	 * @return the number of lessons in the database
	 * 
	 */
	public int getNumberOfLessons() {
		String query = "SELECT COUNT(*) AS count FROM lessonDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) return resultSet.getInt("count");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


	/*******
	 * <p> Method: void deleteAllLessons() </p>
	 * 
	 * <p> Description: Removes every lesson.  This exists only so the automated tests can start
	 * from a known state.  Use it only with the test database.</p>
	 * 
	 */
	public void deleteAllLessons() {
		try {
			statement.execute("DELETE FROM lessonDB");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**************************************************************
	 * CURRENT USER GETTERS
	 **************************************************************/

	/*******
	 * Returns the current username.
	 *
	 * @return the current username
	 */
	public String getCurrentUsername() {
		return currentUsername;
	}

	/*******
	 * Returns the current password.
	 *
	 * @return the current password
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}

	/*******
	 * Returns the current first name.
	 *
	 * @return the current first name
	 */
	public String getCurrentFirstName() {
		return currentFirstName;
	}

	/*******
	 * Returns the current middle name.
	 *
	 * @return the current middle name
	 */
	public String getCurrentMiddleName() {
		return currentMiddleName;
	}

	/*******
	 * Returns the current last name.
	 *
	 * @return the current last name
	 */
	public String getCurrentLastName() {
		return currentLastName;
	}

	/*******
	 * Returns the current preferred first name.
	 *
	 * @return the current preferred first name
	 */
	public String getCurrentPreferredFirstName() {
		return currentPreferredFirstName;
	}

	/*******
	 * Returns the current email address.
	 *
	 * @return the current email address
	 */
	public String getCurrentEmailAddress() {
		return currentEmailAddress;
	}

	/*******
	 * Returns whether the current user has the administrator role.
	 *
	 * @return true if the current user is an administrator; false otherwise
	 */
	public boolean getCurrentAdminRole() {
		return currentAdminRole;
	}

	/*******
	 * Returns whether the current user has the Contributor role.
	 *
	 * @return true if the current user has the Contributor role
	 */
	public boolean getCurrentNewRole1() {
		return currentNewRole1;
	}

	/*******
	 * Returns whether the current user has the Viewer role.
	 *
	 * @return true if the current user has the Viewer role
	 */
	public boolean getCurrentNewRole2() {
		return currentNewRole2;
	}

	/*******
	 * Dumps the database to the console.
	 *
	 * @throws SQLException if a database access error occurs
	 */
	public void dump() throws SQLException {

		String query =
				"SELECT * FROM userDB";

		ResultSet resultSet =
				statement.executeQuery(query);

		ResultSetMetaData meta =
				resultSet.getMetaData();

		while (resultSet.next()) {

			for (int i = 0;
					i < meta.getColumnCount();
					i++) {

				System.out.println(
						meta.getColumnLabel(i + 1)
						+ ": "
						+ resultSet.getString(i + 1));
			}

			System.out.println();
		}

		resultSet.close();
	}

	/*******
	 * Closes the database connection.
	 */
	public void closeConnection() {

		try {

			if (statement != null)
				statement.close();

		} catch (SQLException se2) {
			se2.printStackTrace();
		}

		try {

			if (connection != null)
				connection.close();

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
