package org.webots_remote_control.views;

import org.black_mesa.webots_remote_control.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CameraView extends View {

	private Paint paint;
	private RectF rectangle;
	private Bitmap bitmapUp;
	private Bitmap bitmapLeft;
	private Bitmap bitmapRight;
	private Bitmap bitmapDown;

	public CameraView(Context context) {
		super(context);
		commonConstructor();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonConstructor();
	}

	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		commonConstructor();
	}

	private void commonConstructor() {
		setFocusable(true);
		paint = new Paint();
		rectangle = new RectF();
		bitmapUp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_up);
		bitmapLeft = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_left);
		bitmapRight = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_right);
		bitmapDown = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_down);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		float l = getWidth() / 6;
		float u = getHeight() / 6;
		float r = l * 5;
		float d = u * 5;
		rectangle.set(l, u, r, d);
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setAlpha(0x80);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4.5f);
		canvas.drawOval(rectangle, paint);
		canvas.drawBitmap(bitmapUp, (getWidth() - bitmapUp.getWidth()) / 2, 0, paint);
		canvas.drawBitmap(bitmapLeft, 0, (getHeight() - bitmapLeft.getHeight()) / 2, paint);
		canvas.drawBitmap(bitmapRight, getWidth() - bitmapRight.getWidth(), (getHeight() - bitmapLeft.getHeight()) / 2,
				paint);
		canvas.drawBitmap(bitmapDown, (getWidth() - bitmapDown.getWidth()) / 2, getHeight() - bitmapDown.getHeight(),
				paint);
	}

}
