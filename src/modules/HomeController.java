package modules;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import application.AutoCompleteComboBoxListener;
import application.BarcodeController;
import application.Global;
import application.ValidateHandle;
import database.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import models.Bill;
import models.Buy;

/**
 * FXML Controller class
 *
 * @author tuyenlm
 */
public class HomeController implements Initializable {
	@FXML
	private Label lblTotal;
	@FXML
	private JFXButton btnBarcode, btnSearchProduct, btnPay;
	@FXML
	private TextField txtBarcode;
	@FXML
	private HBox hboxBarcode;
	@FXML
	private TableView<Buy> tableBuyList;
	@FXML
	private Tab tabHistoryPay, tabUnknowProduct, tabAbc;
	private ObservableList<Bill> lists = FXCollections.observableArrayList();
	@FXML
	private TableView<Bill> tableHistoryPay;
	@FXML
	private ListView<String> listViewProductCount;
	HashMap<String, String> hasMUser = new HashMap<String, String>();
	protected Connection connection;
	private DbHandler handler;
	Statement stmt = null;
	ResultSet rs = null;
	StackPane deptStackPane;
	private ComboBox<String> searchProduct = new ComboBox<String>();
	private HashMap<Integer, ObservableList<Buy>> itemBuyList = new HashMap<Integer, ObservableList<Buy>>();
	private DecimalFormat decimalFormat = new DecimalFormat("###,###");
	private int billId = 0;
	private static ObservableList<Buy> itemsBill;
	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		ObservableList<String> itemLabel = FXCollections.observableArrayList("Mã Barcode", "Tên sản phẩm", "Giá gốc", "Giá bán", "Đơn vị", "Vị trí", "Danh mục", "Ngày tạo", "Mô tả");
		listViewProductCount.getItems().addAll(itemLabel);
		tabUnknowProduct.setOnSelectionChanged((event) -> {
			if (tabUnknowProduct.isSelected()) {
				try {
					System.out.println("select tabUnknowProduct");
					FXMLLoader loader = new FXMLLoader(getClass().getResource("tab2.fxml"));
					tabUnknowProduct.setContent(loader.load());
				} catch (Exception e) {
					Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		});
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
					String[] barCodeSp = txtBarcode.getText().split("-");
					System.out.println(barCodeSp.length);
					if (barCodeSp.length > 1) {
						switch (barCodeSp[0]) {
						case "BI":
							getBill(txtBarcode.getText().trim());
							break;
						default:
							break;
						}
					} else {
						doSearch(txtBarcode.getText(), "barcodeProduct");
					}
				}
			}

