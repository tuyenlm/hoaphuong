package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import modules.HomeController;

public class PrintThread implements Runnable {
	private Thread t;
	private int _billId;

	public PrintThread(int billId) {
		_billId = billId;
	}

	@Override
	public void run() {
		try {
			File file = HomeController.exportFile(_billId);
			Desktop.getDesktop().print(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.getLogger(PrintThread.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, String.valueOf(_billId));
			t.start();
		}
	}
}
