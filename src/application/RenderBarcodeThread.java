package application;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RenderBarcodeThread implements Runnable {
	public Thread t;
	private String _barcode;

	public RenderBarcodeThread(String barcode) {
		_barcode = barcode;
	}

	@Override
	public void run() {
		try {
			File f = new File("barImg/" + _barcode + ".png");
			if (!f.exists()) {
				BarcodeController.renderBarcode(_barcode);
			}
		} catch (Exception e) {
			Logger.getLogger(RenderBarcodeThread.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, _barcode);
			t.start();
		}
	}
}
