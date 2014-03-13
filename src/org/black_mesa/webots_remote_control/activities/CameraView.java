package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.CameraModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CameraView extends View{


	public CameraView(Context context) {
		super(context);
		setFocusable(true);
		// TODO Auto-generated constructor stub
	}
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		// TODO Auto-generated constructor stub
	}
	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFocusable(true);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// opacity
		//p.setAlpha(0x80); //
		CameraModel cameraModel = CameraModel.getInstance();
		Rect rectCan=canvas.getClipBounds();
		Paint p = new Paint();
		float l = (cameraModel.getxMax() - cameraModel.getxMin()) / 6;
		float u = (cameraModel.getyMax() - cameraModel.getyMin()) / 6;
		float r = l*5;
		float d = u*5;
		RectF rect= new RectF(l,u,r,d);
		// smooths
		p.setAntiAlias(true);
		p.setColor(Color.RED);
		p.setAlpha(0x80);
		p.setStyle(Paint.Style.STROKE); 
		p.setStrokeWidth(4.5f);
		canvas.drawOval(rect, p);
	}


}
