package br.com.vn.pd2;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.projeto2_segmentacao.R;

public class MainActivity extends Activity implements OnClickListener {

	private CameraBridgeViewBase mOpenCvCameraView;
	private OCVCam ocvCam = new OCVCam();

	private MenuItem mItemHHsv = null;
	private MenuItem mItemGray = null;

	@Override
	public void onClick(View v) {
		ocvCam.catchImg();
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}

	};

	public MainActivity() {
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setMaxFrameSize(640, 480);
		mOpenCvCameraView.setOnClickListener(this);
		mOpenCvCameraView.setCvCameraViewListener(ocvCam);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mItemHHsv = menu.add("H - HSV");
		mItemGray = menu.add("Gray Scale");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item == mItemHHsv) {
			ocvCam.setUseGray(false);
			Toast.makeText(this, "H - HSV", Toast.LENGTH_LONG).show();
		} else if (item == mItemGray) {
			ocvCam.setUseGray(true);
			Toast.makeText(this, "Gray", Toast.LENGTH_LONG).show();
		}

		return true;
	}

	public void onCameraViewStarted(int width, int height) {
	}

	public void onCameraViewStopped() {
	}

}
