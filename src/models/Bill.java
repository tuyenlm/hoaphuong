package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Bill {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty barcodeBill;
	private final SimpleBooleanProperty statusBill;
	private final SimpleStringProperty priceTotal;
	private final SimpleStringProperty createdAtB;
	private final SimpleStringProperty sellerName;

	public Bill(int id, String barcodeBill, String priceTotal, boolean statusBill, String createdAtB, String sellerName) {
		this.id = new SimpleIntegerProperty(id);
		this.barcodeBill = new SimpleStringProperty(barcodeBill);
		this.priceTotal = new SimpleStringProperty(priceTotal);
		this.statusBill = new SimpleBooleanProperty(statusBill);
		this.createdAtB = new SimpleStringProperty(createdAtB);
		this.sellerName = new SimpleStringProperty(sellerName);
	}

	public final SimpleIntegerProperty idProperty() {
		return this.id;
	}
	

	public final int getId() {
		return this.idProperty().get();
	}
	

	public final void setId(final int id) {
		this.idProperty().set(id);
	}
	

	public final SimpleStringProperty barcodeBillProperty() {
		return this.barcodeBill;
	}
	

	public final String getBarcodeBill() {
		return this.barcodeBillProperty().get();
	}
	

	public final void setBarcodeBill(final String barcodeBill) {
		this.barcodeBillProperty().set(barcodeBill);
	}
	

	public final SimpleBooleanProperty statusBillProperty() {
		return this.statusBill;
	}
	

	public final boolean isStatusBill() {
		return this.statusBillProperty().get();
	}
	

	public final void setStatusBill(final boolean statusBill) {
		this.statusBillProperty().set(statusBill);
	}

	public final SimpleStringProperty createdAtBProperty() {
		return this.createdAtB;
	}
	

	public final String getCreatedAtB() {
		return this.createdAtBProperty().get();
	}
	

	public final void setCreatedAtB(final String createdAtB) {
		this.createdAtBProperty().set(createdAtB);
	}
	

	public final SimpleStringProperty sellerNameProperty() {
		return this.sellerName;
	}
	

	public final String getSellerName() {
		return this.sellerNameProperty().get();
	}
	

	public final void setSellerName(final String sellerName) {
		this.sellerNameProperty().set(sellerName);
	}

	public final SimpleStringProperty priceTotalProperty() {
		return this.priceTotal;
	}
	

	public final String getPriceTotal() {
		return this.priceTotalProperty().get();
	}
	

	public final void setPriceTotal(final String priceTotal) {
		this.priceTotalProperty().set(priceTotal);
	}
	
	

}
