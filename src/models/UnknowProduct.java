package models;

import javafx.beans.property.SimpleStringProperty;

public class UnknowProduct {
	public final SimpleStringProperty barcodeUnknow;

	public UnknowProduct(String barcodeUnknow) {
		this.barcodeUnknow = new SimpleStringProperty(barcodeUnknow);
	}

	public SimpleStringProperty barcodeUnknowProperty() {
		return this.barcodeUnknow;
	}

	public String getBarcodeUnknow() {
		return this.barcodeUnknowProperty().get();
	}

	public void setBarcodeUnknow(final String barcodeUnknow) {
		this.barcodeUnknowProperty().set(barcodeUnknow);
	}
}
