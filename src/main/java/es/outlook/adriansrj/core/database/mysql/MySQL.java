package es.outlook.adriansrj.core.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import org.apache.commons.lang.Validate;

import es.outlook.adriansrj.core.util.StringUtil;

/**
 * Class for interacting with a MySQL database.
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 11:50 AM
 */
public final class MySQL {
	
	/**
	 * Connection URL format.
	 */
	private static final String URL_FORMAT = "jdbc:mysql://" 
			+ "%s" // host
			+ ":" 
			+ "%d" // port
			+ "/" 
			+ "%s" // database
			+ "?autoReconnect=" 
			+ "%s" // auto reconnect
			+ "&"
			+ "useSSL="
			+ "%s" // use ssl
			; 
	
	/**
	 * The JDBC driver class.
	 */
	private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";

	private final String       host;
	private final int          port;
	private final String   database;
	private final String   username;
	private final String   password;
	private final boolean reconnect;
	private final boolean       ssl;
	
	private Connection connection;
	private int  lost_connections;
	
	/**
	 * Constructs the MySQL database.
	 * <p>
	 * @param host the host name.
	 * @param port the port number.
	 * @param database the database name.
	 * @param username the user name.
	 * @param password the user password.
	 * @param reconnect <strong>{@code true}</strong> to auto reconnect.
	 * @param ssl <strong>{@code true}</strong> to use SSL.
	 */
	public MySQL ( String host , int port , String database , String username , String password , boolean reconnect , boolean ssl ) {
		Validate.isTrue ( !StringUtil.isBlank ( host ) , "the host cannot be null or empty!" );
		Validate.isTrue ( !StringUtil.isBlank ( database ) , "the database cannot be null or empty!" );
		Validate.notNull ( username , "the username cannot be null!" );
		Validate.notNull ( password , "the password cannot be null!" );
		
		this.host      = host;
		this.port      = port;
		this.database  = database;
		this.username  = username;
		this.password  = password;
		this.reconnect = reconnect;
		this.ssl       = ssl;
	}
	
	/**
	 * Constructs the MySQL database.
	 * <p>
	 * @param host the host name.
	 * @param port the port number.
	 * @param database the database name.
	 * @param username the user name.
	 * @param password the user password.
	 * @param reconnect <strong>{@code true}</strong> to auto reconnect.
	 */
	public MySQL ( String host , int port , String database , String username , String password , boolean reconnect ) {
		this ( host , port , database , username , password , reconnect , true );
	}
	
	/**
	 * Gets whether connected to MySQL.
	 * <p>
	 * @return true if connected.
	 */
	public boolean isConnected ( ) {
		try {
			return this.connection != null && !this.connection.isClosed ( );
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * <h1>Returns:</h1>
	 * <ul>
	 * <li>The current connection if connected to MySQL:
	 * <li>The new connection if and only if:
	 * <ul>
	 * <li>It wasn't connected.
	 * <li>The auto-reconnection is enabled.
	 * <li>The attempt to get connection was successfully.
	 * </ul>
	 * <li><strong>{@code null}</strong> if:
	 * <ul>
	 * <li>It wasn't connected and the auto-reconnection is disabled.
	 * <li>The auto-reconnection is enabled but the attempt to get connection was
	 * unsuccessfully.
	 * </ul>
	 * </ul>
	 * <p>
	 * @return the connection or null if not connected.
	 * @throws SQLTimeoutException   when the driver has determined that the timeout
	 *                               value has been exceeded and has at least tried
	 *                               to cancel the current database connection
	 *                               attempt.
	 * @throws IllegalStateException if the JDBC drivers is unavailable.
	 * @exception SQLException if a database access error occurs.
	 */
	public Connection getConnection ( ) 
			throws IllegalStateException, SQLException {
		if ( !isConnected ( ) && reconnect ) {
			this.lost_connections ++;
			this.connect();
		}
		return this.isConnected() ? this.connection : null;
	}
	
	/**
	 * The times the connection was lost.
	 * <p>
	 * @return times the connection was lost, or
	 *         <strong>{@code -1}</strong> if the auto-reconnection is disabled.
	 */
	public int getLostConnections ( ) {
		return reconnect ? lost_connections : -1;
	}
	
	/**
	 * Stars the connection with MySQL.
	 * <p>
	 * @throws IllegalStateException if the JDBC drivers is unavailable.
	 * @exception SQLException if a database access error occurs.
	 * @throws SQLTimeoutException when the driver has determined that the timeout
	 *                             has been exceeded and has at least tried to
	 *                             cancel the current database connection attempt.
	 */
	public synchronized void connect ( ) 
			throws IllegalStateException , SQLException , SQLTimeoutException {
		try {
			Class.forName ( DRIVER_CLASS );
		} catch ( ClassNotFoundException ex ) {
			throw new IllegalStateException ( "could not connect to MySQL! the JDBC driver is unavailable!" );
		}
		
		this.connection = DriverManager.getConnection (
				String.format ( URL_FORMAT , host , port , database , reconnect , ssl ) , 
				username , password );
	}
	
	/**
	 * Closes the connection with MySQL.
	 * <p>
	 * @throws IllegalStateException if currently not connected, the connection
	 *                               should be checked before calling this:
	 *                               {@link #isConnected()}.
	 * @throws SQLException          if a database access error occurs.
	 */
	public void disconnect ( ) throws SQLException {
		if ( !isConnected ( ) ) {
			throw new IllegalStateException ( "not connected!" );
		}
		
		this.connection.close();
		this.connection = null;
	}
	
	/**
	 * Executes the desired SQL statement.
	 * <p>
	 * @param statement an SQL statement that may contain one or more '?' IN
	 *                  parameter placeholders.
	 * @throws SQLException          if a database access error occurs or this
	 *                               method is called on a closed connection.
	 * @throws IllegalStateException if the JDBC drivers is unavailable.
	 * @throws SQLTimeoutException   when the driver has determined that the timeout
	 *                               has been exceeded and has at least tried to
	 *                               cancel the current database connection attempt.
	 */
	public void update ( String statement ) 
			throws SQLTimeoutException, IllegalStateException, SQLException {
		final Connection connection = getConnection();
		if ( connection == null ) {
			return;
		}
		
		/* executing update */
		PreparedStatement instance = connection.prepareStatement ( statement );
		
		instance.executeUpdate ( );
		instance.close ( );
	}
	
	/**
	 * Executes the desired SQL query statement and returns the ResultSet object
	 * generated by the query.
	 * <p>
	 * @param statement an SQL statement that may contain one or more '?' IN
	 *                  parameter placeholders.
	 * @return a ResultSet object that contains the data produced by the query, or
	 *         <strong>{@code null}</strong> if not connected.
	 * @throws SQLTimeoutException   when the driver has determined that the timeout
	 *                               has been exceeded and has at least attempted to
	 *                               cancel the currently running {@code Statement}.
	 * @throws IllegalStateException if the JDBC drivers is unavailable.
	 * @exception SQLException if a database access error occurs or the SQL
	 *                         statement does not return a <code>ResultSet</code>
	 *                         object.
	 */
	public ResultSet query ( String statement ) 
			throws IllegalStateException, SQLException {
		final Connection connection = getConnection();
		if ( connection == null ) {
			return null;
		}
		return connection.prepareStatement ( statement ).executeQuery ( );
	}
}