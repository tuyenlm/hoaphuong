package modules;

import java.net.URL;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeListener;

import org.apache.commons.collections4.trie.UnmodifiableTrie;

import com.jfoenix.controls.JFXComboBox;

import database.DbHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import models.Users;

public class StatisticalController implements Initializable {
	@FXML
	private TableView<Users> _table;

	@FXML
	private Tab tabRevenue, tabWarehouse, tabImportGoods;
	@FXML
	private Pane RevenuePane;
	@FXML
	private JFXComboBox<String> comboboxYear, comboboxMonth;
	private DbHandler dbHandler;
	private Connection connection;

	public void initialize(URL url, ResourceBundle rb) {
		dbHandler = new DbHandler();
		connection = dbHandler.getConnection();
		buildRevenuePane();
		Calendar calendar = new GregorianCalendar();
		int yearC = calendar.get(Calendar.YEAR);
		int monthC = calendar.get(Calendar.MONTH);
		try {
			for (int i = 2017; i <= yearC; i++) {
				comboboxYear.getItems().add(String.valueOf(i));
			}

			for (int i = 1; i <= 12; i++) {
				comboboxMonth.getItems().add(String.valueOf(i));
			}
			comboboxYear.setValue(String.valueOf(yearC));
			comboboxMonth.setValue(String.valueOf(monthC));
			System.out.println(comboboxMonth.getValue());
			comboboxMonth.valueProperty().addListener((a, b, c) -> {
				System.out.println("|" + b);
				System.out.println("|" + c);
				chartMonths(Integer.parseInt(comboboxYear.getSelectionModel().getSelectedItem()), Integer.parseInt(c));
			});
			chartMonths(Integer.parseInt(comboboxYear.getSelectionModel().getSelectedItem()),
					Integer.parseInt(comboboxMonth.getSelectionModel().getSelectedItem()));
		} catch (Exception e) {
			Logger.getLogger(StatisticalController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void buildRevenuePane() {
		// chartYears();
		// chartMonths();
	}

	private void chartYears() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Year");
		yAxis.setLabel("Tuyen");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
		lineChart.setTitle("Lợi Nhuận Tháng 10/2017");
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("My Year");
		for (int i = 1; i < 13; i++) {
			series.getData().add(new XYChart.Data<String, Number>(String.valueOf(i), i * 1110));
		}
		lineChart.getData().add(series);
		lineChart.setStyle("-fx-border-color:#333;-fx-border-radius:5px");
		RevenuePane.getChildren().add(lineChart);
	}

	@SuppressWarnings("deprecation")
	private void chartMonths(int year, int month) {
		RevenuePane.getChildren().clear();
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
//		xAxis.setLabel("Ngày");
//		yAxis.setLabel("Tiền");
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
		// lineChart.setTitle("Lợi Nhuận Tháng 10/2017");
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("Lợi Nhuận Tháng " + month + "/" + year);
		ComboBox<String> fg = new ComboBox<>();
		fg.getItems().add("ss");
		
		series.getNode().setStyle("-fx-stroke:blue;-fx-stroke-width:1");
		int DaysInMonth = numberOfDaysInMonth(month, year);
		for (int i = 1; i <= DaysInMonth; i++) {
			series.getData().add(new XYChart.Data<String, Number>(String.valueOf(i), i * 1110));
		}
		lineChart.getData().add(series);
		lineChart.setStyle("-fx-border-color:#333;-fx-border-radius:5px");
		RevenuePane.getChildren().add(lineChart);
	}

	public static int numberOfDaysInMonth(int month, int year) {
		Calendar monthStart = new GregorianCalendar(year, month, 1);
		return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}