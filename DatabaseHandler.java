import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Handles all database connections and requests. Only one database 
 * handler should be active at any time. Part of the {@link Driver} 
 * example.
 */
public class DatabaseHandler {
	/** username for mySQL database */
	private String username;
	
	/** password for mySQL database */
	private String password;
	
	/** mySQL database to use */
	private String database;
	
	/** mySQL server hostname */
	private String hostname;
	
	/** properly formatted String for mySQL server */
	private String server;
	
	/** A log4j logger associated with the {@link Driver} class. */
	private static Logger log = Logger.getLogger(Driver.class);
	
	/** Single, static instance that should be used by all other classes. */
	private static final DatabaseHandler DBINSTANCE = new DatabaseHandler();

	/**
	 * Verifies the mySQL configuration, JDBC driver, and database
	 * connection. If any step fails, forces program to exit.
	 */
	DatabaseHandler() {
		// use Status enum to indicate specific error messages
		Status status = null;
		
		// test ability to load database configuration
		status = loadConfig();
		
		if(status != Status.OK) {
			log.fatal(status);
			System.exit(-status.code());
		}
		
		// test database connection
		status = testConnection();
		
		if(status != Status.OK) {
			log.fatal(status);
			System.exit(-status.code());
		}
	}
	
	/**
	 * Used by other classes to get {@link DatabaseHandler} instance.
	 * There should be only one active DatabaseHandler instance at any
	 * time.
	 * 
	 * @return {@link DatabaseHandler} instance
	 */
	public static DatabaseHandler getInstance() {
		return DBINSTANCE;
	}

	/**
	 * Utility method that makes sure the provided String is not null
	 * or empty.
	 * 
	 * @param text
	 * @return true if String is not empty or null
	 */
	public static boolean validString(String text) {
		return (text != null) && !text.trim().isEmpty();
	}
	
	/**
	 * Loads the database configuration from the database.properties file. 
	 * @return {@link Status#OK} if successful
	 */
	private Status loadConfig() {
		Status status = Status.ERROR;
		Properties config = new Properties();

		try {			
			config.load(new FileReader("database.properties"));
			username = config.getProperty("username");
			password = config.getProperty("password");
			database = config.getProperty("database");
			hostname = config.getProperty("hostname");
			
			if(validString(username) && validString(password) 
					&& validString(database) && validString(hostname)) {
				log.debug("Using user " + username + " and database " + database + " on " + hostname + ".");
				server = "jdbc:mysql://" + hostname + "/" + database;
				status = Status.OK;
			}
			else {
				log.error("Invalid database configuration file. Please make sure to specify username, password, database, and hostname in database.properties.");
				status = Status.INVALID_CONFIG;
			}
		}
		catch(FileNotFoundException ex) {
			log.error("Unable to locate database.properties configuration file.", ex);
			status = Status.NO_CONFIG;
		}
		catch(IOException ex) {
			log.error("Unable to read database.properties configuration file.", ex);
			status = Status.NO_CONFIG;
		}
		catch(Exception ex) {
			log.error("Unknown exception occurred.", ex);
			status = Status.ERROR;
		}	
		
		return status;
	}

	public ResultSet getComments(String username){
		Connection connection = null;
		Statement sql   = null;
		ResultSet results     = null;
		
		log.debug("Attempting to create SQL statement...");
		sql = null;
		
		try {
			connection = DriverManager.getConnection(server, this.username, password);
			sql = connection.createStatement();
			results   = sql.executeQuery("SELECT * FROM users WHERE user = \"" + username + " \";");

			log.debug("Created SQL statement.");
		}
		catch(Exception ex) {
			log.warn("Unable to create SQL statement!");
			System.exit(1);
		}
		
		return results;
	}
	
	
	/**
	 * Tests database configuration with simple SQL statement.
	 * @return {@link Status#OK} if successful
	 */

	private Status testConnection() {
		Status status = Status.ERROR;

		Connection connection = null;
		Statement statement   = null;
		ResultSet results     = null;
		
		try {
			connection = DriverManager.getConnection(server, username, password);
			statement  = connection.createStatement();
			results    = statement.executeQuery("SHOW TABLES;");

			/*
			 * We should verify the table setup here, and possibly
			 * create the tables if needed.
			 */
			
			int num = 0;
			
			while(results.next()) {
				num++;
			}
			
			log.debug("Database connection test complete. " + num + " tables found.");
			status = Status.OK;
		}
		catch(SQLException ex) {
			log.error("Unable to establish database connection.", ex);
			status = Status.CONNECTION_FAILED;
		}
		finally {
			try {
				statement.close();
				connection.close();
			}
			catch(Exception ex) {
				log.warn("Encounted exception while trying to close database connection.", ex);
			}
		}
		
		return status;
	}
	
	/**
	 * Returns an MD5 hash of the password as a hexidecimal String.
	 * 
	 * @param password
	 * @return hash
	 */
	private String getHash(String password) {
		String hash = password;
		
		try {
			/*
			 * You should use a stronger hash function with salting.
			 */
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			
			BigInteger bigint = new BigInteger(1, md.digest());
			hash = bigint.toString(16);
			
			// Be careful of losing the leading zero
			if(hash.length() % 2 != 0)
				hash = "0" + hash;
		}
		catch(Exception ex) {
			log.warn("Unable to hash password.");
		}
				
		return hash;
	}
	
