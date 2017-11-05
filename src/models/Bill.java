package models;

import java.util.Date;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Bill {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty barcodeBill;
	private final SimpleBooleanProperty statusBill;
	private final SimpleStringProperty priceTotal;
	private final SimpleStringProperty priceReceive;
	private final SimpleStringProperty createdAtB;
	private final SimpleStringProperty sellerName;

	public Bill(int id, String barcodeBill, String priceTotal, String priceReceive, boolean statusBill,
			String createdAtB, String sellerName) {
		this.id = new SimpleIntegerProperty(id);
		this.barcodeBill = new SimpleStringProperty(barcodeBill);
		this.priceTotal = new SimpleStringProperty(priceTotal);
		this.priceReceive = new SimpleStringProperty(priceReceive);
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

	public SimpleStringProperty priceReceiveProperty() {
		return this.priceReceive;
	}

	public String getPriceReceive() {
		return this.priceReceiveProperty().get();
	}

	public void setPriceReceive(final String priceReceive) {
		this.priceReceiveProperty().set(priceReceive);
	}

	public SimpleStringProperty createdAtBProperty() {
		return this.createdAtB;
	}
	

	public String getCreatedAtB() {
		return this.createdAtBProperty().get();
	}
	

	public void setCreatedAtB(final String createdAtB) {
		this.createdAtBProperty().set(createdAtB);
	}
	

}
