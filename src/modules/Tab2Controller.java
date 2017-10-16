package modules;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import database.DbHandler;
import javafx.fxml.Initializable;

public class Tab2Controller implements Initializable {

	private DbHandler dbHandler;
	private Connection connection;
	private boolean saveStatus = true;
	private int userId = 0;

	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("tab 2");
	}

}
