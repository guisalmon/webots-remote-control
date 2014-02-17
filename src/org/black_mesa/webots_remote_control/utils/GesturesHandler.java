package org.black_mesa.webots_remote_control.utils;

import android.util.Log;

public class GesturesHandler {
	private float minXwindow;
	private float minYwindow;
	private float maxXwindow;
	private float maxYwindow;
	
	private float curX;
	private float curY;
	private float dX;
	private float dY;
	private float secX;
	private float secY;
	
	private boolean pressed;
	private boolean pinch;
	
	public GesturesHandler(float xMin, float xMax, float yMin, float yMax) {
		minXwindow = xMin;
		minYwindow = yMin;
		maxXwindow = xMax;
		maxYwindow = yMax;
		curX = 0;
		curY = 0;
		pressed = false;
		pinch = false;
	}
	
	public void touch(float x, float y){
		curX = x;
		curY = y;
		pressed = true;
	}
	
	public void secondaryTouch(float x, float y){
		secX = x;
		secY = y;
		pinch = true;
	}
	
	public void release(float x, float y, long tps){
		if(pressed){
			dX = x - curX;
			dY = y - curY;
			float percX = dX/(maxXwindow-minXwindow);
			float percY = dY/(maxYwindow-minYwindow);
			if(isCenter()){
				//drag(dX, dY, tps);
				Log.i(getClass().getName(), "Drag : x "+percX+", y "+percY+", tps "+tps);
			}else{
				//move(dX, dY, tps);
				Log.i(getClass().getName(), "Move : x "+percX+", y "+percY+", tps "+tps);
			}
			dX = 0;
			dY = 0;
		}
		pressed = false;
	}
	
	public void pinch(float x, float y){
		if (pinch){
			Log.i(getClass().getName(), "Pinched !");
		}
		pinch = false;
		pressed = false;
	}

	private boolean isCenter() {
		boolean b = true;
		float sizeX = maxXwindow-minXwindow;
		float sizeY = maxYwindow-minYwindow;
		float x = curX-minXwindow;
		float y = curY-minYwindow;
		b = b&&(x<0.75*sizeX);
		b = b&&(y<0.75*sizeY);
		b = b&&(x>0.25*sizeX);
		b = b&&(y>0.25*sizeY);
		return b;
	}

}