			private void getBill(String billCode) {
				try {
					connection = handler.getConnection();
					String query = "SELECT sales.id,sales.productid,sales.quantitys,sales.priceSell, sales.billId, products.nameProduct FROM bills LEFT OUTER JOIN sales ON (sales.billId = Bills.id) LEFT OUTER JOIN products ON (products.id = Sales.productid)  WHERE Bills.barcodeBill = '"
							+ billCode + "' ORDER BY Sales.id ASC";
					System.out.println(query);
					ResultSet rs = connection.createStatement().executeQuery(query);
					itemBuyList.clear();
					if (rs.isBeforeFirst()) {
						while (rs.next()) {
							ObservableList<Buy> items = FXCollections.observableArrayList();
							System.out.println(rs.getString("nameProduct"));
							items.add(new Buy(rs.getInt("productId"), rs.getString("nameProduct"), rs.getInt("quantitys"), rs.getInt("priceSell"), (rs.getInt("priceSell") * rs.getInt("quantitys")),
									rs.getInt("id")));
							itemBuyList.put(rs.getInt("productId"), items);
							billId = rs.getInt("billId");
						}
					}
					builTableBuy();
					connection.close();
					txtBarcode.requestFocus();
					txtBarcode.selectAll();
				} catch (Exception e) {
					Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
				}
			}

		});
		buildTableHistoryPay();
		tableHistoryPay.setOnMousePressed(new EventHandler<MouseEvent>() {
			

			@SuppressWarnings("deprecation")
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					try {
						int id = tableHistoryPay.getSelectionModel().getSelectedItem().getId();
						connection = handler.getConnection();
						String query = "SELECT sales.id,sales.quantitys,sales.priceSell,products.nameProduct FROM sales LEFT OUTER JOIN products ON (sales.productId = products.id) WHERE Sales.billId = '"
								+ id + "' ORDER BY Sales.id ASC";
						System.out.println(query);
						ResultSet rs = connection.createStatement().executeQuery(query);
						itemBuyList.clear();
						ObservableList<Buy> items = FXCollections.observableArrayList();
						if (rs.isBeforeFirst()) {
							while (rs.next()) {
								System.out.println(rs.getString("nameProduct"));
								items.add(new Buy(0, rs.getString("nameProduct"), rs.getInt("quantitys"),  rs.getInt("priceSell"), (rs.getInt("quantitys") * rs.getInt("priceSell")), 0));
								
							}
						}
						rs.close();
						connection.close();
						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tableHistoryPay.getSelectionModel().getSelectedItem().getCreatedAtB());
						Dialog<Pair<String, String>> dialog = new Dialog<>();
						dialog.setTitle(date.toLocaleString());
						setItemDetailBill(items);
						dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
						Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
						closeButton.setDisable(false);
						FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/modules/itemsForBill.fxml"));
						Parent root = (Parent) fxmlLoader.load();
						dialog.getDialogPane().setContent(root);
						dialog.showAndWait();
					} catch (Exception e) {
						Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
					}
				}
			}

			
			
		});
	}
	public void setItemDetailBill(ObservableList<Buy> items) {
		HomeController.itemsBill = items;
		
	}
	public static ObservableList<Buy> getItemDetailBill(){
		return itemsBill;
	}
	@SuppressWarnings("unchecked")
	private void buildTableHistoryPay() {
		lists.clear();
		tableHistoryPay.getItems().clear();
		tableHistoryPay.getColumns().clear();
		try {
			connection = handler.getConnection();
			String query = "SELECT *,to_char(priceTotal, '999,999,990') as priceTotalDe FROM bills LEFT OUTER JOIN users ON (bills.sellerId = users.id) ORDER BY bills.id DESC";
			System.out.println(query);
			ResultSet rs = connection.createStatement().executeQuery(query);

			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					lists.add(new Bill(rs.getInt("id"), rs.getString("barcodeBill"), rs.getString("priceTotalDe"), rs.getBoolean("statusBill"), rs.getString("createdAtB"), rs.getString("fullname")));
				}
			}
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}
		TableColumn<Bill, Number> indexColumn = new TableColumn<Bill, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setStyle("-fx-alignment: CENTER;");
		indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(tableHistoryPay.getItems().indexOf(column.getValue()) + 1));
		tableHistoryPay.getColumns().add(0, indexColumn);
		tableHistoryPay.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Bill> observable, Bill oldValue, Bill newValue) -> {
			if (newValue == null) {
				return;
			}

		});
		TableColumn<Bill, String> barcodeBillCol = new TableColumn<Bill, String>("Mã Hóa Đơn");
		barcodeBillCol.setCellValueFactory(new PropertyValueFactory<>("barcodeBill"));
		barcodeBillCol.setCellFactory(TextFieldTableCell.<Bill>forTableColumn());
		barcodeBillCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		barcodeBillCol.setMinWidth(120);
		barcodeBillCol.setMaxWidth(120);
		TableColumn<Bill, String> priceTotalCol = new TableColumn<Bill, String>("Tổng Tiền");
		priceTotalCol.setCellValueFactory(new PropertyValueFactory<>("priceTotal"));
		priceTotalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceTotalCol.setMinWidth(100);
		priceTotalCol.setMaxWidth(100);
		TableColumn<Bill, String> createdAtBCol = new TableColumn<Bill, String>("Thời Gian");
		createdAtBCol.setCellValueFactory(new PropertyValueFactory<>("createdAtB"));
		createdAtBCol.setCellFactory(column -> new TableCell<Bill, String>() {

			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					try {
						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(item);
						setText(date.toLocaleString());
					} catch (Exception e) {
						Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
					}
				}
			}
		});
		TableColumn<Bill, Integer> sellerNameCol = new TableColumn<Bill, Integer>("Nhân Viên");
		sellerNameCol.setCellValueFactory(new PropertyValueFactory<>("sellerName"));
		sellerNameCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		sellerNameCol.setMinWidth(120);
		sellerNameCol.setMaxWidth(120);
		tableHistoryPay.getColumns().addAll(createdAtBCol, priceTotalCol, barcodeBillCol, sellerNameCol);
		tableHistoryPay.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableHistoryPay.getItems().addAll(lists);
	}

	@SuppressWarnings("unchecked")
	private void builTableBuy() {
		txtBarcode.clear();
		txtBarcode.requestFocus();
		txtBarcode.selectAll();
		tableBuyList.getItems().clear();
		tableBuyList.getColumns().clear();
		TableColumn<Buy, Number> indexColumn = new TableColumn<Buy, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setStyle("-fx-alignment: CENTER;");
		indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(tableBuyList.getItems().indexOf(column.getValue()) + 1));
		tableBuyList.getColumns().add(0, indexColumn);
		tableBuyList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Buy> observable, Buy oldValue, Buy newValue) -> {
			if (newValue == null) {
				return;
			}
		});
		TableColumn<Buy, String> nameProductCol = new TableColumn<Buy, String>("Sản Phẩm");
		nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
		nameProductCol.setCellFactory(TextFieldTableCell.<Buy>forTableColumn());
		nameProductCol.setStyle("-fx-alignment: CENTER-LEFT;");
		TableColumn<Buy, Integer> quatityCol = new TableColumn<Buy, Integer>("Số Lượng");
		quatityCol.setCellValueFactory(new PropertyValueFactory<>("quatity"));
		quatityCol.setMinWidth(110);
		quatityCol.setMaxWidth(110);
		quatityCol.setCellFactory(column -> new TableCell<Buy, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					HBox hbQ = new HBox();
					TextField txtQuatity = new TextField();
					txtQuatity.setMaxHeight(23);
					txtQuatity.setMinHeight(23);
					txtQuatity.setMaxWidth(30);
					txtQuatity.setMinWidth(30);
					txtQuatity.setStyle("-fx-padding: 1px 5px 1px 5px;");
					txtQuatity.setText(item.toString());
					JFXButton btnExcept = new JFXButton();
					btnExcept.setText("-");
					btnExcept.setStyle("-fx-background-color: #333; -fx-text-fill:white;-fx-padding: -5px");
					btnExcept.setMaxHeight(21);
					btnExcept.setMinHeight(21);
					btnExcept.setMaxWidth(30);
					btnExcept.setMinWidth(30);
					JFXButton btnPlus = new JFXButton();
					btnPlus.setText("+");
					btnPlus.setMaxHeight(21);
					btnPlus.setMinHeight(21);
					btnPlus.setMaxWidth(30);
					btnPlus.setMinWidth(30);
					btnPlus.setStyle("-fx-background-color: #333; -fx-text-fill:white;-fx-padding: -5px");
					hbQ.getChildren().addAll(btnExcept, txtQuatity, btnPlus);
					hbQ.setSpacing(5);
					setGraphic(hbQ);
					txtQuatity.textProperty().addListener((a, b, c) -> {
						if (ValidateHandle.isNumericInteger(c) && Integer.parseInt(c) > 0) {
							// tableBuyList.getItems().get(getIndex()).setQuatity(Integer.parseInt(c));
							tableBuyList.getItems().get(getIndex()).setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice() * Integer.parseInt(c));
							txtQuatity.setText(c);
						} else {
							txtQuatity.setText(b);
						}
						updateTotal();
					});
					txtQuatity.setOnKeyReleased(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent ke) {
							if (ke.getText().trim().isEmpty() && !txtQuatity.getText().trim().isEmpty()) {
								tableBuyList.getItems().get(getIndex()).setQuatity(Integer.parseInt(txtQuatity.getText()));
								tableBuyList.getItems().get(getIndex()).setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice() * Integer.parseInt(txtQuatity.getText()));
							}
							updateTotal();
						}
					});
					txtQuatity.focusedProperty().addListener((a, b, c) -> {
						if (b) {
							tableBuyList.getItems().get(getIndex()).setQuatity(Integer.parseInt(txtQuatity.getText()));
							tableBuyList.getItems().get(getIndex()).setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice() * Integer.parseInt(txtQuatity.getText()));
							updateTotal();
						}
					});
					btnExcept.setOnAction(e -> {
						if ((tableBuyList.getItems().get(getIndex()).getQuatity() - 1) > 0) {
							tableBuyList.getItems().get(getIndex()).setQuatity(tableBuyList.getItems().get(getIndex()).getQuatity() - 1);
							tableBuyList.getItems().get(getIndex()).setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice() * tableBuyList.getItems().get(getIndex()).getQuatity());
						}
						updateTotal();
					});
					btnPlus.setOnAction(e -> {
						tableBuyList.getItems().get(getIndex()).setQuatity(tableBuyList.getItems().get(getIndex()).getQuatity() + 1);
						tableBuyList.getItems().get(getIndex()).setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice() * tableBuyList.getItems().get(getIndex()).getQuatity());
						updateTotal();
					});
				}
			}
		});
		TableColumn<Buy, Integer> priceCol = new TableColumn<Buy, Integer>("Đơn Giá");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setMinWidth(70);
		priceCol.setMaxWidth(70);
		priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceCol.setCellFactory(column -> new TableCell<Buy, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(decimalFormat.format(item));
				}
			}
		});
		TableColumn<Buy, Integer> priceTotalCol = new TableColumn<Buy, Integer>("Thành Tiền");
		priceTotalCol.setCellValueFactory(new PropertyValueFactory<>("priceTotal"));
		priceTotalCol.setMinWidth(80);
		priceTotalCol.setMaxWidth(80);
		priceTotalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceTotalCol.setCellFactory(column -> new TableCell<Buy, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(decimalFormat.format(item));
				}
			}
		});
		TableColumn<Buy, Buy> deleteColumn = new TableColumn<>("Xóa");
		deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteColumn.setMinWidth(40);
		deleteColumn.setMaxWidth(40);
		deleteColumn.setCellFactory(param -> new TableCell<Buy, Buy>() {
			@Override
			protected void updateItem(Buy item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setGraphic(null);
					return;
				}
				JFXButton btnDelete = new JFXButton("X");
				btnDelete.setStyle("-fx-background-color: #F48024;-fx-text-fill:black;-fx-padding: -5px");
				btnDelete.setMaxHeight(21);
				btnDelete.setMinHeight(21);
				btnDelete.setMaxWidth(30);
				btnDelete.setMinWidth(30);
				setGraphic(btnDelete);
				btnDelete.setOnAction(event -> {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(Global.tsl_lblConfirmDialog);
					alert.setHeaderText(null);
					alert.setContentText("Xóa '" + item.getNameProduct() + "' ?");
					Optional<ButtonType> action = alert.showAndWait();
					if (action.get() == ButtonType.OK) {
						itemBuyList.remove(tableBuyList.getItems().get(getIndex()).getProductId());
						tableBuyList.getItems().remove(getIndex());
						updateTotal();
					}
				});
			}
		});

		tableBuyList.getColumns().addAll(nameProductCol, priceCol, quatityCol, priceTotalCol, deleteColumn);
		tableBuyList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		int val = 0;
		for (Map.Entry<Integer, ObservableList<Buy>> entry : itemBuyList.entrySet()) {
			ObservableList<Buy> value = entry.getValue();
			tableBuyList.getItems().addAll(value);
			val += value.get(0).getPriceTotal();
		}
		lblTotal.setText(decimalFormat.format(val));
	}

	@FXML
	private void actionPay() {
		createBill();
	}

	private void updateTotal() {
		if (itemBuyList.size() > 0) {
			int val = 0;
			for (Map.Entry<Integer, ObservableList<Buy>> entry : itemBuyList.entrySet()) {
				ObservableList<Buy> value = entry.getValue();
				val += value.get(0).getPriceTotal();
			}
			lblTotal.setText(decimalFormat.format(val));
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
		if (searchProduct.getItems().size() == 0) {
			connection = handler.getConnection();
			try {
				String query = "SELECT nameProduct FROM products";
				ResultSet rs = connection.createStatement().executeQuery(query);
				if (rs.isBeforeFirst()) {
					while (rs.next()) {
						searchProduct.getItems().add(rs.getString("nameProduct"));
					}
				}
			} catch (Exception e) {
				Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		new AutoCompleteComboBoxListener<>(searchProduct);
		searchProduct.valueProperty().addListener((a, b, c) -> {
			if (searchProduct.getSelectionModel().getSelectedItem() != null)
				doSearch(searchProduct.getSelectionModel().getSelectedItem(), "nameProduct");
		});
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

	private void doSearch(String val, String field) {
		try {
			connection = handler.getConnection();
			String query = "SELECT * FROM products WHERE " + field + " = '" + val.trim() + "'";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					if (!itemBuyList.containsKey(rs.getInt("id"))) {
						ObservableList<Buy> items = FXCollections.observableArrayList();
						items.add(new Buy(rs.getInt("id"), rs.getString("nameProduct"), 1, rs.getInt("priceSell"), rs.getInt("priceSell"), 0));
						itemBuyList.put(rs.getInt("id"), items);
					} else {
						if (itemBuyList.get(rs.getInt("id")).get(0).getProductId() == rs.getInt("id")) {
							itemBuyList.get(rs.getInt("id")).get(0).setQuatity(itemBuyList.get(rs.getInt("id")).get(0).getQuatity() + 1);
							itemBuyList.get(rs.getInt("id")).get(0).setPriceTotal(rs.getInt("priceSell") * itemBuyList.get(rs.getInt("id")).get(0).getQuatity());
						}
					}
				}
			} else {
				System.out.println("tim ko ra");
			}
			txtBarcode.requestFocus();
			txtBarcode.selectAll();
			builTableBuy();
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void createBill() {
		if (itemBuyList.size() > 0) {
			try {
				connection = handler.getConnection();
				stmt = connection.createStatement();
				int priceTotal = Integer.parseInt(lblTotal.getText().replaceAll(",", ""));
				boolean statusBill = true;
				int sellerId = 1;
				String barcodeBill = "";
				if (billId == 0) {
					barcodeBill = "BI-" + String.valueOf(Instant.now().getEpochSecond());
					String sqlBills = "insert into Bills (priceTotal,statusBill,sellerId,barcodeBill) " + "values ('" + priceTotal + "','" + statusBill + "','" + sellerId + "','" + barcodeBill + "')";
					stmt.execute(sqlBills, Statement.RETURN_GENERATED_KEYS);
					ResultSet keyset = stmt.getGeneratedKeys();
					keyset.next();
					billId = keyset.getInt("id");
				}
				for (Map.Entry<Integer, ObservableList<Buy>> entry : itemBuyList.entrySet()) {
					ObservableList<Buy> value = entry.getValue();
					Statement stmt2 = connection.createStatement();
					if (value.get(0).getSaleId() == 0) {
						String sqlInsertSale = "insert into Sales (productId,quantityS,priceSell,billId) " + "values ('" + value.get(0).getProductId() + "','" + value.get(0).getQuatity() + "','"
								+ value.get(0).getPrice() + "','" + billId + "')";
						stmt2.execute(sqlInsertSale);
					} else {
						String sqlUpdate = "UPDATE Sales SET quantitys ='" + value.get(0).getQuatity() + "', priceSell ='" + value.get(0).getPrice() + "' WHERE id = '" + value.get(0).getSaleId()
								+ "'; ";
						stmt.executeUpdate(sqlUpdate);
					}
					connection.commit();
					stmt2.close();
				}
				connection.commit();
				stmt.close();
				connection.close();
				itemBuyList.clear();
				builTableBuy();
				tabHistoryPay.isSelected();
				billId = 0;
				buildTableHistoryPay();
				if (!barcodeBill.isEmpty())
					BarcodeController.renderBarcode(barcodeBill);
			} catch (Exception e) {
				Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}
}