package modules;

import javafx.scene.control.Label;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import models.Buy;
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
	private JFXComboBox<String> comboboxYear, comboboxMonth, comboboxChangeView;

	@FXML
	private TextField txtBarcode;
	@FXML
	private Label lblTotal;

	private ObservableList<PurchasesItems> lists = FXCollections.observableArrayList();
	private HashMap<Integer, ObservableList<PurchasesItems>> itemsList = new HashMap<Integer, ObservableList<PurchasesItems>>();
	private DbHandler handler;
	private static Connection connection;
	private HashMap<Integer, Integer> dataMonth;
	private HashMap<Integer, Integer> gocToltal;
	private HashMap<Integer, Integer> thuveToltal;

	public void initialize(URL url, ResourceBundle rb) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtBarcode.requestFocus();
				txtBarcode.selectAll();
			}
		});
		handler = new DbHandler();
		txtBarcode.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {

				if (ke.getCode().toString().equals("ENTER") && !txtBarcode.getText().trim().isEmpty()) {
					doSearch(txtBarcode.getText().trim());
					System.out.println(txtBarcode.getText());
				}
			}
		});
		buildTableItems();
	}

	private void doSearch(String val) {
		try {
			connection = handler.getConnection();
			String query = "SELECT * FROM products WHERE barcodeProduct = '" + val + "'";

			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					System.out.println(rs.getInt("id"));
					System.out.println(rs.getString("nameProduct"));
					System.out.println(rs.getInt("priceOrigin"));
					System.out.println(rs.getInt("priceSell"));

					if (!itemsList.containsKey(rs.getInt("id"))) {
						ObservableList<PurchasesItems> items = FXCollections.observableArrayList();
						items.add(new PurchasesItems(0, 0, rs.getString("nameProduct"), rs.getInt("id"), 1,
								rs.getInt("priceOrigin"), rs.getInt("priceSell"), rs.getInt("priceOrigin")));
						itemsList.put(rs.getInt("id"), items);
					} else {
						if (itemsList.get(rs.getInt("id")).get(0).getProductId() == rs.getInt("id")) {
							itemsList.get(rs.getInt("id")).get(0)
									.setQuantityPur(itemsList.get(rs.getInt("id")).get(0).getQuantityPur() + 1);
							itemsList.get(rs.getInt("id")).get(0).setSubTotal(
									rs.getInt("priceOrigin") * itemsList.get(rs.getInt("id")).get(0).getQuantityPur());
						}
					}
				}
				buildTableItems();

			}
			txtBarcode.requestFocus();
			txtBarcode.selectAll();
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void updateTotal() {
		System.out.println("itemsList.size() " + itemsList.size());
		if (itemsList.size() > 0) {
			int val = 0;
			for (Map.Entry<Integer, ObservableList<PurchasesItems>> entry : itemsList.entrySet()) {
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
			TableColumn<PurchasesItems, Number> indexColumn = new TableColumn<PurchasesItems, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(
					column -> new ReadOnlyObjectWrapper<Number>(tableImport.getItems().indexOf(column.getValue()) + 1));
			tableImport.getColumns().add(0, indexColumn);
			tableImport.getSelectionModel().selectedItemProperty()
					.addListener((ObservableValue<? extends PurchasesItems> observable, PurchasesItems oldValue,
							PurchasesItems newValue) -> {
						if (newValue == null) {
							return;
						}
					});
			TableColumn<PurchasesItems, String> nameProductCol = new TableColumn<PurchasesItems, String>("Sản Phẩm");
			nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
			nameProductCol.setCellFactory(TextFieldTableCell.<PurchasesItems>forTableColumn());
			nameProductCol.setStyle("-fx-alignment: CENTER-LEFT;");
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
							} else {
								txtQuatity.setText(b);
							}
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
						}
					});
				}
			});

			tableImport.getColumns().addAll(nameProductCol, sellCol, originCol, quatityCol, priceTotalCol,
					deleteColumn);
			tableImport.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			int val = 0;
			for (Map.Entry<Integer, ObservableList<PurchasesItems>> entry : itemsList.entrySet()) {
				ObservableList<PurchasesItems> value = entry.getValue();
				tableImport.getItems().addAll(value);
				val += value.get(0).getSubTotal();
			}
			lblTotal.setText(HomeController.decimalFormat.format(val));

		} catch (Exception e) {
			Logger.getLogger(NhapHangController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}