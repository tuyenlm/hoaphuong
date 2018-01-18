package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class BarcodeController {

	public static File renderBarcode(String barcode) {
		File outputFile = null;
		try {
			if (!barcode.isEmpty()) {
				Code39Bean bean = new Code39Bean();
				final int dpi = 203;
				bean.setModuleWidth(UnitConv.in2mm(0.5f / dpi));
				bean.setWideFactor(3);
				bean.doQuietZone(true);
				File fullPathToSubfolder = new File("barImg");
				if (!fullPathToSubfolder.exists()) {
					fullPathToSubfolder.mkdir();
				}
				outputFile = new File("barImg/" + barcode + ".png");
				OutputStream out = new FileOutputStream(outputFile);
				try {
					BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/x-png", dpi,
							BufferedImage.TYPE_BYTE_BINARY, false, 0);
					bean.generateBarcode(canvas, barcode);
					canvas.finish();
				} finally {
					out.close();
				}
				FileInputStream fis = new FileInputStream(outputFile);
				fis.close();
			}
		} catch (Exception e) {
			Logger.getLogger(BarcodeController.class.getName()).log(Level.SEVERE, null, e);
		}
		return outputFile;
	}
}
