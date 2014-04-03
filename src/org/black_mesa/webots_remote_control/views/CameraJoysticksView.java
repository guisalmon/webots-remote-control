package org.black_mesa.webots_remote_control.views;

import org.black_mesa.webots_remote_control.listeners.CameraJoysticksViewListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author Cassim Ketfi
 * 
 */
public class CameraJoysticksView extends View implements CameraJoysticksViewListener {
	// Left Joystick
	private float centerXMainCircleLeft = 0; // Center view x position
	private float centerYMainCircleLeft = 0; // Center view y position
	private float centerXSecondaryCircleLeft = 0; // Center view x position
	private float centerYSecondaryCircleLeft = 0; // Center view y position
	// Right Joystick
	private float centerXMainCircleRight = 0; // Center view x position
	private float centerYMainCircleRight = 0; // Center view y position
	private float centerXSecondaryCircleRight = 0; // Center view x position
	private float centerYSecondaryCircleRight = 0; // Center view y position

	private Paint mainCircle;
	private Paint secondaryCircle;
	private Paint horizontalLine;
	private Paint verticalLine;
	private float joystickRadiusLeft;
	private float joystickRadiusRight;

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
		mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainCircle.setColor(Color.WHITE);
		mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

		secondaryCircle = new Paint();
		secondaryCircle.setColor(Color.GREEN);
		secondaryCircle.setStyle(Paint.Style.STROKE);

		verticalLine = new Paint();
		verticalLine.setStrokeWidth(5);
		verticalLine.setColor(Color.RED);

		horizontalLine = new Paint();
		horizontalLine.setStrokeWidth(2);
		horizontalLine.setColor(Color.BLACK);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawJoystick(canvas, centerXMainCircleLeft, centerYMainCircleLeft, centerXSecondaryCircleLeft,
				centerYSecondaryCircleLeft, joystickRadiusLeft);
		drawJoystick(canvas, centerXMainCircleRight, centerYMainCircleRight, centerXSecondaryCircleRight,
				centerYSecondaryCircleRight, joystickRadiusRight);

	}

	private void drawJoystick(Canvas canvas, float centerXMainCircle, float centerYMainCircle,
			float centerXSecondaryCircle, float centerYSecondaryCircle, float joystickRadius) {
		// painting the main circle
		canvas.drawCircle(centerXMainCircle, centerYMainCircle, joystickRadius, mainCircle);
		// painting the secondary circle
		canvas.drawCircle(centerXSecondaryCircle, centerYSecondaryCircle, joystickRadius / 2, secondaryCircle);
		// paint lines
		canvas.drawLine(centerXMainCircle, centerYMainCircle, centerXMainCircle, (centerYMainCircle - joystickRadius),
				verticalLine);
		canvas.drawLine((centerXMainCircle - joystickRadius), centerYMainCircle, (centerXMainCircle + joystickRadius),
				centerYMainCircle, horizontalLine);
		canvas.drawLine(centerXMainCircle, (centerYMainCircle + joystickRadius), centerXMainCircle, centerYMainCircle,
				horizontalLine);
	}

	@Override
	public void onJoystickLeftCoordinateChanged(float centerXJoystickLeft, float centerYJoystickLeft,
			float joystickRadiusJoystickLeft) {
		this.centerXMainCircleLeft = centerXJoystickLeft;
		this.centerYMainCircleLeft = centerYJoystickLeft;
		this.centerXSecondaryCircleLeft = centerXJoystickLeft;
		this.centerYSecondaryCircleLeft = centerYJoystickLeft;
		this.joystickRadiusLeft = joystickRadiusJoystickLeft;
		this.invalidate();
	}

	@Override
	public void onJoystickRightCoordinateChanged(float centerXJoystickRight, float centerYJoystickRight,
			float joystickRadiusJoystickRight) {
		this.centerXMainCircleRight = centerXJoystickRight;
		this.centerYMainCircleRight = centerYJoystickRight;
		this.centerXSecondaryCircleRight = centerXJoystickRight;
		this.centerYSecondaryCircleRight = centerYJoystickRight;
		this.joystickRadiusRight = joystickRadiusJoystickRight;
		this.invalidate();
	}

}
