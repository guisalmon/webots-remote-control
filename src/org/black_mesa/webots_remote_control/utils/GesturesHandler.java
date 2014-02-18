package org.black_mesa.webots_remote_control.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.black_mesa.webots_remote_control.client.Client;
import org.black_mesa.webots_remote_control.exceptions.IncompatibleClientException;
import org.black_mesa.webots_remote_control.exceptions.InvalidClientException;
import org.black_mesa.webots_remote_control.exceptions.NotReadyClientException;
import org.black_mesa.webots_remote_control.listeners.ClientEventListener;
import org.black_mesa.webots_remote_control.remote_object_state.RemoteCameraState;
import org.black_mesa.webots_remote_control.remote_object_state.RemoteObjectState;

import android.app.Fragment;
import android.util.Log;

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
	private Timer mTimer;

	private Client mClient;
	private RemoteCameraState mCamera;

	public GesturesHandler(float xMin, float xMax, float yMin, float yMax, Fragment frag) {
		minXwindow = xMin;
		minYwindow = yMin;
		maxXwindow = xMax;
		maxYwindow = yMax;
		curX = 0;
		curY = 0;
		pressed = false;
		isDrag = false;
		// TODO
		InetAddress address = null;
		try {
			address = InetAddress.getByName("192.168.1.2");
		} catch (UnknownHostException e) {
			Log.e(getClass().getName(), e.toString());
		}
		mClient = new Client(address, 48200, this, frag.getActivity());
	}

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
				dX = curX - prevX;
				dY = curY - prevY;
				float percX = dX / (maxXwindow - minXwindow);
				float percY = dY / (maxYwindow - minYwindow);
				if (isDrag) {
					// drag(dX, dY, tps);
					// TODO
					mCamera.pitch(percY * Math.PI);
					mCamera.turn(percX * Math.PI);
					try {
						mClient.onStateChange(mCamera);
					} catch (InvalidClientException e) {
						Log.e(getClass().getName(), e.toString());
					} catch (IncompatibleClientException e) {
						Log.e(getClass().getName(), e.toString());
					} catch (NotReadyClientException e) {
						Log.e(getClass().getName(), e.toString());
					}
					Log.i(getClass().getName(), "Drag : x " + percX + ", y " + percY);
				} else {
					// move(dX, dY, tps);
					Log.i(getClass().getName(), "Move : x " + percX + ", y " + percY);
				}
				prevX = curX;
				prevY = curY;
			}
		};
		mTimer.scheduleAtFixedRate(task, 0, 50);
		pressed = true;
	}

	public void release(float x, float y) {
		if (pressed) {
			dX = 0;
			dY = 0;
		}
		mTimer.cancel();
		mTimer.purge();
		pressed = false;
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

	public void update(float rawX, float rawY) {
		curX = rawX;
		curY = rawY;
	}

	public void stop() {
		mClient.dispose();
	}

	@Override
	public void onObjectReceived(RemoteObjectState state) {
		// TODO
		mCamera = (RemoteCameraState) state;
	}

}
