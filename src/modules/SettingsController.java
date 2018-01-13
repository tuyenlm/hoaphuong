package modules;

import java.awt.RenderingHints.Key;
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

import org.apache.poi.ss.formula.functions.Value;
import org.omg.CosNaming._BindingIteratorImplBase;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import application.BarcodeController;
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
	private JFXButton btnRandom, btnCreate;
	@FXML
	private Pane paneImageBarcode;
	@FXML
	private ComboBox<String> comboboxActionList;

	private ObservableList<Revenue> lists = FXCollections.observableArrayList();
	private DbHandler dbHandler;
	private static Connection connection;
	private static HashMap<String, String> actionList = new HashMap<>();

	public void initialize(URL url, ResourceBundle rb) {
		dbHandler = new DbHandler();
		actionList.put("pay", "Chỉ Thanh Toán");
		actionList.put("printAndPay", "Thanh Toán Và In");
		actionList.put("clear", "Xóa Thanh Toán");
		actionList.forEach((key, value) -> {
			System.out.println(value);
			comboboxActionList.getItems().add(value.toString());
		});
	}

	@FXML
	private void actionRandom() {
		txtBarcode.setText(String.valueOf(Instant.now().getEpochSecond()));
		renderBarcodeImage(txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim());
	}

	@FXML
	private void actionCreate() {
		if (!txtBarcode.getText().trim().isEmpty()) {
			renderBarcodeImage(txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim());
		}

	}

	private void renderBarcodeImage(String barcode) {
		try {
			File fileNewBar = BarcodeController.renderBarcode(barcode);
			ImageView imgBarcode = new ImageView();
			imgBarcode.setFitHeight(60);
			imgBarcode.setStyle("-fx-border-color:#333");
			imgBarcode.setImage(new Image(fileNewBar.toURI().toString()));
			paneImageBarcode.getChildren().clear();
			paneImageBarcode.getChildren().add(imgBarcode);
		} catch (Exception ie) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ie);
		}
	}

	@FXML
	private void actionSave() {
		if (!txtBarcode.getText().isEmpty()) {
			String barcode = txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim();
			String action = comboboxActionList.getSelectionModel().getSelectedItem();
			System.out.println(barcode);
			System.err.println(action);
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