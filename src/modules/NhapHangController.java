package modules;

import java.awt.List;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import application.Global;
import application.RenderBarcodeThread;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import models.Purchases;
import models.PurchasesItems;

public class NhapHangController implements Initializable {
	@FXML
	private TableView<PurchasesItems> tableImport;
	@FXML
	private TableView<Purchases> tableHistoryImport;

	@FXML
	private Tab tabHistoryImport, tabNhaCungCap;
	@FXML
	private Pane RevenuePane;
	@FXML
	private JFXComboBox<String> comboboxPay;

	@FXML
	private TextField txtBarcode, txtPay;
	@FXML
	private Label lblTotal, lblThanhToan;
	@FXML
	private JFXButton btnSuccess, btnClear;

	private ObservableList<Purchases> HistoriesLists = FXCollections.observableArrayList();
	private HashMap<String, ObservableList<PurchasesItems>> itemsList = new HashMap<String, ObservableList<PurchasesItems>>();
	private DbHandler handler;
	private static Connection connection;
	private HashMap<Integer, Integer> dataMonth;
	private HashMap<Integer, Integer> gocToltal;
	private HashMap<Integer, Integer> thuveToltal;
	private String selectItem;
	private HashMap<String, Boolean> stsSuccess = new HashMap<>();
	private static ObservableList<PurchasesItems> itemsHistories;
	private static List itemsHistoriesEx = new List();

