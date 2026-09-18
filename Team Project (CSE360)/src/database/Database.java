package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;

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

			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();

			// Uncomment this if you want to completely reset the database.
			// statement.execute("DROP ALL OBJECTS");

			createTables();

		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
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
	}

	/*******
	 * Returns true if there are no users in the database.
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
	 * Validates an Admin user's login.
	 */
	public boolean loginAdmin(User user) {

		String query =
				"SELECT * FROM userDB "
				+ "WHERE userName = ? "
				+ "AND password = ? "
				+ "AND adminRole = TRUE";

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
	 * Validates a Role 1 user's login.
	 */
	public boolean loginRole1(User user) {

		String query =
				"SELECT * FROM userDB "
				+ "WHERE userName = ? "
				+ "AND password = ? "
				+ "AND newRole1 = TRUE";

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

			currentUsername = rs.getString("userName");
			currentPassword = rs.getString("password");
			currentFirstName = rs.getString("firstName");
			currentMiddleName = rs.getString("middleName");
			currentLastName = rs.getString("lastName");
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
		if (username == null || username.length() == 0) {
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
	 * CURRENT USER GETTERS
	 **************************************************************/

	public String getCurrentUsername() {
		return currentUsername;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getCurrentFirstName() {
		return currentFirstName;
	}

	public String getCurrentMiddleName() {
		return currentMiddleName;
	}

	public String getCurrentLastName() {
		return currentLastName;
	}

	public String getCurrentPreferredFirstName() {
		return currentPreferredFirstName;
	}

	public String getCurrentEmailAddress() {
		return currentEmailAddress;
	}

	public boolean getCurrentAdminRole() {
		return currentAdminRole;
	}

	public boolean getCurrentNewRole1() {
		return currentNewRole1;
	}

	public boolean getCurrentNewRole2() {
		return currentNewRole2;
	}

	/*******
	 * Dumps the database to the console.
	 */
	public void dump() throws SQLException {

		String query = "SELECT * FROM userDB";

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