	/**
	 * Tests if a user already exists in the database.
	 * 
	 * @param connection
	 * @param user
	 * @return true if user exists
	 */
	private boolean userExists(Connection connection, String user) {
		assert connection != null;
		
		try {
			/*
			 * Use PreparedStatement whenever placing user input into
			 * the SQL statement. Place a ? where user input should be
			 * inserted.
			 */
			String sql = "SELECT user FROM users WHERE user = ?";
			
			/*
			 * To include the user input, use the setString() method.
			 */
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, user);
			
			ResultSet results = statement.executeQuery();
			
			if(results.next()) {
				return true;
			}
		}
		catch(SQLException ex) {
			log.error("Encountered SQL exception while accessing database. Please check database configuration.", ex);
		}
		
		return false;
	}

	/**
	 * Attempts to register a new user with our website.
	 * 
	 * @param newuser username of new user
	 * @param newpass password of new user
	 * @return {@link Status#OK} if registration succeeded
	 */
	public Status registerUser(String newuser, String newpass, String name, String email) {
		Status status = Status.OK;
		
		String sql = null;		
		Connection connection = null;
		PreparedStatement statement = null;
		
		// test for null values
//		if(newuser == null || newpass == null || name == null || email == null) {
//			return Status.NULL_VALUES;
//		}
		
		// make sure able to create database connection
		try {			
			connection = DriverManager.getConnection(server, username, password);
		}
		catch(SQLException ex) {
			log.error("Unable to establish database connection.", ex);
			return Status.CONNECTION_FAILED;
		}
		
		// test if username already exists
		if(userExists(connection, newuser)) {
			status = Status.DUPLICATE_USER;
		}
		else {
			// attempt to insert new username and password into database
			try {				
				sql = "INSERT INTO users (user, password, name, email) VALUES (?, ?, ?, ?);";
				statement = connection.prepareStatement(sql);
				statement.setString(1, newuser);
				statement.setString(2, getHash(newpass));			
				statement.setString(3, name);
				statement.setString(4, email);
				statement.executeUpdate();
				
				status = Status.OK;
			}
			catch(SQLException ex) {
				log.error("Encountered SQL exception while registering user.", ex);
				status = Status.SQL_ERROR;
			}
		}
		
		try {
			statement.close();
			connection.close();
		}
		catch(Exception ignored){}
		
		return status;
	}
	public Status deleteUser(String user){
		Status status = Status.OK;
		
		String sql = null;		
		Connection connection = null;
		PreparedStatement statement = null;
		
		
		// make sure able to create database connection
		try {			
			connection = DriverManager.getConnection(server, username, password);
		}
		catch(SQLException ex) {
			log.error("Unable to establish database connection.", ex);
			return Status.CONNECTION_FAILED;
		}
		
		// test if username already exists
		
		// attempt to insert new username and password into database
		try {				
			sql = "DELETE FROM users WHERE user = \'" + user + "\';"; 
			statement = connection.prepareStatement(sql);
			
			statement.executeUpdate();
			
			status = Status.OK;
		}
		catch(SQLException ex) {
				log.error("Encountered SQL exception while registering user.", ex);
				status = Status.SQL_ERROR;
			}
	
		
		try {
			statement.close();
			connection.close();
		}
		catch(Exception ignored){}
		
		return status;
	}


	public Status changeUser(String user, String newpass, String name, String email) {
		Status status = Status.OK;
		
		String sql = null;		
		Connection connection = null;
		PreparedStatement statement = null;
		
		
		// make sure able to create database connection
		try {			
			connection = DriverManager.getConnection(server, username, password);
		}
		catch(SQLException ex) {
			log.error("Unable to establish database connection.", ex);
			return Status.CONNECTION_FAILED;
		}
		
		// test if username already exists
		
		// attempt to insert new username and password into database
		try {				
			if(newpass.equals("")){
				sql = "UPDATE users SET email = '"+ email + "'," +
						" name = '"+ name +"' WHERE user = \'" +user + "\';";
			}else{
				sql = "UPDATE users SET password = '" + getHash(newpass) + "', email = '"+ email + "'," +
						" name = '"+ name +"' WHERE user = \'" +user + "\';";
			}
			
			
			statement = connection.prepareStatement(sql);
			
			statement.executeUpdate();
			
			status = Status.OK;
		}
		catch(SQLException ex) {
				log.error("Encountered SQL exception while registering user.", ex);
				status = Status.SQL_ERROR;
			}
	
		
		try {
			statement.close();
			connection.close();
		}
		catch(Exception ignored){}
		
		return status;
	}

	/**
	 * Attempts to verify the provided username and password with database.
	 * 
	 * @param user
	 * @param pass
	 * @return {@link Status#OK} if valid (user, pass) pair provided
	 */
	public Status verifyLogin(String user, String pass) {
		Status status = Status.ERROR;
		String sql = null;
		
		Connection connection = null;
		PreparedStatement statement = null; 
		
		try {			
			connection = DriverManager.getConnection(server, username, password);
			
			sql = "SELECT user FROM users WHERE user = ? AND password = ?";
			
			statement = connection.prepareStatement(sql);
			statement.setString(1, user);
			statement.setString(2, getHash(pass));
			
			log.debug("Executing statement " + sql);
			ResultSet results = statement.executeQuery();
			
			if(results.next()) {
				status = Status.OK;
			}
			else {
				status = Status.INCORRECT_LOGIN;
			}			
		}
		catch(Exception ex) {
			log.warn("Unable to verify user " + user + ".", ex);
			status = Status.CONNECTION_FAILED;
		}

		try {
			statement.close();
			connection.close();
		}
		catch(Exception ignored){}		
		
		return status;
	}
}
