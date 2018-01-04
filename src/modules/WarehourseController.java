package modules;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.DbHandler;

public class WarehourseController {
	private static DbHandler handler;
	private static Connection connection;
	private static Statement stmt6 = null;

	public static void updateData(int id, int num) {
		try {
			handler = new DbHandler();
			connection = handler.getConnection();
			stmt6 = connection.createStatement();
			String sqlUpdate = "UPDATE warehouse SET quantitysold = quantitysold + '" + num
					+ "', remainingAmount = remainingAmount - '" + num + "' WHERE productId = '" + id + "'; ";
			stmt6.executeUpdate(sqlUpdate);
			connection.commit();
		} catch (Exception e) {
			Logger.getLogger(WarehourseController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
