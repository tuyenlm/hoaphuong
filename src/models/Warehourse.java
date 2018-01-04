package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Warehourse {
	private final SimpleIntegerProperty id;
	private final SimpleIntegerProperty productId;
	private final SimpleIntegerProperty quantitySold;
	private final SimpleIntegerProperty remainingAmount;
	private final SimpleStringProperty nameProduct;

	public Warehourse(int id, int productId, String nameProduct, int quantitySold, int remainingAmount) {
		this.id = new SimpleIntegerProperty(id);
		this.productId = new SimpleIntegerProperty(productId);
		this.nameProduct = new SimpleStringProperty(nameProduct);
		this.quantitySold = new SimpleIntegerProperty(quantitySold);
		this.remainingAmount = new SimpleIntegerProperty(remainingAmount);
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

	public SimpleIntegerProperty productIdProperty() {
		return this.productId;
	}

	public int getProductId() {
		return this.productIdProperty().get();
	}

	public void setProductId(final int productId) {
		this.productIdProperty().set(productId);
	}

	public SimpleIntegerProperty quantitySoldProperty() {
		return this.quantitySold;
	}

	public int getQuantitySold() {
		return this.quantitySoldProperty().get();
	}

	public void setQuantitySold(final int quantitySold) {
		this.quantitySoldProperty().set(quantitySold);
	}

	public SimpleIntegerProperty remainingAmountProperty() {
		return this.remainingAmount;
	}

	public int getRemainingAmount() {
		return this.remainingAmountProperty().get();
	}

	public void setRemainingAmount(final int remainingAmount) {
		this.remainingAmountProperty().set(remainingAmount);
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
	
}
