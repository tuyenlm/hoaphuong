package modules;

import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import database.DbHandler;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import models.Products;

public class TabUnknowProductController implements Initializable {

	private DbHandler dbHandler;
	private Connection connection;
	private boolean saveStatus = true;

	// private TableView<Products> tableUnknowProduct;
	private static HashMap<String, ObservableList<Products>> itemUnknowList;
	@FXML
	private TableView<Products> tableUnknowProduct;

	public void initialize(URL url, ResourceBundle rb) {
		builTable();
		
	}

	public static HashMap<String, ObservableList<Products>> getVariable() {
		return itemUnknowList;
	}

	public static void setVariable(HashMap<String, ObservableList<Products>> itemUnknowLis) {
		TabUnknowProductController.itemUnknowList = itemUnknowLis;
	}

	public void builTable() {
		if (itemUnknowList.size() > 0) {
			TableColumn<Products, Number> indexColumn = new TableColumn<Products, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(tableUnknowProduct.getItems().indexOf(column.getValue()) + 1));
			tableUnknowProduct.getColumns().add(0, indexColumn);
			tableUnknowProduct.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Products> observable, Products oldValue, Products newValue) -> {
				if (newValue == null) {
					return;
				}
			});
			TableColumn<Products, String> nameProductCol = new TableColumn<Products, String>("Sản Phẩm");
			nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
			nameProductCol.setCellFactory(TextFieldTableCell.<Products>forTableColumn());
			nameProductCol.setStyle("-fx-alignment: CENTER-LEFT;");

			TableColumn<Products, Integer> priceCol = new TableColumn<Products, Integer>("Đơn Giá");
			priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
			priceCol.setMinWidth(70);
			priceCol.setMaxWidth(70);
			priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			priceCol.setCellFactory(column -> new TableCell<Products, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						// setText(decimalFormat.format(item));
						setText(item.toString());
					}
				}
			});
			tableUnknowProduct.getColumns().addAll(nameProductCol, priceCol);
			tableUnknowProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			for (Map.Entry<String, ObservableList<Products>> entry : itemUnknowList.entrySet()) {
				ObservableList<Products> value = entry.getValue();
				tableUnknowProduct.getItems().addAll(value);

			}
		}

	}

}
