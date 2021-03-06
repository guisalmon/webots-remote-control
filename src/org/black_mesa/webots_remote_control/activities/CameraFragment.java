package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.database.Server;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV1;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV2;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV3;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV4;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV5;

import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class CameraFragment extends Fragment implements OnTouchListener {
	private CameraTouchHandlerV1 mTouchHandlerV1;
	private CameraTouchHandlerV2 mTouchHandlerV2;
	private CameraTouchHandlerV3 mTouchHandlerV3;
	private CameraTouchHandlerV4 mTouchHandlerV4;
	private CameraTouchHandlerV5 mTouchHandlerV5;
	private Server mServer;
	private float mXMin;
	private float mXMax;
	private float mYMin;
	private float mYMax;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle extras = getArguments();
		long id = extras.getLong("ServerId");
		for (Server s : MainActivity.CONNECTED_SERVERS) {
			if (s.getId() == id) {
				mServer = s;
				break;
			}
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		switch (MainActivity.getInteractionMode()) {
		case 1:
			return inflater.inflate(R.layout.camera_fragment, container, false);
		case 2:
			return inflater.inflate(R.layout.camera_fragment_joysticks, container, false);
		case 3:
			return inflater.inflate(R.layout.camera_fragment, container, false);
		case 4:
			return inflater.inflate(R.layout.camera_fragment, container, false);
		case 5:
			return inflater.inflate(R.layout.camera_fragment, container, false);
		default:
			throw new RuntimeException("Unknown interaction mode");
		}
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
		final TypedArray styledAttributes =
				getActivity().getBaseContext().getTheme()
						.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
		actionBarSize = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();

		// compute the size of the usable part of the screen
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mXMin = 0;
			mXMax = size.x;
			mYMin = (float) actionBarSize;
			mYMax = size.y;
		} else {
			mXMin = 0;
			mXMax = size.x;
			mYMin = (float) actionBarSize;
			mYMax = size.y;
		}

		// Initialize Client

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		switch (MainActivity.getInteractionMode()) {
		case 1:
			mTouchHandlerV1 =
					new CameraTouchHandlerV1(mXMin, mYMin, mXMax, mYMax,
							MainActivity.CAMERAS_MANAGER.makeListenerType1(mServer, 0));
			break;
		case 2:
			mTouchHandlerV2 =
					new CameraTouchHandlerV2(mXMin, mYMin, mXMax, mYMax,
							MainActivity.CAMERAS_MANAGER.makeListenerType2(mServer, 0));
			break;
		case 3:
			mTouchHandlerV3 =
					new CameraTouchHandlerV3(mXMin, mYMin, mXMax, mYMax,
							MainActivity.CAMERAS_MANAGER.makeListenerType3(mServer, 0));
			break;
		case 4:
			mTouchHandlerV4 =
					new CameraTouchHandlerV4(mXMin, mYMin, mXMax, mYMax,
							MainActivity.CAMERAS_MANAGER.makeListenerType4(mServer, 0));
			break;
		case 5:
			mTouchHandlerV5 =
					new CameraTouchHandlerV5(mXMin, mYMin, mXMax, mYMax,
							MainActivity.CAMERAS_MANAGER.makeListenerType5(mServer, 0));
			break;
		default:
			throw new RuntimeException("Unknown interaction mode");
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (MainActivity.getInteractionMode()) {
		case 1:
			if (mTouchHandlerV1 != null) {
				mTouchHandlerV1.onTouch(event);
			}
			break;
		case 2:
			if (mTouchHandlerV2 != null) {
				mTouchHandlerV2.onTouch(event);
			}
			break;
		case 3:
			if (mTouchHandlerV3 != null) {
				mTouchHandlerV3.onTouch(event);
			}
			break;
		case 4:
			if (mTouchHandlerV4 != null) {
				mTouchHandlerV4.onTouch(event);
			}
			break;
		case 5:
			if (mTouchHandlerV5 != null) {
				mTouchHandlerV5.onTouch(event);
			}
			break;
		}
		return true;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

}
