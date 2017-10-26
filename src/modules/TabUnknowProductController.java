package modules;

import java.awt.List;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import application.BarcodeController;
import application.Global;
import application.ValidateHandle;
import database.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.control.Alert;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import models.Buy;
import models.UnknowProduct;

public class TabUnknowProductController implements Initializable {

	private static DbHandler handler;
	private static Connection connection;
	private GridPane gridProduct;
	private TextField nameProduct, unit, location, priceOrigin, priceSell;
	private ComboBox<String> comCatalogId;
	private TextArea description;
	private ObservableList<String> checkValidate = FXCollections.observableArrayList();
	private File outputFileP;
	// private TableView<Products> tableUnknowProduct;
	private static HashMap<String, String> itemUnknowList = new HashMap<String, String>();
	@FXML
	private TableView<UnknowProduct> tableUnknowProduct;
	private static String selectItem;

	public void initialize(URL url, ResourceBundle rb) {
		getData();
		builTable();
		tableUnknowProduct.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					String bar = tableUnknowProduct.getSelectionModel().getSelectedItem().getBarcodeUnknow();
					addProduct(bar);
				}
			}
		});
	}

	public static int getData() {
		handler = new DbHandler();
		try {
			connection = handler.getConnection();
			String query = "SELECT barcodeUnknow FROM UnknowProduct";
			ResultSet rs = connection.createStatement().executeQuery(query);
			while (rs.next()) {
				itemUnknowList.put(rs.getString("barcodeUnknow"), rs.getString("barcodeUnknow"));
			}
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
		}
		return itemUnknowList.size();
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
			imgBarcode.setImage(new Image(new File("barImg/" + barcodeDialog + ".png").toURI().toString()));
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
						PreparedStatement ps = connection
								.prepareStatement("DELETE FROM UnknowProduct WHERE barcodeUnknow = ?;");
						ps.setString(1, barcodeDialog);
						int sts = ps.executeUpdate();
						connection.commit();
						ps.close();
						if (sts == 1) {
							int d = tableUnknowProduct.getSelectionModel().getSelectedIndex();
							itemUnknowList.remove(tableUnknowProduct.getItems().get(d).getBarcodeUnknow());
							tableUnknowProduct.getItems().remove(d);
							if (itemUnknowList.size() == 0) {
								tableUnknowProduct.refresh();
								Global.val.setValue("0");
							}
						}
					}
					stmt.close();
					connection.close();
				} catch (Exception e) {
					Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
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

	public static void setVariable(String itemUnknowLis) {
		if (!itemUnknowLis.isEmpty()) {
			selectItem = itemUnknowLis;
			if (TabUnknowProductController.itemUnknowList.get(itemUnknowLis) == null) {

				TabUnknowProductController.itemUnknowList.put(itemUnknowLis, itemUnknowLis);
				try {
					connection = handler.getConnection();
					Statement stmt = connection.createStatement();
					String sql = "insert into UnknowProduct (barcodeUnknow) values ('" + itemUnknowLis + "')";
					stmt.executeUpdate(sql);
					connection.commit();
					stmt.close();
					connection.close();
					BarcodeController.renderBarcode(itemUnknowLis);
				} catch (Exception e) {
					Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		}
		if (itemUnknowList.size() > 0) {
			Global.val.setValue("5");
		}
	}

	@SuppressWarnings("unchecked")
	public void builTable() {
		TableColumn<UnknowProduct, Number> indexColumn = new TableColumn<UnknowProduct, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
				tableUnknowProduct.getItems().indexOf(column.getValue()) + 1));
		tableUnknowProduct.getColumns().add(0, indexColumn);
		tableUnknowProduct.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends UnknowProduct> observable, UnknowProduct oldValue,
						UnknowProduct newValue) -> {
					if (newValue == null) {
						return;
					}
				});
		indexColumn.setCellFactory(column -> new TableCell<UnknowProduct, Number>() {

			@Override
			public void updateItem(Number item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.toString());
					if (tableUnknowProduct.getItems().get(getIndex()).getBarcodeUnknow().equals(selectItem))
						setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black;-fx-alignment: CENTER;");
					else
						setStyle("-fx-alignment: CENTER;");
				}
			}
		});
		TableColumn<UnknowProduct, String> barcodeUnKCol = new TableColumn<UnknowProduct, String>("Mã");
		barcodeUnKCol.setCellValueFactory(new PropertyValueFactory<>("barcodeUnknow"));
		barcodeUnKCol.setCellFactory(TextFieldTableCell.<UnknowProduct>forTableColumn());
		barcodeUnKCol.setStyle("-fx-alignment: CENTER-LEFT;");
		barcodeUnKCol.setCellFactory(column -> new TableCell<UnknowProduct, String>() {

			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(item);

					if (item.equals(selectItem)) {
						setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black");
					} else
						setStyle(null);
				}
			}
		});
		TableColumn<UnknowProduct, UnknowProduct> deleteColumn = new TableColumn<>("Xóa");
		deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteColumn.setMinWidth(40);
		deleteColumn.setMaxWidth(40);
		deleteColumn.setCellFactory(param -> new TableCell<UnknowProduct, UnknowProduct>() {
			@Override
			protected void updateItem(UnknowProduct item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setGraphic(null);
					return;
				}
				if (tableUnknowProduct.getItems().get(getIndex()).getBarcodeUnknow().equals(selectItem)) {
					setStyle("-fx-background-color: #58C9FF; -fx-text-fill: black");
				} else
					setStyle(null);
				JFXButton btnDelete = new JFXButton("Xóa");
				btnDelete.setStyle("-fx-background-color: #F48024;-fx-text-fill:white;-fx-padding: -5px");
				btnDelete.setMaxHeight(21);
				btnDelete.setMinHeight(21);
				btnDelete.setMaxWidth(30);
				btnDelete.setMinWidth(30);
				setGraphic(btnDelete);
				btnDelete.setOnAction(event -> {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(Global.tsl_lblConfirmDialog);
					alert.setHeaderText(null);
					alert.setContentText("Xóa '" + item.getBarcodeUnknow() + "' ?");
					Optional<ButtonType> action = alert.showAndWait();
					if (action.get() == ButtonType.OK) {
						try {
							connection = handler.getConnection();
							PreparedStatement ps = connection
									.prepareStatement("DELETE FROM UnknowProduct WHERE barcodeUnknow = ?");
							ps.setString(1, item.getBarcodeUnknow());
							int sts = ps.executeUpdate();
							connection.commit();
							ps.close();
							if (sts == 1) {
								itemUnknowList.remove(item.getBarcodeUnknow().trim());
								tableUnknowProduct.getItems().remove(getIndex());
								if (itemUnknowList.size() == 0) {

									Global.val.setValue("0");
								}
								tableUnknowProduct.refresh();
								File file = new File("barImg/" + item.getBarcodeUnknow().trim() + ".png");
								file.delete();
							}
							connection.close();
						} catch (Exception e) {
							Logger.getLogger(TabUnknowProductController.class.getName()).log(Level.SEVERE, null, e);
						}
					}
				});
			}
		});
		tableUnknowProduct.getColumns().addAll(barcodeUnKCol, deleteColumn);
		tableUnknowProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		for (Map.Entry<String, String> entry : itemUnknowList.entrySet()) {
			tableUnknowProduct.getItems().addAll(new UnknowProduct(entry.getValue()));
		}
	}
}
