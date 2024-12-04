package yumster.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
	private String dbUrl = "jdbc:mysql://localhost:3306/yumster";
	private String dbUname = "yumster";
	private String dbPassword = "weakpassword123!@#";
	private String dbDriver = "com.mysql.cj.jdbc.Driver";
	private static DbConnection instance = null;
	Connection connection = null;

	/**
	 * Get singleton instance of db connection
	 */
	public static synchronized DbConnection getInstance() {
		if (instance == null)
			instance = new DbConnection();

		return instance;
	}

	public void loadDriver(String dbDriver) {
		try {
			Class.forName(dbDriver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initializes, or returns the currently open connection for the object
	 */
	public synchronized Connection getConnection() {
		if (connection == null) {
			try {
				loadDriver(dbDriver);
				connection = DriverManager.getConnection(dbUrl, dbUname, dbPassword);
				connection.setAutoCommit(true);
//			} catch (ClassNotFoundException e) {
//				// JDBC library likely not found
//				System.err.println(e.getMessage());
			} catch (SQLException e) {
				// Connection failed
				System.err.println(e.getMessage());
			}
		} else {
			try {
				if (connection.isClosed()) {
					loadDriver(dbDriver);
					connection = DriverManager.getConnection(dbUrl, dbUname, dbPassword);
					connection.setAutoCommit(true);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connection;
	}
}