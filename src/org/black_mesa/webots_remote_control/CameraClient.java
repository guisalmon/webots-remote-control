package org.black_mesa.webots_remote_control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.black_mesa.webots_remote_control.exceptions.IncompatibleClientException;
import org.black_mesa.webots_remote_control.exceptions.InvalidClientException;
import org.black_mesa.webots_remote_control.exceptions.NotReadyClientException;

import android.util.Log;

/**
 * @author Ilja Kroonen
 */
/*
 * TODO This class should probably have a close method
 */
public class CameraClient {
	private ObjectOutputStream outputStream = null;
	private Socket socket;

	private boolean ready = false;
	private boolean valid = true;
	private boolean serverCompatible = true;

	Camera receivedCamera = null;

	Thread sender;
	Camera next;

	public CameraClient(InetAddress address, int port) {
		final InetAddress finalAddress = address;
		final int finalPort = port;

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket = new Socket(finalAddress, finalPort);
					sender = new Thread(new Runnable() {

						@Override
						public void run() {
							senderWork();
						}

					});
					sender.start();
					recvCamera();
					ready = true;
				} catch (IOException e) {
					valid = false;
				}
			}
		}).start();
	}

	public boolean isReady() {
		return ready;
	}

	/**
	 * Sends the new state of the camera to the server
	 * 
	 * @param camera
	 *            Reference to the state we want to send
	 * @throws InvalidClientException
	 *             There is no active connection with the server
	 * @throws IncompatibleClientException
	 *             The server is not in a version compatible with the client
	 * @throws NotReadyClientException
	 *             The client is not yet ready ; you should check if the client
	 *             is ready with the isReady() method before using it
	 */
	public void onCameraChange(Camera camera) throws InvalidClientException, IncompatibleClientException,
			NotReadyClientException {
		if (!ready) {
			throw new NotReadyClientException();
		}
		if (!serverCompatible) {
			throw new IncompatibleClientException();
		}
		if (!valid) {
			throw new InvalidClientException();
		}

		next = camera.clone();
		synchronized (sender) {
			sender.notify();
		}
	}

	/**
	 * Retrieves the camera sent by the server and representing the intial state
	 * of the camera in the simulation
	 * 
	 * @return Instance of the Camera class sent by the server
	 * @throws InvalidClientException
	 *             No active connection with the server
	 * @throws IncompatibleClientException
	 *             The server is not in a version compatible with the client
	 */

	public Camera getCamera() throws InvalidClientException, IncompatibleClientException {
		if (!serverCompatible) {
			throw new IncompatibleClientException();
		}
		if (!valid) {
			throw new InvalidClientException();
		}

		// TODO Check if thread-safe statement
		return receivedCamera;
	}

	private void senderWork() {
		Camera previous = next;
		while (true) {
			if (!serverCompatible || !valid) {
				return;
			}
			if (ready && previous != next) {
				sendCamera(next);
				previous = next;
			}
			try {
				synchronized (sender) {
					sender.wait(500);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private void sendCamera(Camera camera) {
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

	private void recvCamera() {
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