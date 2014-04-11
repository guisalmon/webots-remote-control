package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.CamerasManager;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV1;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV2;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandlerV3;

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
	//private CamerasManager camerasManager = new CamerasManager(MainActivity.CONNECTION_MANAGER);
	private Server server;
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle extras = getArguments();
		long id = extras.getLong("ServerId");
		for (Server s : MainActivity.CONNECTION_MANAGER.getServerList()) {
			if (s.getId() == id) {
				server = s;
				break;
			}
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		switch (MainActivity.CAMERA_INTERACTION_MODE) {
		case 1:
			return inflater.inflate(R.layout.camera_fragment, container, false);
		case 2:
			return inflater.inflate(R.layout.camera_fragment_joysticks, container, false);
		case 3:
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
		switch (MainActivity.CAMERA_INTERACTION_MODE) {
		case 1:
			mTouchHandlerV1 =
					new CameraTouchHandlerV1(xMin, yMin, xMax, yMax, MainActivity.CAMERAS_MANAGER.makeListenerType1(server, 0));
			break;
		case 2:
			mTouchHandlerV2 =
					new CameraTouchHandlerV2(xMin, yMin, xMax, yMax, MainActivity.CAMERAS_MANAGER.makeListenerType2(server, 0));
			break;
		case 3:
			mTouchHandlerV3 =
					new CameraTouchHandlerV3(xMin, yMin, xMax, yMax, MainActivity.CAMERAS_MANAGER.makeListenerType3(server, 0));
			break;
		default:
			throw new RuntimeException("Unknown interaction mode");
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (MainActivity.CAMERA_INTERACTION_MODE) {
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
		}
		return true;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

}
