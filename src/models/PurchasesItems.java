package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PurchasesItems {
	private final SimpleIntegerProperty id;
	private final SimpleIntegerProperty purchasesId;
	private final SimpleStringProperty productName;
	private final SimpleIntegerProperty productId;
	private final SimpleIntegerProperty quantityPur;
	private final SimpleIntegerProperty originCost;
	private final SimpleIntegerProperty sellCost;
	private final SimpleIntegerProperty subTotal;

	public PurchasesItems(int id, int purchasesId, String productName, int productId, int quantityPur, int originCost,
			int sellCost, int subTotal) {
		this.id = new SimpleIntegerProperty(id);
		this.purchasesId = new SimpleIntegerProperty(purchasesId);
		this.productName = new SimpleStringProperty(productName);
		this.productId = new SimpleIntegerProperty(productId);
		this.quantityPur = new SimpleIntegerProperty(quantityPur);
		this.originCost = new SimpleIntegerProperty(originCost);
		this.sellCost = new SimpleIntegerProperty(sellCost);
		this.subTotal = new SimpleIntegerProperty(subTotal);
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

	public SimpleIntegerProperty purchasesIdProperty() {
		return this.purchasesId;
	}

	public int getPurchasesId() {
		return this.purchasesIdProperty().get();
	}

	public void setPurchasesId(final int purchasesId) {
		this.purchasesIdProperty().set(purchasesId);
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

	public SimpleIntegerProperty quantityPurProperty() {
		return this.quantityPur;
	}

	public int getQuantityPur() {
		return this.quantityPurProperty().get();
	}

	public void setQuantityPur(final int quantityPur) {
		this.quantityPurProperty().set(quantityPur);
	}

	public SimpleIntegerProperty subTotalProperty() {
		return this.subTotal;
	}

	public int getSubTotal() {
		return this.subTotalProperty().get();
	}

	public void setSubTotal(final int subTotal) {
		this.subTotalProperty().set(subTotal);
	}

	public SimpleIntegerProperty originCostProperty() {
		return this.originCost;
	}

	public int getOriginCost() {
		return this.originCostProperty().get();
	}

	public void setOriginCost(final int originCost) {
		this.originCostProperty().set(originCost);
	}

	public SimpleIntegerProperty sellCostProperty() {
		return this.sellCost;
	}

	public int getSellCost() {
		return this.sellCostProperty().get();
	}

	public void setSellCost(final int sellCost) {
		this.sellCostProperty().set(sellCost);
	}

	public SimpleStringProperty productNameProperty() {
		return this.productName;
	}
	

	public String getProductName() {
		return this.productNameProperty().get();
	}
	

	public void setProductName(final String productName) {
		this.productNameProperty().set(productName);
	}
	

}
