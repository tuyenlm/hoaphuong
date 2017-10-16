package modules;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.DbHandler;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import models.Bill;

public class TabHistoryPayController implements Initializable {

	private ObservableList<Bill> lists = FXCollections.observableArrayList();
	@FXML
	private TableView<Bill> tableHistoryPay;
	@FXML
	private ListView<String> listViewProductCount;
	private DbHandler handler;
	private Connection connection;

	public void initialize(URL url, ResourceBundle rb) {

		handler = new DbHandler();
		ObservableList<String> itemLabel = FXCollections.observableArrayList("Mã Barcode", "Tên sản phẩm", "Giá gốc", "Giá bán", "Đơn vị", "Vị trí", "Danh mục", "Ngày tạo", "Mô tả");
		listViewProductCount.getItems().addAll(itemLabel);
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
		buildTableHistoryPay();

	}

	@SuppressWarnings("unchecked")
	private void buildTableHistoryPay() {
		tableHistoryPay.getItems().clear();
		tableHistoryPay.getColumns().clear();
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

}
