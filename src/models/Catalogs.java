package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Catalogs {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty nameCatalog;
	private final SimpleStringProperty descriptionCatalog;
	private final SimpleStringProperty barcodeCatalog;

	public Catalogs(int id, String nameCatalog, String descriptionCatalog, String barcodeCatalog) {
		this.id = new SimpleIntegerProperty(id);
		this.nameCatalog = new SimpleStringProperty(nameCatalog);
		this.descriptionCatalog = new SimpleStringProperty(descriptionCatalog);
		this.barcodeCatalog = new SimpleStringProperty(barcodeCatalog);
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

	public SimpleStringProperty nameCatalogProperty() {
		return this.nameCatalog;
	}
	

	public String getNameCatalog() {
		return this.nameCatalogProperty().get();
	}
	

	public void setNameCatalog(final String nameCatalog) {
		this.nameCatalogProperty().set(nameCatalog);
	}
	

	public SimpleStringProperty descriptionCatalogProperty() {
		return this.descriptionCatalog;
	}
	

	public String getDescriptionCatalog() {
		return this.descriptionCatalogProperty().get();
	}
	

	public void setDescriptionCatalog(final String descriptionCatalog) {
		this.descriptionCatalogProperty().set(descriptionCatalog);
	}
	

	public SimpleStringProperty barcodeCatalogProperty() {
		return this.barcodeCatalog;
	}
	

	public String getBarcodeCatalog() {
		return this.barcodeCatalogProperty().get();
	}
	

	public void setBarcodeCatalog(final String barcodeCatalog) {
		this.barcodeCatalogProperty().set(barcodeCatalog);
	}
	

}
