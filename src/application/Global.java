package application;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.util.Pair;

public class Global {
	public static Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

	public static Timestamp getTimeStampFormat() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String billNameStoreText = "TẠP HÓA HOA PHƯỢNG";
	public static String billAddressText = "116 Vân Đồn, Đà Nẵng";
	public static String billTelText = "0236.3848431";
	public static String billDetailText = "CHI TIẾT HÓA ĐƠN";
	public static String billTotalText = "Tổng cộng";
	public static String billMoneyReceiveText = "Nhận tiền";
	public static String billMoneyBackText = "Trả lại";
	public static String billTimePayText = "Thời gian thanh toán:";
	public static String billThanksSayText = "Cảm ơn quý khách đã mua hàng!";
	
	//
	public static String userId;
	public static String username;
	public static String fullname;
	public static String isTypeUser;
	public static String employeeId;
	public static String shoyokuName = "空の";
	public static String departmentName = "空の";
	public static String startWork = "09";
	public static String endWork = "18";
	public static int kyuukeiJikan = 1;
	public static int totalHoliday = 0;
	public static int totaldayWork = 0;
	public static int totalTimeWork = 0;
	public static boolean isSave = true;
	public static String folderBackups = "backups";

	public static int UserMinLength = 6;
	public static int UserMaxLength = 15;
	// MENU LEFT
	public static String tsl_menu_kinmu = "勤務";
	public static String tsl_menu_users = "ユーザー";
	public static String tsl_menu_departments = "部門";
	public static String tsl_menu_shozoku = "Shozoku";
	public static String tsl_menu_kintai = "勤怠";
	public static String tsl_menu_active_code = "Active Code";
	public static String tsl_menu_tasks = "Tasks";
	public static String tsl_menu_norikae = "場所";
	public static String tsl_menu_home = "Thanh Toán";
	public static String tsl_menu_Products = "Sản Phẩm";
	public static String tsl_menu_Users = "Nhân Viên";
	public static String tsl_menu_Warehouse = "Kho Hàng";
	public static String tsl_menu_norimono = "乗物";

	// KINMU
	public static String tsl_lbl_total_estimated_time = "合計予定時間：";
	public static String tsl_lbl_total_holiday_days = "総休日日数：";
	public static String tsl_lbl_total_time = "合計時間：";
	public static String tsl_lbl_fullname = "姓氏：";
	public static String tsl_lbl_username = "ユーザー名：";
	public static String tsl_lbl_employeeId = "業員ID：";
	public static String tsl_lbl_Shoyoku = "所属：";
	public static String tsl_lbl_Department = "部名：";
	public static String tsl_lbl_Kamei = "課名";
	public static String tsl_lbl_yaku = "役職";
	public static String tsl_lbl_email = "メール";
	public static String tsl_lbl_tel = "電話番号";
	public static String tsl_btnEdit = "編集";
	public static String tsl_lbl_typeUser = "ユーザーレベル";
	public static String tsl_lbl_passwordOld = "以前のパスワード";
	public static String tsl_lbl_passwordNew = "新しいパスワード";
	public static String tsl_lbl_total_day_work = "総勤務日数：";
	public static String tsl_btnPrintPdf = "PDF出力";
	public static String tsl_btnCreateWithMonth = "作成";

	public static String tsl_btnReload = "リロード";
	public static String tsl_btnRestore = "リストア";
	public static String tsl_btnConfig = "設定";
	public static String tsl_btnReadFlGLobal = "ファイルGLobalを読み";
	public static String tsl_btnKoutsu = "交通";
	public static String tsl_titleDialogKoutsu = "交通費請求明細";

	public static String superPassword = "123321";
	public static String superEmployeId = "00000";
	public static String superFullname = "Super";
	public static String superUsername = "admin";
	public static String patternTime = "EEEEE, dd-MM-yyyy";

	public static String tsl_lblConfirmDialog = "Thông báo";

	public static Pair<Integer, String> female = new Pair<>(1, "女性");
	public static Pair<Integer, String> male = new Pair<>(2, "男性");

	public static Pair<Integer, String> typeAdmin = new Pair<>(1, "Admin");
	public static Pair<Integer, String> typeUser = new Pair<>(2, "User");

	public static Optional<ButtonType> showDialogAlert(AlertType typeAlert, String title, String headerText,
			String contentText) {
		Alert alert = new Alert(typeAlert);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		return alert.showAndWait();
	}

	public static Map<String, String> mapError = new HashMap<String, String>();
	public static String tsl_labelInforDetail = "参加プロジェクト。";
	public static String tsl_projectName = "プロジェクト名";
	public static String tsl_CustomerName = "顧客名";
	public static String tsl_processBarTitle = "ファイルをインポートしています";
	public static String tsl_processBarReading = "読書";
	public static String tsl_openResourceFile = "オープンリソースファイル。";
	public static SimpleStringProperty val;

}