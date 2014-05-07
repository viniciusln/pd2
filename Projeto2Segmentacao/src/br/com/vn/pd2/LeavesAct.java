package br.com.vn.pd2;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

public class LeavesAct extends Activity {

	private Bitmap src;

	private Bitmap gray;
	private Bitmap mask;

	private Bitmap resultSobel;
	private Bitmap hMasked;
	private Bitmap resultSobelGray;
	private Bitmap grayMasked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		src = BitmapFactory.decodeResource(getResources(), R.drawable.l10);
		gray = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		mask = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());

		resultSobel = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		hMasked = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		resultSobelGray = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		grayMasked = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());

		View view = new View(getApplicationContext()) {
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				int height = 0;
				canvas.drawBitmap(src, 0, height, null);
				// canvas.drawBitmap(gray, src.getWidth(), height, null);
				canvas.drawBitmap(mask, src.getWidth(), height, null);

				canvas.drawBitmap(hMasked, 0, height += mask.getHeight(), null);
				canvas.drawBitmap(resultSobel, hMasked.getWidth(), height, null);
				canvas.drawBitmap(grayMasked, 0,
						height += resultSobel.getHeight(), null);
				canvas.drawBitmap(resultSobelGray, resultSobelGray.getWidth(),
						height, null);
			}
		};
		setContentView(view);
	}

	private void onOCVLoad() {
		Mat grayMat = new Mat();
		Mat srcMat = new Mat();
		Utils.bitmapToMat(src, srcMat);
		Utils.bitmapToMat(src, grayMat);

		// //

		Imgproc.cvtColor(grayMat, grayMat, Imgproc.COLOR_RGBA2GRAY);

		// ---

		Mat maskMat = grayMat.clone();
		Imgproc.threshold(maskMat, maskMat, 100, 255, Imgproc.THRESH_OTSU);
		Core.bitwise_not(maskMat, maskMat);

		// ---
		Mat sobel = srcMat.clone();

		Imgproc.cvtColor(sobel, sobel, Imgproc.COLOR_RGBA2RGB);
		Imgproc.cvtColor(sobel, sobel, Imgproc.COLOR_RGB2HSV);
		List<Mat> canais = new ArrayList<>();
		Core.split(sobel, canais);

		Mat canalH = canais.get(0);
		Mat hMaskedMat = new Mat();
		Core.bitwise_and(maskMat, canalH, hMaskedMat);

		Mat resultSobelMat = aplicarSobel(hMaskedMat);

		// Mat resultSobelMatMasked = new Mat();

		// -----------

		Mat sobelGray = srcMat.clone();

		Imgproc.cvtColor(sobelGray, sobelGray, Imgproc.COLOR_RGBA2GRAY);
		Mat grayMaskedMat = new Mat();
		Core.bitwise_and(maskMat, sobelGray, grayMaskedMat);

		Mat resultSobelMatGray = aplicarSobel(grayMaskedMat);

		// //

		Utils.matToBitmap(resultSobelMatGray, resultSobelGray);
		Utils.matToBitmap(grayMaskedMat, this.grayMasked);
		Utils.matToBitmap(hMaskedMat, hMasked);
		Utils.matToBitmap(resultSobelMat, resultSobel);
		Utils.matToBitmap(grayMat, gray);
		Utils.matToBitmap(maskMat, mask);
	}

	private Mat aplicarSobel(Mat src) {
		Mat gradX = new Mat();
		Mat absGradX = new Mat();
		Mat gradY = new Mat();
		Mat absGradY = new Mat();

		Imgproc.Sobel(src, gradX, CvType.CV_16S, 1, 0, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Core.convertScaleAbs(gradX, absGradX);

		Imgproc.Sobel(src, gradY, CvType.CV_16S, 0, 1, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Core.convertScaleAbs(gradY, absGradY);

		Mat resultSobelMat = new Mat();
		Core.addWeighted(absGradX, 0.5, absGradY, 0.5, 0, resultSobelMat);

		gradX.release();
		absGradX.release();
		gradY.release();
		absGradY.release();

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
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

}