	public void initialize(URL url, ResourceBundle rb) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtBarcode.requestFocus();
				txtBarcode.selectAll();
			}
		});
		comboboxPay.getItems().add("Chưa Thanh Toán");
		comboboxPay.getItems().add("Thanh Toán");
		comboboxPay.getSelectionModel().select(0);
		handler = new DbHandler();
		txtBarcode.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().toString().equals("ENTER") && !txtBarcode.getText().trim().isEmpty()) {
					doSearch(txtBarcode.getText().trim());
				}
			}
		});
		buildTableItems();
		lblThanhToan.setDisable(true);
		txtPay.setDisable(true);
		comboboxPay.valueProperty().addListener((a, b, c) -> {
			if (comboboxPay.getSelectionModel().getSelectedItem().toString().equals("Thanh Toán")) {
				lblThanhToan.setDisable(false);
				txtPay.setDisable(false);
			} else {
				lblThanhToan.setDisable(true);
				txtPay.setDisable(true);
			}
		});

		tableImport.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					String id = tableImport.getSelectionModel().getSelectedItem().getId();
					String barcode = tableImport.getSelectionModel().getSelectedItem().getProductId();
					if (!id.equals("0")) {
						try {
							TabUnknowProductController ss = new TabUnknowProductController();
							HashMap<String, String> datas = ss.addProduct(barcode, "child");
							if (datas.size() > 0) {
								int soluong = tableImport.getSelectionModel().getSelectedItem().getQuantityPur();
								int giagoc = Integer.parseInt(datas.get("priceOriginDialog"));
								ObservableList<PurchasesItems> items = FXCollections.observableArrayList();
								items.add(new PurchasesItems("0", 0, datas.get("nameProduct"), 0,
										datas.get("productId"), soluong, giagoc,
										Integer.parseInt(datas.get("priceSell")), soluong * giagoc, barcode));

								itemsList.put(datas.get("productId"), items);
								itemsList.remove(barcode);
								buildTableItems();
								stsSuccess.put(barcode, true);
							}
						} catch (Exception e) {
							Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
						}
					}
				}
			}
		});
		buildTableHistories();
		tableHistoryImport.setOnMousePressed(new EventHandler<MouseEvent>() {

			@SuppressWarnings("deprecation")
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {

					try {
						int id = tableHistoryImport.getSelectionModel().getSelectedItem().getId();
						connection = handler.getConnection();
						String query = "SELECT PurchasesItems.*,Purchases.paymentStatus, Purchases.grandTotal, Purchases.paid, Products.nameProduct FROM PurchasesItems \r\n"
								+ "LEFT OUTER JOIN Purchases ON (PurchasesItems.purchasesId = Purchases.id) \r\n"
								+ "LEFT OUTER JOIN Products ON (PurchasesItems.productId = Products.id)\r\n"
								+ "WHERE PurchasesItems.purchasesId = '" + id + "' ORDER BY Purchases.createdatp ASC";
						System.out.println(query);
						ResultSet rs = connection.createStatement().executeQuery(query);
						ObservableList<PurchasesItems> items = FXCollections.observableArrayList();
						itemsHistoriesEx.clear();
						if (rs.isBeforeFirst()) {
							while (rs.next()) {
								items.add(new PurchasesItems(String.valueOf(id), rs.getInt("purchasesId"),
										rs.getString("nameProduct"), rs.getInt("remainingAmount"),
										rs.getString("productId"), rs.getInt("quantityPur"), rs.getInt("originCost"),
										rs.getInt("sellCost"), rs.getInt("subTotal"), ""));
								itemsHistoriesEx.add(String.valueOf(rs.getInt("paymentStatus")));
								itemsHistoriesEx.add(String.valueOf(rs.getInt("grandTotal")));
								itemsHistoriesEx.add(String.valueOf(rs.getInt("paid")));
								itemsHistoriesEx.add(String.valueOf(rs.getInt("purchasesId")));
							}
						}
						rs.close();
						connection.close();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm",
								new Locale("vi", "VN"));
						String date = simpleDateFormat.format(Timestamp
								.valueOf(tableHistoryImport.getSelectionModel().getSelectedItem().getCreatedAtP()));
						setItemDetailHistories(items);
						setItemDetailHistoriesEx(itemsHistoriesEx);
						Dialog<Pair<String, String>> dialog = new Dialog<>();
						dialog.setTitle("Chi tiết đơn nhập hàng của ngày: " + date);
						dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
						Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
						closeButton.setDisable(false);
						FXMLLoader fxmlLoader = new FXMLLoader(
								getClass().getResource("/modules/itemsForHistoryPurchas.fxml"));
						Parent root = (Parent) fxmlLoader.load();
						dialog.getDialogPane().setContent(root);
						dialog.showAndWait();
					} catch (Exception e) {
						Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
					}
				}
			}
		});
	}

	public void setItemDetailHistories(ObservableList<PurchasesItems> items) {
		NhapHangController.itemsHistories = items;
	}

	public static ObservableList<PurchasesItems> getItemDetailHistories() {
		return itemsHistories;
	}

	public void setItemDetailHistoriesEx(List items) {
		NhapHangController.itemsHistoriesEx = items;
	}

	public static List getItemDetailHistoriesEx() {
		return itemsHistoriesEx;
	}

	private void doSearch(String val) {
		try {
			RenderBarcodeThread barcodeThr = new RenderBarcodeThread(val);
			barcodeThr.start();
			connection = handler.getConnection();
			String query = "SELECT products.*,warehouse.remainingAmount FROM products LEFT JOIN warehouse ON (warehouse.productId = products.id)  WHERE products.barcodeProduct = '"
					+ val + "'";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					String productId = String.valueOf(rs.getInt("id"));
					selectItem = productId;
					if (!itemsList.containsKey(productId)) {
						ObservableList<PurchasesItems> items = FXCollections.observableArrayList();
						items.add(new PurchasesItems("0", 0, rs.getString("nameProduct"), rs.getInt("remainingAmount"),
								String.valueOf(rs.getInt("id")), 1, rs.getInt("priceOrigin"), rs.getInt("priceSell"),
								rs.getInt("priceOrigin"), val));
						itemsList.put(productId, items);
					} else {
						if (itemsList.get(productId).get(0).getProductId().equals(productId)) {
							itemsList.get(productId).get(0)
									.setQuantityPur(itemsList.get(productId).get(0).getQuantityPur() + 1);
							itemsList.get(productId).get(0).setSubTotal(
									rs.getInt("priceOrigin") * itemsList.get(productId).get(0).getQuantityPur());
						}
					}
				}
			} else {
				TabUnknowProductController.setVariable(val);
				selectItem = val;
				if (!itemsList.containsKey(val)) {
					ObservableList<PurchasesItems> items = FXCollections.observableArrayList();
					items.add(new PurchasesItems(val, 0, val, 0, val, 1, 1, 1, 1, val));
					itemsList.put(val, items);
				} else {
					if (itemsList.get(val).get(0).getProductId().equals(val)) {
						itemsList.get(val).get(0).setQuantityPur(itemsList.get(val).get(0).getQuantityPur() + 1);
						int valSub = itemsList.get(val).get(0).getOriginCost()
								* itemsList.get(val).get(0).getQuantityPur();
						itemsList.get(val).get(0).setSubTotal(valSub);
					}
				}
				stsSuccess.put(val, false);
			}
			buildTableItems();
			txtBarcode.requestFocus();
			txtBarcode.selectAll();
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void updateTotal() {
		System.out.println("itemsList.size() " + itemsList.size());
		if (itemsList.size() > 0) {
			int val = 0;
			for (Map.Entry<String, ObservableList<PurchasesItems>> entry : itemsList.entrySet()) {
				ObservableList<PurchasesItems> value = entry.getValue();
				val += value.get(0).getSubTotal();
			}
			lblTotal.setText(HomeController.decimalFormat.format(val));
		} else
			lblTotal.setText("0");
	}

	@SuppressWarnings("unchecked")
	private void buildTableItems() {
		try {
			txtBarcode.clear();
			txtBarcode.requestFocus();
			txtBarcode.selectAll();
			tableImport.getItems().clear();
			tableImport.getColumns().clear();
			TableColumn<PurchasesItems, Number> colorCol = new TableColumn<PurchasesItems, Number>("~");
			colorCol.setSortable(false);
			colorCol.setMinWidth(10);
			colorCol.setMaxWidth(10);
			colorCol.setCellValueFactory(
					column -> new ReadOnlyObjectWrapper<Number>(tableImport.getItems().indexOf(column.getValue()) + 1));
			colorCol.setCellFactory(column -> new TableCell<PurchasesItems, Number>() {

				@Override
				public void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText("");
						if (!tableImport.getItems().get(getIndex()).getId().equals("0")) {
							setStyle("-fx-background-color: red; -fx-text-fill: black;-fx-alignment: CENTER;");
						} else if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});
			tableImport.getColumns().add(0, colorCol);
			TableColumn<PurchasesItems, Number> indexColumn = new TableColumn<PurchasesItems, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(
					column -> new ReadOnlyObjectWrapper<Number>(tableImport.getItems().indexOf(column.getValue()) + 1));
			tableImport.getColumns().add(1, indexColumn);
			tableImport.getSelectionModel().selectedItemProperty()
					.addListener((ObservableValue<? extends PurchasesItems> observable, PurchasesItems oldValue,
							PurchasesItems newValue) -> {
						if (newValue == null)
							return;
					});
			indexColumn.setCellFactory(column -> new TableCell<PurchasesItems, Number>() {

				@Override
				public void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.toString());
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});
			TableColumn<PurchasesItems, String> nameProductCol = new TableColumn<PurchasesItems, String>("Sản Phẩm");
			nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
			nameProductCol.setCellFactory(TextFieldTableCell.<PurchasesItems>forTableColumn());
			nameProductCol.setStyle("-fx-alignment: CENTER-LEFT;");
			nameProductCol.setCellFactory(column -> new TableCell<PurchasesItems, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(item);
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});
			TableColumn<PurchasesItems, Integer> soluonghientaiCol = new TableColumn<PurchasesItems, Integer>(
					"SL Hiện tại");
			soluonghientaiCol.setCellValueFactory(new PropertyValueFactory<>("remainingAmount"));
			soluonghientaiCol.setMinWidth(90);
			soluonghientaiCol.setMaxWidth(90);
			soluonghientaiCol.setStyle("-fx-alignment: CENTER-LEFT;");
			soluonghientaiCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.toString());
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});
			TableColumn<PurchasesItems, Integer> quatityCol = new TableColumn<PurchasesItems, Integer>("Số Lượng");
			quatityCol.setCellValueFactory(new PropertyValueFactory<>("quantityPur"));
			quatityCol.setMinWidth(110);
			quatityCol.setMaxWidth(110);
			quatityCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

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
								tableImport.getItems().get(getIndex()).setSubTotal(
										tableImport.getItems().get(getIndex()).getOriginCost() * Integer.parseInt(c));
								txtQuatity.setText(c);
							} else
								txtQuatity.setText(b);
							updateTotal();
						});
						txtQuatity.setOnKeyReleased(new EventHandler<KeyEvent>() {
							public void handle(KeyEvent ke) {
								if (ke.getText().trim().isEmpty() && !txtQuatity.getText().trim().isEmpty()) {
									tableImport.getItems().get(getIndex())
											.setQuantityPur(Integer.parseInt(txtQuatity.getText()));
									tableImport.getItems().get(getIndex())
											.setSubTotal(tableImport.getItems().get(getIndex()).getOriginCost()
													* Integer.parseInt(txtQuatity.getText()));
								}
								updateTotal();
							}
						});
						txtQuatity.focusedProperty().addListener((a, b, c) -> {
							if (b) {
								tableImport.getItems().get(getIndex())
										.setQuantityPur(Integer.parseInt(txtQuatity.getText()));
								tableImport.getItems().get(getIndex())
										.setSubTotal(tableImport.getItems().get(getIndex()).getOriginCost()
												* Integer.parseInt(txtQuatity.getText()));
								updateTotal();
							}
						});
						btnExcept.setOnAction(e -> {
							if ((tableImport.getItems().get(getIndex()).getQuantityPur() - 1) > 0) {
								tableImport.getItems().get(getIndex())
										.setQuantityPur(tableImport.getItems().get(getIndex()).getQuantityPur() - 1);
								tableImport.getItems().get(getIndex())
										.setSubTotal(tableImport.getItems().get(getIndex()).getOriginCost()
												* tableImport.getItems().get(getIndex()).getQuantityPur());
							}
							updateTotal();
						});
						btnPlus.setOnAction(e -> {
							tableImport.getItems().get(getIndex())
									.setQuantityPur(tableImport.getItems().get(getIndex()).getQuantityPur() + 1);
							tableImport.getItems().get(getIndex())
									.setSubTotal(tableImport.getItems().get(getIndex()).getOriginCost()
											* tableImport.getItems().get(getIndex()).getQuantityPur());
							updateTotal();
						});
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});

			TableColumn<PurchasesItems, Integer> originCol = new TableColumn<PurchasesItems, Integer>("Giá Gốc");
			originCol.setCellValueFactory(new PropertyValueFactory<>("originCost"));
			originCol.setMinWidth(70);
			originCol.setMaxWidth(70);
			originCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			originCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(HomeController.decimalFormat.format(item));
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});

			TableColumn<PurchasesItems, Integer> sellCol = new TableColumn<PurchasesItems, Integer>("Giá Bán");
			sellCol.setCellValueFactory(new PropertyValueFactory<>("sellCost"));
			sellCol.setMinWidth(70);
			sellCol.setMaxWidth(70);
			sellCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			sellCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(HomeController.decimalFormat.format(item));
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});
			TableColumn<PurchasesItems, Integer> priceTotalCol = new TableColumn<PurchasesItems, Integer>("Thành Tiền");
			priceTotalCol.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
			priceTotalCol.setMinWidth(80);
			priceTotalCol.setMaxWidth(80);
			priceTotalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			priceTotalCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(HomeController.decimalFormat.format(item));
						if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
							setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
						else
							setStyle("-fx-alignment: CENTER;");
					}
				}
			});
			TableColumn<PurchasesItems, PurchasesItems> deleteColumn = new TableColumn<>("Xóa");
			deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			deleteColumn.setMinWidth(40);
			deleteColumn.setMaxWidth(40);
			deleteColumn.setCellFactory(param -> new TableCell<PurchasesItems, PurchasesItems>() {
				@Override
				protected void updateItem(PurchasesItems item, boolean empty) {
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
					if (tableImport.getItems().get(getIndex()).getProductId().equals(selectItem))
						setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
					else
						setStyle("-fx-alignment: CENTER;");
					btnDelete.setOnAction(event -> {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle(Global.tsl_lblConfirmDialog);
						alert.setHeaderText(null);
						alert.setContentText("Xóa '" + item.getProductName() + "' ?");
						Optional<ButtonType> action = alert.showAndWait();
						if (action.get() == ButtonType.OK) {
							itemsList.remove(tableImport.getItems().get(getIndex()).getProductId());
							tableImport.getItems().remove(getIndex());
							updateTotal();
							buildTableItems();
						}
					});
				}
			});
			tableImport.getColumns().addAll(nameProductCol, soluonghientaiCol, sellCol, originCol, quatityCol,
					priceTotalCol, deleteColumn);
			tableImport.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			int val = 0;
			for (Map.Entry<String, ObservableList<PurchasesItems>> entry : itemsList.entrySet()) {
				ObservableList<PurchasesItems> value = entry.getValue();
				tableImport.getItems().addAll(value);
				val += value.get(0).getSubTotal();
			}
			lblTotal.setText(HomeController.decimalFormat.format(val));
		} catch (Exception e) {
			Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void buildTableHistories() {
		try {
			HistoriesLists.clear();
			tableHistoryImport.getItems().clear();
			tableHistoryImport.getColumns().clear();
			connection = handler.getConnection();
			String query = "SELECT * FROM Purchases ORDER BY createdatp DESC";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					HistoriesLists
							.add(new Purchases(rs.getInt("id"), rs.getString("barcodePur"), rs.getInt("SupplierId"),
									rs.getInt("PaymentStatus"), rs.getInt("GrandTotal"), rs.getInt("Paid"),
									rs.getString("createdAtP"), rs.getString("note"), rs.getInt("userCreated")));
				}
			}
			System.out.println(HistoriesLists.size());
			TableColumn<Purchases, Number> indexColumn = new TableColumn<Purchases, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
					tableHistoryImport.getItems().indexOf(column.getValue()) + 1));
			tableHistoryImport.getColumns().add(0, indexColumn);
			tableHistoryImport.getSelectionModel().selectedItemProperty().addListener(
					(ObservableValue<? extends Purchases> observable, Purchases oldValue, Purchases newValue) -> {
						if (newValue == null)
							return;
					});
			indexColumn.setCellFactory(column -> new TableCell<Purchases, Number>() {

				@Override
				public void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.toString());

					}
				}
			});
			TableColumn<Purchases, String> timeCol = new TableColumn<Purchases, String>("Thời Gian ");
			timeCol.setCellValueFactory(new PropertyValueFactory<>("createdAtP"));
			timeCol.setCellFactory(TextFieldTableCell.<Purchases>forTableColumn());
			timeCol.setStyle("-fx-alignment: CENTER-LEFT;");
			timeCol.setCellFactory(column -> new TableCell<Purchases, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						try {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm",
									new Locale("vi", "VN"));
							String date = simpleDateFormat.format(Timestamp.valueOf(item));
							setText(date);
						} catch (Exception e) {
							Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
						}
					}
				}
			});

			TableColumn<Purchases, Integer> statusPayCol = new TableColumn<Purchases, Integer>("Thanh Toán");
			statusPayCol.setCellValueFactory(new PropertyValueFactory<>("PaymentStatus"));
			statusPayCol.setMinWidth(150);
			statusPayCol.setMaxWidth(150);
			statusPayCol.setStyle("-fx-alignment: CENTER");
			statusPayCol.setCellFactory(column -> new TableCell<Purchases, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						if (item == 0) {
							setText("Chưa Thanh Toán");
						} else if (item == 1) {
							setText("Thanh Toán 1 Phần");
						} else
							setText("Đã Thanh Toán");
					}
				}
			});

			rs.close();
			connection.close();
			tableHistoryImport.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableHistoryImport.getColumns().addAll(timeCol, statusPayCol);
			tableHistoryImport.getItems().addAll(HistoriesLists);
		} catch (Exception e) {
			Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@FXML
	private void actionSuccess() {
		System.out.println("sucss");
		System.out.println(itemsList.size());
		System.out.println(txtPay.getText().trim().isEmpty());
		if (itemsList.size() > 0) {
			Integer _SupplierId = 0;
			// _PaymentStatus 0: chua thanh toan; 1: thanh toan mot phan; 2: Thanh toan het
			Integer _PaymentStatus = 0;
			Integer _GrandTotal = Integer.parseInt(lblTotal.getText().replace(",", ""));
			Integer _Paid = 0;
			String _Note = "";

			long _barcodePur = Instant.now().getEpochSecond();

			if (txtPay.getText().trim().isEmpty()) {

			} else {
				_Paid = Integer.parseInt(txtPay.getText());
				if (_GrandTotal == _Paid)
					_PaymentStatus = 2;
				else
					_PaymentStatus = 1;
			}

			try {
				if (stsSuccess.containsValue(false)) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Thông Báo");
					alert.setHeaderText(null);
					alert.setContentText(
							"Tồn tại sản phẩm chưa rõ thông tin. Nhấp đôi chuột vào tên sản phẩm chưa xác định để nhập các thông tin cơ bản.");
					alert.showAndWait();
				} else {
					connection = handler.getConnection();
					Statement stmt2 = connection.createStatement();
					stmt2 = connection.createStatement();
					String sql = "insert into Purchases (barcodePur, SupplierId, PaymentStatus, GrandTotal, Paid, note, userCreated) "
							+ "values ('" + _barcodePur + "','" + _SupplierId + "','" + _PaymentStatus + "','"
							+ _GrandTotal + "','" + _Paid + "','" + _Note + "','" + Integer.parseInt(Global.userId)
							+ "')";
					stmt2.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet rs = stmt2.getGeneratedKeys();
					connection.commit();
					if (rs.next()) {
						itemsList.forEach((key, value) -> {
							try {
								Statement stmt3 = connection.createStatement();
								String sql3 = "insert into PurchasesItems (purchasesId, productId, quantityPur, subTotal, originCost, sellCost) "
										+ "values ('" + rs.getInt("id") + "','" + value.get(0).getProductId() + "','"
										+ value.get(0).getQuantityPur() + "','" + value.get(0).getSubTotal() + "','"
										+ value.get(0).getOriginCost() + "','" + value.get(0).getSellCost() + "')";
								stmt3.execute(sql3);
								Statement stmt4 = connection.createStatement();
								String sql4 = "UPDATE warehouse SET remainingAmount = remainingAmount + '"
										+ value.get(0).getQuantityPur() + "' WHERE productId='"
										+ value.get(0).getProductId()
										+ "'; INSERT INTO warehouse (productId, quantitySold, remainingAmount)  SELECT '"
										+ value.get(0).getProductId() + "',0, '" + value.get(0).getQuantityPur()
										+ "' WHERE NOT EXISTS (SELECT 1 FROM warehouse WHERE productId='"
										+ value.get(0).getProductId() + "');";
								int sts = stmt4.executeUpdate(sql4);
								connection.commit();
								if (sts == 1) {
									System.out.println("ok yi");
								} else {
									System.out.println("ko dc");
								}
							} catch (Exception e) {
								Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
							}
						});
						actionClear();
					}
					stmt2.close();
					connection.close();
					RenderBarcodeThread barcodeThr = new RenderBarcodeThread(String.valueOf(_barcodePur));
					barcodeThr.start();
					buildTableHistories();
				}
			} catch (Exception e) {
				Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
			}
			System.out.println(txtPay.getText());
			System.out.println(comboboxPay.getValue());
		}
	}

	@FXML
	private void actionClear() {
		System.out.println("clear");
		itemsList.clear();
		buildTableItems();
	}
}