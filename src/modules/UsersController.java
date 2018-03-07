package modules;

import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import models.Users;

public class UsersController implements Initializable {
	@FXML
	private TableView<Users> _table;

	private ObservableList<Users> lists = FXCollections.observableArrayList();
	@FXML
	private TextField txtUsername, txtFullname;
	@FXML
	private PasswordField txtPassword, txtRePassword;
	@FXML
	private ComboBox<String> comboboxType;
	@FXML
	private JFXButton btnSave;

	private DbHandler dbHandler;
	private Connection connection;
//	private boolean saveStatus = true;
	private int userId = 0;

	public void initialize(URL url, ResourceBundle rb) {
		dbHandler = new DbHandler();
		connection = dbHandler.getConnection();
		comboboxType.getItems().addAll(Global.typeAdmin.getValue(), Global.typeUser.getValue());
		final String PATTERN = "^[a-z0-9_-]{" + Global.UserMinLength + "," + Global.UserMaxLength + "}$";
		Pattern pattern = Pattern.compile(PATTERN);
		buildTable();
		txtUsername.focusedProperty().addListener((ov, oldValue, newValue) -> {
			Matcher matcher = pattern.matcher(txtUsername.getText());
			if (!txtUsername.getText().isEmpty() && !newValue && !matcher.matches()) {
				Global.showDialogAlert(AlertType.WARNING, Global.tsl_lblConfirmDialog, null,
						"Tên đăng nhập không sử dụng ký tự lạ. Độ dài ít nhất là " + Global.UserMinLength
								+ ", dài nhất là " + Global.UserMaxLength + " ký tự. Ví dụ: abc123");
//				saveStatus = false;
			}
		});
		txtPassword.focusedProperty().addListener((ov, oldValue, newValue) -> {
			Matcher matcher = pattern.matcher(txtPassword.getText());
			if (!txtPassword.getText().isEmpty() && !newValue && !matcher.matches()) {
				Global.showDialogAlert(AlertType.WARNING, Global.tsl_lblConfirmDialog, null,
						"Mật khẩu không sử dụng ký tự lạ. Độ dài ít nhất là " + Global.UserMinLength + ", dài nhất là "
								+ Global.UserMaxLength + " ký tự.");
//				saveStatus = false;
			}
		});
		txtRePassword.focusedProperty().addListener((ov, oldValue, newValue) -> {
			if (!txtRePassword.getText().isEmpty() && !newValue) {
				if (!txtRePassword.getText().trim().equals(txtPassword.getText().trim())) {
					Global.showDialogAlert(AlertType.WARNING, Global.tsl_lblConfirmDialog, null,
							"Mật khẩu không trùng nhau.");
					txtRePassword.clear();
				}
//				saveStatus = false;
			}
		});
		btnSave.setOnAction(event -> {
			if (userId == 0) {
				if (!txtUsername.getText().isEmpty() && !txtPassword.getText().isEmpty()
						&& comboboxType.getSelectionModel().getSelectedItem() != null) {
					String username = txtUsername.getText();
					String password = txtPassword.getText().trim();
					String fullname = txtFullname.getText();
					int type = (comboboxType.getValue() == Global.typeAdmin.getValue()) ? Global.typeAdmin.getKey()
							: Global.typeUser.getKey();
					try {
						Statement stmt = connection.createStatement();
						String generatedPassword = "";
						MessageDigest md = MessageDigest.getInstance("MD5");
						md.update(password.getBytes());
						byte[] bytes = md.digest();
						StringBuilder sb = new StringBuilder();
						for (int j = 0; j < bytes.length; j++) {
							sb.append(Integer.toString((bytes[j] & 0xff) + 0x100, 16).substring(1));
						}
						generatedPassword = sb.toString();
						String barcodeUser = String.valueOf(Instant.now().getEpochSecond());
						String sql = "INSERT INTO users (barcodeUser,username,password,fullname,type)" + "VALUES ('"
								+ barcodeUser + "','" + username + "','" + generatedPassword + "','" + fullname + "', '"
								+ type + "');";
						boolean sts = stmt.execute(sql);
						if (sts) {
							BarcodeController.renderBarcode(barcodeUser,true);
						}
						stmt.close();
						connection.commit();
						connection.close();
						txtUsername.clear();
						txtPassword.clear();
						txtRePassword.clear();
						txtFullname.clear();
						comboboxType.setPromptText("Loại tài khoản");
						buildTable();
					} catch (Exception e) {
						Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, e);
					}
				} else {
					String errorMessegae = DisplayError(txtUsername, txtFullname, txtPassword, txtRePassword,
							comboboxType);
					Global.showDialogAlert(AlertType.WARNING, Global.tsl_lblConfirmDialog, null, errorMessegae);
				}
			} else {
				try {
					Statement stmt = connection.createStatement();
					String sql3;
					if (!txtPassword.getText().isEmpty() && !txtRePassword.getText().isEmpty()) {
						String generatedPassword = "";
						MessageDigest md = MessageDigest.getInstance("MD5");
						md.update(txtPassword.getText().trim().getBytes());
						byte[] bytes = md.digest();
						StringBuilder sb = new StringBuilder();
						for (int j = 0; j < bytes.length; j++) {
							sb.append(Integer.toString((bytes[j] & 0xff) + 0x100, 16).substring(1));
						}
						generatedPassword = sb.toString();
						sql3 = "UPDATE USERS SET PASSWORD = '" + generatedPassword + "', FULLNAME = '"
								+ txtFullname.getText() + "', TYPE= '"
								+ (comboboxType.getValue().equals(Global.typeAdmin.getValue())
										? Global.typeAdmin.getKey()
										: Global.typeUser.getKey())
								+ "' WHERE ID = '" + userId + "' ";
					} else {
						sql3 = "UPDATE USERS SET FULLNAME = '" + txtFullname.getText() + "', TYPE= "
								+ (comboboxType.getValue().equals(Global.typeAdmin.getValue())
										? Global.typeAdmin.getKey()
										: Global.typeUser.getKey())
								+ " WHERE ID = '" + userId + "' ";
					}
					int sta = stmt.executeUpdate(sql3);
					if (sta == 1) {
						txtUsername.setDisable(false);
						txtUsername.clear();
						txtPassword.clear();
						txtRePassword.clear();
						txtFullname.clear();
						userId = 0;
						buildTable();
					}
					connection.commit();
					// connection.close();
				} catch (Exception e) {
					Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		});
	}

	public String DisplayError(TextField txtUser, TextField txtFullname, PasswordField txtPass, PasswordField txtRePass,
			ComboBox<String> comboboxType2) {
		String error = "";
		if (txtUser.getText().isEmpty()) {
			error += "Tên đăng nhập. ";
		}
		if (txtPass.getText().isEmpty()) {
			error += "Mật khẩu. ";
		} else {
			if (txtRePass.getText().isEmpty()) {
				error += "Nhập lại mật khẩu. ";
			}
		}
		if (txtFullname.getText().isEmpty()) {
			error += "Họ tên. ";
		}
		if (comboboxType2.getSelectionModel().getSelectedItem() == null) {
			error += "Loại tài khoản. ";
		}
		error += "không được để trống.";
		return error;
	}

	@SuppressWarnings("unchecked")
	public void buildTable() {
		try {
			_table.getItems().clear();
			_table.getColumns().clear();
			lists.clear();
			connection = dbHandler.getConnection();
			String sqlUsers = "select * FROM USERS";
			PreparedStatement pst = connection.prepareStatement(sqlUsers);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				lists.add(new Users(rs.getInt("id"), rs.getString("username"), rs.getString("fullname"),
						rs.getInt("type"), rs.getTimestamp("createdAt")));
			}
			TableColumn<Users, Number> indexColumn = new TableColumn<Users, Number>("#");
			indexColumn.getStyleClass().add("my-special-column-style");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(50);
			indexColumn.setMaxWidth(50);
			indexColumn.setCellValueFactory(
					column -> new ReadOnlyObjectWrapper<Number>(_table.getItems().indexOf(column.getValue()) + 1));
			_table.getColumns().add(0, indexColumn);
			_table.getSelectionModel().selectedItemProperty()
					.addListener((ObservableValue<? extends Users> observable, Users oldValue, Users newValue) -> {
						if (newValue == null) {
							return;
						}
					});
			TableColumn<Users, String> nameCol = new TableColumn<Users, String>("Username");
			nameCol.getStyleClass().add("my-special-column-style");
			nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
			nameCol.setCellFactory(TextFieldTableCell.<Users>forTableColumn());

			TableColumn<Users, String> fullnameCol = new TableColumn<Users, String>("Họ tên");
			fullnameCol.getStyleClass().add("my-special-column-style");
			fullnameCol.setCellValueFactory(new PropertyValueFactory<>("fullname"));
			fullnameCol.setCellFactory(TextFieldTableCell.<Users>forTableColumn());
			TableColumn<Users, Integer> typeCol = new TableColumn<Users, Integer>("Quyền");
			typeCol.getStyleClass().add("my-special-column-style");
			typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
			typeCol.setCellFactory(column -> {
				return new TableCell<Users, Integer>() {
					@Override
					protected void updateItem(Integer item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							if (item == Global.typeAdmin.getKey())
								setText(Global.typeAdmin.getValue());
							else
								setText(Global.typeUser.getValue());
						}
					}
				};
			});
			TableColumn<Users, Users> editColumn = new TableColumn<>("Sửa");
			editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			editColumn.getStyleClass().add("my-special-column-style");
			editColumn.setMaxWidth(50);
			editColumn.setMinWidth(50);
			editColumn.setCellFactory(param -> new TableCell<Users, Users>() {

				@Override
				protected void updateItem(Users item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					}
					JFXButton btnEdit = new JFXButton("Sửa");
					btnEdit.setStyle(
							"-fx-background-color: #333;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
					setGraphic(btnEdit);
					btnEdit.setOnAction(event -> {
						txtUsername.setText(_table.getItems().get(getIndex()).getUsername());
						txtUsername.setDisable(true);
						txtFullname.setText(_table.getItems().get(getIndex()).getFullname());
						txtPassword.clear();
						txtRePassword.clear();
						comboboxType.setValue((_table.getItems().get(getIndex()).getType() == Global.typeAdmin.getKey())
								? Global.typeAdmin.getValue()
								: Global.typeUser.getValue());
						userId = _table.getItems().get(getIndex()).getId();
					});
				}
			});
			TableColumn<Users, Users> deleteColumn = new TableColumn<>("Xóa");
			deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			deleteColumn.setCellFactory(param -> new TableCell<Users, Users>() {
				@Override
				protected void updateItem(Users item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					}
					JFXButton btnDelete = new JFXButton("Xóa");
					btnDelete.setStyle(
							"-fx-background-color: #0073B7;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
					setGraphic(btnDelete);
					btnDelete.setOnAction(event -> {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Xác nhận xóa tài khoản");
						alert.setHeaderText(null);
						alert.setContentText("Bạn có muốn xóa tài khoản này không?");
						Optional<ButtonType> action = alert.showAndWait();
						if (action.get() == ButtonType.OK) {
							PreparedStatement ps;
							try {
								ps = connection.prepareStatement("DELETE FROM USERS WHERE ID = ?");
								ps.setInt(1, item.getId());
								int stt = ps.executeUpdate();
								connection.commit();
								System.out.println("Records Delete Successfully....");
								connection.close();
								if (stt == 1) {
									getTableView().getItems().remove(item);
									_table.refresh();
								} else {
									Global.showDialogAlert(AlertType.WARNING, Global.tsl_lblConfirmDialog, null,
											"Có lỗi xảy ra. Vui lòng thử lại.");
								}
							} catch (SQLException e) {
								Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, e);
							}
						}
					});
				}
			});
			deleteColumn.getStyleClass().add("my-special-column-style");
			deleteColumn.setMaxWidth(50);
			deleteColumn.setMinWidth(50);
			_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			_table.getColumns().addAll(nameCol, fullnameCol, typeCol, editColumn, deleteColumn);
			_table.getItems().addAll(lists);
			rs.close();
		} catch (Exception e) {
			Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, e);
		}

	}
}