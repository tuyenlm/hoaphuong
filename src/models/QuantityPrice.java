package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class QuantityPrice {
	private final SimpleIntegerProperty id;
	private final SimpleIntegerProperty productId;
	private final SimpleIntegerProperty quantity;
	private final SimpleIntegerProperty sellCost;
	private final SimpleBooleanProperty enable;

	public QuantityPrice(int id, int productId, int quantity, int sellCost, boolean enable) {
		this.id = new SimpleIntegerProperty(id);
		this.productId = new SimpleIntegerProperty(productId);
		this.quantity = new SimpleIntegerProperty(quantity);
		this.sellCost = new SimpleIntegerProperty(sellCost);
		this.enable = new SimpleBooleanProperty(enable);
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

	public final SimpleIntegerProperty productIdProperty() {
		return this.productId;
	}

	public final int getProdutcId() {
		return this.productIdProperty().get();
	}

	public final void setProdutcId(final int productId) {
		this.productIdProperty().set(productId);
	}

	public final SimpleIntegerProperty quantityProperty() {
		return this.quantity;
	}

	public final int getQuantity() {
		return this.quantityProperty().get();
	}

	public final void setQuantity(final int quantity) {
		this.quantityProperty().set(quantity);
	}

	public final SimpleIntegerProperty sellCostProperty() {
		return this.sellCost;
	}

	public final int getSellCost() {
		return this.sellCostProperty().get();
	}

	public final void setSellCost(final int sellCost) {
		this.sellCostProperty().set(sellCost);
	}

	public final SimpleBooleanProperty enableProperty() {
		return this.enable;
	}

	public final boolean isEnable() {
		return this.enableProperty().get();
	}

	public final void setEnable(final boolean enable) {
		this.enableProperty().set(enable);
	}
}
