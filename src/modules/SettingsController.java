package modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import application.BarcodeController;
import application.Global;
import database.DbHandler;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.Backups;
import models.Cmd;
import models.Revenue;

public class SettingsController implements Initializable {
	private Stage _stage;
	@FXML
	private TableView<Revenue> tableRevenue;
	@FXML
	private TableView<Backups> tableFiles;

	@FXML
	private Tab tabBarcodeCommand, tabSettings, tabImportGoods;

	@FXML
	private TextField txtBarcode, txtBarcodeRefix;
	@FXML
	private JFXButton btnRandom, btnCreate, btnBrowser;
	@FXML
	private Pane paneImageBarcode;
	@FXML
	private ComboBox<String> comboboxActionList;
	@FXML
	private TextArea txtDescription;
	@FXML
	private TableView<Cmd> _tableCmd;
	@FXML
	private Label lblPath;
	private ObservableList<Cmd> lists = FXCollections.observableArrayList();
	private static DbHandler dbHandler;
	private static Connection connection;
	private ImageView imgBarcode = new ImageView();
	private boolean isEdit = false;
	private int idEdit;
//	private String dumPathBackup = "C:\\Program Files\\PostgreSQL\\9.6\\bin\\pg_dump.exe";
//	private String dumPathRestore = "C:\\Program Files\\PostgreSQL\\9.6\\bin\\pg_restore.exe";
//	public static String dumPathDrop = "C:\\Program Files\\PostgreSQL\\9.6\\bin\\dropdb.exe";
	public static String dumPathBackup = "/Library/PostgreSQL/9.6/bin/pg_dump";
	public static String dumPathRestore = "/Library/PostgreSQL/9.6/bin/pg_restore";
	public static String dumPathDrop = "/Library/PostgreSQL/9.6/bin/dropdb";

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
		buildPath();
	}

	private void buildPath() {
		try {
			connection = dbHandler.getConnection();
			String query = "SELECT * FROM Settings WHERE alias = 'backups'";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					lblPath.setText(rs.getString("values"));
					BuildTableBackups();
				}
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	public void setStage(Stage primaryStage) {
		this._stage = primaryStage;
	}

	private String convertValueToKey(String sf) {
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

	private String convertKeyToValue(String sf) {
		if (Global.pay.getKey().equals(sf)) {
			sf = Global.pay.getValue();
		}
		if (Global.printAndPay.getKey().equals(sf)) {
			sf = Global.printAndPay.getValue();
		}
		if (Global.clear.getKey().equals(sf)) {
			sf = Global.clear.getValue();
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
					comboboxActionList.setValue(convertKeyToValue(rs.getString("action")));
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
			// actionCol.setCellFactory(TextFieldTableCell.<Cmd>forTableColumn());
			actionCol.setMinWidth(150);
			actionCol.setMaxWidth(150);
			actionCol.setCellFactory(param -> new TableCell<Cmd, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					} else {
						setText(convertKeyToValue(item));
					}
				}
			});

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
			File fileNewBar = BarcodeController.renderBarcode(barcode.toLowerCase(), false);
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
			String cvAction = convertValueToKey(action);
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

	@FXML
	private void actionBrowser() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(_stage);

		if (selectedDirectory != null) {
			lblPath.setText(selectedDirectory.getAbsolutePath());
		}

	}

	public static void exportDb2(String path) throws IOException, InterruptedException {
		Process p;
		ProcessBuilder pb;

		Date date = new Date();
		String strDateFormat = "yyyy_MM_dd_hh_mm_ss";
		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
		String formattedDate = dateFormat.format(date);
		pb = new ProcessBuilder(dumPathBackup, "--host", "localhost", "--port", DbHandler.port, "--username",
				DbHandler.user, "--no-password", "--format", "custom", "--blobs", "--verbose", "--file",
				path + "/hoaphuong_" + formattedDate + ".tar", DbHandler.dbname);
		try {
			final Map<String, String> env = pb.environment();
			env.put("PGPASSWORD", DbHandler.password);
			p = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = r.readLine();
			while (line != null) {
				System.err.println(line);
				line = r.readLine();
			}
			r.close();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void restoreDb2(String path) throws IOException, InterruptedException, SQLException {
		try {
			connection = dbHandler.getConnection();
			Statement stmt = connection.createStatement();
			System.out.println("Deleting table in given database...");
			String sql = "DROP TABLE IF EXISTS products,barcodecmd,bills,catalogs,purchases,purchasesitems,quantityprice,sales,settings,unknowproduct,users,warehouse;";
			stmt.executeUpdate(sql);
			stmt.close();
			connection.commit();
			System.out.println("Table  deleted in given database...");

			connection.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
//		new Thread(() -> {
//			try {
//				doWork();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}).start();

		ProcessBuilder pb = new ProcessBuilder(dumPathRestore, "--host=" + DbHandler.host, "--port=" + DbHandler.port,
				"--username=" + DbHandler.user, "--dbname=" + DbHandler.dbname, "--role=postgres", "--no-password",
				"--verbose", path);
		pb.redirectErrorStream(true);
		final Map<String, String> env = pb.environment();
		env.put("PGPASSWORD", DbHandler.password);
		Process p = pb.start();
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String ll;
		while ((ll = br.readLine()) != null) {
			System.out.println(ll);
		}
	}

	private Statement stmt = null;

	@FXML
	private void actionBackup() throws SQLException, IOException, InterruptedException {
		if (!lblPath.getText().equals("Empty")) {

			connection = dbHandler.getConnection();
			stmt = connection.createStatement();
			String sql = "insert into Settings (alias,values) values ('backups','" + lblPath.getText().trim()
					+ "') ON CONFLICT (alias) DO UPDATE SET values= EXCLUDED.values";
			int sts = stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
			connection.close();
			if (sts == 1) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle(Global.tsl_lblConfirmDialog);
				alert.setHeaderText(null);
				alert.setContentText("Da ");
				alert.showAndWait();
				exportDb2(lblPath.getText().trim());
				BuildTableBackups();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void BuildTableBackups() {
		try {
			File file = new File(lblPath.getText());
			if (file.isDirectory()) {
				tableFiles.getItems().clear();
				tableFiles.getColumns().clear();
				File[] listOfFiles = file.listFiles((d, name) -> name.endsWith(".tar"));
				if (listOfFiles.length > 0) {
					ObservableList<Backups> Data = FXCollections.observableArrayList();
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							System.out.println("File " + listOfFiles[i].getName());

							Data.add(new Backups(i + 1, listOfFiles[i].getName().toString(),
									Long.toString(listOfFiles[i].length() / 1024) + " KB", ""));
						} else if (listOfFiles[i].isDirectory()) {
							System.out.println("Directory " + listOfFiles[i].getName());
						}
					}
					
					TableColumn<Backups, Number> indexColumn = new TableColumn<Backups, Number>("#");
					indexColumn.setSortable(false);
					indexColumn.setMinWidth(30);
					indexColumn.setMaxWidth(30);
					indexColumn.setStyle("-fx-alignment: CENTER;");
					indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
							tableFiles.getItems().indexOf(column.getValue()) + 1));
					tableFiles.getColumns().add(0, indexColumn);
					tableFiles.getSelectionModel().selectedItemProperty().addListener(
							(ObservableValue<? extends Backups> observable, Backups oldValue, Backups newValue) -> {
								if (newValue == null) {
									return;
								}
							});
					indexColumn.setSortable(false);
					TableColumn<Backups, String> fileNameCol = new TableColumn<Backups, String>("File Name");
					fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
					fileNameCol.setCellFactory(TextFieldTableCell.<Backups>forTableColumn());
					fileNameCol.setSortType(TableColumn.SortType.DESCENDING);
					TableColumn<Backups, String> sizeCol = new TableColumn<Backups, String>("Size");
					sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
					sizeCol.setCellFactory(TextFieldTableCell.<Backups>forTableColumn());
					sizeCol.setMinWidth(50);
					sizeCol.setMaxWidth(50);
					sizeCol.setSortable(false);

					TableColumn<Backups, Backups> actionCol = new TableColumn<>("Restore");
					actionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
					actionCol.setCellFactory(param -> new TableCell<Backups, Backups>() {
						@Override
						protected void updateItem(Backups item, boolean empty) {
							super.updateItem(item, empty);
							if (item == null) {
								setGraphic(null);
								return;
							} else {
								JFXButton btnRestore = new JFXButton("Restore");
								btnRestore.setStyle(
										"-fx-background-color: #0073B7;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
								setGraphic(btnRestore);
								btnRestore.setOnAction(event -> {
									try {
										System.out.println(tableFiles.getItems().get(getIndex()).getFileName());
										restoreDb2(lblPath.getText() + "/"
												+ tableFiles.getItems().get(getIndex()).getFileName());
									} catch (Exception e) {
										Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
									}
								});
							}
						}
					});
					actionCol.setMinWidth(80);
					actionCol.setMaxWidth(80);
					actionCol.setSortable(false);
					TableColumn<Backups, Backups> deleteCol = new TableColumn<>("Delete");
					deleteCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
					deleteCol.setCellFactory(param -> new TableCell<Backups, Backups>() {
						@Override
						protected void updateItem(Backups item, boolean empty) {
							super.updateItem(item, empty);
							if (item == null) {
								setGraphic(null);
								return;
							} else {
								JFXButton btnDelete = new JFXButton("Delete");
								btnDelete.setStyle(
										"-fx-background-color: red;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
								setGraphic(btnDelete);
								btnDelete.setOnAction(event -> {
									try {
										Alert alert = new Alert(AlertType.CONFIRMATION);
										alert.setTitle(Global.tsl_lblConfirmDialog);
										alert.setHeaderText(null);
										alert.setContentText("Bạn muốn xóa file: ['"
												+ tableFiles.getItems().get(getIndex()).getFileName() + "'] ?");
										Optional<ButtonType> action = alert.showAndWait();
										if (action.get() == ButtonType.OK) {
											File f = null;
											boolean bool = false;
											try {
												// create new file
												f = new File(lblPath.getText() + "/"
														+ tableFiles.getItems().get(getIndex()).getFileName());

												// tries to delete a non-existing file
												bool = f.delete();

												// prints
												System.out.println("File deleted: " + bool);
												if (bool)
													BuildTableBackups();
											} catch (Exception e) {
												// if any error occurs
												e.printStackTrace();
											}

										}
									} catch (Exception e) {
										Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
									}
								});
							}
						}
					});
					deleteCol.setMinWidth(80);
					deleteCol.setMaxWidth(80);
					deleteCol.setSortable(false);
					tableFiles.setEditable(false);
					tableFiles.getColumns().addAll(fileNameCol, sizeCol, actionCol, deleteCol);
					tableFiles.setItems(Data);
				}

			} else {
				System.out.println("This is not a directory");
			}
		} catch (Exception e) {
			Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, e);
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