package modules;

import java.awt.List;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.BarcodeController;
import application.Global;
import application.ValidateHandle;
import database.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import models.Products;

public class TabUnknowProductController implements Initializable {

	private DbHandler handler;
	private Connection connection;
	private GridPane gridProduct;
	private TextField nameProduct, unit, location, priceOrigin, priceSell;
	private ComboBox<String> comCatalogId;
	private TextArea description;
	private ObservableList<String> checkValidate = FXCollections.observableArrayList();
	private File outputFileP;
	private SimpleStringProperty bindTxtCountHoliday;
	// private TableView<Products> tableUnknowProduct;
	private static HashMap<String, ObservableList<Products>> itemUnknowList;
	@FXML
	private TableView<Products> tableUnknowProduct;

	public void initialize(URL url, ResourceBundle rb) {
		builTable();
		handler = new DbHandler();
		tableUnknowProduct.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					String bar = tableUnknowProduct.getSelectionModel().getSelectedItem().getBarcodeProduct();
					addProduct(bar);
				}
			}
		});

	}

	private void addProduct(String bar) {
		try {
			outputFileP = null;
			Dialog<List> dialg = renderDialogProduct();
			TextField txtBarcode = new TextField(bar);
			txtBarcode.setDisable(true);
			gridProduct.add(new Label("Mã Barcode:"), 0, 2);
			gridProduct.add(txtBarcode, 1, 2);
			ImageView imgBarcode = new ImageView();
			imgBarcode.setFitHeight(50);
			gridProduct.add(imgBarcode, 1, 3);
			String barcodeDialog = bar.trim();
			File fileNewBar = BarcodeController.renderBarcode(barcodeDialog);
			outputFileP = fileNewBar;
			imgBarcode.setImage(new Image(fileNewBar.toURI().toString()));
			Optional<List> result = dialg.showAndWait();
			if (result.isPresent()) {
				List value = result.get();
				String nameProductDialog = value.getItem(0);
				String catalogIdDialog = value.getItem(1);
				String descriptionDialog = value.getItem(2);
				String locationDialog = value.getItem(3);
				int priceOriginDialog = Integer.parseInt(value.getItem(4));
				int priceSellDialog = Integer.parseInt(value.getItem(5));
				String unitDialog = value.getItem(6);
				try {
					connection = handler.getConnection();
					Statement stmt = connection.createStatement();
					String sql = "insert into products (nameProduct, catalogId, barcodeProduct, descriptionProduct,location,priceOrigin,priceSell,unit) "
							+ "values ('" + nameProductDialog
							+ "',(Select id From catalogs Where catalogs.nameCatalog = '" + catalogIdDialog + "'),'"
							+ barcodeDialog + "','" + descriptionDialog + "','" + locationDialog + "','"
							+ priceOriginDialog + "','" + priceSellDialog + "','" + unitDialog + "')";
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet rs = stmt.getGeneratedKeys();
					connection.commit();
					if (rs.next()) {
						int d = tableUnknowProduct.getSelectionModel().getSelectedIndex();
						itemUnknowList.remove(tableUnknowProduct.getItems().get(d).getBarcodeProduct());
						tableUnknowProduct.getItems().remove(d);
						if (itemUnknowList.size() == 0) {
							tableUnknowProduct.refresh();
							Global.val.setValue( "0");
						}
					}
					stmt.close();
					connection.close();
				} catch (Exception e) {
					Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
				}
			} else {
				if (outputFileP != null && outputFileP.exists()) {
					outputFileP.delete();
				}
			}
		} catch (Exception e) {
			Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public Dialog<List> renderDialogProduct() {
		// Create the custom dialog.
		Dialog<List> dialogProduct = new Dialog<>();
		dialogProduct.setTitle("Thêm Sản Phẩm");
		// Set the button types.
		ButtonType saveButtonTypeButtonType = new ButtonType("Lưu", ButtonData.OK_DONE);
		dialogProduct.getDialogPane().getButtonTypes().addAll(saveButtonTypeButtonType, ButtonType.CANCEL);
		// Create the username and password labels and fields.
		gridProduct = new GridPane();
		gridProduct.setHgap(10);
		gridProduct.setVgap(10);
		gridProduct.setPadding(new Insets(20, 10, 10, 10));
		nameProduct = new TextField();
		gridProduct.add(new Label("Tên sản phẩm:"), 0, 0);
		gridProduct.add(nameProduct, 1, 0);
		nameProduct.setMinWidth(300);
		nameProduct.setMaxWidth(300);
		comCatalogId = new ComboBox<String>();
		try {
			connection = handler.getConnection();
			String query = "SELECT * FROM catalogs ORDER BY id asc";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					comCatalogId.getItems().add(rs.getString("nameCatalog"));
				}
			}
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
		}

		comCatalogId.setMinWidth(300);
		comCatalogId.setMaxWidth(300);
		gridProduct.add(new Label("Danh mục:"), 0, 1);
		gridProduct.add(comCatalogId, 1, 1);
		description = new TextArea();
		description.setWrapText(true);
		gridProduct.add(new Label("Mô tả:"), 0, 4);
		gridProduct.add(description, 1, 4);
		description.setMinWidth(300);
		description.setMaxWidth(300);
		description.setPrefRowCount(3);
		location = new TextField();
		gridProduct.add(new Label("Vi tri:"), 0, 5);
		gridProduct.add(location, 1, 5);
		location.setMinWidth(300);
		location.setMaxWidth(300);
		priceOrigin = new TextField();
		gridProduct.add(new Label("Giá gốc:"), 0, 6);
		gridProduct.add(priceOrigin, 1, 6);
		priceOrigin.setMinWidth(300);
		priceOrigin.setMaxWidth(300);
		priceSell = new TextField();
		gridProduct.add(new Label("Giá bán:"), 0, 7);
		gridProduct.add(priceSell, 1, 7);
		priceSell.setMinWidth(300);
		priceSell.setMaxWidth(300);
		unit = new TextField();
		gridProduct.add(new Label("Đơn vị:"), 0, 8);
		gridProduct.add(unit, 1, 8);
		unit.setMinWidth(300);
		unit.setMaxWidth(300);
		Node saveButton = dialogProduct.getDialogPane().lookupButton(saveButtonTypeButtonType);
		saveButton.setDisable(true);
		saveButton.addEventFilter(ActionEvent.ACTION, ae -> {
			ObservableList<String> checkValidate = validateProduct();
			if (checkValidate.contains("false")) {
				ae.consume();
			}
		});
		nameProduct.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
		dialogProduct.getDialogPane().setContent(gridProduct);
		Platform.runLater(() -> nameProduct.requestFocus());
		dialogProduct.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonTypeButtonType) {
				List valueDialog = new List();
				valueDialog.add(nameProduct.getText());
				valueDialog.add(comCatalogId.getValue());
				valueDialog.add(description.getText());
				valueDialog.add(location.getText());
				valueDialog.add(priceOrigin.getText());
				valueDialog.add(priceSell.getText());
				valueDialog.add(unit.getText());
				return valueDialog;
			}
			return null;
		});
		return dialogProduct;
	}

	public ObservableList<String> validateProduct() {
		checkValidate.clear();
		final ContextMenu nameProductValidator = new ContextMenu();
		validateElement(nameProductValidator, null, nameProduct, "Nhập tên sản phẩm.", false, "", null);

		final ContextMenu locationValidator = new ContextMenu();
		validateElement(locationValidator, null, location, "Nhập vị trí sản phẩm.", false, "", null);

		final ContextMenu catalogIdValidator = new ContextMenu();
		validateElement(catalogIdValidator, comCatalogId, null, "Chọn danh mục.", true, "", null);

		final ContextMenu priceOriginValidator = new ContextMenu();
		validateElement(priceOriginValidator, null, priceOrigin, "Nhập giá gốc.", false, "number",
				"Nhập giá trị là số.");

		final ContextMenu priceSellValidator = new ContextMenu();
		validateElement(priceSellValidator, null, priceSell, "Nhập giá bán.", false, "number", "Nhập giá trị là số.");

		final ContextMenu unitValidator = new ContextMenu();
		validateElement(unitValidator, null, unit, "Nhập đơn vị.", false, "", null);

		return (ObservableList<String>) checkValidate;
	}

	private void validateElement(ContextMenu elementValidator, ComboBox<String> elementCom, TextField element,
			String mess, Boolean isComboBox, String typeValidate, String messForType) {
		elementValidator.setAutoHide(true);
		elementValidator.setStyle("-fx-text-fill: WHITE;-fx-background-color:YELLOW");
		if (isComboBox) {
			if (elementCom.getValue() == null) {
				checkValidate.add("false");
				elementValidator.getItems().clear();
				elementValidator.getItems().add(new MenuItem(mess));
				elementValidator.show(elementCom, Side.RIGHT, 10, 0);
				elementCom.setStyle("-fx-border-color:red;-fx-max-height:25px;");
				elementCom.valueProperty().addListener((a, old, newvalue) -> {
					if (!newvalue.isEmpty()) {
						elementValidator.hide();
						elementCom.setStyle(null);
					}
				});
			} else
				checkValidate.add("true");
		} else {
			if (element.getText().isEmpty()) {
				checkValidate.add("false");
				elementValidator.getItems().clear();
				elementValidator.getItems().add(new MenuItem(mess));
				elementValidator.show(element, Side.RIGHT, 10, 0);
				element.setStyle("-fx-border-color:red;-fx-max-height:25px;");
				element.textProperty().addListener((a, old, newvalue) -> {
					if (!newvalue.isEmpty()) {
						elementValidator.hide();
						element.setStyle(null);
					}
				});
			} else {
				if (typeValidate.equals("number") && !ValidateHandle.isNumeric(element.getText())) {
					elementValidator.getItems().clear();
					elementValidator.getItems().add(new MenuItem(messForType));
					elementValidator.show(element, Side.RIGHT, 10, 0);
					element.setStyle("-fx-border-color:red;-fx-max-height:25px;");
					checkValidate.add("false");
				} else {
					elementValidator.hide();
					element.setStyle(null);
					checkValidate.add("true");
				}
			}
		}
	}

	public static HashMap<String, ObservableList<Products>> getVariable() {
		return itemUnknowList;
	}

	public static void setVariable(HashMap<String, ObservableList<Products>> itemUnknowLis) {
		TabUnknowProductController.itemUnknowList = itemUnknowLis;
	}

	@SuppressWarnings("unchecked")
	public void builTable() {
		if (itemUnknowList.size() > 0) {
			TableColumn<Products, Number> indexColumn = new TableColumn<Products, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(30);
			indexColumn.setMaxWidth(30);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
					tableUnknowProduct.getItems().indexOf(column.getValue()) + 1));
			tableUnknowProduct.getColumns().add(0, indexColumn);
			tableUnknowProduct.getSelectionModel().selectedItemProperty().addListener(
					(ObservableValue<? extends Products> observable, Products oldValue, Products newValue) -> {
						if (newValue == null) {
							return;
						}
					});
			TableColumn<Products, String> barcodeUnKCol = new TableColumn<Products, String>("Mã");
			barcodeUnKCol.setCellValueFactory(new PropertyValueFactory<>("barcodeProduct"));
			barcodeUnKCol.setCellFactory(TextFieldTableCell.<Products>forTableColumn());
			barcodeUnKCol.setStyle("-fx-alignment: CENTER-LEFT;");
			barcodeUnKCol.setMinWidth(130);
			barcodeUnKCol.setMaxWidth(130);
			TableColumn<Products, String> nameProductCol = new TableColumn<Products, String>("Sản Phẩm");
			nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
			nameProductCol.setCellFactory(TextFieldTableCell.<Products>forTableColumn());
			nameProductCol.setStyle("-fx-alignment: CENTER-LEFT;");

			TableColumn<Products, String> priceSellCol = new TableColumn<Products, String>("Giá Bán");
			priceSellCol.setCellValueFactory(new PropertyValueFactory<>("priceSell"));
			priceSellCol.setMinWidth(70);
			priceSellCol.setMaxWidth(70);
			priceSellCol.setStyle("-fx-alignment: CENTER-RIGHT;");
			priceSellCol.setCellFactory(column -> new TableCell<Products, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						// setText(decimalFormat.format(item));
						setText(item);
					}
				}
			});
			tableUnknowProduct.getColumns().addAll(barcodeUnKCol, nameProductCol, priceSellCol);
			tableUnknowProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			for (Map.Entry<String, ObservableList<Products>> entry : itemUnknowList.entrySet()) {
				ObservableList<Products> value = entry.getValue();
				tableUnknowProduct.getItems().addAll(value);

			}
		}

	}

}
