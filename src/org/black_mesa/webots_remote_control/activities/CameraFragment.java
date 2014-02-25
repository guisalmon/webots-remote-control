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

public class CameraFragment extends Fragment implements OnTouchListener{
	private GesturesHandler mGestureHandler;
	private boolean mIsPinch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.camera_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// handle touch events
		this.getView().setOnTouchListener(this);

		// get the size of the screen
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		// get the size of the action bar
		int actionBarSize;
		final TypedArray styledAttributes = getActivity().getBaseContext().getTheme()
				.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
		actionBarSize = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();

		// compute the size of the usable part of the screen
		float xMin;
		float xMax;
		float yMin;
		float yMax;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			xMin = 0;
			xMax = size.x;
			yMin = (float) actionBarSize;
			yMax = size.y;
		} else {
			xMin = 0;
			xMax = size.x;
			yMin = (float) actionBarSize;
			yMax = size.y;
		}

		// Initiate gesture handler
		mGestureHandler = new GesturesHandler(xMin, xMax, yMin, yMax, this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		mGestureHandler.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		mGestureHandler.initiate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mIsPinch = (event.getPointerCount() == 2);
		switch (event.getAction()) {
		case android.view.MotionEvent.ACTION_DOWN:
			mGestureHandler.touch(event.getRawX(), event.getRawY());
			break;
		case android.view.MotionEvent.ACTION_UP:
			mGestureHandler.release();
		case android.view.MotionEvent.ACTION_MOVE:
			mGestureHandler.update(event.getRawX(), event.getRawY(), mIsPinch);
		}
		return false;
	}
}
