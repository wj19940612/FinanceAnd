package com.sbai.finance.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * Created by lixiaokuan0819 on 2017/5/17.
 */

public class QRCodeUtil {
	/**
	 * 生成二维码
	 *
	 * @param text 需要生成二维码的文字、网址等
	 * @param size 需要生成二维码的大小（）
	 * @return bitmap
	 */
	public static Bitmap createQRCode(String text, int size) {
		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			//hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
            //hints.put(EncodeHintType.MARGIN, String.valueOf(0)); //default is 4

			BitMatrix bitMatrix = new QRCodeWriter().encode(text,
					BarcodeFormat.QR_CODE, size, size, hints);
			int[] pixels = new int[size * size];
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * size + x] = 0xff000000;
					} else {
						pixels[y * size + x] = 0xffffffff;
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(size, size,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}
}
