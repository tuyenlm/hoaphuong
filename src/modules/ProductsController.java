package modules;

import java.awt.Desktop;
import java.awt.List;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;

import application.BarcodeController;
import application.Global;
import application.RenderBarcodeThread;
import application.ValidateHandle;
import database.DbHandler;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import models.Catalogs;
import models.Print;
import models.Products;

public class ProductsController implements Initializable {
	@FXML
	private TableView<Catalogs> tableCatalog;
	@FXML
	private TableView<Products> tableProducts;
	@FXML
	private TableView<Print> tablePrint;
	@FXML
	private JFXButton btnAddCatalog, btnAddProduct, btnEditProduct, btnPrintAll, btnOpenFolder, btnSaveQ;
	@FXML
	private ListView<String> lvLabel;
	@FXML
	private ListView<Text> lvDetail;
	@FXML
	private Label labelBarCode;
	@FXML
	private TextField txtSearchCatalog, txtSearchProduct, txtQuatityQ, txtPriceQ, txtSearchBarcodeP;
	@FXML
	private JFXCheckBox isCheckPrint, isEnable;
	@FXML
	private JFXRadioButton radio30, radio57;

	private static DbHandler dbHandler;
	private static Connection connection;
	private ObservableList<Catalogs> catalogList = FXCollections.observableArrayList();
	private ObservableList<Products> productList = FXCollections.observableArrayList();
	private Statement stmt = null;
	private HashMap<String, Integer> hmCatalog = new HashMap<String, Integer>();
	private TextField nameProduct, unit, location, priceOrigin, priceSell, nameCatalog;
	private TextArea description, descriptionCatalog;
	private ComboBox<String> comCatalogId;
	private Dialog<List> dialogProduct;
	private Dialog<Pair<String, String>> dialogCatalog;
	private ObservableList<String> checkValidate = FXCollections.observableArrayList();
	private HashMap<String, ObservableList<Print>> printList = new HashMap<String, ObservableList<Print>>();
	private ObservableList<Print> gg = FXCollections.observableArrayList();
	private GridPane gridProduct, gridCatalog;
	private File outputFileP;
	private String textTmp = "";
	private HSSFWorkbook wb;
	private int _productId;
	private int _QId = 0;

	public void initialize(URL url, ResourceBundle rb) {
		try {
			dbHandler = new DbHandler();
			BuilderCatalog(txtSearchCatalog.getText());
			if (tableCatalog.getItems().size() > 0 && tableCatalog.getItems().get(0).getId() != 0) {
				BuilderProduct(tableCatalog.getItems().get(0).getId(), txtSearchProduct.getText(),
						txtSearchBarcodeP.getText());
				tableCatalog.getSelectionModel().selectFirst();
			}
			if (tableProducts.getItems().size() > 0 && tableProducts.getItems().get(0).getId() != 0) {
				tableProducts.getSelectionModel().selectFirst();
				loadProduct(tableProducts.getItems().get(0).getId());
				_productId = tableProducts.getItems().get(0).getId();
				connection = dbHandler.getConnection();
				String query = "SELECT * FROM QuantityPrice WHERE productId = '" + _productId + "'";
				ResultSet rs = connection.createStatement().executeQuery(query);
				if (rs.isBeforeFirst())
					while (rs.next()) {
						_QId = rs.getInt("id");
						isEnable.setSelected(rs.getBoolean("enable"));
						txtPriceQ.setText(String.valueOf(rs.getInt("sellCost")));
						txtQuatityQ.setText(String.valueOf(rs.getInt("quantity")));
					}
				else
					isEnable.setSelected(false);
				rs.close();
				connection.close();

			}
			tableCatalog.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					BuilderProduct(observable.getValue().getId(), txtSearchProduct.getText(),
							txtSearchBarcodeP.getText());
				}
			});
			tableProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					loadProduct(observable.getValue().getId());
					_productId = observable.getValue().getId();
					System.out.println("_productId" + _productId);
					String query = "SELECT * FROM QuantityPrice WHERE productId = '" + _productId + "'";
					ResultSet rs;
					try {
						connection = dbHandler.getConnection();
						rs = connection.createStatement().executeQuery(query);
						if (rs.isBeforeFirst())
							while (rs.next()) {
								_QId = rs.getInt("id");
								isEnable.setSelected(rs.getBoolean("enable"));
								txtPriceQ.setText(String.valueOf(rs.getInt("sellCost")));
								txtQuatityQ.setText(String.valueOf(rs.getInt("quantity")));
							}
						else {
							_QId = 0;
							isEnable.setSelected(false);
						}

						rs.close();
						connection.close();
						System.out.println(isEnable.isSelected());
						if (isEnable.isSelected()) {
							System.out.println("szo 1");
							txtPriceQ.setDisable(false);
							txtQuatityQ.setDisable(false);
						} else {
							System.out.println(" szo2");

							txtPriceQ.setText("");
							txtQuatityQ.setText("");

						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			ObservableList<String> itemLabel = FXCollections.observableArrayList("Mã Barcode", "Tên sản phẩm",
					"Giá gốc", "Giá bán", "Đơn vị", "Vị trí", "Danh mục", "Ngày tạo", "Mô tả");
			lvLabel.getItems().addAll(itemLabel);
			txtSearchCatalog.textProperty().addListener((a, b, c) -> {
				BuilderCatalog(txtSearchCatalog.getText());
			});

			txtSearchProduct.textProperty().addListener((a, b, c) -> {
				txtSearchBarcodeP.setText("");
				System.out.println(
						tableCatalog.getItems().get(tableCatalog.getSelectionModel().getSelectedIndex()).getId());

				if (txtSearchProduct.getText().isEmpty() && tableCatalog.getItems()
						.get(tableCatalog.getSelectionModel().getSelectedIndex()).getId() != 0) {
					BuilderProduct(
							tableCatalog.getItems().get(tableCatalog.getSelectionModel().getSelectedIndex()).getId(),
							txtSearchProduct.getText(), "");
				} else {
					BuilderProduct(0, txtSearchProduct.getText(), "");
				}
			});

			txtSearchBarcodeP.setOnKeyReleased(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent ke) {
					if (ke.getCode().toString().equals("ENTER") && !txtSearchBarcodeP.getText().trim().isEmpty()) {
						BuilderProduct(tableCatalog.getItems().get(tableCatalog.getSelectionModel().getSelectedIndex())
								.getId(), "", txtSearchBarcodeP.getText());
						txtSearchBarcodeP.requestFocus();
						txtSearchBarcodeP.selectAll();
					}

				}

			});
