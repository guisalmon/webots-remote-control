package org.black_mesa.webots_remote_control.views;

import org.black_mesa.webots_remote_control.activities.MainActivity;
import org.black_mesa.webots_remote_control.listeners.CameraJoysticksViewListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author Cassim Ketfi
 * 
 */
public class CameraJoysticksView extends View implements CameraJoysticksViewListener {
	// Left Joystick
	private float mCenterXMainCircleLeft = 0; // Center view x position
	private float mCenterYMainCircleLeft = 0; // Center view y position
	private float mCenterXSecondaryCircleLeft = 0; // Center view x position
	private float mCenterYSecondaryCircleLeft = 0; // Center view y position
	// Right Joystick
	private float mCenterXMainCircleRight = 0; // Center view x position
	private float mCenterYMainCircleRight = 0; // Center view y position
	private float mCenterXSecondaryCircleRight = 0; // Center view x position
	private float mCenterYSecondaryCircleRight = 0; // Center view y position

	private Paint mMainCircle;
	private Paint mSecondaryCircle;
	private Paint mHorizontalLine;
	private Paint mVerticalLine;
	private float mJoystickRadiusLeft;
	private float mJoystickRadiusRight;

	public CameraJoysticksView(Context context) {
		super(context);
		initPaints();

	}

	public CameraJoysticksView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaints();
	}

	public CameraJoysticksView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaints();
	}

	protected void initPaints() {
		mMainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMainCircle.setColor(Color.WHITE);
		mMainCircle.setStyle(Paint.Style.FILL_AND_STROKE);
		mSecondaryCircle = new Paint();
		mSecondaryCircle.setColor(Color.GREEN);
		mSecondaryCircle.setStyle(Paint.Style.STROKE);

		mVerticalLine = new Paint();
		mVerticalLine.setStrokeWidth(5);
		mVerticalLine.setColor(Color.RED);

		mHorizontalLine = new Paint();
		mHorizontalLine.setStrokeWidth(2);
		mHorizontalLine.setColor(Color.BLACK);
		Log.d("DERP", "DURRR");
		MainActivity.CAMERAS_MANAGER.registerV2(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawJoystick(canvas, mCenterXMainCircleLeft, mCenterYMainCircleLeft, mCenterXSecondaryCircleLeft,
				mCenterYSecondaryCircleLeft, mJoystickRadiusLeft);
		drawJoystick(canvas, mCenterXMainCircleRight, mCenterYMainCircleRight, mCenterXSecondaryCircleRight,
				mCenterYSecondaryCircleRight, mJoystickRadiusRight);

	}

	private void drawJoystick(Canvas canvas, float centerXMainCircle, float centerYMainCircle,
			float centerXSecondaryCircle, float centerYSecondaryCircle, float joystickRadius) {
		// painting the main circle
		canvas.drawCircle(centerXMainCircle, centerYMainCircle, joystickRadius, mMainCircle);
		// painting the secondary circle
		canvas.drawCircle(centerXSecondaryCircle, centerYSecondaryCircle, joystickRadius / 2, mSecondaryCircle);
		// paint lines
		canvas.drawLine(centerXMainCircle, centerYMainCircle, centerXMainCircle, (centerYMainCircle - joystickRadius),
				mVerticalLine);
		canvas.drawLine((centerXMainCircle - joystickRadius), centerYMainCircle, (centerXMainCircle + joystickRadius),
				centerYMainCircle, mHorizontalLine);
		canvas.drawLine(centerXMainCircle, (centerYMainCircle + joystickRadius), centerXMainCircle, centerYMainCircle,
				mHorizontalLine);
	}

	@Override
	public void onJoystickLeftCoordinateChanged(float centerXJoystickLeft, float centerYJoystickLeft,
			float joystickRadiusJoystickLeft) {
		this.mCenterXMainCircleLeft = centerXJoystickLeft;
		this.mCenterYMainCircleLeft = centerYJoystickLeft;
		this.mCenterXSecondaryCircleLeft = centerXJoystickLeft;
		this.mCenterYSecondaryCircleLeft = centerYJoystickLeft;
		this.mJoystickRadiusLeft = joystickRadiusJoystickLeft;
		this.invalidate();
	}

	@Override
	public void onJoystickRightCoordinateChanged(float centerXJoystickRight, float centerYJoystickRight,
			float joystickRadiusJoystickRight) {
		this.mCenterXMainCircleRight = centerXJoystickRight;
		this.mCenterYMainCircleRight = centerYJoystickRight;
		this.mCenterXSecondaryCircleRight = centerXJoystickRight;
		this.mCenterYSecondaryCircleRight = centerYJoystickRight;
		this.mJoystickRadiusRight = joystickRadiusJoystickRight;
		this.invalidate();
	}

}