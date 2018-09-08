package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Backups {

		private final SimpleIntegerProperty no;
		private final SimpleStringProperty fileName;
		private final SimpleStringProperty size;
		private final SimpleStringProperty dateTime;

		public final SimpleIntegerProperty noProperty() {
			return this.no;
		}

		public final int getNo() {
			return this.noProperty().get();
		}

		public final void setNo(final int no) {
			this.noProperty().set(no);
		}

		public final SimpleStringProperty fileNameProperty() {
			return this.fileName;
		}

		public final String getFileName() {
			return this.fileNameProperty().get();
		}

		public final void setFileName(final String fileName) {
			this.fileNameProperty().set(fileName);
		}

		public final SimpleStringProperty sizeProperty() {
			return this.size;
		}

		public final String getSize() {
			return this.sizeProperty().get();
		}

		public final void setSize(final String size) {
			this.sizeProperty().set(size);
		}

		public final SimpleStringProperty dateTimeProperty() {
			return this.dateTime;
		}

		public final String getDateTime() {
			return this.dateTimeProperty().get();
		}

		public final void setDateTime(final String dateTime) {
			this.dateTimeProperty().set(dateTime);
		}

		public Backups(int no, String fileName, String size, String dateTime) {
			this.no = new SimpleIntegerProperty(no);
			this.fileName = new SimpleStringProperty(fileName);
			this.size = new SimpleStringProperty(size);
			this.dateTime = new SimpleStringProperty(dateTime);
		}

	}
