package modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.controlsfx.control.textfield.TextFields;

import com.jfoenix.controls.JFXButton;

import application.AutoCompleteComboBoxListener;
import application.Global;
import application.PrintThread;
import application.RenderBarcodeThread;
import database.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import models.Bill;
import models.Buy;
import models.Warehourse;

/**
 * FXML Controller class
 *
 * @author tuyenlm
 */
public class HomeController implements Initializable {
	@FXML
	private Label lblTotal, lblSum, lblTest, lblTurnedBack;
	@FXML
	private JFXButton btnBarcode, btnSearchProduct, btnPay, btnDeletePay, btnJustPay;
	@FXML
	private TextField txtBarcode, txtSearchHistory, txtMoneyReceived;
	@FXML
	private HBox hboxBarcode;
	@FXML
	private TableView<Buy> tableBuyList;
	@FXML
	private Tab tabHistoryPay, tabUnknowProduct, tabAbc;
	@FXML
	private ComboBox<String> comCondition, comYear, comMonth;
	private ObservableList<Bill> lists = FXCollections.observableArrayList();
	@FXML
	private TableView<Bill> tableHistoryPay;
	@FXML
	public Circle iconNotifiUnkknowProduct;
	@FXML
	private TableView<Warehourse> _tableHetHang;
	HashMap<String, String> hasMUser = new HashMap<String, String>();
	protected static Connection connection;
	private static DbHandler handler;
	Statement stmt = null;
	ResultSet rs = null;
	StackPane deptStackPane;
	private TextField searchProduct = new TextField();
	private ComboBox<String> searchBarcode = new ComboBox<String>();
	// private ComboBox<String> searchProduct = new ComboBox<String>();
	private HashMap<Integer, ObservableList<Buy>> itemBuyList = new HashMap<Integer, ObservableList<Buy>>();
	private HashMap<Integer, Integer> remainHangHoa = null;
	public static DecimalFormat decimalFormat = new DecimalFormat("###,###");
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtBarcode.requestFocus();
				txtBarcode.selectAll();
				buildTableHetHang();
			}
		});
		searchBarcode.setVisible(false);
		searchBarcode.setMinWidth(100);
		searchBarcode.setPrefWidth(100);
		new AutoCompleteComboBoxListener<>(searchBarcode);
		hboxBarcode.getChildren().add(1, searchBarcode);
		tabUnknowProduct.setOnSelectionChanged((event) -> {
			if (tabUnknowProduct.isSelected()) {
				try {
					setDataUnknowProduct("");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							txtBarcode.requestFocus();
							txtBarcode.selectAll();
						}
					});
				} catch (Exception e) {
					Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		});
		btnBarcode.setStyle("-fx-background-color: green;");
		handler = new DbHandler();
		txtBarcode.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().toString().equals("ENTER") && !txtBarcode.getText().trim().isEmpty()) {

					if (txtBarcode.getText().trim().toLowerCase().contains("t-")) {
						connection = handler.getConnection();
						try {
							String[] brsp = txtBarcode.getText().trim().split("-");
							String query = "SELECT nameproduct,barcodeproduct FROM products WHERE RIGHT(barcodeproduct, 5) = '"
									+ brsp[1] + "';";
							Statement s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							ResultSet rs = s.executeQuery(query);
							rs.last();
							int count = rs.getRow();
							rs.beforeFirst();
							if (count > 1) {
								searchBarcode.getItems().clear();
								if (rs.isBeforeFirst()) {
									while (rs.next()) {
										searchBarcode.getItems().add(rs.getString("nameProduct"));
									}
								}
								searchBarcode.setVisible(true);
								txtBarcode.setMinWidth(200);
								searchBarcode.setMinWidth(100);
								searchBarcode.setPrefWidth(100);
								searchBarcode.requestFocus();
								searchBarcode.show();
								searchBarcode.valueProperty().addListener((a, b, c) -> {
									if (searchBarcode.getSelectionModel().getSelectedItem() != null)
										doSearch(searchBarcode.getSelectionModel().getSelectedItem(), "nameProduct",
												false);
								});

							} else {
								if (rs.isBeforeFirst()) {
									while (rs.next()) {
										doSearch(rs.getString("barcodeProduct"), "barcodeProduct", true);
									}

								} else {
									Alert alert = new Alert(AlertType.WARNING);
									alert.setTitle(Global.tsl_lblConfirmDialog);
									alert.setHeaderText(null);
									alert.setContentText("Không tìm thấy mã " + brsp[1]);
									alert.showAndWait();
								}
							}
							connection.close();
						} catch (Exception e) {
							Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
						}
					} else {
						String bcode = txtBarcode.getText().trim().toLowerCase();
						System.out.println("bcode " + bcode);
						String[] barCodeSp = bcode.split("-");
						if (barCodeSp.length > 1) {
							System.out.println("barCodeSp[0] " + barCodeSp[0]);
							switch (barCodeSp[0]) {
							case "bi":
								getBill(bcode.toUpperCase());
								break;
							case "cmd":
								doActionCMD(bcode, false);
								break;
							default:
								break;
							}
						} else {
							doSearch(bcode, "barcodeProduct", true);
						}
					}
				}

			}

			private void doActionCMD(String trim, boolean isAlert) {
				try {

					connection = handler.getConnection();
					String query = "SELECT action FROM BarcodeCmd WHERE barcodecmd = '" + trim + "' ORDER BY id asc";
					ResultSet rs = connection.createStatement().executeQuery(query);
					System.out.println(query);
					if (rs.isBeforeFirst()) {
						while (rs.next()) {
							System.out.println("doActionCMD |" + rs.getString("action") + "|");
							switch (rs.getString("action")) {
							case "pay":
								System.out.println("pay ne");
								actionPay();
								break;
							case "printAndPay":
								System.out.println("printAndPay ne");
								actionPayPrint();
								break;
							case "clear":
								System.out.println("clear ne");
								DeletePay(isAlert);
								break;
							default:
								break;
							}
						}
					}
					rs.close();
					connection.close();
				} catch (Exception e) {
					Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
				}
			}

			private void getBill(String billCode) {
				try {
					connection = handler.getConnection();
					String query = "SELECT sales.id,sales.productid,sales.quantitys,sales.priceSell, sales.billId, products.nameProduct,products.priceOrigin FROM bills LEFT OUTER JOIN sales ON (sales.billId = Bills.id) LEFT OUTER JOIN products ON (products.id = Sales.productid)  WHERE Bills.barcodeBill = '"
							+ billCode + "' ORDER BY Sales.id ASC";
					ResultSet rs = connection.createStatement().executeQuery(query);
					itemBuyList.clear();
					if (rs.isBeforeFirst()) {
						while (rs.next()) {
							ObservableList<Buy> items = FXCollections.observableArrayList();
							items.add(new Buy(rs.getInt("productId"), rs.getString("nameProduct"),
									rs.getInt("quantitys"), rs.getInt("priceSell"), rs.getInt("priceOrigin"),
									(rs.getInt("priceSell") * rs.getInt("quantitys")), rs.getInt("id"), 0, 0, false));
							itemBuyList.put(rs.getInt("productId"), items);
							billId = rs.getInt("billId");
						}
					}
					builTableBuy();
					rs.close();
					connection.close();
					txtBarcode.requestFocus();
					txtBarcode.selectAll();
				} catch (Exception e) {
					Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
				}
			}

		});
		txtBarcode.focusedProperty().addListener((a, b, c) -> {
			if (!b) {
				searchBarcode.setVisible(false);
				searchBarcode.setMaxWidth(0);
				searchBarcode.setMinWidth(0);
				searchBarcode.setPrefWidth(0);
				txtBarcode.setPrefWidth(400);
				txtBarcode.setMaxWidth(400);
				txtBarcode.setMinWidth(400);
			}
		});
		txtSearchHistory.textProperty().addListener((a, b, c) -> {
			if (c != null && comCondition.getValue() != null) {
				String field;
				if (!comCondition.getValue().equals("Mã Hóa Đơn"))
					field = "CAST(createdatb as TEXT)";
				else
					field = "barcodeBill";
				buildTableHistoryPay(c, field);
			} else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(Global.tsl_lblConfirmDialog);
				alert.setHeaderText(null);
				alert.setContentText("Hãy chọn điều kiện tìm kiếm.");
				Optional<ButtonType> action = alert.showAndWait();
				if (action.get() == ButtonType.OK) {
					comCondition.show();
					txtSearchHistory.requestFocus();
					txtSearchHistory.selectAll();
				}
			}
		});

		tableHistoryPay.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					try {
						if (tableHistoryPay.getSelectionModel().getSelectedItem() != null) {
							int id = tableHistoryPay.getSelectionModel().getSelectedItem().getId();
							connection = handler.getConnection();
							String query = "SELECT sales.id,sales.quantitys,sales.priceSell,products.nameProduct,products.priceOrigin FROM sales LEFT OUTER JOIN products ON (sales.productId = products.id) WHERE Sales.billId = '"
									+ id + "' ORDER BY Sales.id ASC";
							ResultSet rs = connection.createStatement().executeQuery(query);
							itemBuyList.clear();
							ObservableList<Buy> items = FXCollections.observableArrayList();
							if (rs.isBeforeFirst()) {
								while (rs.next()) {
									items.add(new Buy(0, rs.getString("nameProduct"), rs.getInt("quantitys"),
											rs.getInt("priceSell"), rs.getInt("priceOrigin"),
											(rs.getInt("quantitys") * rs.getInt("priceSell")), 0, 0, 0, false));
								}
							}
							rs.close();
							connection.close();
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm",
									new Locale("vi", "VN"));
							String date = simpleDateFormat.format(Timestamp
									.valueOf(tableHistoryPay.getSelectionModel().getSelectedItem().getCreatedAtB()));
							Dialog<Pair<String, String>> dialog = new Dialog<>();
							dialog.setTitle("Chi tiết thanh toán của ngày: " + date);
							setItemDetailBill(items);
							ButtonType buttonTypePrint = new ButtonType("IN", ButtonData.OK_DONE);

							dialog.getDialogPane().getButtonTypes().addAll(buttonTypePrint, ButtonType.CANCEL);
							Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
							closeButton.setDisable(false);
							FXMLLoader fxmlLoader = new FXMLLoader(
									getClass().getResource("/modules/itemsForBill.fxml"));
							Parent root = (Parent) fxmlLoader.load();
							dialog.getDialogPane().setContent(root);
							dialog.setResultConverter(dialogButton -> {
								if (dialogButton == buttonTypePrint) {
									try {
										PrintThread prunt = new PrintThread(id);
										prunt.start();
									} catch (Exception e) {
										Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
									}
								}
								return null;
							});
							dialog.showAndWait();
						}
					} catch (Exception e) {
						Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
					}
				}
			}

		});
		Date date = new Date(); // your date
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		for (int i = 2017; i <= year; i++) {
			comYear.getItems().add(String.valueOf(i));
		}
		comYear.setValue(String.valueOf(year));
		for (int i = 1; i <= 12; i++) {
			comMonth.getItems().add(String.valueOf(i));
		}
		comMonth.setValue(String.valueOf(month));
		buildTableHistoryPay(String.valueOf(year) + "-" + String.format("%02d", month), "CAST(createdatb as TEXT)");
		comCondition.getItems().addAll("Mã Hóa Đơn", "Thời gian");
		comCondition.getSelectionModel().select(0);
		comYear.valueProperty().addListener((a, b, c) -> {
			changeComboboxTime();
		});
		comMonth.valueProperty().addListener((a, b, c) -> {
			changeComboboxTime();
		});
		comMonth.setStyle("-fx-border-insets: 3px;-fx-background-insets: 2px;-fx-border-color: WHITE");
		comYear.setStyle("-fx-border-insets: 3px;-fx-background-insets: 2px;-fx-border-color: WHITE");
		comYear.setMaxWidth(90);
		comYear.setMinWidth(90);
		comCondition.setStyle("-fx-border-insets: 3px;-fx-background-insets: 2px;-fx-border-color: WHITE");
		txtSearchHistory.setStyle("-fx-border-insets: 1px;-fx-background-insets: 1px;-fx-border-color: WHITE");
		Global.val = new SimpleStringProperty("0");
		lblTest.setVisible(false);
		lblTest.textProperty().bind(Global.val);
		lblTest.textProperty().addListener((a, b, c) -> {
			if (c != null) {
				iconNotifiUnkknowProduct.setRadius(Integer.parseInt(lblTest.getText()));
			}
		});
		if (TabUnknowProductController.getData() > 0) {
			iconNotifiUnkknowProduct.setRadius(5);
		}
		statusDisableButton();
		txtMoneyReceived.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					String gh = newValue.replace(",", "");
					if (!gh.isEmpty() && gh.length() < 9) {
						System.out.println("<9");
						if (gh.matches("\\d*")) {
							int value = Integer.parseInt(gh);
							int priceTotal = Integer.parseInt(lblTotal.getText().replaceAll(",", ""));
							lblTurnedBack.setText(decimalFormat.format(value - priceTotal));
							NumberFormat numberFormatter = new DecimalFormat("#,###,###");
							String formattedNumber = numberFormatter.format(value);

							txtMoneyReceived.setText(formattedNumber);
							// Platform.runLater(() -> {
							//
							// });
						} else {

							txtMoneyReceived.setText(oldValue);
						}
					} else {
						lblTurnedBack.setText("");
					}
				} catch (Exception e) {
					Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		});

		// new AutoCompleteComboBoxListener<>(searchProduct);
		// searchProduct.valueProperty().addListener((a, b, c) -> {
		// System.out.println(b);
		// if (searchProduct.getSelectionModel().getSelectedItem() != null)
		// doSearch(searchProduct.getSelectionModel().getSelectedItem(), "nameProduct");
		// });

		try {
			connection = handler.getConnection();
			ArrayList<String> ar = new ArrayList<String>();
			if (ar.size() == 0) {
				String query = "SELECT nameProduct FROM products";
				ResultSet rs = connection.createStatement().executeQuery(query);
				if (rs.isBeforeFirst()) {
					while (rs.next()) {
						ar.add(rs.getString("nameProduct"));
					}
				}
			}
			TextFields.bindAutoCompletion(searchProduct, ar);
			searchProduct.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if (!newValue.isEmpty()) {
						doSearch(newValue, "nameProduct", false);

					}

				}
			});
			
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	private void changeComboboxTime() {
		String yearCom = comYear.getValue();
		int ff = Integer.parseInt(comMonth.getValue());
		String monthCom = String.format("%02d", ff);
		buildTableHistoryPay(yearCom + "-" + monthCom, "CAST(createdatb as TEXT)");
	}

	public void setItemDetailBill(ObservableList<Buy> items) {
		HomeController.itemsBill = items;
	}

	public static ObservableList<Buy> getItemDetailBill() {
		return itemsBill;
	}

	@SuppressWarnings("unchecked")
	private void buildTableHistoryPay(String text, String field) {
		lists.clear();
		tableHistoryPay.getItems().clear();
		tableHistoryPay.getColumns().clear();
		try {
			connection = handler.getConnection();
			String query;
			if (!text.isEmpty() && !field.isEmpty()) {
				query = "SELECT *,to_char(priceTotal, '999,999,990') as priceTotalDe, to_char(priceReceive, '999,999,990') as priceReceiveDe FROM bills LEFT OUTER JOIN users ON (bills.sellerId = users.id) WHERE "
						+ field + " ILIKE '%" + text + "%' ORDER BY bills.id DESC";
			} else {
				query = "SELECT *,to_char(priceTotal, '999,999,990') as priceTotalDe, to_char(priceReceive, '999,999,990') as priceReceiveDe FROM bills LEFT OUTER JOIN users ON (bills.sellerId = users.id) ORDER BY bills.id DESC";
			}
			ResultSet rs = connection.createStatement().executeQuery(query);
			int sum = 0;
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					lists.add(new Bill(rs.getInt("id"), rs.getString("barcodeBill"), rs.getString("priceTotalDe"),
							rs.getString("priceReceiveDe"), rs.getBoolean("statusBill"), rs.getString("createdAtB"),
							rs.getString("fullname")));
					sum += Integer.parseInt(rs.getString("priceTotalDe").trim().replaceAll(",", ""));
				}
			}
			lblSum.setText(decimalFormat.format(sum));
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}
		TableColumn<Bill, Number> indexColumn = new TableColumn<Bill, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setStyle("-fx-alignment: CENTER;");
		indexColumn.setCellValueFactory(
				column -> new ReadOnlyObjectWrapper<Number>(tableHistoryPay.getItems().indexOf(column.getValue()) + 1));
		tableHistoryPay.getColumns().add(0, indexColumn);
		tableHistoryPay.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Bill> observable, Bill oldValue, Bill newValue) -> {
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
		TableColumn<Bill, String> priceReceiveCol = new TableColumn<Bill, String>("Tiền Nhận");
		priceReceiveCol.setCellValueFactory(new PropertyValueFactory<>("priceReceive"));
		priceReceiveCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceReceiveCol.setMinWidth(100);
		priceReceiveCol.setMaxWidth(100);
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
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm",
								new Locale("vi", "VN"));
						String date = simpleDateFormat.format(Timestamp.valueOf(item));
						setText(date);
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

		TableColumn<Bill, Bill> printCol = new TableColumn<>("In");
		printCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		printCol.setMinWidth(40);
		printCol.setMaxWidth(40);
		printCol.setCellFactory(param -> new TableCell<Bill, Bill>() {
			@Override
			protected void updateItem(Bill item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setGraphic(null);
					return;
				}
				ImageView imageViewPrint = new ImageView(
						new Image(getClass().getResourceAsStream("/icons/printer.png")));
				imageViewPrint.setFitWidth(18);
				imageViewPrint.setFitHeight(18);
				JFXButton btnPrint = new JFXButton();
				btnPrint.setTooltip(new Tooltip("In"));
				btnPrint.setGraphic(imageViewPrint);
				btnPrint.setStyle("-fx-background-color: #333;-fx-padding: -5px");
				btnPrint.setMaxHeight(21);
				btnPrint.setMinHeight(21);
				btnPrint.setMaxWidth(30);
				btnPrint.setMinWidth(30);
				setGraphic(btnPrint);
				btnPrint.setOnAction(event -> {
					PrintThread prunt = new PrintThread(tableHistoryPay.getItems().get(getIndex()).getId());
					prunt.start();
				});
			}
		});
		tableHistoryPay.getColumns().addAll(createdAtBCol, priceTotalCol, priceReceiveCol, barcodeBillCol,
				sellerNameCol, printCol);
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
		indexColumn.setCellValueFactory(
				column -> new ReadOnlyObjectWrapper<Number>(tableBuyList.getItems().indexOf(column.getValue()) + 1));
		tableBuyList.getColumns().add(0, indexColumn);
		tableBuyList.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Buy> observable, Buy oldValue, Buy newValue) -> {
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
//					int productId = tableBuyList.getItems().get(getIndex()).getProductId();
//					txtQuatity.textProperty().addListener((a, b, c) -> {
//						System.out.println("=================c|" + c);
//						System.out.println("remain|" + remainHangHoa.get(productId));
//						if (ValidateHandle.isNumericInteger(c) && Integer.parseInt(c) > 0
//								&& (remainHangHoa.get(productId) != null && Integer.parseInt(c) <= remainHangHoa.get(productId))) {
//							System.out.println("dzo 1");
//							tableBuyList.getItems().get(getIndex()).setPriceTotal(
//									tableBuyList.getItems().get(getIndex()).getPrice() * Integer.parseInt(c));
//							txtQuatity.setText(c);
//						} else {
//							System.out.println("dzo day");
//							txtQuatity.setText(b);
//						}
//					});
					txtQuatity.setOnKeyReleased(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent ke) {
							if (ke.getText().trim().isEmpty() && !txtQuatity.getText().trim().isEmpty()) {
								tableBuyList.getItems().get(getIndex())
										.setQuatity(Integer.parseInt(txtQuatity.getText()));
								if (tableBuyList.getItems().get(getIndex()).isEnable()) {
									if (tableBuyList.getItems().get(getIndex()).getQuatity() >= tableBuyList.getItems()
											.get(getIndex()).getQuantityQ()) {
										tableBuyList.getItems().get(getIndex())
												.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPriceQ()
														* tableBuyList.getItems().get(getIndex()).getQuatity());
									} else {
										tableBuyList.getItems().get(getIndex())
												.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
														* tableBuyList.getItems().get(getIndex()).getQuatity());
									}

								} else {
									tableBuyList.getItems().get(getIndex())
											.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
													* tableBuyList.getItems().get(getIndex()).getQuatity());
								}
							}
						}
					});
					txtQuatity.focusedProperty().addListener((a, b, c) -> {
						if (b) {
							tableBuyList.getItems().get(getIndex()).setQuatity(Integer.parseInt(txtQuatity.getText()));
							if (tableBuyList.getItems().get(getIndex()).isEnable()) {
								if (tableBuyList.getItems().get(getIndex()).getQuatity() >= tableBuyList.getItems()
										.get(getIndex()).getQuantityQ()) {
									tableBuyList.getItems().get(getIndex())
											.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPriceQ()
													* tableBuyList.getItems().get(getIndex()).getQuatity());
								} else {
									tableBuyList.getItems().get(getIndex())
											.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
													* tableBuyList.getItems().get(getIndex()).getQuatity());
								}

							} else {
								tableBuyList.getItems().get(getIndex())
										.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
												* tableBuyList.getItems().get(getIndex()).getQuatity());
							}
						}
					});
					btnExcept.setOnAction(e -> {
						if ((tableBuyList.getItems().get(getIndex()).getQuatity() - 1) > 0) {
							tableBuyList.getItems().get(getIndex())
									.setQuatity(tableBuyList.getItems().get(getIndex()).getQuatity() - 1);
							if (tableBuyList.getItems().get(getIndex()).isEnable()) {
								if (tableBuyList.getItems().get(getIndex()).getQuatity() >= tableBuyList.getItems()
										.get(getIndex()).getQuantityQ()) {
									tableBuyList.getItems().get(getIndex())
											.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPriceQ()
													* tableBuyList.getItems().get(getIndex()).getQuatity());
								} else {
									tableBuyList.getItems().get(getIndex())
											.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
													* tableBuyList.getItems().get(getIndex()).getQuatity());
								}

							} else {
								tableBuyList.getItems().get(getIndex())
										.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
												* tableBuyList.getItems().get(getIndex()).getQuatity());
							}
						}
					});
					btnPlus.setOnAction(e -> {

						tableBuyList.getItems().get(getIndex())
								.setQuatity(tableBuyList.getItems().get(getIndex()).getQuatity() + 1);
						if (tableBuyList.getItems().get(getIndex()).isEnable()) {
							if (tableBuyList.getItems().get(getIndex()).getQuatity() >= tableBuyList.getItems()
									.get(getIndex()).getQuantityQ()) {
								tableBuyList.getItems().get(getIndex())
										.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPriceQ()
												* tableBuyList.getItems().get(getIndex()).getQuatity());
							} else {
								tableBuyList.getItems().get(getIndex())
										.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
												* tableBuyList.getItems().get(getIndex()).getQuatity());
							}

						} else {
							tableBuyList.getItems().get(getIndex())
									.setPriceTotal(tableBuyList.getItems().get(getIndex()).getPrice()
											* tableBuyList.getItems().get(getIndex()).getQuatity());
						}

						// if (tableBuyList.getItems().get(getIndex()).getQuatity() <
						// remainHangHoa.get(productId)) {
						//
						// }
					});
					updateTotal();
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
		TableColumn<Buy, Integer> priceQCol = new TableColumn<Buy, Integer>("Giá Giảm");
		priceQCol.setCellValueFactory(new PropertyValueFactory<>("priceQ"));
		priceQCol.setMinWidth(70);
		priceQCol.setMaxWidth(70);
		priceQCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceQCol.setCellFactory(column -> new TableCell<Buy, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					if (tableBuyList.getItems().get(getIndex()).isEnable()) {
						setText(decimalFormat.format(item) + "/"
								+ tableBuyList.getItems().get(getIndex()).getQuantityQ());
						setStyle("-fx-text-fill: Green; -fx-alignment: center;");
					} else
						setText("");

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

		tableBuyList.getColumns().addAll(nameProductCol, priceCol, priceQCol, quatityCol, priceTotalCol, deleteColumn);
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
		createBill(false);
	}

	@FXML
	private void actionPayPrint() {
		createBill(true);
	}

	@FXML
	private void actionDeletePay() {
		DeletePay(true);
	}

	private void DeletePay(boolean isAlert) {
		if (isAlert) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle(Global.tsl_lblConfirmDialog);
			alert.setHeaderText(null);
			alert.setContentText("Có muốn hủy thanh toán này không?");
			Optional<ButtonType> action = alert.showAndWait();
			if (action.get() == ButtonType.OK) {
				itemBuyList.clear();
				tableBuyList.getItems().clear();
				lblTotal.setText("0");
				statusDisableButton();
				txtBarcode.setText("");
				txtBarcode.requestFocus();
			}
		} else {
			itemBuyList.clear();
			tableBuyList.getItems().clear();
			lblTotal.setText("0");
			statusDisableButton();
			txtBarcode.setText("");
			txtBarcode.requestFocus();
		}

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
			btnSearchProduct.setStyle(
					"-fx-background-color: black; -fx-background-radius: 5; -fx-border-color: white; -fx-border-radius: 3; -fx-border-width: 2px;");
		}
	}

	@FXML
	private void actionSwitchProduct() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				searchProduct.requestFocus();
				searchProduct.selectAll();
			}
		});

		searchProduct.setVisible(true);
		searchProduct.setMaxWidth(395);
		searchProduct.setMinWidth(395);
		searchProduct.setStyle("-fx-border-insets: 2px;-fx-background-insets: 2px;-fx-border-color: WHITE");
		txtBarcode.setVisible(false);
		txtBarcode.setMinWidth(0);
		txtBarcode.setMaxWidth(0);
		if (!hboxBarcode.getChildren().contains(searchProduct)) {
			hboxBarcode.getChildren().add(0, searchProduct);
		}
		btnBarcode.setStyle("-fx-background-color: white;");
		btnSearchProduct.setStyle(
				"-fx-background-color: green; -fx-background-radius: 5; -fx-border-color: green; -fx-border-radius: 2; -fx-border-width: 1px;");
	}

	private void doSearch(String val, String field, boolean isAdd) {
		try {
			connection = handler.getConnection();
			String query = "SELECT products.*,QuantityPrice.* FROM products LEFT JOIN QuantityPrice ON products.id = QuantityPrice.productId WHERE products."
					+ field + "='" + val + "'";
			System.out.println(query);
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					if (!itemBuyList.containsKey(rs.getInt("id"))) {
						ObservableList<Buy> items = FXCollections.observableArrayList();
						items.add(new Buy(rs.getInt("id"), rs.getString("nameProduct"), 1, rs.getInt("priceSell"),
								rs.getInt("priceOrigin"), rs.getInt("priceSell"), 0, rs.getInt("quantity"),
								rs.getInt("sellCost"), rs.getBoolean("enable")));
						itemBuyList.put(rs.getInt("id"), items);
					} else {

						if (itemBuyList.get(rs.getInt("id")).get(0).getProductId() == rs.getInt("id")) {
							itemBuyList.get(rs.getInt("id")).get(0)
									.setQuatity(itemBuyList.get(rs.getInt("id")).get(0).getQuatity() + 1);
							if (rs.getBoolean("enable")) {
								System.out.println("++++++++++++");
								System.out.println(itemBuyList.get(rs.getInt("id")).get(0).getQuatity());
								System.out.println(rs.getInt("quantity"));
								if (itemBuyList.get(rs.getInt("id")).get(0).getQuatity() >= rs.getInt("quantity")) {
									itemBuyList.get(rs.getInt("id")).get(0).setPriceTotal(rs.getInt("sellCost")
											* itemBuyList.get(rs.getInt("id")).get(0).getQuatity());
								} else {
									itemBuyList.get(rs.getInt("id")).get(0).setPriceTotal(rs.getInt("priceSell")
											* itemBuyList.get(rs.getInt("id")).get(0).getQuatity());
								}
							} else {
								itemBuyList.get(rs.getInt("id")).get(0).setPriceTotal(
										rs.getInt("priceSell") * itemBuyList.get(rs.getInt("id")).get(0).getQuatity());
							}

						}
					}
					// if (remainHangHoa.get(rs.getInt("id")) != null) {
					//
					// } else {
					// Alert alert = new Alert(AlertType.INFORMATION);
					// alert.setTitle("Thông Báo");
					// alert.setHeaderText(null);
					// alert.setContentText("'" + rs.getString("nameProduct") + "' đã hết trong kho
					// hàng.");
					// alert.showAndWait();
					// }
				}
				builTableBuy();
				updateMoneyReturn();
				searchProduct.setText("");
			} else {
				System.out.println("isAdd " + isAdd);
				if (isAdd) {
					setDataUnknowProduct(val);
				}
			}
			txtBarcode.requestFocus();
			txtBarcode.selectAll();
			rs.close();
			connection.close();
			statusDisableButton();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void statusDisableButton() {
		boolean b = true;
		if (itemBuyList.size() > 0) {
			b = false;
		}
		btnPay.setDisable(b);
		btnJustPay.setDisable(b);
		btnDeletePay.setDisable(b);
	}

	public void setDataUnknowProduct(String items) throws IOException {
		TabUnknowProductController.setVariable(items);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("tabUnknowProduct.fxml"));
		tabUnknowProduct.setContent(loader.load());

	}

	private void createBill(boolean isPrinted) {
		System.out.println("createBill createBill");
		if (itemBuyList.size() > 0) {
			try {
				connection = handler.getConnection();
				stmt = connection.createStatement();
				int priceTotal = Integer.parseInt(lblTotal.getText().replaceAll(",", ""));
				System.out.println("priceTotal|" + priceTotal);
				int priceReceive = txtMoneyReceived.getText().isEmpty() ? 0
						: Integer.parseInt(txtMoneyReceived.getText().replace(",", ""));
				boolean statusBill = true;
				int sellerId = 1;
				String barcodeBill = "";
				if (billId == 0) {
					barcodeBill = "BI-" + String.valueOf(Instant.now().getEpochSecond());
					String sqlBills = "insert into Bills (priceTotal,priceReceive,statusBill,sellerId,barcodeBill) "
							+ "values ('" + priceTotal + "','" + priceReceive + "','" + statusBill + "','" + sellerId
							+ "','" + barcodeBill + "')";
					stmt.execute(sqlBills, Statement.RETURN_GENERATED_KEYS);
					ResultSet keyset = stmt.getGeneratedKeys();
					keyset.next();
					billId = keyset.getInt("id");
				}

				for (Map.Entry<Integer, ObservableList<Buy>> entry : itemBuyList.entrySet()) {
					ObservableList<Buy> value = entry.getValue();
					Statement stmt2 = connection.createStatement();
					if (value.get(0).getSaleId() == 0) {
						String sqlInsertSale;
						if (value.get(0).isEnable()) {
							if (value.get(0).getQuatity() >= value.get(0).getQuantityQ()) {
								sqlInsertSale = "insert into Sales (productId,quantityS,priceSell,priceOrigin,billId) "
										+ "values ('" + value.get(0).getProductId() + "','" + value.get(0).getQuatity()
										+ "','" + value.get(0).getPriceQ() + "','" + value.get(0).getPriceOrigin()
										+ "','" + billId + "')";
							} else {
								sqlInsertSale = "insert into Sales (productId,quantityS,priceSell,priceOrigin,billId) "
										+ "values ('" + value.get(0).getProductId() + "','" + value.get(0).getQuatity()
										+ "','" + value.get(0).getPrice() + "','" + value.get(0).getPriceOrigin()
										+ "','" + billId + "')";
							}
						} else {
							sqlInsertSale = "insert into Sales (productId,quantityS,priceSell,priceOrigin,billId) "
									+ "values ('" + value.get(0).getProductId() + "','" + value.get(0).getQuatity()
									+ "','" + value.get(0).getPrice() + "','" + value.get(0).getPriceOrigin() + "','"
									+ billId + "')";
						}

						stmt2.execute(sqlInsertSale);
					} else {
						String sqlUpdate = "UPDATE Sales SET quantitys ='" + value.get(0).getQuatity()
								+ "', priceSell ='" + value.get(0).getPriceOrigin() + "' WHERE id = '"
								+ value.get(0).getSaleId() + "'; ";
						stmt.executeUpdate(sqlUpdate);
						String sqlUpdateBill = "UPDATE Bills SET priceTotal ='" + priceTotal + "', priceReceive ='"
								+ priceReceive + "' WHERE id = '" + billId + "'; ";
						stmt.executeUpdate(sqlUpdateBill);

					}
					WarehourseController.updateData(value.get(0).getProductId(), value.get(0).getQuatity());
					connection.commit();
					stmt2.close();
				}
				connection.commit();
				stmt.close();
				connection.close();
				itemBuyList.clear();
				builTableBuy();
				tabHistoryPay.isSelected();
				txtBarcode.setText("");
				txtBarcode.requestFocus();

				String yearCom = comYear.getValue();
				int ff = Integer.parseInt(comMonth.getValue());
				String monthCom = String.format("%02d", ff);
				buildTableHistoryPay(yearCom + "-" + monthCom, "CAST(createdatb as TEXT)");
				txtMoneyReceived.clear();
				lblTurnedBack.setText("0");
				if (!barcodeBill.isEmpty()) {
					RenderBarcodeThread barcodeThr = new RenderBarcodeThread(barcodeBill, false);
					barcodeThr.start();
				}
				if (isPrinted) {
					System.out.println("billId" + billId);
					PrintThread prunt = new PrintThread(billId);
					prunt.start();
					// File file = HomeController.exportFile(billId);
					// Desktop.getDesktop().print(file);
				}
				billId = 0;
				// buildTableHetHang();
			} catch (Exception e) {
				Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	private void updateMoneyReturn() {
		if (!txtMoneyReceived.getText().trim().isEmpty()) {
			int value = Integer.parseInt(txtMoneyReceived.getText().replace(",", ""));
			int priceTotal = Integer.parseInt(lblTotal.getText().replaceAll(",", ""));
			lblTurnedBack.setText(decimalFormat.format(value - priceTotal));
		}
	}

	@SuppressWarnings("deprecation")
	public static File exportFile(int id) {
		File file = null;
		try {
			connection = handler.getConnection();
			String query = "SELECT Bills.*, Products.nameProduct,Products.unit,Sales.quantityS,Sales.priceSell FROM Bills INNER JOIN Sales ON (Bills.id = Sales.billId) INNER JOIN Products ON (Products.id = Sales.productId) WHERE Bills.id = '"
					+ id + "' ORDER by Sales.id asc;";
			System.out.println("queyerer====== " + query);
			ResultSet rs = connection.createStatement().executeQuery(query);
			int i = 6, k = 1, g = 7;
			HashMap<String, String> wpExportList = new HashMap<String, String>();
			int total = 0;
			int total2 = 0;
			int moneyReceive = 0;
			Date timeCreate = null;
			String barcodeBill = "";
			while (rs.next()) {
				wpExportList.put("A" + i, String.valueOf(k));
				wpExportList.put("B" + i, rs.getString("nameProduct"));
				wpExportList.put("A" + g, decimalFormat.format(rs.getInt("priceSell")) + " x " + rs.getInt("quantitys")
						+ "(" + rs.getString("unit") + ") =");
//				total = rs.getInt("priceTotal");
				total = rs.getInt("priceSell") * rs.getInt("quantitys");
				wpExportList.put("H" + g, decimalFormat.format(total));
				i = i + 2;
				g = g + 2;
				k++;
				total2 += total;
				moneyReceive = rs.getInt("pricereceive");
				timeCreate = rs.getTimestamp("createdAtB");
				barcodeBill = rs.getString("barcodebill");
			}
			rs.close();
			connection.close();
			// final URL FILE_NAME =
			// this.getClass().getResource("/files/bill.xls");
			final String FILE_NAME = "files/bill.xls";

			// POIFSFileSystem fs = new POIFSFileSystem(new
			// FileInputStream(FILE_NAME.getPath()));
			int rowRange = 4;
			int say = 1;
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(FILE_NAME));
			HSSFWorkbook wb = new HSSFWorkbook(fs, true);
			HSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				while (cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();

					if (currentRow.getRowNum() > 4 && currentRow.getRowNum() < ((k * 2) + rowRange)) {
						if (currentCell.getCellTypeEnum() == CellType.BLANK) {
							if (currentCell.getAddress().toString().equals("A" + (currentRow.getRowNum() + 1))) {
								currentCell.setCellValue(wpExportList.get(currentCell.getAddress().toString()));
								HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_BOLD, false);
								currentCell.setCellStyle(style);
								if ((currentRow.getRowNum() + 1) % 2 == 0) {

								} else {
									style = styleCell(wb, CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_NORMAL, true);
								}
								currentCell.setCellStyle(style);
							}
							if (currentCell.getAddress().toString().equals("B" + (currentRow.getRowNum() + 1))) {
								HSSFCellStyle style;
								if ((currentRow.getRowNum() + 1) % 2 == 0) {
									style = styleCell(wb, CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_BOLD, false);
									sheet.addMergedRegion(
											new CellRangeAddress(currentRow.getRowNum(), currentRow.getRowNum(), 1, 7));
								} else {
									style = styleCell(wb, CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_NORMAL, true);
									sheet.addMergedRegion(
											new CellRangeAddress(currentRow.getRowNum(), currentRow.getRowNum(), 0, 6));
								}
								currentCell.setCellStyle(style);
								currentCell.setCellValue(wpExportList.get(currentCell.getAddress().toString()));
							}
							if (currentCell.getAddress().toString().equals("H" + (currentRow.getRowNum() + 1))
									&& wpExportList.get(currentCell.getAddress().toString()) != null) {
								HSSFCellStyle style;
								currentCell.setCellValue(wpExportList.get(currentCell.getAddress().toString()));
								style = styleCell(wb, CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_BOLD, false);
								currentCell.setCellStyle(style);
							}
						}
					}
					if (currentCell.getAddress().toString().equals("B" + ((k * 2) + rowRange))) {
						currentCell.setCellValue(Global.billTotalText);
						sheet.removeMergedRegion((k * 2) + 2);
						HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_BOLD, false);
						currentCell.setCellStyle(style);
					}
					if (currentRow.getRowNum() == ((k * 2) + rowRange - 1)) {
						HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_BOLD, false);
						style.setBorderTop(CellStyle.BORDER_DOTTED);
						style.setTopBorderColor(IndexedColors.BLACK.getIndex());
						currentCell.setCellStyle(style);
					}
					if (currentCell.getAddress().toString().equals("H" + ((k * 2) + rowRange))) {
						currentCell.setCellValue(decimalFormat.format(total2));
						HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_BOLD, false);
						style.setBorderTop(CellStyle.BORDER_DOTTED);
						style.setTopBorderColor(IndexedColors.BLACK.getIndex());
						currentCell.setCellStyle(style);
					}

					if (currentCell.getAddress().toString().equals("B" + ((k * 2) + rowRange + 1))) {
						if (moneyReceive > 0) {
							currentCell.setCellValue(Global.billMoneyReceiveText);
							HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_NORMAL, false);
							currentCell.setCellStyle(style);
						}
					}
					if (currentCell.getAddress().toString().equals("H" + ((k * 2) + rowRange + 1))) {
						if (moneyReceive > 0) {
							currentCell.setCellValue(decimalFormat.format(moneyReceive));
							HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_BOLD, false);
							currentCell.setCellStyle(style);
						}
					}

					if (currentCell.getAddress().toString().equals("B" + ((k * 2) + rowRange + 2))) {
						if (moneyReceive > 0) {
							currentCell.setCellValue(Global.billMoneyBackText);
							HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_NORMAL, false);
							currentCell.setCellStyle(style);
						}
					}
					if (currentCell.getAddress().toString().equals("H" + ((k * 2) + rowRange + 2))) {
						if (moneyReceive > 0) {
							currentCell.setCellValue(decimalFormat.format(moneyReceive - total2));
							HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_BOLD, false);
							currentCell.setCellStyle(style);
						}
					}

					if (moneyReceive > 0) {
						say = 3;
					}
					if (currentCell.getAddress().toString().equals("A" + ((k * 2) + rowRange + say))) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm",
								new Locale("vi", "VN"));
						String date = simpleDateFormat.format(timeCreate);
						currentCell.setCellValue(Global.billTimePayText + date);
						HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_CENTER, Font.BOLDWEIGHT_NORMAL, true);
						currentCell.setCellStyle(style);
						sheet.addMergedRegion(new CellRangeAddress(((k * 2) + rowRange + say - 1),
								((k * 2) + rowRange + say - 1), 0, 7));
					}
					if (currentCell.getAddress().toString().equals("A" + ((k * 2) + rowRange + say + 1))) {
						currentCell.setCellValue(Global.billThanksSayText);
						HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_CENTER, Font.BOLDWEIGHT_NORMAL, true);
						currentCell.setCellStyle(style);
						sheet.addMergedRegion(
								new CellRangeAddress((k * 2) + rowRange + say, (k * 2) + rowRange + say, 0, 7));
					}
					if (currentCell.getAddress().toString().equals("A" + ((k * 2) + rowRange + say + 2))) {
						currentCell.setCellValue("Mã Hóa Đơn: " + barcodeBill);
						HSSFCellStyle style = styleCell(wb, CellStyle.ALIGN_CENTER, Font.BOLDWEIGHT_NORMAL, true);
						currentCell.setCellStyle(style);
						sheet.addMergedRegion(new CellRangeAddress(((k * 2) + rowRange + say + 1),
								((k * 2) + rowRange + say + 1), 0, 7));
					}
					// if (currentCell.getAddress().toString().equals("A" + ((k * 2) + rowRange +
					// say + 5))) {
					// File fileO = new File("barImg" + "/" + barcodeBill + ".png");
					// FileInputStream fis = new FileInputStream(fileO);
					// ByteArrayOutputStream img_bytes = new ByteArrayOutputStream();
					// int b;
					// while ((b = fis.read()) != -1)
					// img_bytes.write(b);
					// fis.close();
					// HSSFClientAnchor anchor = new HSSFClientAnchor(155, 0, 0, 255, (short) 0,
					// ((k * 2) + rowRange + say + 1), (short) 8, ((k * 2) + rowRange + say + 2));
					// int index = wb.addPicture(img_bytes.toByteArray(),
					// HSSFWorkbook.PICTURE_TYPE_PNG);
					// HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
					// patriarch.createPicture(anchor, index);
					// anchor.setAnchorType(2);
					// }
				}
			}
			wb.setPrintArea(0, "$A$1:$H$" + ((k * 2) + rowRange + say + 3));
			FileOutputStream out = new FileOutputStream("files/bill_copy.xls");
			Path src = Paths.get("files/bill_copy.xls");
			file = new File(src.toString());
			wb.write(out);
			out.close();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}

		System.out.println(file);
		return file;
	}

	@SuppressWarnings("deprecation")
	private static HSSFCellStyle styleCell(HSSFWorkbook wb, short alignLeft, short boldweightBold, boolean italic) {
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 22);
		font.setFontName("Verdana");
		font.setBoldweight(boldweightBold);
		font.setItalic(italic);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(alignLeft);
		style.setFont(font);
		return style;
	}

	@SuppressWarnings("unchecked")
	private void buildTableHetHang() {
		try {
			remainHangHoa = new HashMap<>();
			_tableHetHang.getItems().clear();
			_tableHetHang.getColumns().clear();
			ObservableList<Warehourse> listHetHang = FXCollections.observableArrayList();
			connection = handler.getConnection();
			String query = "SELECT warehouse.*,Products.nameProduct FROM warehouse LEFT OUTER JOIN Products ON (warehouse.productId = Products.id) WHERE  warehouse.remainingAmount <=10";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					listHetHang.add(new Warehourse(rs.getInt("id"), rs.getInt("productId"), rs.getString("nameProduct"),
							rs.getInt("quantitySold"), rs.getInt("remainingAmount")));
					remainHangHoa.put(rs.getInt("productId"), rs.getInt("remainingAmount"));
				}
			}

			remainHangHoa.forEach((key, value) -> {
				System.out.println("key " + key);
				System.out.println("value " + value);
			});
			rs.close();
			connection.close();
			TableColumn<Warehourse, Number> indexColumn = new TableColumn<Warehourse, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
					_tableHetHang.getItems().indexOf(column.getValue()) + 1));
			_tableHetHang.getColumns().add(0, indexColumn);
			_tableHetHang.getSelectionModel().selectedItemProperty().addListener(
					(ObservableValue<? extends Warehourse> observable, Warehourse oldValue, Warehourse newValue) -> {
						if (newValue == null) {
							return;
						}
					});
			TableColumn<Warehourse, String> nameProductCol = new TableColumn<Warehourse, String>("Sản Phẩm");
			nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
			nameProductCol.setCellFactory(TextFieldTableCell.<Warehourse>forTableColumn());
			nameProductCol.setStyle("-fx-alignment: CENTER-LEFT;");
			TableColumn<Warehourse, Integer> remainingAmountCol = new TableColumn<Warehourse, Integer>("Còn Lại");
			remainingAmountCol.setCellValueFactory(new PropertyValueFactory<>("remainingAmount"));
			remainingAmountCol.setMinWidth(70);
			remainingAmountCol.setMaxWidth(70);
			remainingAmountCol.setCellFactory(column -> new TableCell<Warehourse, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						if (item >= 0 && item <= 5) {
							setStyle("-fx-text-fill: RED;-fx-alignment: center;");
						} else {
							setStyle("-fx-text-fill: Orange;-fx-alignment: center;");
						}
						setText(String.valueOf(item));
					}
				}
			});
			remainingAmountCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			_tableHetHang.getColumns().addAll(nameProductCol, remainingAmountCol);
			_tableHetHang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			_tableHetHang.getItems().addAll(listHetHang);
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}