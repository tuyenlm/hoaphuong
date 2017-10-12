package models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Kinmu {
	private final SimpleStringProperty user_cd;
	private final SimpleStringProperty ymd;
	private final SimpleStringProperty holiday_flg;
	private SimpleStringProperty p_wtime_s;
	private SimpleStringProperty p_wtime_e;
	private SimpleStringProperty p_kinmu_time;
	private SimpleStringProperty p_kintai_flg;
	private SimpleStringProperty p_basyo_no;
	private SimpleStringProperty wtime_s;
	private SimpleStringProperty wtime_e;
	private SimpleStringProperty kinmu_time;
	private final SimpleStringProperty kintai_flg;
	private SimpleStringProperty basyo_no;
	private SimpleStringProperty tekiyou;
	private SimpleStringProperty jpDay;
	private SimpleBooleanProperty detail;

	public Kinmu(String user_cd, String ymd, String holiday_flg, String p_wtime_s, String p_wtime_e, String p_kinmu_time, String p_kintai_flg, String p_basyo_no, String wtime_s, String wtime_e,
			String kinmu_time, String kintai_flg, String basyo_no, String tekiyou, String jpDay, boolean detail) {
		this.user_cd = new SimpleStringProperty(user_cd);
		this.ymd = new SimpleStringProperty(ymd);
		this.holiday_flg = new SimpleStringProperty(holiday_flg);
		this.p_wtime_s = new SimpleStringProperty(p_wtime_s);
		this.p_wtime_e = new SimpleStringProperty(p_wtime_e);
		this.p_kinmu_time = new SimpleStringProperty(p_kinmu_time);
		this.p_kintai_flg = new SimpleStringProperty(p_kintai_flg);
		this.p_basyo_no = new SimpleStringProperty(p_basyo_no);
		this.wtime_s = new SimpleStringProperty(wtime_s);
		this.wtime_e = new SimpleStringProperty(wtime_e);
		this.kinmu_time = new SimpleStringProperty(kinmu_time);
		this.kintai_flg = new SimpleStringProperty(kintai_flg);
		this.basyo_no = new SimpleStringProperty(basyo_no);
		this.tekiyou = new SimpleStringProperty(tekiyou);
		this.jpDay = new SimpleStringProperty(jpDay);
		this.detail = new SimpleBooleanProperty(detail);
	}

	public final SimpleStringProperty user_cdProperty() {
		return this.user_cd;
	}

	public final String getUser_cd() {
		return this.user_cdProperty().get();
	}

	public final void setUser_cd(final String user_cd) {
		this.user_cdProperty().set(user_cd);
	}

	public final SimpleStringProperty ymdProperty() {
		return this.ymd;
	}

	public final String getYmd() {
		return this.ymdProperty().get();
	}

	public final void setYmd(final String ymd) {
		this.ymdProperty().set(ymd);
	}

	public final SimpleStringProperty holiday_flgProperty() {
		return this.holiday_flg;
	}

	public final String getHoliday_flg() {
		return this.holiday_flgProperty().get();
	}

	public final void setHoliday_flg(final String holiday_flg) {
		this.holiday_flgProperty().set(holiday_flg);
	}

	public final SimpleStringProperty p_wtime_sProperty() {
		return this.p_wtime_s;
	}

	public final String getP_wtime_s() {
		return this.p_wtime_sProperty().get();
	}

	public final void setP_wtime_s(final String p_wtime_s) {
		this.p_wtime_sProperty().set(p_wtime_s);
	}

	public final SimpleStringProperty p_wtime_eProperty() {
		return this.p_wtime_e;
	}

	public final String getP_wtime_e() {
		return this.p_wtime_eProperty().get();
	}

	public final void setP_wtime_e(final String p_wtime_e) {
		this.p_wtime_eProperty().set(p_wtime_e);
	}

	public final SimpleStringProperty p_kinmu_timeProperty() {
		return this.p_kinmu_time;
	}

	public final String getP_kinmu_time() {
		return this.p_kinmu_timeProperty().get();
	}

	public final void setP_kinmu_time(final String p_kinmu_time) {
		this.p_kinmu_timeProperty().set(p_kinmu_time);
	}

	public final SimpleStringProperty p_kintai_flgProperty() {
		return this.p_kintai_flg;
	}

	public final String getP_kintai_flg() {
		return this.p_kintai_flgProperty().get();
	}

	public final void setP_kintai_flg(final String p_kintai_flg) {
		this.p_kintai_flgProperty().set(p_kintai_flg);
	}

	public final SimpleStringProperty p_basyo_noProperty() {
		return this.p_basyo_no;
	}

	public final String getP_basyo_no() {
		return this.p_basyo_noProperty().get();
	}

	public final void setP_basyo_no(final String p_basyo_no) {
		this.p_basyo_noProperty().set(p_basyo_no);
	}

	public final SimpleStringProperty wtime_sProperty() {
		return this.wtime_s;
	}

	public final String getWtime_s() {
		return this.wtime_sProperty().get();
	}

	public final void setWtime_s(final String wtime_s) {
		this.wtime_sProperty().set(wtime_s);
	}

	public final SimpleStringProperty wtime_eProperty() {
		return this.wtime_e;
	}

	public final String getWtime_e() {
		return this.wtime_eProperty().get();
	}

	public final void setWtime_e(final String wtime_e) {
		this.wtime_eProperty().set(wtime_e);
	}

	public final SimpleStringProperty kinmu_timeProperty() {
		return this.kinmu_time;
	}

	public final String getKinmu_time() {
		return this.kinmu_timeProperty().get();
	}

	public final void setKinmu_time(final String kinmu_time) {
		this.kinmu_timeProperty().set(kinmu_time);
	}

	public final SimpleStringProperty kintai_flgProperty() {
		return this.kintai_flg;
	}

	public final String getKintai_flg() {
		return this.kintai_flgProperty().get();
	}

	public final void setKintai_flg(final String kintai_flg) {
		this.kintai_flgProperty().set(kintai_flg);
	}

	public final SimpleStringProperty basyo_noProperty() {
		return this.basyo_no;
	}

	public final String getBasyo_no() {
		return this.basyo_noProperty().get();
	}

	public final void setBasyo_no(final String basyo_no) {
		this.basyo_noProperty().set(basyo_no);
	}

	public final SimpleStringProperty tekiyouProperty() {
		return this.tekiyou;
	}

	public final String getTekiyou() {
		return this.tekiyouProperty().get();
	}

	public final void setTekiyou(final String tekiyou) {
		this.tekiyouProperty().set(tekiyou);
	}

	public final SimpleStringProperty jpDayProperty() {
		return this.jpDay;
	}

	public final String getJpDay() {
		return this.jpDayProperty().get();
	}

	public final void setJpDay(final String jpDay) {
		this.jpDayProperty().set(jpDay);
	}

	public final SimpleBooleanProperty detailProperty() {
		return this.detail;
	}
	

	public final boolean isDetail() {
		return this.detailProperty().get();
	}
	

	public final void setDetail(final boolean detail) {
		this.detailProperty().set(detail);
	}
	

}
