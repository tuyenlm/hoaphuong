package models;

import java.util.Date;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Products {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty nameProduct;
	private final SimpleIntegerProperty catalogId;
	private final SimpleStringProperty barcodeProduct;
	private final SimpleStringProperty descriptionProduct;
	private final SimpleStringProperty location;
	private final SimpleStringProperty priceOrigin;
	private final SimpleStringProperty priceSell;
	private Date createdAtP;
	private final SimpleStringProperty unit;

	public Products(int id, String nameProduct, int catalogId, String barcodeProduct, String descriptionProduct,
			String location, String priceOrigin, String priceSell, String unit, Date createdAtP) {
		this.id = new SimpleIntegerProperty(id);
		this.nameProduct = new SimpleStringProperty(nameProduct);
		this.catalogId = new SimpleIntegerProperty(catalogId);
		this.barcodeProduct = new SimpleStringProperty(barcodeProduct);
		this.descriptionProduct = new SimpleStringProperty(descriptionProduct);
		this.location = new SimpleStringProperty(location);
		this.priceOrigin = new SimpleStringProperty(priceOrigin);
		this.priceSell = new SimpleStringProperty(priceSell);
		this.unit = new SimpleStringProperty(unit);
		this.createdAtP = createdAtP;
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

	public SimpleStringProperty locationProperty() {
		return this.location;
	}

	public String getLocation() {
		return this.locationProperty().get();
	}

	public void setLocation(final String location) {
		this.locationProperty().set(location);
	}

	public SimpleStringProperty unitProperty() {
		return this.unit;
	}

	public String getUnit() {
		return this.unitProperty().get();
	}

	public void setUnit(final String unit) {
		this.unitProperty().set(unit);
	}

	public SimpleIntegerProperty catalogIdProperty() {
		return this.catalogId;
	}

	public int getCatalogId() {
		return this.catalogIdProperty().get();
	}

	public void setCatalogId(final int catalogId) {
		this.catalogIdProperty().set(catalogId);
	}

	public SimpleStringProperty priceOriginProperty() {
		return this.priceOrigin;
	}

	public String getPriceOrigin() {
		return this.priceOriginProperty().get();
	}

	public void setPriceOrigin(final String priceOrigin) {
		this.priceOriginProperty().set(priceOrigin);
	}

	public SimpleStringProperty priceSellProperty() {
		return this.priceSell;
	}

	public String getPriceSell() {
		return this.priceSellProperty().get();
	}

	public void setPriceSell(final String priceSell) {
		this.priceSellProperty().set(priceSell);
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
	

	public SimpleStringProperty barcodeProductProperty() {
		return this.barcodeProduct;
	}
	

	public String getBarcodeProduct() {
		return this.barcodeProductProperty().get();
	}
	

	public void setBarcodeProduct(final String barcodeProduct) {
		this.barcodeProductProperty().set(barcodeProduct);
	}
	

	public SimpleStringProperty descriptionProductProperty() {
		return this.descriptionProduct;
	}
	

	public String getDescriptionProduct() {
		return this.descriptionProductProperty().get();
	}
	

	public void setDescriptionProduct(final String descriptionProduct) {
		this.descriptionProductProperty().set(descriptionProduct);
	}
	
}
