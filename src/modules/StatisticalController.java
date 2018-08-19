package modules;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXComboBox;

import database.DbHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import models.Revenue;

public class StatisticalController implements Initializable {
	@FXML
	private TableView<Revenue> tableRevenue;

	@FXML
	private Tab tabRevenue, tabWarehouse, tabImportGoods;
	@FXML
	private Pane RevenuePane;
	@FXML
	private JFXComboBox<String> comboboxYear, comboboxMonth, comboboxChangeView;
	private ObservableList<Revenue> lists = FXCollections.observableArrayList();
	private DbHandler dbHandler;
	private static Connection connection;
	private HashMap<Integer, Integer> dataMonth;
	private HashMap<Integer, Integer> dataYear;
	private HashMap<Integer, Integer> gocToltal;
	private HashMap<Integer, Integer> thuveToltal;

	public void initialize(URL url, ResourceBundle rb) {
		dbHandler = new DbHandler();
		Calendar calendar = new GregorianCalendar();
		int yearC = calendar.get(Calendar.YEAR);
		int monthC = calendar.get(Calendar.MONTH) + 1;
		try {
			for (int i = 2017; i <= yearC; i++) {
				comboboxYear.getItems().add(String.valueOf(i));
			}
			for (int i = 1; i <= 12; i++) {
				comboboxMonth.getItems().add(String.valueOf(i));
			}
			comboboxYear.setValue(String.valueOf(yearC));
			comboboxMonth.setValue(String.valueOf(monthC));
			comboboxMonth.valueProperty().addListener((a, b, c) -> {
				chartMonths();
				buildRevenueTable("month");
			});
			chartMonths();
			buildRevenueTable("month");
			comboboxYear.valueProperty().addListener((a, b, c) -> {
				chartYears();
				buildRevenueTable("year");
			});
			comboboxChangeView.getItems().addAll("Theo Tháng", "Theo Năm");
			comboboxChangeView.getSelectionModel().select(0);
			comboboxChangeView.valueProperty().addListener((a, b, c) -> {
				if (comboboxChangeView.getSelectionModel().getSelectedItem().toString().equals("Theo Tháng")) {
					comboboxMonth.setDisable(false);
					chartMonths();
					buildRevenueTable("month");
				} else {
					comboboxMonth.setDisable(true);
					chartYears();
					buildRevenueTable("year");
				}
			});
		} catch (Exception e) {
			Logger.getLogger(StatisticalController.class.getName()).log(Level.SEVERE, null, e);
		}

		tabWarehouse.setOnSelectionChanged((event) -> {
			if (tabWarehouse.isSelected()) {
				try {

					FXMLLoader loader = new FXMLLoader(getClass().getResource("tabKhoHang.fxml"));
					tabWarehouse.setContent(loader.load());

				} catch (Exception e) {
					Logger.getLogger(StatisticalController.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void buildRevenueTable(String type) {
		tableRevenue.getColumns().clear();
		tableRevenue.getItems().clear();
		lists.clear();
		TableColumn<Revenue, String> timeCol;
		if (type.equals("month")) {
			System.out.println("month type : " +type);
			System.out.println("dataMonth.size(): " +dataMonth.size());
			if (dataMonth.size() > 0) {
				dataMonth.forEach((key, value) -> {
					System.out.println("Key : " + key + " Value : " + value);
					lists.add(new Revenue("" + key + "/" + comboboxMonth.getValue(), gocToltal.get(key),
							thuveToltal.get(key), value));
				});
			}
			timeCol = new TableColumn<Revenue, String>("Ngày");
			timeCol.setMinWidth(45);
			timeCol.setMaxWidth(45);
		} else {
			if (dataYear.size() > 0) {
				dataYear.forEach((key, value) -> {
					System.out.println("Key : " + key + " Value : " + value);
					lists.add(new Revenue("" + key, gocToltal.get(key), thuveToltal.get(key), value));
				});
			}
			timeCol = new TableColumn<Revenue, String>("Tháng");
			timeCol.setMinWidth(50);
			timeCol.setMaxWidth(50);
		}

		timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
		timeCol.setCellFactory(TextFieldTableCell.<Revenue>forTableColumn());
		timeCol.setStyle("-fx-alignment: CENTER;-fx-text-size:9pt");

		TableColumn<Revenue, Integer> originCol = new TableColumn<Revenue, Integer>("Vốn");
		originCol.setCellValueFactory(new PropertyValueFactory<>("origin"));
		originCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		originCol.setMinWidth(80);
		originCol.setMaxWidth(80);
		originCol.setCellFactory(column -> new TableCell<Revenue, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(HomeController.decimalFormat.format(item));
				}
			}
		});

		TableColumn<Revenue, Integer> totalCol = new TableColumn<Revenue, Integer>("Thu Về");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
		totalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		totalCol.setMinWidth(80);
		totalCol.setMaxWidth(80);
		totalCol.setCellFactory(column -> new TableCell<Revenue, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(HomeController.decimalFormat.format(item));
				}
			}
		});
		TableColumn<Revenue, Integer> profitCol = new TableColumn<Revenue, Integer>("Lời");
		profitCol.setCellValueFactory(new PropertyValueFactory<>("profit"));
		profitCol.setStyle("-fx-alignment: CENTER-RIGHT;");
		profitCol.setMinWidth(60);
		profitCol.setMaxWidth(60);
		profitCol.setCellFactory(column -> new TableCell<Revenue, Integer>() {

			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(HomeController.decimalFormat.format(item));
				}
			}
		});
		tableRevenue.getColumns().addAll(timeCol, originCol, totalCol, profitCol);
		tableRevenue.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableRevenue.getItems().addAll(lists);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void chartYears() {
		int year = Integer.parseInt(comboboxYear.getSelectionModel().getSelectedItem());
		RevenuePane.getChildren().clear();
		try {
			connection = dbHandler.getConnection();
			String query = "Select bills.id,bills.pricetotal, bills.statusbill,bills.createdAtB,sales.pricesell,sales.priceOrigin, sales.quantitys"
					+ " from bills inner join sales on (bills.id = sales.billid) inner join products on (products.id = sales.productid) "
					+ "WHERE CAST(createdatb as TEXT) like '" + year + "-%';";
			System.out.println("nawm " + query);
			ResultSet rs = connection.createStatement().executeQuery(query);
			dataYear = new HashMap<Integer, Integer>();
			gocToltal = new HashMap<Integer, Integer>();
			thuveToltal = new HashMap<Integer, Integer>();
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
					String date = simpleDateFormat.format(rs.getTimestamp("createdAtB"));
					int loinhuan = rs.getInt("priceSell") * rs.getInt("quantitys")
							- rs.getInt("priceOrigin") * rs.getInt("quantitys");
					int gocc = rs.getInt("priceOrigin") * rs.getInt("quantitys");
					int thuvee = rs.getInt("priceSell") * rs.getInt("quantitys");
					if (dataYear.get(Integer.parseInt(date)) == null) {
						dataYear.put(Integer.parseInt(date), loinhuan);
						gocToltal.put(Integer.parseInt(date), gocc);
						thuveToltal.put(Integer.parseInt(date), thuvee);
					} else {
						int gt = dataYear.get(Integer.parseInt(date));
						int goc = gocToltal.get(Integer.parseInt(date));
						int thuve = thuveToltal.get(Integer.parseInt(date));
						gt += loinhuan;
						goc += gocc;
						thuve += thuvee;
						dataYear.put(Integer.parseInt(date), gt);
						gocToltal.put(Integer.parseInt(date), goc);
						thuveToltal.put(Integer.parseInt(date), thuve);
					}
				}
			}
			NumberAxis xAxis = new NumberAxis(1, 12, 1);
			NumberAxis yAxis = new NumberAxis();
			LineChart lineChart = new LineChart(xAxis, yAxis);
			XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
			series.setName("Lợi Nhuận Năm " + year);
			for (int i = 0; i <= 11; i++) {
				if (dataYear.get(i) != null)
					series.getData().add(new XYChart.Data(i, dataYear.get(i)));
			}
			lineChart.getData().add(series);
			lineChart.setStyle("-fx-border-color:#333;-fx-border-radius:5px");
			RevenuePane.getChildren().add(lineChart);
			rs.close();
			connection.close();
		} catch (Exception e) {
			Logger.getLogger(StatisticalController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void chartMonths() {
		int year = Integer.parseInt(comboboxYear.getSelectionModel().getSelectedItem());
		int month = Integer.parseInt(comboboxMonth.getSelectionModel().getSelectedItem());
		RevenuePane.getChildren().clear();
		int DaysInMonth = numberOfDaysInMonth(month, year);
		NumberAxis xAxis = new NumberAxis(1, DaysInMonth, 1);
		NumberAxis yAxis = new NumberAxis();
		// xAxis.setLabel("Ngày");
		// yAxis.setLabel("Tiền");
		LineChart lineChart = new LineChart(xAxis, yAxis);
		// lineChart.setTitle("Lợi Nhuận Tháng 10/2017");
		XYChart.Series series = new XYChart.Series();
		series.setName("Lợi Nhuận Tháng " + month + "/" + year);
		ComboBox<String> fg = new ComboBox<>();
		fg.getItems().add("ss");
		try {
			connection = dbHandler.getConnection();
			String query = "Select bills.id, bills.pricetotal, bills.statusbill, bills.createdAtB,sales.pricesell,sales.priceOrigin, sales.quantitys"
					+ " from bills inner join sales on (bills.id = sales.billid) inner join products on (products.id = sales.productid) "
					+ "WHERE CAST(createdatb as TEXT) like '" + year + "-" + String.format("%02d", month )+ "-%';";
			System.out.println("thang "+query);
			ResultSet rs = connection.createStatement().executeQuery(query);
			dataMonth = new HashMap<Integer, Integer>();
			gocToltal = new HashMap<Integer, Integer>();
			thuveToltal = new HashMap<Integer, Integer>();
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
					String date = simpleDateFormat.format(rs.getTimestamp("createdAtB"));
					int loinhuan = rs.getInt("priceSell") * rs.getInt("quantitys")
							- rs.getInt("priceOrigin") * rs.getInt("quantitys");
					int gocc = rs.getInt("priceOrigin") * rs.getInt("quantitys");
					int thuvee = rs.getInt("priceSell") * rs.getInt("quantitys");
					if (dataMonth.get(Integer.parseInt(date)) == null) {
						dataMonth.put(Integer.parseInt(date), loinhuan);
						gocToltal.put(Integer.parseInt(date), gocc);
						thuveToltal.put(Integer.parseInt(date), thuvee);
					} else {
						int gt = dataMonth.get(Integer.parseInt(date));
						int goc = gocToltal.get(Integer.parseInt(date));
						int thuve = thuveToltal.get(Integer.parseInt(date));
						gt += loinhuan;
						goc += gocc;
						thuve += thuvee;
						dataMonth.put(Integer.parseInt(date), gt);
						gocToltal.put(Integer.parseInt(date), goc);
						thuveToltal.put(Integer.parseInt(date), thuve);
					}

				}

			}

			for (int i = 1; i <= DaysInMonth; i++) {
				if (dataMonth.get(i) != null)
					series.getData().add(new XYChart.Data(i, dataMonth.get(i)));
			}

			lineChart.getData().add(series);
			lineChart.setStyle("-fx-border-color:#333;-fx-border-radius:5px");
			RevenuePane.getChildren().add(lineChart);
			rs.close();
			connection.close();

		} catch (Exception e) {
			Logger.getLogger(StatisticalController.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	public static int numberOfDaysInMonth(int month, int year) {
		Calendar monthStart = new GregorianCalendar(year, month - 1, 1);
		return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}