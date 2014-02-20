package org.black_mesa.webots_remote_control.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.exceptions.IncompatibleClientException;
import org.black_mesa.webots_remote_control.exceptions.InvalidClientException;
import org.black_mesa.webots_remote_control.listeners.ClientEventListener;
import org.black_mesa.webots_remote_control.remote_object_state.RemoteObjectState;

import android.app.Activity;
import android.util.Log;

/**
 * This class controls communications between the application and the server
 * 
 * @author Ilja Kroonen
 * 
 */
public class Client {
	// TODO This should be a parameter
	// TODO Timeout
	private static final int REFRESH_TICK = 100;

	private ObjectOutputStream outputStream = null;
	private Socket socket;

	private boolean valid = true;
	private boolean serverCompatible = true;

	private RemoteObjectState received = null;

	private final Object lock = new Object();
	private final Thread clientThread;
	private RemoteObjectState next;

	private final ClientEventListener listener;
	private final Activity activity;

	/**
	 * Instantiates a Client
	 * 
	 * @param address
	 *            Address of the server
	 * @param port
	 *            Port for the connection
	 * @param listener
	 *            Listener that will be notified when the server sends the
	 *            initial state of the object
	 * @param activity
	 *            The activity of the application ; the onObjectReceived event
	 *            will be dispatched using the runOnUiThread method on this
	 *            activity
	 */
	public Client(InetAddress address, int port, ClientEventListener listener, Activity activity) {
		final InetAddress finalAddress = address;
		final int finalPort = port;
		this.listener = listener;
		this.activity = activity;

		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket = new Socket(finalAddress, finalPort);
					recv();
					clientRoutine();
				} catch (IOException e) {
					valid = false;
				}
			}
		});
		clientThread.start();
	}

	/**
	 * Sends the new state to the server
	 * 
	 * @param state
	 *            Reference to the state we want to send
	 * @throws InvalidClientException
	 *             There is no active connection with the server
	 * @throws IncompatibleClientException
	 *             The server is not in a version compatible with the client
	 */
	public void onStateChange(RemoteObjectState state) throws InvalidClientException, IncompatibleClientException {
		if (!serverCompatible) {
			throw new IncompatibleClientException(R.string.server_incompatible_with_client);
		}
		if (!valid) {
			throw new InvalidClientException(R.string.invalid_client);
		}

		next = state.clone();
		synchronized (lock) {
			lock.notify();
		}
	}

	/**
	 * Liberates the resources used by the Client: terminates the thread and
	 * closes the socket
	 */
	public void dispose() {
		valid = false;
	}

	private void clientRoutine() {
		RemoteObjectState previous = null;
		while (true) {
			if (!serverCompatible || !valid) {
				try {
					socket.close();
				} catch (Exception e) {
				}
				return;
			}
			if (previous != next) {
				send(next);
				previous = next;
			}
			try {
				synchronized (lock) {
					lock.wait(REFRESH_TICK);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private void send(RemoteObjectState state) {
		try {
			if (outputStream == null) {
				outputStream = new ObjectOutputStream(socket.getOutputStream());
			}
			outputStream.writeObject(state);
		} catch (IOException e) {
			Log.e(this.getClass().getName(), e.toString());
			valid = false;
		}
	}

	private void recv() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			received = (RemoteObjectState) in.readObject();
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.onObjectReceived(received);
				}
			});
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
