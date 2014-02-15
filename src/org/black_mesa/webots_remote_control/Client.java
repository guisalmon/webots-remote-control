package org.black_mesa.webots_remote_control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

/**
 * @author Ilja Kroonen
 */
public class Client {
	private Socket socket;
	private ObjectOutputStream outputStream = null;

	private Camera receivedCamera = null;

	private boolean valid = true;
	private boolean serverCompatible = true;

	public Client(InetAddress address, int port) {
		final InetAddress finalAddress = address;
		final int finalPort = port;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket = new Socket(finalAddress, finalPort);
					recvCamera();
				} catch (IOException e) {
					valid = false;
				}
			}

		}).start();
	}

	public void onCameraChange(Camera camera) throws InvalidClientException, IncompatibleServerException {
		if (!serverCompatible) {
			throw new IncompatibleServerException();
		}
		if (!valid) {
			throw new InvalidClientException();
		}
		final Camera toSend = camera.clone();
		new Thread(new Runnable() {

			@Override
			public void run() {
				sendCamera(toSend);
			}

		}).start();
	}

	public Camera getCamera() throws InvalidClientException, IncompatibleServerException {
		if (!serverCompatible) {
			throw new IncompatibleServerException();
		}
		if (!valid) {
			throw new InvalidClientException();
		}
		Camera ret = receivedCamera;
		return ret;
	}

	private void recvCamera() {
		synchronized (socket) {
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				receivedCamera = (Camera) in.readObject();
			} catch (IOException e) {
				Log.e(this.getClass().getName(), e.toString());
				valid = false;
			} catch (ClassNotFoundException e) {
				Log.e(this.getClass().getName(), e.toString());
				serverCompatible = false;
				valid = false;
			}
		}
	}

	private void sendCamera(Camera camera) {
		synchronized (socket) {
			try {
				if (outputStream == null) {
					outputStream = new ObjectOutputStream(socket.getOutputStream());
				}
				outputStream.writeObject(camera);
			} catch (IOException e) {
				Log.e(this.getClass().getName(), e.toString());
				valid = false;
			}
		}
	}
}
