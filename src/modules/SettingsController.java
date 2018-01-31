package modules;

import java.awt.List;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.scene.control.GlobalMenuAdapter;

import application.BarcodeController;
import application.Global;
import database.DbHandler;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import models.Cmd;
import models.Revenue;

public class SettingsController implements Initializable {
	@FXML
	private TableView<Revenue> tableRevenue;

	@FXML
	private Tab tabBarcodeCommand, tabWarehouse, tabImportGoods;

	@FXML
	private TextField txtBarcode, txtBarcodeRefix;
	@FXML
	private JFXButton btnRandom, btnCreate;
	@FXML
	private Pane paneImageBarcode;
	@FXML
	private ComboBox<String> comboboxActionList;
	@FXML
	private TextArea txtDescription;
	@FXML
	private TableView<Cmd> _tableCmd;
	private ObservableList<Cmd> lists = FXCollections.observableArrayList();
	private DbHandler dbHandler;
	private static Connection connection;
	private ImageView imgBarcode = new ImageView();
	private boolean isEdit = false;
	private int idEdit;

	public void initialize(URL url, ResourceBundle rb) {

		dbHandler = new DbHandler();
		buildTable();
		comboboxActionList.getItems().add(Global.pay.getValue());
		comboboxActionList.getItems().add(Global.printAndPay.getValue());
		comboboxActionList.getItems().add(Global.clear.getValue());
		imgBarcode.setFitHeight(60);
		imgBarcode.setStyle("-fx-border-color:#333");
		paneImageBarcode.getChildren().clear();
		paneImageBarcode.getChildren().add(imgBarcode);
		_tableCmd.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				System.out.println(newSelection.getId());
				edit(newSelection.getId());
			}
		});
	}

	private String convertAction(String sf) {
		if (Global.pay.getValue().equals(sf)) {
			sf = Global.pay.getKey();
		}
		if (Global.printAndPay.getValue().equals(sf)) {
			sf = Global.printAndPay.getKey();
		}
		if (Global.clear.getValue().equals(sf)) {
			sf = Global.clear.getKey();
		}
		return sf;
	}

	private void edit(int id) {
		try {
			connection = dbHandler.getConnection();
			String query = "SELECT * FROM BarcodeCmd WHERE id = '" + id + "' ORDER BY id asc";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					lists.add(new Cmd(rs.getInt("id"), rs.getString("BarcodeCmd"), rs.getString("action"),
							rs.getString("descriptionCmd")));
					String[] barSp = rs.getString("BarcodeCmd").split("-");
					txtBarcode.setText(barSp[1]);
					comboboxActionList.setValue(rs.getString("action"));
					txtDescription.setText(rs.getString("descriptionCmd"));
					isEdit = true;
					idEdit = rs.getInt("id");
					File fileO = new File("barImg" + "/" + rs.getString("BarcodeCmd") + ".png");
					if (fileO.exists())
						imgBarcode.setImage(new Image(fileO.toURI().toString()));
				}
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void buildTable() {
		try {
			_tableCmd.getItems().clear();
			_tableCmd.getColumns().clear();
			lists.clear();
			connection = dbHandler.getConnection();
			String query = "SELECT * FROM BarcodeCmd ORDER BY id asc";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					lists.add(new Cmd(rs.getInt("id"), rs.getString("BarcodeCmd"), rs.getString("action"),
							rs.getString("descriptionCmd")));
				}
			}
			rs.close();
			connection.close();
			TableColumn<Cmd, Number> indexColumn = new TableColumn<Cmd, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(40);
			indexColumn.setMaxWidth(40);
			indexColumn.getStyleClass().add("my-special-column-style");
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(
					column -> new ReadOnlyObjectWrapper<Number>(_tableCmd.getItems().indexOf(column.getValue()) + 1));
			_tableCmd.getColumns().add(0, indexColumn);
			TableColumn<Cmd, String> barcodeCol = new TableColumn<Cmd, String>("BarcodeCMD");
			barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));
			barcodeCol.getStyleClass().add("my-special-column-style");
			barcodeCol.setStyle("-fx-alignment: CENTER;");
			barcodeCol.setCellFactory(TextFieldTableCell.<Cmd>forTableColumn());
			barcodeCol.setMinWidth(150);
			barcodeCol.setMaxWidth(150);

			TableColumn<Cmd, String> actionCol = new TableColumn<Cmd, String>("Action");
			actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
			actionCol.getStyleClass().add("my-special-column-style");
			actionCol.setStyle("-fx-alignment: CENTER;");
			actionCol.setCellFactory(TextFieldTableCell.<Cmd>forTableColumn());
			actionCol.setMinWidth(150);
			actionCol.setMaxWidth(150);

			TableColumn<Cmd, String> descCol = new TableColumn<Cmd, String>("Mô Tả");
			descCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
			descCol.getStyleClass().add("my-special-column-style");
			descCol.setCellFactory(TextFieldTableCell.<Cmd>forTableColumn());

			TableColumn<Cmd, Cmd> deleteColumn = new TableColumn<>("Xóa");
			deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			deleteColumn.setMinWidth(50);
			deleteColumn.setMaxWidth(50);
			deleteColumn.getStyleClass().add("my-special-column-style");
			deleteColumn.setStyle("-fx-alignment: CENTER;");
			deleteColumn.setCellFactory(param -> new TableCell<Cmd, Cmd>() {
				@Override
				protected void updateItem(Cmd item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					} else {
						JFXButton btnDelete = new JFXButton("Xóa");
						btnDelete.setStyle(
								"-fx-background-color: #0073B7;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
						setGraphic(btnDelete);
						btnDelete.setOnAction(event -> {
							try {
								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle(Global.tsl_lblConfirmDialog);
								alert.setHeaderText(null);
								alert.setContentText("Bạn có muốn xóa '" + item.getAction() + "' không?");
								Optional<ButtonType> action = alert.showAndWait();
								if (action.get() == ButtonType.OK) {
									connection = dbHandler.getConnection();
									PreparedStatement ps = connection
											.prepareStatement("DELETE FROM BarcodeCmd WHERE id = ?;");
									ps.setInt(1, item.getId());
									int sts = ps.executeUpdate();
									connection.commit();
									ps.close();
									if (sts == 1) {
										buildTable();
									}
								}

							} catch (Exception e) {
								Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
							}
						});
					}
				}
			});

			_tableCmd.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			_tableCmd.getColumns().addAll(barcodeCol, actionCol, descCol, deleteColumn);
			_tableCmd.getItems().addAll(lists);
		} catch (Exception e) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@FXML
	private void actionRandom() {
		txtBarcode.setText(String.valueOf(Instant.now().getEpochSecond()));
		renderBarcodeImage(txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim());
	}

	@FXML
	private void actionCreate() {
		if (!txtBarcode.getText().trim().isEmpty()) {
			renderBarcodeImage(txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim());
		}
	}

	private void renderBarcodeImage(String barcode) {
		try {
			File fileNewBar = BarcodeController.renderBarcode(barcode.toLowerCase());
			imgBarcode.setImage(new Image(fileNewBar.toURI().toString()));
		} catch (Exception ie) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ie);
		}
	}

	@FXML
	private void actionSave() {
		if (!txtBarcode.getText().isEmpty()) {
			String barcode = txtBarcodeRefix.getText() + "-" + txtBarcode.getText().trim();
			String action = comboboxActionList.getSelectionModel().getSelectedItem();
			String desc = txtDescription.getText();
			String cvAction = convertAction(action);
			ArrayList<String> asf = new ArrayList<String>();
			connection = dbHandler.getConnection();
			try {
				String query = "SELECT action FROM BarcodeCmd";
				ResultSet rs = connection.createStatement().executeQuery(query);
				connection.commit();
				if (rs.isBeforeFirst()) {
					while (rs.next()) {
						asf.add(rs.getString("action"));
					}
				}
				if (isEdit) {
					addData(barcode.toLowerCase(), cvAction, desc);
				} else {
					if (!asf.contains(cvAction))
						addData(barcode.toLowerCase(), cvAction, desc);
					else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle(Global.tsl_lblConfirmDialog);
						alert.setHeaderText(null);
						alert.setContentText("Lệnh '" + action + "' này đã tồn tại");
						alert.showAndWait();
					}
				}
				rs.close();
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	private void addData(String bar, String action, String desc) {
		try {
			connection = dbHandler.getConnection();
			Statement stmt2 = connection.createStatement();
			stmt2 = connection.createStatement();
			String queryAdd;
			if (isEdit) {
				queryAdd = "UPDATE BarcodeCmd SET BarcodeCmd = '" + bar + "',action = '" + action
						+ "' ,descriptionCmd = '" + desc + "' WHERE id = '" + idEdit + "'";
			} else {
				queryAdd = "INSERT INTO BarcodeCmd (BarcodeCmd, action, descriptionCmd) VALUES ('" + bar + "', '"
						+ action + "', '" + desc + "');";
			}
			stmt2.executeUpdate(queryAdd);
			connection.commit();
			buildTable();
			clearInput();
		} catch (Exception e) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void clearInput() {
		txtBarcode.clear();
		comboboxActionList.getSelectionModel().select(-1);
		txtDescription.clear();
		imgBarcode.setImage(null);
	}
}