package modules;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ResourceBundle;

import database.DbHandler;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author tuyenlm
 */
public class HomeController implements Initializable {

	HashMap<String, String> hasMUser = new HashMap<String, String>();
	protected Connection connection;
	private DbHandler handler;
	Statement stmt = null;
	ResultSet rs = null;
	StackPane deptStackPane;

	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// txtBarcode.requestFocus();
			}
		});
		handler = new DbHandler();
		connection = handler.getConnection();

		// txtBarcode.setMinWidth(1);
		// txtBarcode.setMaxWidth(1);
		// txtBarcode.setMinHeight(1);
		// txtBarcode.setMaxHeight(1);
		// txtBarcode.setOnKeyReleased(new EventHandler<KeyEvent>() {
		// public void handle(KeyEvent ke) {
		// if (ke.getText().trim().isEmpty() && !txtBarcode.getText().trim().isEmpty())
		// {
		// try {
		// System.out.println(txtBarcode.getText().trim() );
		// String query = "SELECT * FROM products WHERE barcodeProduct = '" +
		// txtBarcode.getText().trim() + "'";
		// ResultSet rs = connection.createStatement().executeQuery(query);
		// if(rs.isBeforeFirst()) {
		// rs.next();
		// System.out.println(rs.getString("nameProduct"));
		// System.out.println(rs.getInt("priceSell"));
		// }
		// txtBarcode.selectAll();
		//
		// } catch (Exception e) {
		// Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, e);
		// }
		// }
		// }
		// });
	}
}