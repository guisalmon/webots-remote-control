package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.utils.GesturesHandler;

import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CameraFragment extends Fragment implements OnTouchListener{
	private GesturesHandler mGestureHandler;
	private int mTouchState;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.camera_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//handle touch events
		this.getView().setOnTouchListener(this);
		
		//get the size of the screen
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		//get the size of the action bar
		int actionBarSize;
		final TypedArray styledAttributes = getActivity().getBaseContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
		actionBarSize = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();
		
		//compute the size of the usable part of the screen
		float xMin;
		float xMax;
		float yMin;
		float yMax;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			xMin = 0;
			xMax = size.x;
			yMin = (float)actionBarSize;
			yMax = size.y;
		}else{
			xMin = 0;
			xMax = size.x;
			yMin = (float)actionBarSize;
			yMax = size.y;
		}
		
		//Initiate gesture handler
		mGestureHandler = new GesturesHandler(xMin, xMax, yMin, yMax, this);
		mTouchState = 0;
		super.onActivityCreated(savedInstanceState);
	}
	
	

	@Override
	public void onPause() {
		super.onPause();
		mGestureHandler.stopClient();
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()){
		case android.view.MotionEvent.ACTION_DOWN:
			if (mTouchState == 0){
				mGestureHandler.touch(event.getRawX(), event.getRawY());
				((Button)getView()).setText("touchEvent");
			}
			/*if (mTouchState == 1){
				mGestureHandler.secondaryTouch(event.getRawX(), event.getRawY());
				((Button)getView()).setText("pinchEvent");
			}*/
			mTouchState++;
			break;
		case android.view.MotionEvent.ACTION_UP:
			if(mTouchState == 1){
	    		mGestureHandler.release(event.getRawX(), event.getRawY());
	    		((Button)getView()).setText("touchEvent Release");
	    	}/*else{
	    		mGestureHandler.pinch(event.getRawX(), event.getRawY());
	    		((Button)getView()).setText("pinchEvent Release");
	    	}*/
	    	mTouchState = 0;
		case android.view.MotionEvent.ACTION_MOVE:
			mGestureHandler.update(event.getRawX(), event.getRawY());
		
		}
		return false;
	}
}
