package br.com.vn.pd2;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class OCVCam implements CvCameraViewListener2 {

	private boolean useGray = false;

	public void setUseGray(boolean useGray) {
		this.useGray = true;
	}

	public void setUseHHSV(boolean useHHSV) {
		this.useGray = false;
	}

	// empty mask
	// private Mat empty;

	// parametros para cálculos do histograma
	// private MatOfInt channels;
	// private MatOfInt histSize;
	// private MatOfFloat ranges;

	@Override
	public void onCameraViewStarted(int width, int height) {

		// empty = new Mat();
		//
		// last = new Mat();
		// toShow = new Mat();
		//
		// // histMarcada = new Mat();
		// // histLast = new Mat();
		//
		// channels = new MatOfInt(0, 1);
		// histSize = new MatOfInt(50, 60);
		//
		// ranges = new MatOfFloat();
		// ranges.put(0, 0, 0.0f);
		// ranges.put(0, 1, 256.0f);
		// ranges.put(1, 0, 0.0f);
		// ranges.put(1, 1, 180.0f);

	}

	@Override
	public void onCameraViewStopped() {
		// releaseMat(empty);
		//
		// releaseMat(toShow);
		// releaseMat(capturada);
		// releaseMat(last);
		//
		// releaseMat(channels);
		// releaseMat(histSize);
		// releaseMat(ranges);
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		releaseMat(last);

		Mat current = inputFrame.rgba();

		Mat gradX = new Mat();
		Mat absGradX = new Mat();
		Mat gradY = new Mat();
		Mat absGradY = new Mat();
		Mat toUse = current.clone();

		if (useGray) {
			Imgproc.cvtColor(toUse, toUse, Imgproc.COLOR_RGBA2GRAY);
		} else {
			Imgproc.cvtColor(toUse, toUse, Imgproc.COLOR_RGBA2RGB);
			Imgproc.cvtColor(toUse, toUse, Imgproc.COLOR_RGB2HSV);
			List<Mat> canais = new ArrayList<>();
			Core.split(toUse, canais);

			releaseMat(toUse);
			toUse = canais.get(0);
			Imgproc.GaussianBlur(toUse, toUse, new Size(3, 3),
					Imgproc.BORDER_DEFAULT);

			releaseMat(canais.get(1));
			releaseMat(canais.get(2));

		}

		Imgproc.Sobel(toUse, gradX, CvType.CV_16S, 1, 0, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Core.convertScaleAbs(gradX, absGradX);

		Imgproc.Sobel(toUse, gradY, CvType.CV_16S, 0, 1, 3, 1, 0,
				Imgproc.BORDER_DEFAULT);
		Core.convertScaleAbs(gradY, absGradY);

		Mat grad = new Mat();

		Core.addWeighted(absGradX, 0.5, absGradY, 0.5, 0, grad);

		releaseMat(current);
		releaseMat(toUse);
		releaseMat(gradX);
		releaseMat(gradY);
		releaseMat(absGradX);
		releaseMat(absGradY);

		last = grad;

		return grad;
	}

	Mat last;

	public void catchImg() {

//		for (int i = 0; i < last.rows(); i++) {
//			for (int j = 0; j < last.cols(); j++) {
//				double[] cur = last.get(i, j);
//				StringBuffer sb = new StringBuffer();
//				for (int z = 0; z < cur.length; z++) {
//					sb.append(cur[z] + " | ");
//				}
//				Log.d("matriz", sb.toString());
//
//			}
//		}

	}

	private void releaseMat(Mat mat) {
		if (mat != null)
			mat.release();
	}

}
