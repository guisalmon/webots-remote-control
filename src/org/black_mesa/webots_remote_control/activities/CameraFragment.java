package org.black_mesa.webots_remote_control.activities;


import org.black_mesa.webots_remote_control.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class CameraFragment extends Fragment implements OnGestureListener, OnTouchListener{
	private GestureDetector mGestureScanner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.camera_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		this.getView().setOnTouchListener(this);
		mGestureScanner = new GestureDetector(getActivity(), this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i(this.getClass().getName(), "onTouch");
		return mGestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Log.i(this.getClass().getName(), "onDown");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.i(this.getClass().getName(), "onFling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.i(this.getClass().getName(), "onLongPress");
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		Log.i(this.getClass().getName(), "onScroll");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Log.i(this.getClass().getName(), "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(this.getClass().getName(), "onSingleTapUp");
		return false;
	}
}
