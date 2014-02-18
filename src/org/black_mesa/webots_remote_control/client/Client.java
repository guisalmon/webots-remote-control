package org.black_mesa.webots_remote_control.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.black_mesa.webots_remote_control.exceptions.IncompatibleClientException;
import org.black_mesa.webots_remote_control.exceptions.InvalidClientException;
import org.black_mesa.webots_remote_control.exceptions.NotReadyClientException;
import org.black_mesa.webots_remote_control.views.View;

import android.util.Log;

public class Client {

	private ObjectOutputStream outputStream = null;
	private Socket socket;

	private boolean ready = false;
	private boolean valid = true;
	private boolean serverCompatible = true;

	View received = null;

	Thread sender;
	View next;

	public Client(InetAddress address, int port) {
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
					recv();
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
	 * Sends the new state of the view to the server
	 * 
	 * @param view
	 *            Reference to the state we want to send
	 * @throws InvalidClientException
	 *             There is no active connection with the server
	 * @throws IncompatibleClientException
	 *             The server is not in a version compatible with the client
	 * @throws NotReadyClientException
	 *             The client is not yet ready ; you should check if the client
	 *             is ready with the isReady() method before using it
	 */
	public void onViewChange(View view) throws InvalidClientException, IncompatibleClientException,
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

		next = view.clone();
		synchronized (sender) {
			sender.notify();
		}
	}

	/**
	 * Retrieves the view sent by the server and representing the intial state
	 * of the view in the simulation
	 * 
	 * @return Instance of the View class sent by the server
	 * @throws InvalidClientException
	 *             No active connection with the server
	 * @throws IncompatibleClientException
	 *             The server is not in a version compatible with the client
	 */

	public View get() throws InvalidClientException, IncompatibleClientException {
		if (!serverCompatible) {
			throw new IncompatibleClientException();
		}
		if (!valid) {
			throw new InvalidClientException();
		}

		// TODO Check if thread-safe statement
		return received;
	}

	private void senderWork() {
		View previous = next;
		while (true) {
			if (!serverCompatible || !valid) {
				return;
			}
			if (ready && previous != next) {
				send(next);
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

	private void send(View view) {
		try {
			if (outputStream == null) {
				outputStream = new ObjectOutputStream(socket.getOutputStream());
			}
			outputStream.writeObject(view);
		} catch (IOException e) {
			Log.e(this.getClass().getName(), e.toString());
			valid = false;
		}
	}

	private void recv() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			received = (View) in.readObject();
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
