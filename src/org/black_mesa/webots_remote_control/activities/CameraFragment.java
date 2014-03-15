package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.CameraModel;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.CamerasManager;
import org.black_mesa.webots_remote_control.client.ConnectionManager;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandler;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class CameraFragment extends Fragment implements OnTouchListener, ConnectionManagerListener {
	private CameraTouchHandler touchHandler;
	private ConnectionManager connectionManager = new ConnectionManager();
	private CamerasManager camerasManager = new CamerasManager(connectionManager);
	private Server server;
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		connectionManager.addListener(this);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int port = Integer.parseInt(prefs.getString("edittext_port_preference", "42511"));
		String address = prefs.getString("edittext_address_preference", "0.0.0.0");

		server = new Server(0, "Derp", address, port);
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
		CameraModel cameraModel = CameraModel.getInstance();
		cameraModel.setxMax(xMax);
		cameraModel.setxMin(xMin);
		cameraModel.setyMax(yMax);
		cameraModel.setyMin(yMin);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		connectionManager.stop();
		super.onPause();
	}

	@Override
	public void onResume() {
		connectionManager.start();
		connectionManager.addServer(server);
		super.onResume();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (touchHandler != null) {
			touchHandler.onTouch(event);
		}
		return true;
	}

	@Override
	public void onStateChange(Server server, ConnectionState state) {
		if (state == ConnectionState.CONNECTED) {
			touchHandler = new CameraTouchHandler(xMin, yMin, xMax, yMax, camerasManager.makeListener(server, 0));
		}
	}
}