package br.com.vn.pd2;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.example.projeto2_segmentacao.R;

public class LeavesAct extends Activity {

	private Bitmap bmp;
	private Rect r = new Rect(0, 0, 0, 0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.leaves);

		View view = new View(getApplicationContext()) {
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				r.set(0, 0, canvas.getWidth(), canvas.getHeight());
				canvas.drawBitmap(bmp, r, r, null);
			}
		};
		setContentView(view);
	}

	private void onOCVLoad() {
		Mat matLeaves = new Mat();
		Utils.bitmapToMat(bmp, matLeaves);

		// //
		Imgproc.cvtColor(matLeaves, matLeaves, Imgproc.COLOR_RGBA2GRAY);
		Imgproc.threshold(matLeaves, matLeaves, 100, 255, Imgproc.THRESH_OTSU);
		// //

		Utils.matToBitmap(matLeaves, bmp);
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
