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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class LeavesAct extends Activity {

	private Bitmap src;

	private Config config = Config.RGB_565;

	ImageGrid view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		src = BitmapFactory.decodeResource(getResources(), R.drawable.l10);

		view = new ImageGrid(getApplicationContext(), 3);
		view.addBitmap(src);

		setContentView(view);
	}

	private void onOCVLoad() {

		view.addBitmap(src);

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
		view.addBitmap(toBmp(maskMat));

		// ---
		Mat hsv = srcMat.clone();

		Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_RGBA2RGB);
		Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_RGB2HSV);
		List<Mat> canais = new ArrayList<>();
		Core.split(hsv, canais);

		Mat canalH = canais.get(0);
		Mat hMaskedMat = new Mat();
		Core.bitwise_and(maskMat, canalH, hMaskedMat);

		view.addBitmap(toBmp(hMaskedMat));
		releaseMat(hsv, canais.get(1), canais.get(2));

		// ---

		Mat resultSobelMat = aplicarSobel(hMaskedMat);
		view.addBitmap(toBmp(resultSobelMat));
		releaseMat(resultSobelMat);

		// -----------

		Mat toCanny = canalH.clone();
		Imgproc.blur(toCanny, toCanny, new Size(3, 3));
		Imgproc.Canny(toCanny, toCanny, 5, 15);
		Core.bitwise_and(maskMat, toCanny, toCanny);
		view.addBitmap(toBmp(toCanny));
		releaseMat(hMaskedMat, toCanny, canalH);

		releaseMat(srcMat, grayMat);

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
