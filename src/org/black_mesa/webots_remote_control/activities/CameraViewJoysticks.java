package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.listeners.CameraJoystickCoordinateListener;

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
public class CameraViewJoysticks extends View implements CameraJoystickCoordinateListener {
	//Left Joystick
	private int centerXMainCircleLeft = 0; // Center view x position
	private int centerYMainCircleLeft = 0; // Center view y position
	private int centerXSecondaryCircleLeft = 0; // Center view x position
	private int centerYSecondaryCircleLeft = 0; // Center view y position
	//Right Joystick
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
		drawJoystick(canvas, centerXMainCircleLeft, centerYMainCircleLeft, centerXSecondaryCircleLeft, centerYSecondaryCircleLeft, joystickRadiusLeft);
		drawJoystick(canvas, centerXMainCircleRight, centerYMainCircleRight, centerXSecondaryCircleRight, centerYSecondaryCircleRight, joystickRadiusRight);

	}
	
	private void drawJoystick(Canvas canvas, float centerXMainCircle, float centerYMainCircle, float centerXSecondaryCircle, float centerYSecondaryCircle, float joystickRadius)
	{
		// painting the main circle
		canvas.drawCircle(centerXMainCircle, centerYMainCircle, joystickRadius,
				mainCircle);
		// painting the secondary circle
		canvas.drawCircle(centerXSecondaryCircle, centerYSecondaryCircle, joystickRadius / 2,
				secondaryCircle);
		// paint lines
		canvas.drawLine((float) centerXMainCircle, (float) centerYMainCircle, (float) centerXMainCircle,
				(float) (centerYMainCircle - joystickRadius), verticalLine);
		canvas.drawLine((float) (centerXMainCircle - joystickRadius), (float) centerYMainCircle,
				(float) (centerXMainCircle + joystickRadius), (float) centerYMainCircle,
				horizontalLine);
		canvas.drawLine((float) centerXMainCircle, (float) (centerYMainCircle + joystickRadius),
				(float) centerXMainCircle, (float) centerYMainCircle, horizontalLine);
	}
	
	@Override
	public void onJoystickLeftCoordinateChanged(int centerXJoystickLeft, int centerYJoystickLeft, int joystickRadiusJoystickLeft)
	{
		this.centerXMainCircleLeft=centerXJoystickLeft;
		this.centerYMainCircleLeft=centerYJoystickLeft;
		this.centerXSecondaryCircleLeft=centerXJoystickLeft;
		this.centerYSecondaryCircleLeft=centerYJoystickLeft;
		this.joystickRadiusLeft=joystickRadiusJoystickLeft;
		this.invalidate();
	}
	
	@Override
	public void onJoystickRightCoordinateChanged(int centerXJoystickRight, int centerYJoystickRight, int joystickRadiusJoystickRight)
	{
		this.centerXMainCircleRight=centerXJoystickRight;
		this.centerYMainCircleRight=centerYJoystickRight;
		this.centerXSecondaryCircleRight=centerXJoystickRight;
		this.centerYSecondaryCircleRight=centerYJoystickRight;
		this.joystickRadiusRight=joystickRadiusJoystickRight;
		this.invalidate();
	}

}
