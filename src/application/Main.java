package application;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.DbHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
	protected Connection connection;
	private DbHandler handler;
	Statement stmt = null;

	@Override
	public void start(Stage primaryStage) {
		try {
			handler = new DbHandler();
			connection = handler.getConnection();
			stmt = connection.createStatement();
			String sqlCreateBills = "CREATE TABLE IF NOT EXISTS Bills (id SERIAL PRIMARY KEY NOT NULL, priceTotal INT, statusBill boolean, sellerId INT, barcodeBill VARCHAR(20), createdAtB timestamp default current_timestamp);";
			stmt.execute(sqlCreateBills);
			String sqlCreateSales = "CREATE TABLE IF NOT EXISTS Sales (id SERIAL PRIMARY KEY NOT NULL, productId INT, quantityS INT, priceSell INT, billId INT);";
			stmt.execute(sqlCreateSales);
			String sqlCreate = "CREATE TABLE IF NOT EXISTS Users (id SERIAL PRIMARY KEY NOT NULL, username VARCHAR(30), password VARCHAR(35), fullname VARCHAR(90), type INT, createdAt timestamp default current_timestamp);";
			stmt.execute(sqlCreate);
			String sqlCreateUnknowProduct = "CREATE TABLE IF NOT EXISTS UnknowProduct (id SERIAL PRIMARY KEY NOT NULL, barcodeUnknow VARCHAR(20));";
			stmt.execute(sqlCreateUnknowProduct);
			connection.commit();
			stmt.close();
			connection.close();
			Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch (Exception e) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void main(String[] args) {
		launch(args);

	}
}
