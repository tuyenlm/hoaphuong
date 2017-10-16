package modules;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Bill;
import models.Buy;

public class ItemsForBillController implements Initializable {
	@FXML
	private JFXButton btnNext, btnBack;
	@FXML
	private TableView<Buy> _table;

	public void initialize(URL url, ResourceBundle rb) {
		Image imageNext = new Image(getClass().getResourceAsStream("/icons/next.png"));
		Image imageBack = new Image(getClass().getResourceAsStream("/icons/back.png"));
		btnNext.setGraphic(new ImageView(imageNext));
		btnBack.setGraphic(new ImageView(imageBack));
		if (HomeController.getItemDetailBill().size() > 0) {
			buildTable();
		}
	}

	private void buildTable() {

		TableColumn<Buy, Number> indexColumn = new TableColumn<Buy, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setStyle("-fx-alignment: CENTER;");
		indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(_table.getItems().indexOf(column.getValue()) + 1));
		_table.getColumns().add(0, indexColumn);
		_table.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Buy> observable, Buy oldValue, Buy newValue) -> {
			if (newValue == null) {
				return;
			}

		});
		TableColumn<Buy, String> nameProductCol = new TableColumn<Buy, String>("Ten");
		nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
		nameProductCol.setCellFactory(TextFieldTableCell.<Buy>forTableColumn());
		nameProductCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		TableColumn<Buy, Integer> quantityCol = new TableColumn<Buy, Integer>("So Luong");
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quatity"));
		quantityCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		quantityCol.setMinWidth(80);
		quantityCol.setMaxWidth(80);

		TableColumn<Buy, Integer> priceCol = new TableColumn<Buy, Integer>("Gia Tien");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceCol.setMinWidth(80);
		priceCol.setMaxWidth(80);
		TableColumn<Buy, Integer> priceTotalCol = new TableColumn<Buy, Integer>("Tổng Tiền");
		priceTotalCol.setCellValueFactory(new PropertyValueFactory<>("priceTotal"));
		priceTotalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		priceTotalCol.setMinWidth(100);
		priceTotalCol.setMaxWidth(100);
		_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		_table.getColumns().addAll(nameProductCol, quantityCol, priceCol, priceTotalCol);
		_table.getItems().addAll(HomeController.getItemDetailBill());
	}
}
