package br.com.vn.pd2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ImageGrid extends View {

	private int cols;
	private List<Bitmap> bitmaps = new ArrayList<>();

	public ImageGrid(Context context) {
		super(context);
	}

	public ImageGrid(Context context, int cols) {
		super(context);
		this.cols = cols;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = 0;
		int width = 0;
		int col = 0;

		for (Bitmap bmp : bitmaps) {
			int maxHeight = Integer.MIN_VALUE;
			if (bmp != null) {
				if (col >= cols) {
					col = 0;
					width = 0;
					height += bmp.getHeight();
					maxHeight = Integer.MIN_VALUE;
				}
				canvas.drawBitmap(bmp, width, height, null);
				if (bmp.getHeight() > maxHeight) {
					maxHeight = bmp.getHeight();
				}
				width += bmp.getWidth();
				col++;
			}
		}
	}

	public Bitmap thisViewToBitmap() {
		View view = this;
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.WHITE);
		view.draw(canvas);
		return returnedBitmap;
	}

	public void addBitmap(Bitmap... bmps) {
		for (Bitmap bmp : bmps) {
			if (bmp != null) {
				bitmaps.add(bmp);
				this.invalidate();
			}
		}
	}

	public void clear() {
		bitmaps.clear();
		this.invalidate();
	}
}
