package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.utils.GesturesHandler;

import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class CameraFragment extends Fragment implements OnTouchListener, OnDragListener{
	private GesturesHandler mGestureHandler;
	private int touchState;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.camera_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//handle touch events
		this.getView().setOnTouchListener(this);
		this.getView().setOnDragListener(this);
		
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
		mGestureHandler = new GesturesHandler(xMin, xMax, yMin, yMax);
		touchState = 0;
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				if (touchState == 0){
					mGestureHandler.touch(event.getRawX(), event.getRawY());
				}
				if (touchState == 1){
					mGestureHandler.secondaryTouch(event.getRawX(), event.getRawY());
				}
				touchState++;
				
		    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
		    	if(touchState == 0){
		    		mGestureHandler.release(event.getRawX(), event.getRawY(), event.getEventTime() - event.getDownTime());
		    	}else{
		    		mGestureHandler.pinch(event.getRawX(), event.getRawY());
		    	}
		    	touchState = 0;
		    }
		return false;
	}


	@Override
	public boolean onDrag(View v, DragEvent event) {
		((TextView)getView()).setText("OnDrag !");
		return false;
	}
}
