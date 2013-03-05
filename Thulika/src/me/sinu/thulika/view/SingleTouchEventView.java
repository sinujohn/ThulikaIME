/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SingleTouchEventView extends View {
	private Paint paint = new Paint();
	private Path path = new Path();
	private final String TAG = "LOG";
	
	private final String touchupDelayKey = "me.sinu.stupidpen.touchupDelay";
	private final String stupidpenPrefs = "me.sinu.stupidpen";
	private long delay;
	
	protected boolean hideView = false;
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	Handler h2 = new Handler();
	   Runnable run = new Runnable() {

	        @Override
	        public void run() {
	        	onTouchUp();
	        }
	    };

	public SingleTouchEventView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		
		SharedPreferences prefs = this.getContext().getSharedPreferences(stupidpenPrefs, Context.MODE_PRIVATE);
		delay = prefs.getLong(touchupDelayKey, 100);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(eventX, eventY);
			
			h2.removeCallbacks(run);
			return true;
		case MotionEvent.ACTION_MOVE:
			path.lineTo(eventX, eventY);
			break;
		case MotionEvent.ACTION_UP:
			h2.postDelayed(run, delay);
			if(hideView) {
				return false;
			}
			break;
		default:
			return false;
		}

		// Schedules a repaint.
		invalidate();
		return true;
	}
	
	protected void onTouchUp() {
		//do nothing
	}

	public int[] getImagePixels() {
		Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		this.draw(canvas);
		int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, this.getWidth(), 0, 0, this.getWidth(), this.getHeight());
		return pixels;
		/*for(int i=0;i<this.getHeight(); i++) {
			String str="";
			for(int j=0; j<this.getWidth(); j++) {
				str = str+pixels[(i*this.getWidth())+j];
			}
			Log.i(TAG, str);
			
		}*/
		// compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new
		// File("D:/tt.jpg")));
	}
	
	public void clear() {
		path.reset();
		postInvalidate();
	}
	
	public boolean isEmpty() {
		return path.isEmpty();
	}
	
	public boolean isSmallPath() {
		PathMeasure pm = new PathMeasure(path, false);
		if(pm.getLength()<5) {
			return true;
		}
		return false;
	}
}