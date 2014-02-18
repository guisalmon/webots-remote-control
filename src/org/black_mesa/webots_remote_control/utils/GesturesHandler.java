package org.black_mesa.webots_remote_control.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.util.Log;

public class GesturesHandler {
	private float minXwindow;
	private float minYwindow;
	private float maxXwindow;
	private float maxYwindow;
	
	private float curX;
	private float curY;
	private float prevX;
	private float prevY;
	private float dX;
	private float dY;
	
	private boolean pressed;
	private boolean isDrag;
	
	private Timer mTimer;
	
	public GesturesHandler(float xMin, float xMax, float yMin, float yMax) {
		minXwindow = xMin;
		minYwindow = yMin;
		maxXwindow = xMax;
		maxYwindow = yMax;
		curX = 0;
		curY = 0;
		pressed = false;
		isDrag = false;
	}
	
	public void touch(float x, float y){
		prevX = x;
		prevY = y;
		curX = x;
		curY = y;
		isDrag = isCenter();
		mTimer = new Timer(true);
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				dX = curX - prevX;
				dY = curY - prevY;
				float percX = dX/(maxXwindow-minXwindow);
				float percY = dY/(maxYwindow-minYwindow);
				if(isDrag){
					//drag(dX, dY, tps);
					Log.i(getClass().getName(), "Drag : x "+percX+", y "+percY);
				}else{
					//move(dX, dY, tps);
					Log.i(getClass().getName(), "Move : x "+percX+", y "+percY);
				}
				prevX = curX;
				prevY = curY;
			}
		};
		mTimer.scheduleAtFixedRate(task, 0, 50);
		pressed = true;
	}
	
	public void release(float x, float y){
		if(pressed){
			dX = 0;
			dY = 0;
		}
		mTimer.cancel();
		mTimer.purge();
		pressed = false;
	}

	private boolean isCenter() {
		boolean b = true;
		float sizeX = maxXwindow-minXwindow;
		float sizeY = maxYwindow-minYwindow;
		float x = prevX-minXwindow;
		float y = prevY-minYwindow;
		b = b&&(x<0.75*sizeX);
		b = b&&(y<0.75*sizeY);
		b = b&&(x>0.25*sizeX);
		b = b&&(y>0.25*sizeY);
		return b;
	}

	public void update(float rawX, float rawY) {
		curX = rawX;
		curY = rawY;
	}

}
