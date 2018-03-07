package modules;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import models.Warehourse;

public class TabKhoHangController implements Initializable {

	private static DbHandler handler;
	private static Connection connection;
	private ObservableList<Warehourse> listHetHang = FXCollections.observableArrayList();
	private ObservableList<Warehourse> listKhoHang = FXCollections.observableArrayList();
	@FXML
	private TableView<Warehourse> _tableHetHang;
	@FXML
	private TableView<Warehourse> _tableKhoHang;
	@FXML
	private TextField txtSearchHetHang, txtSearchKho;

	public void initialize(URL url, ResourceBundle rb) {
		handler = new DbHandler();
		buildTableHetHang("");
		buildTableKho("");
		txtSearchHetHang.textProperty().addListener((a, b, c) -> {
			if (c != null) {
				buildTableHetHang(c);
			}
		});
		txtSearchKho.textProperty().addListener((a, b, c) -> {
			if (c != null) {
				buildTableKho(c);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void buildTableHetHang(String text) {
		try {
			_tableHetHang.getItems().clear();
			_tableHetHang.getColumns().clear();
			listHetHang.clear();
			connection = handler.getConnection();
			String query;
			if (!text.isEmpty()) {
				query = "SELECT warehouse.*,Products.nameProduct FROM warehouse LEFT OUTER JOIN Products ON (warehouse.productId = Products.id) WHERE Products.nameProduct LIKE '%"
						+ text + "%' AND warehouse.remainingAmount <=10";
			} else {
				query = "SELECT warehouse.*,Products.nameProduct FROM warehouse LEFT OUTER JOIN Products ON (warehouse.productId = Products.id) WHERE  warehouse.remainingAmount <=10";
			}
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					listHetHang.add(new Warehourse(rs.getInt("id"), rs.getInt("productId"), rs.getString("nameProduct"),
							rs.getInt("quantitySold"), rs.getInt("remainingAmount")));
				}
			}
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
						if (item > 0 && item <= 5) {
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
			Logger.getLogger(TabKhoHangController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void buildTableKho(String text) {
		try {
			_tableKhoHang.getItems().clear();
			_tableKhoHang.getColumns().clear();
			listKhoHang.clear();
			connection = handler.getConnection();
			String query;
			if (!text.isEmpty()) {
				query = "SELECT warehouse.*,Products.nameProduct FROM warehouse LEFT OUTER JOIN Products ON (warehouse.productId = Products.id) WHERE Products.nameProduct LIKE '%"
						+ text + "%'";
			} else {
				query = "SELECT warehouse.*,Products.nameProduct FROM warehouse LEFT OUTER JOIN Products ON (warehouse.productId = Products.id)";
			}
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					listKhoHang.add(new Warehourse(rs.getInt("id"), rs.getInt("productId"), rs.getString("nameProduct"),
							rs.getInt("quantitySold"), rs.getInt("remainingAmount")));
				}
			}
			rs.close();
			connection.close();
			TableColumn<Warehourse, Number> indexColumn = new TableColumn<Warehourse, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
					_tableKhoHang.getItems().indexOf(column.getValue()) + 1));
			_tableKhoHang.getColumns().add(0, indexColumn);
			_tableKhoHang.getSelectionModel().selectedItemProperty().addListener(
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
						if (item > 0 && item <= 5) {
							setStyle("-fx-text-fill: RED;-fx-alignment: center;");
						} else {
							setStyle("-fx-text-fill: Orange;-fx-alignment: center;");
						}

						setText(String.valueOf(item));
					}
				}
			});
			remainingAmountCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			TableColumn<Warehourse, Integer> quantitySoldCol = new TableColumn<Warehourse, Integer>("Đã Bán");
			quantitySoldCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
			quantitySoldCol.setMinWidth(60);
			quantitySoldCol.setMaxWidth(60);
			quantitySoldCol.setStyle("-fx-alignment: CENTER;");

			_tableKhoHang.getColumns().addAll(nameProductCol, quantitySoldCol, remainingAmountCol);
			_tableKhoHang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			_tableKhoHang.getItems().addAll(listKhoHang);
		} catch (Exception e) {
			Logger.getLogger(TabKhoHangController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

}