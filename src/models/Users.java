package models;

import java.util.Date;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Users {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty username;
	private final SimpleStringProperty fullname;
	private final SimpleIntegerProperty type;
	private Date createdAt;

	public Users(int id, String username, String fullname, int type, Date createdAt) {
		this.id = new SimpleIntegerProperty(id);
		this.username = new SimpleStringProperty(username);
		this.fullname = new SimpleStringProperty(fullname);
		this.type = new SimpleIntegerProperty(type);
		this.createdAt = createdAt;
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

	public final SimpleStringProperty usernameProperty() {
		return this.username;
	}

	public final String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final String username) {
		this.usernameProperty().set(username);
	}

	public final SimpleStringProperty fullnameProperty() {
		return this.fullname;
	}

	public final String getFullname() {
		return this.fullnameProperty().get();
	}

	public final void setFullname(final String fullname) {
		this.fullnameProperty().set(fullname);
	}

	public final SimpleIntegerProperty typeProperty() {
		return this.type;
	}

	public final int getType() {
		return this.typeProperty().get();
	}

	public final void setType(final int type) {
		this.typeProperty().set(type);
	}

	public Date getDateTime() {
		return createdAt;
	}

	public void setDateTime(Date createdAt) {
		this.createdAt = createdAt;
	}

}
