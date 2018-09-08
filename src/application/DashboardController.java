/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import modules.SettingsController;

/**
 * FXML Controller class
 *
 * @author tuyen
 */

public class DashboardController implements Initializable {
	private Stage _stage;
	@FXML
	private AnchorPane containIndex;
	@FXML
	private FlowPane containBottom;
	@FXML
	private JFXButton btnHome, btnProducts, btnUsers, btnStatistical, btnNhapHang, btnSettings;
	@FXML
	private Label lblToday;
	private AnchorPane home, products, users, statistical, nhaphang, settings;
	@FXML
	private MenuItem menuReadGolbalFile;

	@FXML
	private MenuButton menuConfig;

	/**
	 * Initializes the controller class.
	 *
	 * @param URL
	 * @param ResourceBundle
	 */
	public void initialize(URL location, ResourceBundle resources) {
		createPages();

		String pattern = Global.patternTime;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("vi", "VN"));
		String date = simpleDateFormat.format(new Date());
		lblToday.setText(date);

	}

	// Load all fxml files to a cahce for swapping
	private void createPages() {
		try {
			btnHome.setText(Global.tsl_menu_home);
			btnProducts.setText(Global.tsl_menu_Products);
			btnUsers.setText(Global.tsl_menu_Users);
			btnStatistical.setText(Global.tsl_menu_Statistical);
			btnNhapHang.setText(Global.tsl_menu_NhapHang);
			// tableWorks.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			home = FXMLLoader.load(getClass().getResource("/modules/Home.fxml"));
			// users =
			// FXMLLoader.load(getClass().getResource("/modules/UsersNew.fxml"));
			// if (Global.isTypeUser == Global.typeUser.getKey()) {
			// department.setVisible(false);
			// btnDepartment.setVisible(false);
			// shoyoku.setVisible(false);
			// btnShoyoku.setVisible(false);
			// activeCode.setVisible(false);
			// btnActiveCode.setVisible(false);
			// users.setVisible(false);
			// btnUsers.setVisible(false);
			// }
			setNode(home);

		} catch (IOException ex) {
			Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// Set selected node to a content holder
	private void setNode(Node node) {
		if (node.toString() == "kinmu") {
			btnHome.setStyle("-fx-background-color:#3F51B5;");
		}
		containIndex.getChildren().clear();
		containIndex.getChildren().add((Node) node);
		FadeTransition ft = new FadeTransition(Duration.millis(1500));
		ft.setNode(node);
		ft.setFromValue(0.1);
		ft.setToValue(1);
		ft.setCycleCount(1);
		ft.setAutoReverse(false);
		ft.play();
	}

	@FXML
	private void openKinmu(ActionEvent event) throws IOException {
		home = FXMLLoader.load(getClass().getResource("/modules/Home.fxml"));
		btnHome.setStyle("-fx-background-color:#3F51B5;-fx-text-fill: WHITE;");
		btnProducts.setStyle("-fx-background-color: #333;");
		btnStatistical.setStyle("-fx-background-color: #333;");
		btnNhapHang.setStyle("-fx-background-color: #333;");
		btnSettings.setStyle("-fx-background-color: #333;");
		setNode(home);
	}

	@FXML
	private void openProducts(ActionEvent event) throws IOException {
		products = FXMLLoader.load(getClass().getResource("/modules/Products.fxml"));
		btnProducts.setStyle("-fx-background-color:#3F51B5;-fx-text-fill: WHITE;");
		btnHome.setStyle("-fx-background-color: #333;");
		btnUsers.setStyle("-fx-background-color: #333;");
		btnStatistical.setStyle("-fx-background-color: #333;");
		btnNhapHang.setStyle("-fx-background-color: #333;");
		btnSettings.setStyle("-fx-background-color: #333;");
		setNode(products);
	}

	@FXML
	private void openUsers(ActionEvent event) throws IOException {
		users = FXMLLoader.load(getClass().getResource("/modules/Users.fxml"));
		btnProducts.setStyle("-fx-background-color: #333;");
		btnUsers.setStyle("-fx-background-color:#3F51B5;-fx-text-fill: WHITE;");
		btnHome.setStyle("-fx-background-color: #333;");
		btnStatistical.setStyle("-fx-background-color: #333;");
		btnNhapHang.setStyle("-fx-background-color: #333;");
		btnSettings.setStyle("-fx-background-color: #333;");
		setNode(users);
	}

	@FXML
	private void openStatistical(ActionEvent event) throws IOException {
		statistical = FXMLLoader.load(getClass().getResource("/modules/Statistical.fxml"));
		btnStatistical.setStyle("-fx-background-color:#3F51B5;-fx-text-fill: WHITE;");
		btnProducts.setStyle("-fx-background-color: #333;");
		btnHome.setStyle("-fx-background-color: #333;");
		btnUsers.setStyle("-fx-background-color: #333;");
		btnNhapHang.setStyle("-fx-background-color: #333;");
		btnSettings.setStyle("-fx-background-color: #333;");
		setNode(statistical);
	}

	@FXML
	private void openNhapHang(ActionEvent event) throws IOException {
		nhaphang = FXMLLoader.load(getClass().getResource("/modules/NhapHang.fxml"));
		btnNhapHang.setStyle("-fx-background-color:#3F51B5;-fx-text-fill: WHITE;");
		btnProducts.setStyle("-fx-background-color: #333;");
		btnHome.setStyle("-fx-background-color: #333;");
		btnUsers.setStyle("-fx-background-color: #333;");
		btnStatistical.setStyle("-fx-background-color: #333;");
		btnSettings.setStyle("-fx-background-color: #333;");
		setNode(nhaphang);
	}

	@FXML
	private void openSettings(ActionEvent event) throws IOException {
		settings = FXMLLoader.load(getClass().getResource("/modules/Settings.fxml"));


		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/modules/Settings.fxml"));   
		settings = (AnchorPane)fxmlLoader.load();   

		SettingsController controller = fxmlLoader.<SettingsController>getController();
		controller.setStage(_stage);
		
		
		btnSettings.setStyle("-fx-background-color:#3F51B5;-fx-text-fill: WHITE;");
		btnProducts.setStyle("-fx-background-color: #333;");
		btnHome.setStyle("-fx-background-color: #333;");
		btnUsers.setStyle("-fx-background-color: #333;");
		btnStatistical.setStyle("-fx-background-color: #333;");
		btnNhapHang.setStyle("-fx-background-color: #333;");
		setNode(settings);
	}

	@FXML
	private void actionReload() throws IOException {
		// File path = new
		// File(DashboardController.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		Desktop.getDesktop().open(new File("/Users/leminhtuyen/eclipse-workspace/hoaphuong/hoaphuong.jar"));
		System.exit(0);

	}

	@FXML
	private void actionExit() {
		System.exit(0);
	}
	
	public void setStage(Stage primaryStage) {
		this._stage = primaryStage;
	}

}
