package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.classes.CameraModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CameraView extends View{

	private CameraModel cameraModel;
	private Paint paint;
	private RectF rectangle;
	public CameraView(Context context) {
		super(context);
		setFocusable(true);
		cameraModel = CameraModel.getInstance();
		paint = new Paint();
		rectangle = new RectF();
	}
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		cameraModel = CameraModel.getInstance();
		paint = new Paint();
		rectangle = new RectF();
	}
	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFocusable(true);
		cameraModel = CameraModel.getInstance();
		paint = new Paint();
		rectangle = new RectF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		cameraModel = CameraModel.getInstance();
		float l = (cameraModel.getxMax() - cameraModel.getxMin()) / 6;
		float u = (cameraModel.getyMax() - cameraModel.getyMin()) / 6;
		float r = l*5;
		float d = u*5;
		rectangle.set(l, u, r, d);
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setAlpha(0x80);
		paint.setStyle(Paint.Style.STROKE); 
		paint.setStrokeWidth(4.5f);
		canvas.drawOval(rectangle, paint);
	}


}
