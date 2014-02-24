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
	private static final int REFRESH_TICK = 100;
	private static final int TIMEOUT = 1000;

	private ObjectOutputStream outputStream = null;
	private Socket socket;

	private ClientState s = ClientState.CREATED;
	private boolean dispose = false;

	private final Object threadWaitingObject = new Object();
	private final Thread thread;

	private final ClientEventListener listener;
	private final Activity activity;

	/*
	 * We use a Hashtable because we do not want objects about to be sent to
	 * accumulate ; if one object gets updated 10 times before it can be sent,
	 * we only want to send the most recent value
	 */
	private final Object boardingLock = new Object();
	private final Hashtable<Integer, RemoteObjectState> boarding = new Hashtable<Integer, RemoteObjectState>();

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

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SocketAddress destination = new InetSocketAddress(finalAddress, finalPort);
					socket = new Socket();
					socket.connect(destination, TIMEOUT);
					socket.setSoTimeout(TIMEOUT);
					recv();
					clientRoutine();
				} catch (IOException e) {
					s = ClientState.INVALID;
				}
			}
		});
		thread.start();
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
		switch (s) {
		case INVALID:
			throw new InvalidClientException(R.string.invalid_client);
		case INCOMPATIBLE:
			throw new IncompatibleClientException(R.string.server_incompatible_with_client);
		default:
			break;
		}

		synchronized (boardingLock) {
			boarding.put(state.getId(), state.clone());
		}

		synchronized (threadWaitingObject) {
			threadWaitingObject.notify();
		}
	}

	/**
	 * Liberates the resources used by the Client: terminates the thread and
	 * closes the socket
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

			List<RemoteObjectState> l;

			synchronized (boardingLock) {
				// We have to entirely copy the the references while we have the
				// lock because the iterator on Hashtable is only fail-fast
				l = new ArrayList<RemoteObjectState>(boarding.values());
				boarding.clear();
			}

			for (RemoteObjectState s : l) {
				send(s);
			}

			try {
				synchronized (threadWaitingObject) {
					threadWaitingObject.wait(REFRESH_TICK);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private void send(RemoteObjectState state) {
		try {
			outputStream.writeObject(state);
		} catch (IOException e) {
			Log.e(this.getClass().getName(), e.toString());
			s = ClientState.INVALID;
		}
	}

	private void recv() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Integer nb = (Integer) in.readObject();

			// We use a Hashtable to check for doubles
			final Hashtable<Integer, RemoteObjectState> receptionTable = new Hashtable<Integer, RemoteObjectState>();

			for (int i = 0; i < nb; i++) {
				RemoteObjectState o = (RemoteObjectState) in.readObject();
				receptionTable.put(o.getId(), o);
			}

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.onReception(new ArrayList<RemoteObjectState>(receptionTable.values()));
				}
			});
		} catch (IOException e) {
			Log.d(this.getClass().getName(), e.toString());
			s = ClientState.INVALID;
		} catch (ClassNotFoundException e) {
			Log.d(this.getClass().getName(), e.toString());
			s = ClientState.INCOMPATIBLE;
		} catch (ClassCastException e) {
			Log.d(this.getClass().getName(), e.toString());
			s = ClientState.INCOMPATIBLE;
		}
	}
}
