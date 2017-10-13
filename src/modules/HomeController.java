package modules;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jfoenix.controls.JFXButton;
import database.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import models.Buy;

/**
 * FXML Controller class
 *
 * @author tuyenlm
 */
public class HomeController implements Initializable {
	@FXML
	private JFXButton btnBarcode, btnSearchProduct;
	@FXML
	private TextField txtBarcode;
	@FXML
	private HBox hboxBarcode;
	@FXML
	private TableView<Buy> tableBuyList;

	HashMap<String, String> hasMUser = new HashMap<String, String>();
	protected Connection connection;
	private DbHandler handler;
	Statement stmt = null;
	ResultSet rs = null;
	StackPane deptStackPane;
	private ComboBox<String> searchProduct = new ComboBox<String>();
	private HashMap<Integer, ObservableList<Buy>> itemBuyList = new HashMap<Integer, ObservableList<Buy>>();

	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		btnBarcode.setStyle("-fx-background-color: green;");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtBarcode.requestFocus();
			}
		});
		handler = new DbHandler();

		txtBarcode.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getText().trim().isEmpty() && !txtBarcode.getText().trim().isEmpty()) {
					connection = handler.getConnection();
					try {
						String query = "SELECT * FROM products WHERE barcodeProduct = '" + txtBarcode.getText().trim() + "'";
						ResultSet rs = connection.createStatement().executeQuery(query);
						if (rs.isBeforeFirst()) {
							while (rs.next()) {
								if (!itemBuyList.containsKey(rs.getInt("id"))) {
									ObservableList<Buy> items = FXCollections.observableArrayList();
									items.add(new Buy(rs.getInt("id"), rs.getString("nameProduct"), 1, rs.getInt("priceSell"), rs.getInt("priceSell")));
									itemBuyList.put(rs.getInt("id"), items);
								} else {
									if (itemBuyList.get(rs.getInt("id")).size() > 0) {
										for (int i = 0; i < itemBuyList.get(rs.getInt("id")).size(); i++) {
											if (itemBuyList.get(rs.getInt("id")).get(i).getProductId() == rs.getInt("id")) {
												itemBuyList.get(rs.getInt("id")).get(i).setQuatity(itemBuyList.get(rs.getInt("id")).get(i).getQuatity() + 1);
												itemBuyList.get(rs.getInt("id")).get(i).setPriceTotal(rs.getInt("priceSell") * itemBuyList.get(rs.getInt("id")).get(i).getQuatity());
											}
										}
									}
								}
							}
						}
						txtBarcode.requestFocus();
						txtBarcode.selectAll();
						builTableBuy();
						connection.close();
					} catch (Exception e) {
						Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void builTableBuy() {
		tableBuyList.getItems().clear();
		tableBuyList.getColumns().clear();
		TableColumn<Buy, Number> indexColumn = new TableColumn<Buy, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(tableBuyList.getItems().indexOf(column.getValue()) + 1));
		tableBuyList.getColumns().add(0, indexColumn);
		tableBuyList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Buy> observable, Buy oldValue, Buy newValue) -> {
			if (newValue == null) {
				return;
			}
		});
		TableColumn<Buy, String> nameProductCol = new TableColumn<Buy, String>("Tên sản phẩm");
		nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
		nameProductCol.setCellFactory(TextFieldTableCell.<Buy>forTableColumn());

		TableColumn<Buy, Integer> quatityCol = new TableColumn<Buy, Integer>("So luong");
		quatityCol.setCellValueFactory(new PropertyValueFactory<>("quatity"));
		quatityCol.setMinWidth(60);
		quatityCol.setMaxWidth(60);
		quatityCol.setCellFactory(column -> new TableCell<Buy, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					TextField txtQuatity = new TextField();
					txtQuatity.setMaxHeight(23);
					txtQuatity.setMinHeight(23);
					txtQuatity.setText(item.toString());
					setGraphic(txtQuatity);
				}
			}
		});
		TableColumn<Buy, Integer> priceCol = new TableColumn<Buy, Integer>("Don gia");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setMinWidth(70);
		priceCol.setMaxWidth(70);
		TableColumn<Buy, Integer> priceTotalCol = new TableColumn<Buy, Integer>("Thanh tien");
		priceTotalCol.setCellValueFactory(new PropertyValueFactory<>("priceTotal"));
		priceTotalCol.setMinWidth(80);
		priceTotalCol.setMaxWidth(80);
		tableBuyList.getColumns().addAll(nameProductCol, priceCol, quatityCol, priceTotalCol);
		tableBuyList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		for (Map.Entry<Integer, ObservableList<Buy>> entry : itemBuyList.entrySet()) {
			ObservableList<Buy> value = entry.getValue();
			tableBuyList.getItems().addAll(value);
		}
	}

	@FXML
	private void actionSwitchBarcode() {
		if (hboxBarcode.getChildren().contains(searchProduct)) {
			searchProduct.setVisible(false);
			txtBarcode.setVisible(true);
			searchProduct.setMinWidth(0);
			searchProduct.setMaxWidth(0);
			txtBarcode.setMinWidth(395);
			txtBarcode.setMaxWidth(395);
			txtBarcode.requestFocus();
			btnBarcode.setStyle("-fx-background-color: green;");
			btnSearchProduct.setStyle("-fx-background-color: black; -fx-background-radius: 5; -fx-border-color: white; -fx-border-radius: 3; -fx-border-width: 2px;");
		}
	}

	@FXML
	private void actionSwitchProduct() {
		searchProduct.setVisible(true);
		searchProduct.setMaxWidth(395);
		searchProduct.setMinWidth(395);
		txtBarcode.setVisible(false);
		txtBarcode.setMinWidth(0);
		txtBarcode.setMaxWidth(0);
		if (!hboxBarcode.getChildren().contains(searchProduct)) {
			hboxBarcode.getChildren().add(0, searchProduct);
		}
		btnBarcode.setStyle("-fx-background-color: white;");
		btnSearchProduct.setStyle("-fx-background-color: green; -fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 3; -fx-border-width: 2px;");
	}
}