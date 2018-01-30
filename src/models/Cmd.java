package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Cmd {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty barcode;
	private final SimpleStringProperty action;
	private final SimpleStringProperty desc;

	public Cmd(int id, String barcode, String action, String desc) {
		this.id = new SimpleIntegerProperty(id);
		this.barcode = new SimpleStringProperty(barcode);
		this.action = new SimpleStringProperty(action);
		this.desc = new SimpleStringProperty(desc);
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

	public final SimpleStringProperty barcodeProperty() {
		return this.barcode;
	}
	

	public final String getBarcode() {
		return this.barcodeProperty().get();
	}
	

	public final void setBarcode(final String barcode) {
		this.barcodeProperty().set(barcode);
	}
	

	public final SimpleStringProperty actionProperty() {
		return this.action;
	}
	

	public final String getAction() {
		return this.actionProperty().get();
	}
	

	public final void setAction(final String action) {
		this.actionProperty().set(action);
	}
	

	public final SimpleStringProperty descProperty() {
		return this.desc;
	}
	

	public final String getDesc() {
		return this.descProperty().get();
	}
	

	public final void setDesc(final String desc) {
		this.descProperty().set(desc);
	}
	
}
