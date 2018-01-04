package modules;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CosNaming._BindingIteratorImplBase;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import application.RenderBarcodeThread;
import database.DbHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import models.Bill;
import models.Revenue;

public class SettingsController implements Initializable {
	@FXML
	private TableView<Revenue> tableRevenue;

	@FXML
	private Tab tabBarcodeCommand, tabWarehouse, tabImportGoods;

	@FXML
	private TextField txtBarcode, txtBarcodeRefix;
	@FXML
	private JFXButton btnRandom;
	@FXML
	private Pane paneImageBarcode;

	private ObservableList<Revenue> lists = FXCollections.observableArrayList();
	private DbHandler dbHandler;
	private static Connection connection;

	public void initialize(URL url, ResourceBundle rb) {
		dbHandler = new DbHandler();
	}

	@FXML
	private void actionRandom() {
		txtBarcode.setText(String.valueOf(Instant.now().getEpochSecond()));
		String barCode = txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim();
		RenderBarcodeThread barcodeThr = new RenderBarcodeThread(barCode);
		barcodeThr.start();

		try {
			// barcodeThr.t.join();
			File fileNewBar = new File("barImg/" + barCode + ".png");
			System.out.println(fileNewBar.exists());
			System.out.println(fileNewBar);
			ImageView imgBarcode2 = new ImageView();
			Image image2 = new Image(fileNewBar.toURI().toString());
			imgBarcode2.setImage(image2);
			paneImageBarcode.getChildren().add(imgBarcode2);
		} catch (Exception ie) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ie);
		}

	}

	// public static void refreshImage(File _file) {
	// try {
	// imgBarcode.setImage(
	// new
	// Image("file:/Users/leminhtuyen/eclipse-workspace/hoaphuong/barImg/CMD-1514926379.png"));
	// } catch (Exception e) {
	// Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null,
	// e);
	// }
	// }

}