//			txtSearchBarcodeP.textProperty().addListener((a, b, c) -> {
//				if (txtSearchBarcodeP.getText().isEmpty() && tableCatalog.getItems()
//						.get(tableCatalog.getSelectionModel().getSelectedIndex()).getId() != 0) {
//					BuilderProduct(
//							tableCatalog.getItems().get(tableCatalog.getSelectionModel().getSelectedIndex()).getId(),
//							"", "");
//				}
//			});

			isEnable.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
				if (isSelected) {
					connection = dbHandler.getConnection();
					String query = "SELECT * FROM QuantityPrice WHERE productId = '" + _productId + "'";
					ResultSet rs;
					try {
						rs = connection.createStatement().executeQuery(query);
						if (rs.isBeforeFirst())
							while (rs.next()) {
								txtPriceQ.setText(String.valueOf(rs.getInt("sellCost")));
								txtQuatityQ.setText(String.valueOf(rs.getInt("quantity")));
							}
						rs.close();
						connection.close();
					} catch (SQLException e) {
						Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
						e.printStackTrace();
					}
					txtPriceQ.setDisable(false);
					txtQuatityQ.setDisable(false);
				} else {
					txtPriceQ.setDisable(true);
					txtQuatityQ.setDisable(true);
					txtPriceQ.setText("");
					txtQuatityQ.setText("");
				}
			});

			txtQuatityQ.textProperty().addListener((a, b, c) -> {
				System.out.println("txtQuatityQ b|" + b + "|");
				if (ValidateHandle.isNumericInteger(c) && Integer.parseInt(c) > 0) {
					txtQuatityQ.setText(c);
				} else {
					txtQuatityQ.setText("");

				}

			});

			txtPriceQ.textProperty().addListener((a, b, c) -> {
				System.out.println("txtPriceQ b|" + b + "|");
				if (ValidateHandle.isNumericInteger(c) && Integer.parseInt(c) > 0) {
					txtPriceQ.setText(c);
				} else {
					txtPriceQ.setText("");

				}
			});
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void loadProduct(int id) {
		if (id != 0) {
			try {
				lvDetail.getItems().clear();
				connection = dbHandler.getConnection();
				PreparedStatement ps = connection.prepareStatement(
						"SELECT *,to_char(priceOrigin, '999,999,990') as priceOriginDe,to_char(priceSell, '999,999,990') as priceSellDe,to_char(products.createdAtP, 'DD-MM-YYYY HH12:MI') as dateTime FROM products LEFT OUTER JOIN catalogs ON (products.catalogid = catalogs.id) WHERE products.id=?");
				ps.setInt(1, id);
				ResultSet rsP = ps.executeQuery();
				connection.commit();
				if (rsP != null) {
					while (rsP.next()) {
						// ImageView as = new ImageView(new Image(
						// new File("barImg/" + rsP.getString("barcodeProduct") +
						// ".png").toURI().toString()));
						// as.setFitHeight(60);
						// labelBarCode.setGraphic(as);
						// Text text = new Text(rsP.getString("description"));
						// text.setStyle("-fx-text-fill: red");
						// text.wrappingWidthProperty().bind(lvDetail.widthProperty());
						lvDetail.getItems().add(0, new Text(rsP.getString("barcodeProduct")));
						lvDetail.getItems().add(1, new Text(rsP.getString("nameProduct")));
						lvDetail.getItems().add(2, new Text(rsP.getString("priceOriginDe").replace(" ", "")));
						lvDetail.getItems().add(3, new Text(rsP.getString("priceSellDe").replace(" ", "")));
						lvDetail.getItems().add(4, new Text(rsP.getString("unit")));
						lvDetail.getItems().add(5, new Text(rsP.getString("location")));
						lvDetail.getItems().add(6, new Text(rsP.getString("nameCatalog")));
						lvDetail.getItems().add(7, new Text(rsP.getString("dateTime")));
						lvDetail.getItems().add(8, new Text(rsP.getString("descriptionProduct")));

					}
				}
				rsP.close();
				ps.close();
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
			}
		} else {
			lvDetail.getItems().clear();
			labelBarCode.setGraphic(null);
		}
	}

	@SuppressWarnings("unchecked")
	public void BuilderCatalog(String nameSearch) {
		try {
			tableCatalog.getItems().clear();
			tableCatalog.getColumns().clear();
			hmCatalog.clear();
			catalogList.clear();
			// comSearchCatalog.getItems().clear();
			connection = dbHandler.getConnection();
			String query;
			if (nameSearch.isEmpty()) {
				query = "SELECT * FROM catalogs ORDER BY id asc";
			} else {
				query = "SELECT * FROM catalogs WHERE nameCatalog ILIKE '%" + nameSearch + "%' ORDER BY id asc";
			}
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					hmCatalog.put(rs.getString("nameCatalog"), rs.getInt("id"));
					catalogList.add(new Catalogs(rs.getInt("id"), rs.getString("nameCatalog"),
							rs.getString("descriptionCatalog"), rs.getString("barcodeCatalog")));
				}
			}
			TableColumn<Catalogs, Number> indexColumn = new TableColumn<Catalogs, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(40);
			indexColumn.setMaxWidth(40);
			indexColumn.getStyleClass().add("my-special-column-style");
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
					tableCatalog.getItems().indexOf(column.getValue()) + 1));
			tableCatalog.getColumns().add(0, indexColumn);
			TableColumn<Catalogs, String> nameCatalogCol = new TableColumn<Catalogs, String>("Danh mục");
			nameCatalogCol.setCellValueFactory(new PropertyValueFactory<>("nameCatalog"));
			nameCatalogCol.getStyleClass().add("my-special-column-style");
			nameCatalogCol.setCellFactory(TextFieldTableCell.<Catalogs>forTableColumn());
			nameCatalogCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Catalogs, String>>() {
				@Override
				public void handle(TableColumn.CellEditEvent<Catalogs, String> t) {
					if (!t.getNewValue().isEmpty()) {
						try {
							connection = dbHandler.getConnection();
							stmt = connection.createStatement();
							int id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
							String sql3 = "UPDATE catalogs SET nameCatalog = '" + t.getNewValue() + "' WHERE id = '"
									+ id + "'";
							int sts = stmt.executeUpdate(sql3);
							connection.commit();
							if (sts == 1) {
								BuilderProduct(id, txtSearchProduct.getText(), "");
								if (tableCatalog.getItems().size() > 0) {
									BuilderProduct(id, txtSearchProduct.getText(), "");
									tableCatalog.getSelectionModel().select(t.getTablePosition().getRow());
								}
								if (tableProducts.getItems().size() > 0
										&& tableProducts.getItems().get(0).getId() != 0) {
									tableProducts.getSelectionModel().selectFirst();
									loadProduct(tableProducts.getItems().get(0).getId());
								} else {
									loadProduct(0);
								}
							}
							stmt.close();
							connection.close();
						} catch (Exception e) {
							Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
						}
					}
				}
			});

			TableColumn<Catalogs, Catalogs> editColumn = new TableColumn<>("Sửa");
			editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			editColumn.setMinWidth(50);
			editColumn.setMaxWidth(50);
			editColumn.getStyleClass().add("my-special-column-style");
			editColumn.setCellFactory(param -> new TableCell<Catalogs, Catalogs>() {
				@Override
				protected void updateItem(Catalogs item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					} else {
						JFXButton btnEdit = new JFXButton("Sửa");
						btnEdit.setStyle(
								"-fx-background-color: GREEN;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
						setGraphic(btnEdit);
						btnEdit.setOnAction(event -> {
							Dialog<Pair<String, String>> dialgC = renderDialogCatalog();
							dialogCatalog.setTitle("Chỉnh sửa danh mục");
							TextField txtBarcode = new TextField();
							txtBarcode.setDisable(true);
							gridCatalog.add(new Label("Mã Barcode:"), 0, 2);
							gridCatalog.add(txtBarcode, 1, 2);
							ImageView imgBarcode = new ImageView();
							imgBarcode.setFitHeight(50);
							gridCatalog.add(imgBarcode, 1, 3);
							int id = item.getId();
							try {
								connection = dbHandler.getConnection();
								String query = "SELECT * FROM catalogs WHERE id = '" + id + "'";
								ResultSet rs = connection.createStatement().executeQuery(query);
								while (rs.next()) {
									txtBarcode.setText(rs.getString("barcodeCatalog"));
									imgBarcode.setImage(
											new Image(new File("barImg/" + rs.getString("barcodeCatalog") + ".png")
													.toURI().toString()));
									nameCatalog.setText(rs.getString("nameCatalog"));
									descriptionCatalog.setText(rs.getString("descriptionCatalog"));
								}
								connection.close();
							} catch (Exception e) {
								Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
							}
							Optional<Pair<String, String>> result = dialgC.showAndWait();
							result.ifPresent(value -> {
								String nameC = value.getKey();
								String descC = value.getValue();
								try {
									connection = dbHandler.getConnection();
									stmt = connection.createStatement();
									String sql3 = "UPDATE catalogs SET nameCatalog = '" + nameC
											+ "', descriptionCatalog = '" + descC + "'  WHERE id = '" + id + "'";
									int sts = stmt.executeUpdate(sql3);
									connection.commit();
									if (sts == 1) {
										BuilderCatalog(txtSearchCatalog.getText());
										BuilderProduct(id, txtSearchProduct.getText(), "");
									}
									stmt.close();
									connection.close();
								} catch (Exception e) {
									Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
								}
							});
						});
					}
				}
			});
			TableColumn<Catalogs, Catalogs> deleteColumn = new TableColumn<>("Xóa");
			deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			deleteColumn.setMinWidth(50);
			deleteColumn.setMaxWidth(50);
			deleteColumn.getStyleClass().add("my-special-column-style");
			deleteColumn.setCellFactory(param -> new TableCell<Catalogs, Catalogs>() {
				@Override
				protected void updateItem(Catalogs item, boolean empty) {
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
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle(Global.tsl_lblConfirmDialog);
							alert.setHeaderText(null);
							alert.setContentText("Bạn có muốn xóa danh mục '" + item.getBarcodeCatalog() + "' không?");
							Optional<ButtonType> action = alert.showAndWait();
							if (action.get() == ButtonType.OK) {
								try {
									connection = dbHandler.getConnection();
									String query = "SELECT barcodeProduct FROM products WHERE catalogId = '"
											+ item.getId() + "'";
									ResultSet rs = connection.createStatement().executeQuery(query);
									ObservableList<String> barcodeImageList = FXCollections.observableArrayList();
									while (rs.next()) {
										barcodeImageList.add(rs.getString("barcodeProduct"));
									}
									PreparedStatement ps = connection.prepareStatement(
											"DELETE FROM catalogs WHERE id = ?; DELETE FROM products WHERE catalogid = ?;");
									ps.setInt(1, item.getId());
									ps.setInt(2, item.getId());
									int sts = ps.executeUpdate();
									connection.commit();
									ps.close();
									if (sts == 1) {
										BuilderCatalog(txtSearchCatalog.getText());
										BuilderProduct(item.getId(), txtSearchProduct.getText(), "");
										if (tableCatalog.getItems().size() > 0
												&& tableCatalog.getItems().get(0).getId() != 0) {
											BuilderProduct(tableCatalog.getItems().get(0).getId(),
													txtSearchProduct.getText(), "");
											tableCatalog.getSelectionModel().selectFirst();
										}
										if (tableProducts.getItems().size() > 0
												&& tableProducts.getItems().get(0).getId() != 0) {
											tableProducts.getSelectionModel().selectFirst();
											loadProduct(tableProducts.getItems().get(0).getId());
										} else {
											loadProduct(0);
										}
										for (int i = 0; i < barcodeImageList.size(); i++) {
											File file = new File("barImg/" + barcodeImageList.get(i) + ".png");
											file.delete();
										}
										File fileC = new File("barImg/" + item.getBarcodeCatalog() + ".png");
										fileC.delete();
									}
									connection.close();
								} catch (Exception e) {
									Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
								}
							}
						});
					}
				}
			});
			tableCatalog.setEditable(true);
			tableCatalog.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableCatalog.getColumns().addAll(nameCatalogCol, editColumn, deleteColumn);
			tableCatalog.getItems().addAll(catalogList);
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@SuppressWarnings("unchecked")
	public void BuilderProduct(int catalogId, String nameSearch, String barcode) {
		try {
			tableProducts.getItems().clear();
			tableProducts.getColumns().clear();
			productList.clear();
			connection = dbHandler.getConnection();
			String query;
			if (nameSearch.isEmpty()) {
				if (barcode.isEmpty()) {
					query = "SELECT * FROM products WHERE catalogId = '" + catalogId + "' ORDER BY id ASC";
				} else {
					query = "SELECT * FROM products WHERE barcodeProduct = '" + barcode + "' ORDER BY id ASC";
				}
			} else {
				query = "SELECT * FROM products WHERE nameProduct ILIKE '%" + nameSearch + "%' ORDER BY id ASC";
			}
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					productList.add(new Products(rs.getInt("id"), rs.getString("nameProduct"), rs.getInt("catalogId"),
							rs.getString("barcodeProduct"), rs.getString("descriptionProduct"),
							rs.getString("location"), rs.getString("priceOrigin"), rs.getString("priceSell"),
							rs.getString("unit")));
				}
			}
			connection.close();
			TableColumn<Products, Number> indexColumn = new TableColumn<Products, Number>("#");
			indexColumn.setSortable(false);
			indexColumn.setMinWidth(40);
			indexColumn.setMaxWidth(40);
			indexColumn.setStyle("-fx-alignment: CENTER;");
			indexColumn.getStyleClass().add("my-special-column-style");
			indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(
					tableProducts.getItems().indexOf(column.getValue()) + 1));
			tableProducts.getColumns().add(0, indexColumn);
			TableColumn<Products, String> nameProductCol = new TableColumn<Products, String>("Tên sản phẩm");
			nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
			nameProductCol.getStyleClass().add("my-special-column-style");
			nameProductCol.setCellFactory(TextFieldTableCell.<Products>forTableColumn());
			nameProductCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Products, String>>() {
				@Override
				public void handle(TableColumn.CellEditEvent<Products, String> t) {
					if (!t.getNewValue().isEmpty()) {
						try {
							connection = dbHandler.getConnection();
							stmt = connection.createStatement();
							int id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
							String sql3 = "UPDATE products SET nameProduct = '" + t.getNewValue() + "' WHERE id = '"
									+ id + "'";
							int sts = stmt.executeUpdate(sql3);
							connection.commit();
							if (sts == 1) {
								loadProduct(id);
							}
							stmt.close();
							connection.close();
						} catch (Exception e) {
							Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
						}
					}
				}
			});

			TableColumn<Products, Products> editColumn = new TableColumn<>("Sửa");
			editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			editColumn.getStyleClass().add("my-special-column-style");
			editColumn.setMinWidth(50);
			editColumn.setMaxWidth(50);
			editColumn.setCellFactory(param -> new TableCell<Products, Products>() {
				@Override
				protected void updateItem(Products item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					} else {
						JFXButton btnEdit = new JFXButton("Sửa");
						btnEdit.setStyle(
								"-fx-background-color: GREEN;-fx-text-fill: WHITE; -fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5); -fx-background-radius: 4;");
						setGraphic(btnEdit);
						btnEdit.setOnAction(event -> {
							Dialog<List> dialg = renderDialogProduct();
							dialogProduct.setTitle("Thay đổi thông tin sản phẩm");
							int id = item.getId();
							try {
								TextField txtBarcode = new TextField();
								txtBarcode.setDisable(true);

								gridProduct.add(new Label("Mã Barcode:"), 0, 2);
								gridProduct.add(txtBarcode, 1, 2);
								ImageView imgBarcode = new ImageView();
								imgBarcode.setFitHeight(50);

								connection = dbHandler.getConnection();
								String query = "SELECT products.*,catalogs.nameCatalog FROM products LEFT OUTER JOIN catalogs ON (products.catalogid = catalogs.id) WHERE products.id = '"
										+ id + "'";
								ResultSet rs = connection.createStatement().executeQuery(query);
								File f = null;
								while (rs.next()) {
									nameProduct.setText(rs.getString("nameProduct"));
									comCatalogId.setValue(rs.getString("nameCatalog"));
									description.setText(rs.getString("descriptionProduct"));
									location.setText(rs.getString("location"));
									priceOrigin.setText(String.valueOf(rs.getInt("priceOrigin")));
									priceSell.setText(String.valueOf(rs.getInt("priceSell")));
									unit.setText(rs.getString("unit"));
									txtBarcode.setText(rs.getString("barcodeProduct"));
									f = new File("barImg/" + rs.getString("barcodeProduct") + ".png");
									if (f.exists()) {
										imgBarcode.setImage(
												new Image(new File("barImg/" + rs.getString("barcodeProduct") + ".png")
														.toURI().toString()));
									}
								}
								if (f.exists()) {
									gridProduct.add(imgBarcode, 1, 3);
								}
								// Platform.runLater(new Runnable() {
								// @Override
								// public void run() {
								// description.requestFocus();
								// }
								// });
								// description.setPrefRowCount(1);
								// description.setMaxHeight(27);
								// description.setMinHeight(27);
								connection.close();
							} catch (Exception e) {
								Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
							}
							Optional<List> result = dialg.showAndWait();
							description.requestFocus();
							result.ifPresent(value -> {
								String nameProductDialog = value.getItem(0);
								String catalogIdDialog = value.getItem(1);
								String descriptionDialog = value.getItem(2);
								String locationDialog = value.getItem(3);
								int priceOriginDialog = Integer.parseInt(value.getItem(4));
								int priceSellDialog = Integer.parseInt(value.getItem(5));
								String unitDialog = value.getItem(6);
								try {
									connection = dbHandler.getConnection();
									stmt = connection.createStatement();
									String sql3 = "UPDATE products SET nameProduct = '" + nameProductDialog
											+ "', catalogid = (SELECT id FROM catalogs WHERE nameCatalog = '"
											+ catalogIdDialog + "'), descriptionProduct = '" + descriptionDialog
											+ "', priceOrigin = '" + priceOriginDialog + "', priceSell = '"
											+ priceSellDialog + "', location = '" + locationDialog + "', unit = '"
											+ unitDialog + "'  WHERE id = '" + id + "'";
									int sts = stmt.executeUpdate(sql3);
									connection.commit();
									if (sts == 1) {
										BuilderProduct(hmCatalog.get(catalogIdDialog), txtSearchProduct.getText(), "");
										loadProduct(id);
									}
									stmt.close();
									connection.close();
								} catch (Exception e) {
									Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
								}
							});
						});
					}
				}
			});

			TableColumn<Products, Products> deleteColumn = new TableColumn<>("Xóa");
			deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			deleteColumn.getStyleClass().add("my-special-column-style");
			deleteColumn.setMinWidth(50);
			deleteColumn.setMaxWidth(50);
			deleteColumn.setCellFactory(param -> new TableCell<Products, Products>() {
				@Override
				protected void updateItem(Products item, boolean empty) {
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
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle(Global.tsl_lblConfirmDialog);
							alert.setHeaderText(null);
							alert.setContentText("Bạn có muốn xóa '" + item.getNameProduct() + "' không?");
							Optional<ButtonType> action = alert.showAndWait();
							if (action.get() == ButtonType.OK) {
								try {
									connection = dbHandler.getConnection();
									PreparedStatement ps = connection
											.prepareStatement("DELETE FROM products WHERE id = ?;");
									ps.setInt(1, item.getId());
									int sts = ps.executeUpdate();
									connection.commit();
									ps.close();
									if (sts == 1) {
										BuilderProduct(catalogId, txtSearchProduct.getText(), "");
										if (tableProducts.getItems().size() > 0) {
											tableProducts.getSelectionModel().selectFirst();
											loadProduct(tableProducts.getItems().get(0).getId());
										} else {
											loadProduct(0);
										}
										File file = new File("barImg/" + item.getBarcodeProduct() + ".png");
										file.delete();
									}
									connection.close();
								} catch (Exception e) {
									Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
								}
							}
						});
					}
				}
			});
			TableColumn<Products, Products> printAddColumn = new TableColumn<>("IN");
			printAddColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
			printAddColumn.getStyleClass().add("my-special-column-style");
			printAddColumn.setMinWidth(45);
			printAddColumn.setMaxWidth(45);
			printAddColumn.setCellFactory(param -> new TableCell<Products, Products>() {
				@Override
				protected void updateItem(Products item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setGraphic(null);
						return;
					} else {
						ImageView imageViewPrint = new ImageView(
								new Image(getClass().getResourceAsStream("/icons/printer.png")));
						imageViewPrint.setFitWidth(18);
						imageViewPrint.setFitHeight(18);
						JFXButton btnPrint = new JFXButton();
						btnPrint.setTooltip(new Tooltip("In"));
						btnPrint.setGraphic(imageViewPrint);
						btnPrint.setStyle("-fx-background-color: #333;-fx-padding: -5px");
						btnPrint.setMaxHeight(21);
						btnPrint.setMinHeight(21);
						btnPrint.setMaxWidth(30);
						btnPrint.setMinWidth(30);
						setGraphic(btnPrint);
						btnPrint.setOnAction(event -> {
							// PrintThread prunt = new
							// PrintThread(tableHistoryPay.getItems().get(getIndex()).getId());
							// prunt.start();
							int productId = tableProducts.getItems().get(getIndex()).getId();
							String nameP = tableProducts.getItems().get(getIndex()).getNameProduct();
							String barP = tableProducts.getItems().get(getIndex()).getBarcodeProduct();
							int quanPrint = 1;
							if (!printList.containsKey(barP)) {
								if (barP.length() == 13) {

									gg.add(new Print(productId, nameP, barP, quanPrint));
									printList.put(barP, gg);
									buildPrintTable();
								} else {
									Alert alert = new Alert(AlertType.WARNING);
									alert.setTitle(Global.tsl_lblConfirmDialog);
									alert.setHeaderText(null);
									alert.setContentText("Mã sản phẩm không đúng tiêu chuẩn.");
									alert.showAndWait();
								}
							} else {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle(Global.tsl_lblConfirmDialog);
								alert.setHeaderText(null);
								alert.setContentText("Sản phẩm này đã chọn.");
								alert.showAndWait();
							}

						});
					}
				}
			});
			tableProducts.setEditable(false);
			tableProducts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableProducts.getColumns().addAll(nameProductCol, editColumn, deleteColumn, printAddColumn);
			tableProducts.getItems().addAll(productList);
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void buildPrintTable() {
		tablePrint.getItems().clear();
		tablePrint.getColumns().clear();
		TableColumn<Print, Number> indexColumn = new TableColumn<Print, Number>("#");
		indexColumn.setSortable(false);
		indexColumn.setMinWidth(40);
		indexColumn.setMaxWidth(40);
		indexColumn.setStyle("-fx-alignment: CENTER;");
		indexColumn.getStyleClass().add("my-special-column-style");
		indexColumn.setCellValueFactory(
				column -> new ReadOnlyObjectWrapper<Number>(tablePrint.getItems().indexOf(column.getValue()) + 1));
		tablePrint.getColumns().add(0, indexColumn);

		TableColumn<Print, String> nameProductCol = new TableColumn<Print, String>("Tên sản phẩm");
		nameProductCol.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
		nameProductCol.getStyleClass().add("my-special-column-style");
		nameProductCol.setCellFactory(TextFieldTableCell.<Print>forTableColumn());

		TableColumn<Print, Integer> quatityCol = new TableColumn<Print, Integer>("S.Lượng");
		quatityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quatityCol.setMinWidth(70);
		quatityCol.setMaxWidth(70);
		quatityCol.setCellFactory(column -> new TableCell<Print, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					TextField txtQuatity = new TextField();
					txtQuatity.setStyle("-fx-padding: 1px 5px 1px 5px;");
					txtQuatity.setText(item.toString());
					setGraphic(txtQuatity);
					txtQuatity.textProperty().addListener((a, b, c) -> {
						if (ValidateHandle.isNumericInteger(c) && Integer.parseInt(c) > 0) {
							tablePrint.getItems().get(getIndex()).setQuantity(Integer.parseInt(c));
							txtQuatity.setText(c);
						} else {
							txtQuatity.setText(b);
						}
					});
					txtQuatity.setOnKeyReleased(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent ke) {
							if (ke.getText().trim().isEmpty() && !txtQuatity.getText().trim().isEmpty()) {
								tablePrint.getItems().get(getIndex())
										.setQuantity(Integer.parseInt(txtQuatity.getText()));
							}
						}
					});
					txtQuatity.focusedProperty().addListener((a, b, c) -> {
						if (b) {
							tablePrint.getItems().get(getIndex()).setQuantity(Integer.parseInt(txtQuatity.getText()));
						}
					});
				}
			}
		});
		TableColumn<Print, Print> deleteColumn = new TableColumn<>("Xóa");
		deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteColumn.getStyleClass().add("my-special-column-style");
		deleteColumn.setMinWidth(50);
		deleteColumn.setMaxWidth(50);
		deleteColumn.setCellFactory(param -> new TableCell<Print, Print>() {
			@Override
			protected void updateItem(Print item, boolean empty) {
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
							gg.remove(tablePrint.getItems().get(getIndex()));
							printList.remove(tablePrint.getItems().get(getIndex()).getBarcode());
							buildPrintTable();
						} catch (Exception e) {
							Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
						}
					});
				}
			}
		});
		tablePrint.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tablePrint.getColumns().addAll(nameProductCol, quatityCol, deleteColumn);
		tablePrint.getItems().addAll(gg);
	}

	@FXML
	private void actionOpenFolder() {
		try {
			System.out.println(System.getProperty("user.dir"));
			Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "/files/printFiles"));
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@FXML
	private void addCatalog() {
		try {
			Dialog<Pair<String, String>> dialgC = renderDialogCatalog();
			dialogCatalog.setTitle("Thêm mới danh mục");
			Optional<Pair<String, String>> result = dialgC.showAndWait();
			result.ifPresent(value -> {
				try {
					connection = dbHandler.getConnection();
					long barcodeDialog = Instant.now().getEpochSecond();
					stmt = connection.createStatement();
					String sql = "insert into catalogs (nameCatalog,descriptionCatalog,barcodeCatalog) " + "values ('"
							+ value.getKey() + "','" + value.getValue() + "','" + barcodeDialog + "')";
					int sts = stmt.executeUpdate(sql);
					connection.commit();
					stmt.close();
					connection.close();
					if (sts == 1) {
						BuilderCatalog(txtSearchCatalog.getText());
					}
					RenderBarcodeThread barcodeThr = new RenderBarcodeThread(String.valueOf(barcodeDialog), true);
					barcodeThr.start();
					// File fileNewBar = barcodeThr.getLink();
					// System.out.println("fileNewBar " + fileNewBar);
					// File fileNewBar = new File("barImg/" + barcodeDialog + ".png");
					// FileInputStream fis = new FileInputStream(fileNewBar);
					// fis.close();
				} catch (Exception e) {
					Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
				}
			});
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@FXML
	private void actionPrintAll() {
		System.out.println(gg.size());
		final String FILE_NAME;
		if (radio30.isSelected()) {
			FILE_NAME = "files/PrintTemp30.xls";
		} else {
			FILE_NAME = "files/PrintTemp57.xls";
		}
		if (gg.size() > 0) {

			try {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(FILE_NAME));

				int k = 0;
				wb = new HSSFWorkbook(fs, true);
				HSSFSheet sheet = wb.getSheetAt(0);
				for (Print item : gg) {

					String barcodeBill = item.getBarcode();

					BarcodeController.renderBarcode(barcodeBill, true);
					File fileO;
					fileO = new File("barImg" + "/" + barcodeBill + ".png");

					if (fileO.exists()) {

						FileInputStream fis = new FileInputStream(fileO);
						ByteArrayOutputStream img_bytes = new ByteArrayOutputStream();
						int b;
						while ((b = fis.read()) != -1)
							img_bytes.write(b);
						fis.close();
						InputStream inputStream = new FileInputStream("barImg" + "/" + barcodeBill + ".png");
						byte[] bytes = IOUtils.toByteArray(inputStream);
						int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
						CreationHelper helper = wb.getCreationHelper();

						HSSFPatriarch drawing = sheet.createDrawingPatriarch();
						for (int i = 0; i < item.getQuantity(); i++) {
							ClientAnchor anchor = helper.createClientAnchor();
							System.out.println("row" + (k) + "| code " + item.getBarcode());
							anchor.setCol1(0);
							anchor.setRow1(k);
							anchor.setDx1(20);
							anchor.setDy1(10);
							HSSFPicture pict = drawing.createPicture(anchor, pictureIdx);
							pict.resize(1);
							k++;
						}
					}

				}
				
				FileOutputStream out = new FileOutputStream("files/printTemp.xls");
				Path src = Paths.get("files/printTemp.xls");
				File file1 = new File(src.toString());
				wb.write(out);
				
				
				
//				File file1 = new File("files/printFiles/printTemp.xls");
//				 file1.canWrite();
//				FileOutputStream out = new FileOutputStream(file1);
				wb.write(out);
				out.close();
				Desktop.getDesktop().print(file1);
//				file1.delete();
				gg.clear();
				printList.clear();
				buildPrintTable();
			
			} catch (Exception e) {
				Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	Service<Void> service = new Service<Void>() {
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					// Background work
					final CountDownLatch latch = new CountDownLatch(1);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								// FX Stuff done here
							} finally {
								latch.countDown();
							}
						}
					});
					latch.await();
					// Keep with the background work
					return null;
				}
			};
		}
	};

	@FXML
	private void addProduct() {
		try {
			outputFileP = null;
			Dialog<List> dialg = renderDialogProduct();
			TextField txtBarcode = new TextField();
			gridProduct.add(new Label("Mã Barcode:"), 0, 2);
			gridProduct.add(txtBarcode, 1, 2);

			// ImageView imgBarcode = new ImageView();
			// imgBarcode.setFitHeight(50);
			// gridProduct.add(imgBarcode, 1, 3);
			txtBarcode.setOnKeyReleased(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent ke) {
					if (ke.getText().trim().isEmpty() && !txtBarcode.getText().trim().isEmpty()) {
						if (checkBarcodeIsExist(txtBarcode.getText())) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle(Global.tsl_lblConfirmDialog);
							alert.setHeaderText(null);
							alert.setContentText("Mã sản phẩm '" + txtBarcode.getText() + "' này đã tồn tại?");
							alert.showAndWait();
							txtBarcode.clear();
							txtBarcode.requestFocus();
						}
					}
				}
			});
			Optional<List> result = dialg.showAndWait();
			if (result.isPresent()) {
				String barcodeDialog;
				if (txtBarcode.getText().trim().isEmpty()) {
					barcodeDialog = BarcodeController
							.calculateCodeWithcheckSum("89" + String.valueOf(Instant.now().getEpochSecond()));
				} else
					barcodeDialog = txtBarcode.getText().trim();
				List value = result.get();
				String nameProductDialog = value.getItem(0);
				String catalogIdDialog = value.getItem(1);
				String descriptionDialog = value.getItem(2);
				String locationDialog = value.getItem(3);
				int priceOriginDialog = Integer.parseInt(value.getItem(4));
				int priceSellDialog = Integer.parseInt(value.getItem(5));
				String unitDialog = value.getItem(6);
				try {
					connection = dbHandler.getConnection();
					stmt = connection.createStatement();
					String sql = "insert into products (nameProduct, catalogId, barcodeProduct, descriptionProduct,location,priceOrigin,priceSell,unit) "
							+ "values ('" + nameProductDialog
							+ "',(Select id From catalogs Where catalogs.nameCatalog = '" + catalogIdDialog + "'),'"
							+ barcodeDialog + "','" + descriptionDialog + "','" + locationDialog + "','"
							+ priceOriginDialog + "','" + priceSellDialog + "','" + unitDialog + "')";
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet rs = stmt.getGeneratedKeys();
					connection.commit();
					if (rs.next()) {
						BuilderProduct(hmCatalog.get(catalogIdDialog), txtSearchProduct.getText(), "");
						if (tableProducts.getItems().size() > 0) {
							tableProducts.getSelectionModel().selectLast();
							loadProduct(rs.getInt("id"));
						} else {
							loadProduct(0);
						}
					}
					stmt.close();
					connection.close();

				} catch (Exception e) {
					Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
				}
			} else {
				if (outputFileP != null && outputFileP.exists()) {
					outputFileP.delete();
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	// private void refreshImage(ImageView imgBarcode, String barCode) {
	// File fileNewBar = new File("barImg/" + barCode + ".png");
	// outputFileP = fileNewBar;
	// imgBarcode.setImage(new Image(fileNewBar.toURI().toString()));
	// }

	@FXML
	private void actionSaveQ() throws SQLException {
		connection = dbHandler.getConnection();
		stmt = connection.createStatement();
		if (isEnable.isSelected()) {
			if (!txtPriceQ.getText().isEmpty() && !txtQuatityQ.getText().isEmpty()) {
				String sql;
				if (_QId == 0) {
					sql = "insert into QuantityPrice (productId,quantity,sellCost,enable) " + "values ('" + _productId
							+ "','" + txtQuatityQ.getText() + "','" + txtPriceQ.getText() + "'," + isEnable.isSelected()
							+ ")";
				} else
					sql = "UPDATE QuantityPrice SET quantity = '" + txtQuatityQ.getText() + "',sellCost  = '"
							+ txtPriceQ.getText() + "',enable = true WHERE id = '" + _QId + "'";
				int sts = stmt.executeUpdate(sql);
				connection.commit();
				stmt.close();
				connection.close();
				if (sts == 1) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Lưu thành công!");
					alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setHeaderText(null);
				alert.setContentText("Số lượng và Giá không được trống!");
				alert.showAndWait();
			}
		} else {
			if (_QId != 0) {
				String sql = "UPDATE QuantityPrice SET enable = false  WHERE id = '" + _QId + "'";
				int sts = stmt.executeUpdate(sql);
				connection.commit();
				stmt.close();
				connection.close();
				if (sts == 1) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Lưu thành công!");
					alert.showAndWait();
				}
			}
		}

	}

	@FXML
	private void actionPrint() {

	}

	@FXML
	private void actionFixBarcode() {
		try {
			connection = dbHandler.getConnection();
			String query = "SELECT * FROM products WHERE barcodeproduct LIKE '00%'";
			ResultSet rs = connection.createStatement().executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString("nameProduct"));
				stmt = connection.createStatement();
				String abc = rs.getString("barcodeProduct").substring(2, 12);
				String barG = BarcodeController.calculateCodeWithcheckSum("89" + abc);
				String sql3 = "UPDATE products SET barcodeProduct = '" + barG + "'  WHERE id = '" + rs.getInt("id")
						+ "'";
				stmt.executeUpdate(sql3);
				connection.commit();
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public Dialog<Pair<String, String>> renderDialogCatalog() {
		dialogCatalog = new Dialog<>();
		ButtonType saveButtonTypeButtonType = new ButtonType("Lưu", ButtonData.OK_DONE);
		dialogCatalog.getDialogPane().getButtonTypes().addAll(saveButtonTypeButtonType, ButtonType.CANCEL);
		gridCatalog = new GridPane();
		gridCatalog.setHgap(10);
		gridCatalog.setVgap(10);
		gridCatalog.setPadding(new Insets(20, 10, 10, 10));
		nameCatalog = new TextField();
		nameCatalog.setMinWidth(300);
		nameCatalog.setMaxWidth(300);
		descriptionCatalog = new TextArea();
		descriptionCatalog.setWrapText(true);
		descriptionCatalog.setPrefRowCount(3);
		descriptionCatalog.setMinWidth(300);
		descriptionCatalog.setMaxWidth(300);
		gridCatalog.add(new Label("Tên danh mục:"), 0, 0);
		gridCatalog.add(nameCatalog, 1, 0);
		gridCatalog.add(new Label("Mô tả:"), 0, 1);
		gridCatalog.add(descriptionCatalog, 1, 1);
		Node saveButton = dialogCatalog.getDialogPane().lookupButton(saveButtonTypeButtonType);
		saveButton.setDisable(true);
		nameCatalog.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
			if (!newValue.trim().isEmpty()) {
				if (!oldValue.isEmpty() && !oldValue.equals(newValue)) {
					if (!textTmp.equals(newValue) && checkIsExist(nameCatalog.getText().trim(), true)) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle(Global.tsl_lblConfirmDialog);
						alert.setHeaderText(null);
						alert.setContentText("Danh mục '" + nameCatalog.getText().trim() + "' đã tồn tại?");
						alert.showAndWait();
						Platform.runLater(() -> {
							this.nameCatalog.clear();
							nameCatalog.requestFocus();
						});
					}
				}
			}
		});
		nameCatalog.focusedProperty().addListener((a, b, c) -> {
			if (c)
				textTmp = nameCatalog.getText();
		});
		dialogCatalog.getDialogPane().setContent(gridCatalog);
		// Platform.runLater(() -> nameCatalog.requestFocus());
		dialogCatalog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonTypeButtonType) {
				return new Pair<>(nameCatalog.getText(), descriptionCatalog.getText());
			}
			return null;
		});
		return dialogCatalog;
	}

	public Dialog<List> renderDialogProduct() {
		// Create the custom dialog.
		dialogProduct = new Dialog<>();
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
		catalogList.stream().forEach(item -> {
			comCatalogId.getItems().add(item.getNameCatalog());
		});
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
			if (!newValue.trim().isEmpty()) {
				if (!oldValue.isEmpty() && !oldValue.equals(newValue)) {
					if (!textTmp.equals(newValue) && checkIsExist(nameProduct.getText().trim(), false)) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle(Global.tsl_lblConfirmDialog);
						alert.setHeaderText(null);
						alert.setContentText("Sản phẩm '" + nameProduct.getText().trim() + "' đã tồn tại?");
						alert.showAndWait();
						Platform.runLater(() -> {
							nameProduct.clear();
							nameProduct.requestFocus();
						});
					}
				}
			}
		});
		nameProduct.focusedProperty().addListener((a, b, c) -> {
			if (c)
				textTmp = nameProduct.getText();
		});
		dialogProduct.getDialogPane().setContent(gridProduct);
		// Platform.runLater(() -> nameProduct.requestFocus());
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

	public static boolean checkIsExist(String txt, boolean type) {
		boolean sts = false;
		try {
			dbHandler = new DbHandler();
			connection = dbHandler.getConnection();
			String query;
			if (type) {
				query = "SELECT nameCatalog FROM catalogs WHERE lower(nameCatalog) = '" + txt.toLowerCase() + "'";
			} else
				query = "SELECT nameProduct FROM products WHERE lower(nameProduct) = '" + txt.toLowerCase() + "'";
			ResultSet rs = connection.createStatement().executeQuery(query);
			connection.commit();
			if (rs.isBeforeFirst()) {
				sts = true;
			} else {
				sts = false;
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
		System.out.println(sts);
		return sts;
	}

	private boolean checkBarcodeIsExist(String txt) {
		boolean sts = false;
		try {
			connection = dbHandler.getConnection();
			String query = "SELECT barcodeProduct FROM products WHERE lower(barcodeProduct) = '" + txt.toLowerCase()
					+ "'";
			ResultSet rs = connection.createStatement().executeQuery(query);
			if (rs.isBeforeFirst()) {
				sts = true;
			} else {
				sts = false;
			}
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(ProductsController.class.getName()).log(Level.SEVERE, null, e);
		}
		return sts;
	}
}