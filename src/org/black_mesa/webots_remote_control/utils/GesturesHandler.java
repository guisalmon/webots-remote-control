package org.black_mesa.webots_remote_control.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.black_mesa.webots_remote_control.client.Client;
import org.black_mesa.webots_remote_control.exceptions.IncompatibleClientException;
import org.black_mesa.webots_remote_control.exceptions.InvalidClientException;
import org.black_mesa.webots_remote_control.listeners.ClientEventListener;
import org.black_mesa.webots_remote_control.remote_object_state.RemoteCameraState;
import org.black_mesa.webots_remote_control.remote_object_state.RemoteObjectState;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Handles the positions of the events returned from the view and calls the
 * remote objects with the right parameters
 * 
 * @author guisalmon@gmail.com
 * 
 */
public class GesturesHandler implements ClientEventListener {
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
	private boolean mIsPinch;
	private Timer mTimer;

	private Client mClient;
	private RemoteCameraState mCamera;

	private Fragment mFrag;

	public GesturesHandler(float xMin, float xMax, float yMin, float yMax, Fragment frag) {
		minXwindow = xMin;
		minYwindow = yMin;
		maxXwindow = xMax;
		maxYwindow = yMax;
		curX = 0;
		curY = 0;
		pressed = false;
		isDrag = false;
		mIsPinch = false;
		mFrag = frag;
	}

	@Override
	public void onReception(List<RemoteObjectState> states) {
		// TODO
		mCamera = (RemoteCameraState) states.get(0);
		Log.i(getClass().getName(), "Camera received: " + mCamera);
	}

	/**
	 * Is called when the user touches the screen. Determines if the gesture
	 * aims at moving the remote object or rotating it, and starts a timer to
	 * give regularly new informations to the remote object
	 * 
	 * @param x
	 *            coordinate of the touch event along the x axis (in pixels from
	 *            the left side of the screen)
	 * @param y
	 *            coordinate of the touch event along the y axis (in pixels from
	 *            the upper side of the screen)
	 */
	public void touch(float x, float y) {
		prevX = x;
		prevY = y;
		curX = x;
		curY = y;
		isDrag = isCenter();
		mTimer = new Timer(true);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (mCamera == null) {
					Log.w(getClass().getName(), "Event before camera was initialized");
					// State of the connection should be displayed at all times
					// for the user
					return;
				}
				if (mIsPinch) {
					pinch();
				} else {
					if (isDrag) {
						drag();
					} else {
						move();
					}
				}
				prevX = curX;
				prevY = curY;
			}
		};
		mTimer.scheduleAtFixedRate(task, 0, 50);
		pressed = true;
	}

	/**
	 * Is called when the user stops touching the screen. Cancels the timer.
	 * 
	 */
	public void release() {
		if (pressed) {
			dX = 0;
			dY = 0;
		}
		mTimer.cancel();
		mTimer.purge();
		pressed = false;
		mIsPinch = false;
	}

	/**
	 * Updates the current position informations when the user moves his finger
	 * on the screen
	 * 
	 * @param rawX
	 *            current coordinate of the touch event along the x axis (in
	 *            pixels from the left side of the screen)
	 * @param rawY
	 *            current coordinate of the touch event along the y axis (in
	 *            pixels from the upper side of the screen)
	 */
	public void update(float rawX, float rawY, boolean isPinch) {
		mIsPinch = mIsPinch || isPinch;
		curX = rawX;
		curY = rawY;
	}

	/**
	 * Stops the client. It will be no longer waiting for position updates
	 */
	public void stop() {
		mClient.dispose();
		if (mTimer != null){
			mTimer.cancel();
			mTimer.purge();
		}
	}

	/**
	 * Initiates the client with the right IP and port from the preferences
	 */
	public void initiate() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mFrag.getActivity());
		InetAddress address = null;
		int port = Integer.parseInt(prefs.getString("edittext_port_preference", "42511"));
		try {
			address = InetAddress.getByName(prefs.getString("edittext_address_preference", "0.0.0.0"));
		} catch (UnknownHostException e) {
			Log.e(getClass().getName(), e.toString());
		}

		mClient = new Client(address, port, this, mFrag.getActivity());
	}

	private void pinch() {
		dX = (curX - (maxXwindow / 2)) / (maxXwindow / 2);
		dY = ((maxYwindow / 2) - curY) / (maxYwindow / 2);
		float delta = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		float prevDX = (prevX - (maxXwindow / 2)) / (maxXwindow / 2);
		float prevDY = ((maxYwindow / 2) - prevY) / (maxYwindow / 2);
		float prevDelta = (float) Math.sqrt(Math.pow(prevDX, 2) + Math.pow(prevDY, 2));
		delta = delta - prevDelta;
		Log.i(getClass().getName(), "Delta : " + delta);
		mCamera.move(0, 0, 50 * delta);
	}

	private boolean isCenter() {
		boolean b = true;
		float sizeX = maxXwindow - minXwindow;
		float sizeY = maxYwindow - minYwindow;
		float x = prevX - minXwindow;
		float y = prevY - minYwindow;
		b = b && (x < 0.75 * sizeX);
		b = b && (y < 0.75 * sizeY);
		b = b && (x > 0.25 * sizeX);
		b = b && (y > 0.25 * sizeY);
		return b;
	}

	private void drag() {
		dX = curX - prevX;
		dY = curY - prevY;
		float percX = dX / (maxXwindow - minXwindow);
		float percY = dY / (maxYwindow - minYwindow);
		Log.i(getClass().getName(), "Pitch of " + percY);
		mCamera.pitch(percY * Math.PI);
		Log.i(getClass().getName(), "New camera: " + mCamera);
		Log.i(getClass().getName(), "Turn of " + percY);
		mCamera.turn(percX * Math.PI);
		Log.i(getClass().getName(), "New camera: " + mCamera);
		try {
			mClient.onStateChange(mCamera);
		} catch (InvalidClientException e) {
			// Connection is broken
			// We should notify the user and give him the choice between
			// changing connection parameters and trying to establish a new
			// connection
			Log.e(getClass().getName(), e.toString());
		} catch (IncompatibleClientException e) {
			// Server not compatible with client
			// User should be notified and given the choice of changing
			// connection parameters
			Log.e(getClass().getName(), e.toString());
		}
		Log.i(getClass().getName(), "Drag : x " + percX + ", y " + percY);
	}

	private void move() {
		dX = (curX - (maxXwindow / 2)) / (maxXwindow / 2);
		dY = ((maxYwindow / 2) - curY) / (maxYwindow / 2);
		mCamera.move(dX, dY, 0);
		try {
			mClient.onStateChange(mCamera);
		} catch (InvalidClientException e) {
			// Connection is broken
			// We should notify the user and give him the choice between
			// changing connection parameters and trying to establish a new
			// connection
			Log.e(getClass().getName(), e.toString());
		} catch (IncompatibleClientException e) {
			// Server not compatible with client
			// User should be notified and given the choice of changing
			// connection parameters
			Log.e(getClass().getName(), e.toString());
		}
		Log.i(getClass().getName(), "Move : x " + dX + ", y " + dY);
	}

	//@Override
	public void onObjectReceived(RemoteObjectState state) {
		// TODO Auto-generated method stub
		mCamera = (RemoteCameraState) state;
		Log.i(getClass().getName(), "Camera received: " + mCamera);
	}

}
