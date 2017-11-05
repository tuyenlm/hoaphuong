package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class FxChartUtil {
	// Create PieChart Data
	public static ObservableList<PieChart.Data> getChartData() {
		ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
		data.add(new PieChart.Data("China", 1275));
		data.add(new PieChart.Data("India", 1017));
		data.add(new PieChart.Data("Brazil", 172));
		data.add(new PieChart.Data("UK", 59));
		data.add(new PieChart.Data("USA", 285));
		return data;
	}

	// Create XYChart Country Data
	public static ObservableList<XYChart.Series<Number, Number>> getCountrySeries() {
		XYChart.Series<Number, Number> seriesChina = new XYChart.Series<Number, Number>();
		seriesChina.setName("China");
		seriesChina.getData().add(new XYChart.Data<Number, Number>(1, 555));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(2, 1275));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(3, 453));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(4, 865));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(5, 6665));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(6, 553));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(7, 4532));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(8, 1182));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(9, 5354));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(10, 1182));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(11, 3452));
		seriesChina.getData().add(new XYChart.Data<Number, Number>(12, 343));
		

		XYChart.Series<Number, Number> seriesIndia = new XYChart.Series<Number, Number>();
		seriesIndia.setName("India");
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(1, 555));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(2, 1275));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(3, 444));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(4, 354));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(5, 765));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(6, 553));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(7, 356));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(8, 775));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(9, 843));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(10, 1182));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(11, 3452));
		seriesIndia.getData().add(new XYChart.Data<Number, Number>(12, 343));

		XYChart.Series<Number, Number> seriesUSA = new XYChart.Series<Number, Number>();
		seriesUSA.setName("USA");
		seriesUSA.getData().add(new XYChart.Data<Number, Number>(1950, 158));
		seriesUSA.getData().add(new XYChart.Data<Number, Number>(2000, 285));
		seriesUSA.getData().add(new XYChart.Data<Number, Number>(2050, 409));
		seriesUSA.getData().add(new XYChart.Data<Number, Number>(2100, 437));
		seriesUSA.getData().add(new XYChart.Data<Number, Number>(2150, 453));

		ObservableList<XYChart.Series<Number, Number>> data = FXCollections.<XYChart.Series<Number, Number>>observableArrayList();
		data.add(seriesChina);
		data.add(seriesIndia);
//		data.add(seriesUSA);
		return data;
	}

	// Create XYChart Year Data
	public static ObservableList<XYChart.Series<String, Number>> getYearSeries() {
		XYChart.Series<String, Number> series1950 = new XYChart.Series<String, Number>();
		series1950.setName("1950");
		series1950.getData().add(new XYChart.Data<String, Number>("China", 555));
		series1950.getData().add(new XYChart.Data<String, Number>("India", 358));
		series1950.getData().add(new XYChart.Data<String, Number>("Brazil", 54));
		series1950.getData().add(new XYChart.Data<String, Number>("UK", 50));
		series1950.getData().add(new XYChart.Data<String, Number>("USA", 158));

		XYChart.Series<String, Number> series2000 = new XYChart.Series<String, Number>();
		series2000.setName("2000");
		series2000.getData().add(new XYChart.Data<String, Number>("China", 1275));
		series2000.getData().add(new XYChart.Data<String, Number>("India", 1017));
		series2000.getData().add(new XYChart.Data<String, Number>("Brazil", 172));
		series2000.getData().add(new XYChart.Data<String, Number>("UK", 59));
		series2000.getData().add(new XYChart.Data<String, Number>("USA", 285));

		XYChart.Series<String, Number> series2050 = new XYChart.Series<String, Number>();
		series2050.setName("2050");
		series2050.getData().add(new XYChart.Data<String, Number>("China", 1395));
		series2050.getData().add(new XYChart.Data<String, Number>("India", 1531));
		series2050.getData().add(new XYChart.Data<String, Number>("Brazil", 233));
		series2050.getData().add(new XYChart.Data<String, Number>("UK", 66));
		series2050.getData().add(new XYChart.Data<String, Number>("USA", 409));

		ObservableList<XYChart.Series<String, Number>> data = FXCollections.<XYChart.Series<String, Number>>observableArrayList();
		data.add(series1950);
		data.add(series2000);
		data.add(series2050);
		return data;
	}
}