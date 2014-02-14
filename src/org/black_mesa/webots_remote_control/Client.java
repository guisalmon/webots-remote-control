package org.black_mesa.webots_remote_control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class Client {
	private final Socket socket;

	private Camera receivedCamera;

	public Client(InetAddress address, int port) throws IOException {
		socket = new Socket(address, port);
		new Thread(new Runnable() {

			@Override
			public void run() {
				recvCamera();
			}

		}).start();
	}

	public void onCameraChange(Camera camera) {
		final Camera toSend = camera.clone();
		new Thread(new Runnable() {

			@Override
			public void run() {
				sendCamera(toSend);
			}

		}).start();
	}

	public Camera getCamera() {
		Camera ret;
		synchronized (socket) {
			ret = receivedCamera.clone();
		}
		return ret;
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	private void recvCamera() {
		synchronized (socket) {
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				receivedCamera = (Camera) in.readObject();
			} catch (IOException e) {
				Log.e(this.getClass().getName(), e.toString());
			} catch (ClassNotFoundException e) {
				Log.e(this.getClass().getName(), e.toString());
			}
		}
	}

	private void sendCamera(Camera camera) {
		synchronized (socket) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(camera);
			} catch (IOException e) {
				Log.e(this.getClass().getName(), e.toString());
			}
		}
	}
}
