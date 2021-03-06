package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Buy {
	private final SimpleIntegerProperty productId;
	private final SimpleStringProperty nameProduct;
	private final SimpleIntegerProperty quatity;
	private final SimpleIntegerProperty price;
	private final SimpleIntegerProperty priceOrigin;
	private final SimpleIntegerProperty priceTotal;
	private final SimpleIntegerProperty saleId;
	private final SimpleIntegerProperty quantityQ;
	private final SimpleIntegerProperty priceQ;
	private final SimpleBooleanProperty enable;

	public Buy(int productId, String nameProduct, int quatity, int price, int priceOrigin, int priceTotal, int saleId,
			int quantityQ, int priceQ, boolean enable) {
		this.productId = new SimpleIntegerProperty(productId);
		this.nameProduct = new SimpleStringProperty(nameProduct);
		this.quatity = new SimpleIntegerProperty(quatity);
		this.price = new SimpleIntegerProperty(price);
		this.priceOrigin = new SimpleIntegerProperty(priceOrigin);
		this.priceTotal = new SimpleIntegerProperty(priceTotal);
		this.saleId = new SimpleIntegerProperty(saleId);
		this.quantityQ = new SimpleIntegerProperty(quantityQ);
		this.priceQ = new SimpleIntegerProperty(priceQ);
		this.enable = new SimpleBooleanProperty(enable);
	}

	public final SimpleIntegerProperty quantityQProperty() {
		return this.quantityQ;
	}

	public final int getQuantityQ() {
		return this.quantityQProperty().get();
	}

	public final void setQuantityQ(final int quantityQ) {
		this.quantityQProperty().set(quantityQ);
	}

//	
	public final SimpleIntegerProperty priceQProperty() {
		return this.priceQ;
	}

	public final int getPriceQ() {
		return this.priceQProperty().get();

	}

	public final void setPriceQ(final int priceQ) {
		this.priceQProperty().set(priceQ);
	}

//
	public final SimpleBooleanProperty enableProperty() {
		return this.enable;
	}

	public final boolean isEnable() {
		return this.enableProperty().get();
	}

	public final void setEnable(final boolean enable) {
		this.enableProperty().set(enable);
	}

	public final SimpleIntegerProperty productIdProperty() {
		return this.productId;
	}

	public final int getProductId() {
		return this.productIdProperty().get();
	}

	public final void setProductId(final int productId) {
		this.productIdProperty().set(productId);
	}

	public final SimpleStringProperty nameProductProperty() {
		return this.nameProduct;
	}

	public final String getNameProduct() {
		return this.nameProductProperty().get();
	}

	public final void setNameProduct(final String nameProduct) {
		this.nameProductProperty().set(nameProduct);
	}

	public final SimpleIntegerProperty quatityProperty() {
		return this.quatity;
	}

	public final int getQuatity() {
		return this.quatityProperty().get();
	}

	public final void setQuatity(final int quatity) {
		this.quatityProperty().set(quatity);
	}

	public final SimpleIntegerProperty priceProperty() {
		return this.price;
	}

	public final int getPrice() {
		return this.priceProperty().get();
	}

	public final void setPrice(final int price) {
		this.priceProperty().set(price);
	}

	public final SimpleIntegerProperty priceTotalProperty() {
		return this.priceTotal;
	}

	public final int getPriceTotal() {
		return this.priceTotalProperty().get();
	}

	public final void setPriceTotal(final int priceTotal) {
		this.priceTotalProperty().set(priceTotal);
	}

	public SimpleIntegerProperty saleIdProperty() {
		return this.saleId;
	}

	public int getSaleId() {
		return this.saleIdProperty().get();
	}

	public void setSaleId(final int saleId) {
		this.saleIdProperty().set(saleId);
	}

	public SimpleIntegerProperty priceOriginProperty() {
		return this.priceOrigin;
	}

	public int getPriceOrigin() {
		return this.priceOriginProperty().get();
	}

	public void setPriceOrigin(final int priceOrigin) {
		this.priceOriginProperty().set(priceOrigin);
	}

}
