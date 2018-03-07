package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Print {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty nameProduct;
	private final SimpleStringProperty barcode;
	private final SimpleIntegerProperty quantity;

	public Print(int id, String nameProduct, String barcode, int quantity) {
		this.id = new SimpleIntegerProperty(id);
		this.nameProduct = new SimpleStringProperty(nameProduct);
		this.barcode = new SimpleStringProperty(barcode);
		this.quantity = new SimpleIntegerProperty(quantity);
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
	

	public SimpleStringProperty nameProductProperty() {
		return this.nameProduct;
	}
	

	public String getNameProduct() {
		return this.nameProductProperty().get();
	}
	

	public void setNameProduct(final String nameProduct) {
		this.nameProductProperty().set(nameProduct);
	}
	

	public SimpleStringProperty barcodeProperty() {
		return this.barcode;
	}
	

	public String getBarcode() {
		return this.barcodeProperty().get();
	}
	

	public void setBarcode(final String barcode) {
		this.barcodeProperty().set(barcode);
	}
	

	public SimpleIntegerProperty quantityProperty() {
		return this.quantity;
	}
	

	public int getQuantity() {
		return this.quantityProperty().get();
	}
	

	public void setQuantity(final int quantity) {
		this.quantityProperty().set(quantity);
	}
	

}
