package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHandler {
	protected Connection conn;

	public Connection getConnection() {
		String host = "localhost";
		String port = "5432";
		String dbname = "hoaphuong";
		String user = "postgres";
		String password = "postgres";
		final String ConnectionString = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
		try {
			conn = DriverManager.getConnection(ConnectionString, user, password);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return conn;

	}

}
