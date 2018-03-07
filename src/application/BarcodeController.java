package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class BarcodeController {

	public static File renderBarcode(String barcode, boolean isE13) {
		File outputFile = null;
		try {
			if (!barcode.isEmpty()) {
				if (isE13) {
					try {
						EAN13Bean e13 = new EAN13Bean();
						int resolution = 160;
						e13.setModuleWidth(UnitConv.in2mm(1.7f / resolution));
						e13.setBarHeight(8);
						e13.setFontSize(2);
						e13.setQuietZone(3.5);
						outputFile = new File("barImg/" + barcode + ".png");
						OutputStream out2 = new FileOutputStream(outputFile);
						BitmapCanvasProvider canvas3 = new BitmapCanvasProvider(out2, "image/x-png", resolution,
								BufferedImage.TYPE_BYTE_BINARY, false, 0);
						e13.generateBarcode(canvas3, barcode);
						canvas3.finish();
						out2.close();
					} catch (Exception e) {
						Logger.getLogger(BarcodeController.class.getName()).log(Level.SEVERE, null, e);
					}

				} else {
					Code39Bean bean = new Code39Bean();
					final int dpi = 203;
					bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));
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
				}

				FileInputStream fis = new FileInputStream(outputFile);
				fis.close();
			}
		} catch (Exception e) {
			Logger.getLogger(BarcodeController.class.getName()).log(Level.SEVERE, null, e);
		}
		return outputFile;
	}

	@SuppressWarnings("static-access")
	public static String calculateCodeWithcheckSum(String codigo) {
		EAN13Bean generator = new EAN13Bean();
		UPCEANLogicImpl impl = generator.createLogicImpl();
		codigo += impl.calcChecksum(codigo);
		return codigo;
	}
}
