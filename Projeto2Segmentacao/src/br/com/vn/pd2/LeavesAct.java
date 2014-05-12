package br.com.vn.pd2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class LeavesAct extends Activity {

	private Bitmap src;

	private Config config = Config.RGB_565;

	private ImageGrid view;
	private int leafNumber = 1;

	private HashMap<Integer, Integer> leaves = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new ImageGrid(getApplicationContext(), 2);

		leaves.put(1, R.drawable.l01);
		leaves.put(2, R.drawable.l02);
		leaves.put(3, R.drawable.l03);
		leaves.put(4, R.drawable.l04);
		leaves.put(5, R.drawable.l05);
		leaves.put(6, R.drawable.l06);
		leaves.put(7, R.drawable.l07);
		leaves.put(8, R.drawable.l08);
		leaves.put(9, R.drawable.l09);
		leaves.put(10, R.drawable.l10);
		leaves.put(11, R.drawable.l11);
		leaves.put(12, R.drawable.l12);
		leaves.put(13, R.drawable.l13);
		leaves.put(14, R.drawable.l14);
		leaves.put(15, R.drawable.l15);
		leaves.put(16, R.drawable.l16);
		leaves.put(17, R.drawable.l17);
		leaves.put(18, R.drawable.l18);
		leaves.put(19, R.drawable.l19);
		leaves.put(20, R.drawable.l20);
		leaves.put(21, R.drawable.l21);
		leaves.put(22, R.drawable.l22);
		leaves.put(23, R.drawable.l23);
		leaves.put(24, R.drawable.l24);
		leaves.put(25, R.drawable.l25);
		leaves.put(26, R.drawable.l26);
		leaves.put(27, R.drawable.l27);
		leaves.put(28, R.drawable.l28);
		leaves.put(29, R.drawable.l29);
		leaves.put(30, R.drawable.l30);
		leaves.put(31, R.drawable.l31);
		leaves.put(32, R.drawable.l32);

		setContentView(view);
	}

	private void onOCVLoad() {
		for (Entry<Integer, Integer> l : leaves.entrySet()) {
			src = BitmapFactory.decodeResource(getResources(), l.getValue());
			leafNumber = l.getKey();
			List<Integer> counting = doIt();
			final Integer total = counting.get(0);
			final Integer leaf = counting.get(1);
			final Integer lesions = total - counting.get(2);
			Log.d("contagem", "Folha " + leafNumber + ": " + total + "/"
					+ lesions + "/" + leaf + "="
					+ ((double) lesions / (double) leaf));
		}
	}

	private List<Integer> doIt() {
		List<Integer> retorno = new ArrayList<>();
		view.clear();

		view.addBitmap(src);

		save(src, intToString(leafNumber, 2), intToString(1, 2));

		Mat grayMat = new Mat();
		Mat srcMat = new Mat();
		Utils.bitmapToMat(src, srcMat);
		Utils.bitmapToMat(src, grayMat);

		// //

		Imgproc.cvtColor(grayMat, grayMat, Imgproc.COLOR_RGBA2GRAY);

		// ---

		Mat maskMat = grayMat.clone();

		Imgproc.blur(maskMat, maskMat, new Size(5, 5));
		Imgproc.threshold(maskMat, maskMat, 100, 255, Imgproc.THRESH_OTSU);
		Core.bitwise_not(maskMat, maskMat);

		Mat test = maskMat.clone();
		test.setTo(new Scalar(1));
		retorno.add(Core.countNonZero(test));
		retorno.add(Core.countNonZero(maskMat));

		final Bitmap maskMatBmp = toBmp(maskMat);
		view.addBitmap(maskMatBmp);
		save(maskMatBmp, intToString(leafNumber, 2), intToString(2, 2));

		// ---
		Mat hsv = srcMat.clone();

		Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_RGBA2RGB);
		Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_RGB2HSV);
		List<Mat> canais = new ArrayList<>();
		Core.split(hsv, canais);

		Mat canalH = canais.get(0);
		final Bitmap canalHBmp = toBmp(canalH);
		view.addBitmap(canalHBmp);
		save(canalHBmp, intToString(leafNumber, 2), intToString(3, 2));

		// --

		Mat hMaskedMat = new Mat();
		Core.bitwise_and(maskMat, canalH, hMaskedMat);

		final Bitmap hMaskedMatBmp = toBmp(hMaskedMat);
		view.addBitmap(hMaskedMatBmp);
		save(hMaskedMatBmp, intToString(leafNumber, 2), intToString(4, 2));

		// ---

		Mat resultSobelMat3 = aplicarSobel(hMaskedMat, 3);
		final Bitmap resultSobelMat3Bmp = toBmp(resultSobelMat3);
		view.addBitmap(resultSobelMat3Bmp);
		save(resultSobelMat3Bmp, intToString(leafNumber, 2), intToString(5, 2));

		// ---

		Mat resultSobelMat5 = aplicarSobel(hMaskedMat, 5);
		final Bitmap resultSobelMat5Bmp = toBmp(resultSobelMat5);
		view.addBitmap(resultSobelMat5Bmp);
		save(resultSobelMat5Bmp, intToString(leafNumber, 2), intToString(6, 2));

		// ---

		Mat toCanny = hMaskedMat.clone();
		Imgproc.blur(toCanny, toCanny, new Size(3, 3));
		Imgproc.Canny(toCanny, toCanny, 5, 10);
		Core.bitwise_and(maskMat, toCanny, toCanny);
		final Bitmap toCannyBmp = toBmp(toCanny);
		view.addBitmap(toCannyBmp);
		save(resultSobelMat5Bmp, intToString(leafNumber, 2), intToString(7, 2));

		// ---

		Mat canalV = canais.get(2);
		final Bitmap canalVBmp = toBmp(canalV);
		view.addBitmap(canalVBmp);
		save(canalVBmp, intToString(leafNumber, 2), intToString(8, 2));

		// ---

		Mat vMaskedMat = new Mat();
		Core.bitwise_and(maskMat, canalV, vMaskedMat);
		final Bitmap vMaskedMatBmp = toBmp(vMaskedMat);
		view.addBitmap(vMaskedMatBmp);
		save(vMaskedMatBmp, intToString(leafNumber, 2), intToString(9, 2));

		// ---

		Imgproc.blur(vMaskedMat, vMaskedMat, new Size(5, 5));
		Imgproc.threshold(vMaskedMat, vMaskedMat, 120, 255,
				Imgproc.THRESH_BINARY_INV);
		Core.bitwise_xor(maskMat, vMaskedMat, vMaskedMat);
		final Bitmap vMaskedMatBmp2 = toBmp(vMaskedMat);
		retorno.add(Core.countNonZero(vMaskedMat));
		view.addBitmap(vMaskedMatBmp2);
		save(vMaskedMatBmp2, intToString(leafNumber, 2), intToString(10, 2));

		releaseMat(resultSobelMat3, resultSobelMat5);
		releaseMat(hMaskedMat, vMaskedMat, toCanny, canalH);
		releaseMat(hsv, canais.get(1), canalV);
		releaseMat(srcMat, grayMat);
		return retorno;
	}

	private static String intToString(int num, int digits) {
		assert digits > 0 : "Invalid number of digits";

		// create variable length array of zeros
		char[] zeros = new char[digits];
		Arrays.fill(zeros, '0');
		// format number as String
		DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

		return df.format(num);
	}

	private void save(Bitmap bmp, String leafNumber, String stepNumber) {
		String file_path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/pd2";
		File dir = new File(file_path);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(dir, leafNumber + "-" + stepNumber + ".png");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (FileNotFoundException e) {
			Toast.makeText(this.getApplicationContext(),
					"falha ao salvar o arquivo", Toast.LENGTH_SHORT).show();
			Log.e("arquivo", "falha ao salvar o arquivo", e);
		} catch (IOException e) {
			Toast.makeText(this.getApplicationContext(),
					"falha ao salvar o arquivo", Toast.LENGTH_SHORT).show();
			Log.e("arquivo", "falha ao salvar o arquivo", e);
		} finally {
			try {
				if (fOut != null)
					fOut.close();
			} catch (IOException ignored) {
			}
		}

	}

	private Bitmap toBmp(Mat mat) {
		Bitmap retorno = Bitmap.createBitmap(mat.cols(), mat.rows(), config);
		Utils.matToBitmap(mat, retorno);
		return retorno;
	}

	private void releaseMat(Mat mat) {
		if (mat != null)
			mat.release();
	}

	private void releaseMat(Mat... mats) {
		for (Mat m : mats) {
			releaseMat(m);
		}
	}

	private Mat aplicarSobel(Mat src, int kernelSize) {
		Mat gradX = new Mat();
		Mat absGradX = new Mat();
		Mat gradY = new Mat();
		Mat absGradY = new Mat();

		Imgproc.Sobel(src, gradX, CvType.CV_16S, 1, 0, kernelSize, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Core.convertScaleAbs(gradX, absGradX);

		Imgproc.Sobel(src, gradY, CvType.CV_16S, 0, 1, kernelSize, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Core.convertScaleAbs(gradY, absGradY);

		Mat resultSobelMat = new Mat();
		Core.addWeighted(absGradX, 0.5, absGradY, 0.5, 0, resultSobelMat);

		releaseMat(gradY, gradY, absGradX, absGradY);

		return resultSobelMat;
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				onOCVLoad();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}

	};

	@Override
	public void onResume() {
		view.clear();
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

}
