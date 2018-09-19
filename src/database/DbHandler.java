package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHandler {
	protected Connection conn;
	public static String port = "5432";
	public static String host = "localhost";
	public static String dbname = "hoaphuong";
	public static String user = "postgres";
	public static String password = "postgres";

	public Connection getConnection() {

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