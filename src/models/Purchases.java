package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Purchases {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty barcodePur;
	private final SimpleIntegerProperty SupplierId;
	private final SimpleBooleanProperty PaymentStatus;
	private final SimpleIntegerProperty GrandTotal;
	private final SimpleBooleanProperty Paid;
	private final SimpleStringProperty createdAtP;
	private final SimpleStringProperty note;
	private final SimpleIntegerProperty userCreated;

	public Purchases(int id, String barcodePur, int SupplierId, Boolean PaymentStatus, int GrandTotal, Boolean Paid,
			String createdAtP, String note, int userCreated) {
		this.id = new SimpleIntegerProperty(id);
		this.barcodePur = new SimpleStringProperty(barcodePur);
		this.SupplierId = new SimpleIntegerProperty(SupplierId);
		this.PaymentStatus = new SimpleBooleanProperty(PaymentStatus);
		this.GrandTotal = new SimpleIntegerProperty(GrandTotal);
		this.Paid = new SimpleBooleanProperty(Paid);
		this.createdAtP = new SimpleStringProperty(createdAtP);
		this.note = new SimpleStringProperty(note);
		this.userCreated = new SimpleIntegerProperty(userCreated);
	}

	public SimpleIntegerProperty idProperty() {
		return this.id;
	}

	public int getId() {
		return this.idProperty().get();
	}

	public void setId(final int id) {
		this.idProperty().set(id);
	}

	public SimpleStringProperty barcodePurProperty() {
		return this.barcodePur;
	}

	public String getBarcodePur() {
		return this.barcodePurProperty().get();
	}

	public void setBarcodePur(final String barcodePur) {
		this.barcodePurProperty().set(barcodePur);
	}

	public SimpleIntegerProperty SupplierIdProperty() {
		return this.SupplierId;
	}

	public int getSupplierId() {
		return this.SupplierIdProperty().get();
	}

	public void setSupplierId(final int SupplierId) {
		this.SupplierIdProperty().set(SupplierId);
	}

	public SimpleIntegerProperty GrandTotalProperty() {
		return this.GrandTotal;
	}

	public int getGrandTotal() {
		return this.GrandTotalProperty().get();
	}

	public void setGrandTotal(final int GrandTotal) {
		this.GrandTotalProperty().set(GrandTotal);
	}

	public SimpleBooleanProperty PaidProperty() {
		return this.Paid;
	}

	public boolean isPaid() {
		return this.PaidProperty().get();
	}

	public void setPaid(final boolean Paid) {
		this.PaidProperty().set(Paid);
	}

	public SimpleStringProperty createdAtPProperty() {
		return this.createdAtP;
	}

	public String getCreatedAtP() {
		return this.createdAtPProperty().get();
	}

	public void setCreatedAtP(final String createdAtP) {
		this.createdAtPProperty().set(createdAtP);
	}

	public SimpleStringProperty noteProperty() {
		return this.note;
	}

	public String getNote() {
		return this.noteProperty().get();
	}

	public void setNote(final String note) {
		this.noteProperty().set(note);
	}

	public SimpleIntegerProperty userCreatedProperty() {
		return this.userCreated;
	}

	public int getUserCreated() {
		return this.userCreatedProperty().get();
	}

	public void setUserCreated(final int userCreated) {
		this.userCreatedProperty().set(userCreated);
	}

	public SimpleBooleanProperty PaymentStatusProperty() {
		return this.PaymentStatus;
	}
	

	public boolean isPaymentStatus() {
		return this.PaymentStatusProperty().get();
	}
	

	public void setPaymentStatus(final boolean PaymentStatus) {
		this.PaymentStatusProperty().set(PaymentStatus);
	}
	

}
