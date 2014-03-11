package org.black_mesa.webots_remote_control.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.black_mesa.webots_remote_control.exceptions.IncompatibleClientException;
import org.black_mesa.webots_remote_control.exceptions.InvalidClientException;
import org.black_mesa.webots_remote_control.listeners.ClientListener;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

import android.app.Activity;
import android.util.Log;

/**
 * This class controls communications between the application and the server.
 * 
 * @author Ilja Kroonen
 * 
 */
public class Client {
	// Time the Client thread will wait if not woken up
	private static final int WAIT_DURATION = 100;
	// Timeout for the socket
	private static final int SOCKET_TIMEOUT = 5000;

	private ObjectOutputStream outputStream = null;
	private Socket socket;

	private ClientState s = ClientState.CREATED;
	private boolean dispose = false;

	private final Thread thread;

	private final ClientListener listener;
	private final Activity activity;

	private final Object boardingLock = new Object();
	private final Hashtable<Integer, RemoteObject> boarding = new Hashtable<Integer, RemoteObject>();

	/**
	 * Instantiates a Client.
	 * 
	 * @param address
	 *            Address of the server.
	 * @param port
	 *            Port for the connection.
	 * @param listener
	 *            Listener that will be notified when the server sends the
	 *            initial state of the object.
	 * @param activity
	 *            The activity of the application ; the onObjectReceived event
	 *            will be dispatched using the runOnUiThread method on this
	 *            activity.
	 */
	public Client(InetAddress address, int port, ClientListener listener, Activity activity) {
		final InetAddress finalAddress = address;
		final int finalPort = port;
		this.listener = listener;
		this.activity = activity;

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SocketAddress destination = new InetSocketAddress(finalAddress, finalPort);
					socket = new Socket();
					socket.connect(destination, SOCKET_TIMEOUT);
					socket.setSoTimeout(SOCKET_TIMEOUT);
					receiveInitialStates();
					clientRoutine();
				} catch (IOException e) {
					s = ClientState.INVALID;
					Log.e(getClass().getName(), e.toString());
				}
			}
		});
		thread.start();
	}

	/**
	 * Sends the new state to the server.
	 * 
	 * @param state
	 *            Reference to the state we want to send.
	 * @throws InvalidClientException
	 *             There is no active connection with the server.
	 * @throws IncompatibleClientException
	 *             The server is not in a version compatible with the client.
	 */
	public void onStateChange(RemoteObject state) throws InvalidClientException, IncompatibleClientException {
		switch (s) {
		case INVALID:
			throw new InvalidClientException("The client is in an invalid state");
		case INCOMPATIBLE:
			throw new IncompatibleClientException("The client and the server are not compatible");
		default:
			break;
		}

		synchronized (boardingLock) {
			boarding.put(state.getId(), state.board(boarding.get(state.getId())));
		}

		synchronized (thread) {
			thread.notify();
		}
	}

	/**
	 * Liberates the resources used by the Client: terminates the thread and
	 * closes the socket.
	 */
	public void dispose() {
		dispose = true;
	}

	private void clientRoutine() throws IOException {
		outputStream = new ObjectOutputStream(socket.getOutputStream());
		while (true) {
			if (dispose || s == ClientState.INCOMPATIBLE || s == ClientState.INVALID) {
				if (dispose && s != ClientState.INCOMPATIBLE) {
					s = ClientState.INVALID;
				}
				try {
					socket.close();
				} catch (Exception e) {
				}
				return;
			}

			List<RemoteObject> l;

			synchronized (boardingLock) {
				// We have to entirely copy the the references while we have the
				// lock because the iterator on Hashtable is only fail-fast
				// Remember we can not perform IO operations while holding this
				// lock, because it can be held by the UI thread
				l = new ArrayList<RemoteObject>(boarding.values());
				boarding.clear();
			}

			for (RemoteObject s : l) {
				outputStream.writeObject(s);
			}

			try {
				synchronized (thread) {
					thread.wait(WAIT_DURATION);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private void receiveInitialStates() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Integer nb = (Integer) in.readObject();

			// We use a Hashtable to check for doubles
			final Hashtable<Integer, RemoteObject> receptionTable = new Hashtable<Integer, RemoteObject>();

			for (int i = 0; i < nb; i++) {
				RemoteObject o = (RemoteObject) in.readObject();
				receptionTable.put(o.getId(), o);
			}

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.onReception(new ArrayList<RemoteObject>(receptionTable.values()));
				}
			});
		} catch (IOException e) {
			// We have a serious problem with the stream
			Log.d(getClass().getName(), e.toString());
			s = ClientState.INVALID;
		} catch (ClassNotFoundException e) {
			// The server sent us a class that we don't have, check the server
			// specifications to solve this problem
			Log.d(getClass().getName(), e.toString());
			s = ClientState.INCOMPATIBLE;
		} catch (ClassCastException e) {
			// The server did send an unexpected type, check the server
			// specifications to solve this problem
			Log.d(getClass().getName(), e.toString());
			s = ClientState.INCOMPATIBLE;
		}
	}

	private enum ClientState {
		CREATED, CONNECTED, INCOMPATIBLE, INVALID
	}
}