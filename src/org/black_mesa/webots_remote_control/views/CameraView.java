package org.black_mesa.webots_remote_control.views;

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
	private static final int DIV_HOR = 6;
	private static final int DIV_VER = 6;
	private static final int ALPHA = 0x80;
	private static final float STROKE_WIDTH = 4.5F;

	private Paint mPaint;
	private RectF mRectangle;
	private Bitmap mBitmapUp;
	private Bitmap mBitmapLeft;
	private Bitmap mBitmapRight;
	private Bitmap mBitmapDown;
	private float mL;
	private float mU;
	private float mR;
	private float mD;

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
		mPaint = new Paint();
		mRectangle = new RectF();
		mBitmapUp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_up);
		mBitmapLeft = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_left);
		mBitmapRight = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_right);
		mBitmapDown = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_down);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mL = getWidth() / DIV_HOR;
		mU = getHeight() / DIV_VER;
		mR = mL * 5;
		mD = mU * 5;
		mRectangle.set(mL, mU, mR, mD);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
		mPaint.setAlpha(ALPHA);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(STROKE_WIDTH);
		canvas.drawOval(mRectangle, mPaint);
		canvas.drawBitmap(mBitmapUp, (getWidth() - mBitmapUp.getWidth()) / 2, 0, mPaint);
		canvas.drawBitmap(mBitmapLeft, 0, (getHeight() - mBitmapLeft.getHeight()) / 2, mPaint);
		canvas.drawBitmap(mBitmapRight, getWidth() - mBitmapRight.getWidth(),
				(getHeight() - mBitmapLeft.getHeight()) / 2, mPaint);
		canvas.drawBitmap(mBitmapDown, (getWidth() - mBitmapDown.getWidth()) / 2,
				getHeight() - mBitmapDown.getHeight(), mPaint);
	}
}
