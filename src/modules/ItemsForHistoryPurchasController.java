package modules;

import java.awt.List;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import database.DbHandler;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.PurchasesItems;

public class ItemsForHistoryPurchasController implements Initializable {
	@FXML
	private JFXButton btnNext, btnBack, btnPay;
	@FXML
	private TableView<PurchasesItems> _table;
	@FXML
	private TextField txtPay;
	@FXML
	private Label lblTotalNumber, lblStatusPay, lbSoTienThanhToan;
	private DecimalFormat decimalFormat = new DecimalFormat("###,###");
	protected Connection connection;
	private DbHandler handler;
	private List ex;

	public void initialize(URL url, ResourceBundle rb) {
		ex = NhapHangController.getItemDetailHistoriesEx();
		Image imageNext = new Image(getClass().getResourceAsStream("/icons/next.png"));
		Image imageBack = new Image(getClass().getResourceAsStream("/icons/back.png"));
		btnNext.setGraphic(new ImageView(imageNext));
		btnBack.setGraphic(new ImageView(imageBack));
		try {
			if (NhapHangController.getItemDetailHistories().size() > 0) {
				buildTable();
				String stsPay = ex.getItem(0);
				String grandTotal = ex.getItem(1);
				String paid = ex.getItem(2);
				if (stsPay.equals("0")) {
					lblStatusPay.setText("Chưa Thanh Toán");
				} else if (stsPay.equals("1")) {
					lblStatusPay.setText("Đã Thanh Toán: " + decimalFormat.format(Integer.parseInt(paid)) + ". Còn lại:"
							+ decimalFormat.format(Integer.parseInt(grandTotal) - Integer.parseInt(paid)));
				} else {
					lblStatusPay.setText("Đã Thanh Toán");
					lbSoTienThanhToan.setVisible(false);
					txtPay.setVisible(false);
					btnPay.setVisible(false);
				}
				lblTotalNumber.setText(decimalFormat.format(Integer.parseInt(grandTotal)));
			}
		} catch (Exception e) {
			Logger.getLogger(ItemsForHistoryPurchasController.class.getName()).log(Level.SEVERE, null, e);
		}

		txtPay.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (!newValue.isEmpty() && newValue.length() < 9) {
					int kq = (Integer.parseInt(ex.getItem(1)) - Integer.parseInt(ex.getItem(2)));

					if (newValue.matches("\\d*") && Integer.parseInt(newValue) <= kq) {
						int value = Integer.parseInt(newValue);
						System.out.println(value);
					} else {
						txtPay.setText(oldValue);
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void buildTable() {

		TableColumn<PurchasesItems, Number> indexColumn = new TableColumn<PurchasesItems, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setStyle("-fx-alignment: CENTER;");
		indexColumn.setCellValueFactory(
				column -> new ReadOnlyObjectWrapper<Number>(_table.getItems().indexOf(column.getValue()) + 1));
		_table.getColumns().add(0, indexColumn);
		_table.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends PurchasesItems> observable, PurchasesItems oldValue,
						PurchasesItems newValue) -> {
					if (newValue == null) {
						return;
					}

				});
		TableColumn<PurchasesItems, String> nameProductCol = new TableColumn<PurchasesItems, String>("Mặt Hàng");
		nameProductCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		nameProductCol.setCellFactory(TextFieldTableCell.<PurchasesItems>forTableColumn());
		nameProductCol.setStyle("-fx-alignment: CENTER");
		TableColumn<PurchasesItems, Integer> quantityCol = new TableColumn<PurchasesItems, Integer>("Số Lượng");
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantityPur"));
		quantityCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		quantityCol.setMinWidth(80);
		quantityCol.setMaxWidth(80);

		TableColumn<PurchasesItems, Integer> priceOriginCol = new TableColumn<PurchasesItems, Integer>("Giá Gốc");
		priceOriginCol.setCellValueFactory(new PropertyValueFactory<>("originCost"));
		priceOriginCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceOriginCol.setMinWidth(80);
		priceOriginCol.setMaxWidth(80);
		priceOriginCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

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

		TableColumn<PurchasesItems, Integer> priceSellCol = new TableColumn<PurchasesItems, Integer>("Giá Bán");
		priceSellCol.setCellValueFactory(new PropertyValueFactory<>("sellCost"));
		priceSellCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceSellCol.setMinWidth(80);
		priceSellCol.setMaxWidth(80);
		priceSellCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

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
		TableColumn<PurchasesItems, Integer> priceTotalCol = new TableColumn<PurchasesItems, Integer>("Tổng Tiền");
		priceTotalCol.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
		priceTotalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceTotalCol.setMinWidth(100);
		priceTotalCol.setMaxWidth(100);
		priceTotalCol.setCellFactory(column -> new TableCell<PurchasesItems, Integer>() {

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
		_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		_table.getColumns().addAll(nameProductCol, quantityCol, priceSellCol, priceOriginCol, priceTotalCol);
		_table.getItems().addAll(NhapHangController.getItemDetailHistories());
	}

	@FXML
	private void actionPay() {

		if (!txtPay.getText().isEmpty()) {
			int valueP = Integer.parseInt(txtPay.getText());
			int total = Integer.parseInt(lblTotalNumber.getText().replace(",", ""));
			try {
				int sts;

				if (valueP == total) {
					sts = 2;
					btnPay.setVisible(false);
					txtPay.setVisible(false);
					lbSoTienThanhToan.setVisible(false);
				} else if (valueP > 0 && valueP < total) {
					sts = 1;
				} else {
					sts = 0;
				}
				handler = new DbHandler();
				connection = handler.getConnection();
				Statement stmt = connection.createStatement();
				String sql4 = "UPDATE Purchases SET paid = paid + '" + valueP + "', paymentStatus = '" + sts
						+ "' WHERE id='" + ex.getItem(3) + "'";
				stmt.executeUpdate(sql4);
				connection.commit();
				stmt.close();
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(ItemsForHistoryPurchasController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}
}