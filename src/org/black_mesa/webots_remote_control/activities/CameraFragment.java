package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.Client;
import org.black_mesa.webots_remote_control.client.ConnectionManager;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.listeners.CameraTouchHandlerListener;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;
import org.black_mesa.webots_remote_control.remote_object.CameraInstruction;
import org.black_mesa.webots_remote_control.remote_object.InstructionQueue;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;
import org.black_mesa.webots_remote_control.utils.CameraTouchHandler;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class CameraFragment extends Fragment implements OnTouchListener, CameraTouchHandlerListener,
		ConnectionManagerListener {
	private CameraTouchHandler touchHandler;
	private ConnectionManager connectionManager = new ConnectionManager();
	private InstructionQueue camera = null;
	Server server;

	public CameraFragment() {
		connectionManager.addListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

		touchHandler = new CameraTouchHandler(xMin, yMin, xMax, yMax, this);

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
		return touchHandler.onTouch(event);
	}

	@Override
	public void moveForward(float forward) {
		if (camera == null) {
			Log.d(getClass().getName(), "moveForward event before camera was initialized");
			return;
		}
		CameraInstruction instruction = CameraInstruction.move(0, 0, forward * 16);
		camera.add(instruction);
		connectionManager.getClient(server).board(camera);
	}

	@Override
	public void moveSide(float right, float up, long time) {
		if (camera == null) {
			Log.d(getClass().getName(), "moveSide event before camera was initialized");
			return;
		}
		CameraInstruction instruction = CameraInstruction.move((right * time) / 128., (-up * time) / 128., 0);
		camera.add(instruction);
		connectionManager.getClient(server).board(camera);
	}

	@Override
	public void turnPitch(float turn, float pitch) {
		if (camera == null) {
			Log.d(getClass().getName(), "turnPitch event before camera was initialized");
			return;
		}
		CameraInstruction instruction = CameraInstruction.turn(turn * Math.PI);
		camera.add(instruction);
		instruction = CameraInstruction.pitch(pitch * Math.PI);
		camera.add(instruction);
		connectionManager.getClient(server).board(camera);
	}

	@Override
	public void onStateChange(Server server, ConnectionState state) {
		Log.d(getClass().getName(), "Onstatechange");
		Client client = connectionManager.getClient(server);
		if (client == null) {
			Log.e(getClass().getName(), "null client!");
		} else {
			SparseArray<RemoteObject> array = client.getInitialData();
			if(array != null) {
				camera = (InstructionQueue) client.getInitialData().valueAt(0);
			}
		}
	}
}
