package com.hotmail.AdrianSR.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the MySQL database class.
 * <p>
 * @author AdrianSR
 */
public final class MySQL {

	/**
	 * Class values.
	 */
	private final String host;
	private final int    port;
	private final String database;
	private final String username;
	private final String password;
	private Connection   dataSource;
	private int          lostConnections = 0;
	private final boolean autoreconnect;

	/**
	 * Construct a new MySQL database.
	 * <p>
	 * @param host the Host name.
	 * @param port the port.
	 * @param database the database name.
	 * @param username the user name.
	 * @param password the user password.
	 */
	public MySQL(final String host, final int port, final String database, final String username,
			final String password, final boolean autoreconnect) {
		// load database data.
		this.host     = host;
		this.port     = port;
		this.database = database;
		this.username = username;
		this.password = password;
		this.autoreconnect = autoreconnect;
	}
	
	/**
	 * Checks this MySQL is valid
	 * and can be connected with 
	 * gived data in constructor.
	 * <p>
	 * @return true if is valid.
	 */
	public boolean isValid() {
		return host != null && !host.isEmpty() 
				&& database != null && !database.isEmpty()
				&& username != null
				&& password != null;
	}

	/**
	 * Connect to database.
	 */
	public synchronized void connect() throws SQLException {
		// make url.
		String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=" + this.autoreconnect;

		// check driver.
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("could not connect to MySQL!. JDBC driver unavailable!");
			e.printStackTrace();
			return;
		}

		// start conection.
		try {
			this.dataSource = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * Disconnect from database.
	 */
	public void disconnect() {
		try {
			// check state.
			if (this.dataSource != null && !this.dataSource.isClosed()) {
				// close.
				this.dataSource.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check is connected to MySQL.
	 * <p>
	 * @return true if is connected.
	 */
	public boolean isConnected() {
		try {
			return getConnection() != null 
					&& !getConnection().isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Execute update.
	 * <p>
	 * @param update the Update to execute.
	 */
	public void update(String update) {
		// make connection and statement.
		Connection conn      = null;
		PreparedStatement ps = null;
		
		// execute update.
		try {
			conn = getConnection();
			ps   = conn.prepareStatement(update);
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Execute query.
	 * <p>
	 * @param query the Query to execute.
	 * @return the query {@link ResultSet} or null.
	 */
	public ResultSet query(String query) {
		// make connection and statement.
		Connection conn      = null;
		PreparedStatement ps = null;
		
		// execute query.
		try {
			conn = getConnection();
			ps   = conn.prepareStatement(query);
			return ps.executeQuery();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Get connection. (this auto try again when is not connected).
	 * <p>
	 * @return the current connection or null.
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		// check connection.
		if (this.dataSource == null || this.dataSource.isClosed()) {
			// try again if auto reconnect is enabled.
			if (autoreconnect) {
				// try again connection.
				connect();
				
				// count lost connection.
				lostConnections++;

				// print faild connections.
				System.out.println("[INFO] MySQL database has " + lostConnections + " connections faileds!");
				return this.dataSource;
			}
		}
		return this.dataSource;
	}
}
