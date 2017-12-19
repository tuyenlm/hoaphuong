package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Revenue {
	private final SimpleStringProperty time;
	private final SimpleIntegerProperty origin;
	private final SimpleIntegerProperty total;
	private final SimpleIntegerProperty profit;

	public Revenue(String time, int origin, int total, int profit) {
		this.time = new SimpleStringProperty(time);
		this.origin = new SimpleIntegerProperty(origin);
		this.total = new SimpleIntegerProperty(total);
		this.profit = new SimpleIntegerProperty(profit);
	}

	public SimpleStringProperty timeProperty() {
		return this.time;
	}
	

	public String getTime() {
		return this.timeProperty().get();
	}
	

	public void setTime(final String time) {
		this.timeProperty().set(time);
	}
	

	public SimpleIntegerProperty originProperty() {
		return this.origin;
	}
	

	public int getOrigin() {
		return this.originProperty().get();
	}
	

	public void setOrigin(final int origin) {
		this.originProperty().set(origin);
	}
	

	public SimpleIntegerProperty totalProperty() {
		return this.total;
	}
	

	public int getTotal() {
		return this.totalProperty().get();
	}
	

	public void setTotal(final int total) {
		this.totalProperty().set(total);
	}
	

	public SimpleIntegerProperty profitProperty() {
		return this.profit;
	}
	

	public int getProfit() {
		return this.profitProperty().get();
	}
	

	public void setProfit(final int profit) {
		this.profitProperty().set(profit);
	}
	
}
