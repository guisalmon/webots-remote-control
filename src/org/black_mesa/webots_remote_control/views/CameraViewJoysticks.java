package org.black_mesa.webots_remote_control.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CameraViewJoysticks extends View {
	// Left Joystick
	private int centerXMainCircleLeft = 0; // Center view x position
	private int centerYMainCircleLeft = 0; // Center view y position
	private int centerXSecondaryCircleLeft = 0; // Center view x position
	private int centerYSecondaryCircleLeft = 0; // Center view y position
	// Right Joystick
	private int centerXMainCircleRight = 0; // Center view x position
	private int centerYMainCircleRight = 0; // Center view y position
	private int centerXSecondaryCircleRight = 0; // Center view x position
	private int centerYSecondaryCircleRight = 0; // Center view y position
	private Paint mainCircle;
	private Paint secondaryCircle;
	private Paint horizontalLine;
	private Paint verticalLine;
	private int joystickRadiusLeft;
	private int joystickRadiusRight;

	public CameraViewJoysticks(Context context) {
		super(context);
		initPaints();
	}

	public CameraViewJoysticks(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaints();
	}

	public CameraViewJoysticks(Context context, AttributeSet attrs, int defStyleAttr) {
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
		canvas.drawLine((float) centerXMainCircle, (float) centerYMainCircle, (float) centerXMainCircle,
				(float) (centerYMainCircle - joystickRadius), verticalLine);
		canvas.drawLine((float) (centerXMainCircle - joystickRadius), (float) centerYMainCircle,
				(float) (centerXMainCircle + joystickRadius), (float) centerYMainCircle, horizontalLine);
		canvas.drawLine((float) centerXMainCircle, (float) (centerYMainCircle + joystickRadius),
				(float) centerXMainCircle, (float) centerYMainCircle, horizontalLine);
	}

	/**
	 * Do this operation for two joysticks : Create an area of given radius and
	 * initialize a joystick in the center with radius half of the given radius.
	 * 
	 * @param centerXJoystickLeft
	 *            coordinate on axis X for the center of the left joystick
	 * @param centerYJoystickLeft
	 *            coordinate on axis Y for the center of the left joystick
	 * @param joystickRadiusJoystickLeft
	 *            radius of the area of the left joystick
	 * @param centerXJoystickRight
	 *            coordinate on axis X for the center of the right joystick
	 * @param centerYJoystickRight
	 *            coordinate on axis Y for the center of the right joystick
	 * @param joystickRadiusJoystickRight
	 *            radius of the area of the right joystick
	 */
	public void initJoystick(int centerXJoystickLeft, int centerYJoystickLeft, int joystickRadiusJoystickLeft,
			int centerXJoystickRight, int centerYJoystickRight, int joystickRadiusJoystickRight) {
		this.centerXMainCircleLeft = centerXJoystickLeft;
		this.centerYMainCircleLeft = centerYJoystickLeft;
		this.centerXSecondaryCircleLeft = centerXJoystickLeft;
		this.centerYSecondaryCircleLeft = centerYJoystickLeft;
		this.joystickRadiusLeft = joystickRadiusJoystickLeft;

		this.centerXMainCircleRight = centerXJoystickRight;
		this.centerYMainCircleRight = centerYJoystickRight;
		this.centerXSecondaryCircleRight = centerXJoystickRight;
		this.centerYSecondaryCircleRight = centerYJoystickRight;
		this.joystickRadiusRight = joystickRadiusJoystickRight;
		this.invalidate();
	}

	/**
	 * draw two joystick at the given position
	 * 
	 * @param centerXJoystickLeft
	 *            coordinate on axis X for the center of the left joystick
	 * @param centerYJoystickLeft
	 *            coordinate on axis Y for the center of the left joystick
	 * @param centerXJoystickRight
	 *            coordinate on axis X for the center of the right joystick
	 * @param centerYJoystickRight
	 *            coordinate on axis Y for the center of the right joystick
	 */
	public void drawJoystick(int centerXJoystickLeft, int centerYJoystickLeft, int centerXJoystickRight,
			int centerYJoystickRight) {
		this.centerXSecondaryCircleLeft = centerXJoystickLeft;
		this.centerYSecondaryCircleLeft = centerYJoystickLeft;
		this.centerXSecondaryCircleRight = centerXJoystickRight;
		this.centerYSecondaryCircleRight = centerYJoystickRight;
		this.invalidate();
	}

}
