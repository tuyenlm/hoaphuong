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
			String sqlCreateBills = "CREATE TABLE IF NOT EXISTS Bills (id SERIAL PRIMARY KEY NOT NULL, priceTotal INT, priceReceive INT, statusBill boolean, sellerId INT, barcodeBill VARCHAR(20), createdAtB timestamp default current_timestamp);";
			stmt.execute(sqlCreateBills);
			String sqlCreateSales = "CREATE TABLE IF NOT EXISTS Sales (id SERIAL PRIMARY KEY NOT NULL, productId INT, quantityS INT, priceSell INT, priceOrigin INT, billId INT);";
			stmt.execute(sqlCreateSales);
			String sqlCreate = "CREATE TABLE IF NOT EXISTS Users (id SERIAL PRIMARY KEY NOT NULL, username VARCHAR(30), password VARCHAR(35), fullname VARCHAR(90), barcodeUser VARCHAR(10),type INT, createdAt timestamp default current_timestamp);";
			stmt.execute(sqlCreate);
			String sqlCreateUnknowProduct = "CREATE TABLE IF NOT EXISTS UnknowProduct (id SERIAL PRIMARY KEY NOT NULL, barcodeUnknow VARCHAR(20));";
			stmt.execute(sqlCreateUnknowProduct);
			String sqlCreatePurchases = "CREATE TABLE IF NOT EXISTS Purchases (id SERIAL PRIMARY KEY NOT NULL, barcodePur VARCHAR(20), SupplierId INT, PaymentStatus Int, GrandTotal INT, Paid INT, createdAtP timestamp default current_timestamp, note VARCHAR(200), userCreated INT);";
			stmt.execute(sqlCreatePurchases);
			String sqlCreatePurchasesItems = "CREATE TABLE IF NOT EXISTS PurchasesItems (id SERIAL PRIMARY KEY NOT NULL, purchasesId INT, productId INT, quantityPur Int, subTotal INT, originCost INT, sellCost INT);";
			stmt.execute(sqlCreatePurchasesItems);
			String sqlCreateCatalogs = "CREATE TABLE IF NOT EXISTS Catalogs (id SERIAL PRIMARY KEY NOT NULL, nameCatalog VARCHAR(90), descriptionCatalog VARCHAR(90), barcodeCatalog VARCHAR(20), createdAtC timestamp default current_timestamp);";
			stmt.execute(sqlCreateCatalogs);
			String sqlCreateproducts = "CREATE TABLE IF NOT EXISTS products (id SERIAL PRIMARY KEY NOT NULL, nameProduct VARCHAR(90), catalogId INT, barcodeProduct VARCHAR(20), descriptionProduct VARCHAR(100), location VARCHAR(50), priceOrigin Int, priceSell Int, unit VARCHAR(10), createdAtP timestamp default current_timestamp);";
			stmt.execute(sqlCreateproducts);
			String sqlWarehouse = "CREATE TABLE IF NOT EXISTS Warehouse(id SERIAL PRIMARY KEY NOT NULL, productId INT, quantitySold INT, remainingAmount INT)";
			stmt.execute(sqlWarehouse);
			String sqlBarcodeCMD = "CREATE TABLE IF NOT EXISTS BarcodeCmd(id SERIAL PRIMARY KEY NOT NULL, BarcodeCmd VARCHAR(30), action VARCHAR(30), descriptionCmd VARCHAR(100))";
			stmt.execute(sqlBarcodeCMD);
			String sqlCreateQuantityPrice = "CREATE TABLE IF NOT EXISTS QuantityPrice (id SERIAL PRIMARY KEY NOT NULL, productId INT, quantity INT, sellCost INT, enable boolean);";
			stmt.execute(sqlCreateQuantityPrice);
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
