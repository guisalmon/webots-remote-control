package org.black_mesa.webots_remote_control.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.listeners.ClientListener;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

/**
 * This class controls communications between the application and a server.
 * 
 * @author Ilja Kroonen
 * 
 */

public class Client {
	private static final int SENDING_INTERVAL = 32;
	private static final int SOCKET_TIMEOUT = 1000;
	
	private final Server server;

	private ConnectionState s = ConnectionState.INIT;

	private final ClientListener listener;

	private final Socket socket = new Socket();

	private boolean dispose = false;

	private final Thread receivingThread;
	private final Thread sendingThread;

	private final SparseArray<RemoteObject> boarding = new SparseArray<RemoteObject>();
	private SparseArray<RemoteObject> initialData;
	private final SparseArray<List<RemoteObject>> additionalData = new SparseArray<List<RemoteObject>>();

	/**
	 * Instantiates a Client and connects it to the server. This allocates
	 * resources that you must not forget to free later by calling dispose().
	 * 
	 * @param server
	 *            Server this client will connect to.
	 * @param listener
	 *            Listener to be notified of state changes and object
	 *            receptions.
	 */
	public Client(Server server, ClientListener listener) {
		this.listener = listener;
		this.server = server;

		receivingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InetSocketAddress address = new InetSocketAddress(Client.this.server.getAdress(), Client.this.server.getPort());
					socket.connect(address, SOCKET_TIMEOUT);
					socket.setSoTimeout(SOCKET_TIMEOUT);
				} catch (IOException e) {
					Log.d(getClass().getName(), e.getLocalizedMessage());
					changeState(ConnectionState.CONNECTION_ERROR);
					return;
				}
				sendingThread.start();
				receivingRoutine();
			}
		});

		sendingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				sendingRoutine();
			}
		});

		receivingThread.start();
	}

	/**
	 * Boards data into the client. It will be sent as soon as possible.
	 * 
	 * @param data
	 *            Data that must be sent to the server.
	 */
	public void board(RemoteObject data) {
		int id = data.getId();
		synchronized (boarding) {
			boarding.put(id, data.board(boarding.get(id)));
		}
	}

	/**
	 * Liberates the resources allocated to this client (terminates threads and
	 * closes socket).
	 */
	public void dispose() {
		Log.d(getClass().getName(), "Dispose");
		dispose = true;
	}

	/**
	 * Getter for the client state.
	 * 
	 * @return Current state of the client.
	 */
	public ConnectionState getState() {
		return s;
	}

	/**
	 * When the connection is established, the server sends initial data to the
	 * client. The data is available after OnStateChange has been called with
	 * CONNECTED as the new state.
	 * 
	 * @return Initial data received by the client.
	 */
	public SparseArray<RemoteObject> getInitialData() {
		// This array will never be modified by the Client, we don't need to
		// copy it
		return initialData;
	}

	/**
	 * Use this function to retrieve additional objects sent by the server.
	 * Availability of objects is notified by an OnReception call.
	 * 
	 * @return Array containing the data.
	 */
	public SparseArray<List<RemoteObject>> getAdditionalData() {
		// We need to make a copy of the data, because the reception of a new
		// object would trigger a modification of the array
		SparseArray<List<RemoteObject>> ret;
		synchronized (additionalData) {
			ret = additionalData.clone();
		}
		return ret;
	}

	private void receivingRoutine() {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
			return;
		}

		Integer n;

		try {
			n = (Integer) in.readObject();
		} catch (ClassNotFoundException e) {
			changeState(ConnectionState.COMMUNICATION_ERROR);
			return;
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
			return;
		} catch (ClassCastException e) {
			changeState(ConnectionState.COMMUNICATION_ERROR);
			return;
		}

		Log.d(getClass().getName(), "Received the fucking integer: " + n);

		SparseArray<RemoteObject> initialData = new SparseArray<RemoteObject>();

		for (int i = 0; i < n; i++) {
			try {
				RemoteObject r = (RemoteObject) in.readObject();
				initialData.put(r.getId(), r);
			} catch (ClassNotFoundException e) {
				changeState(ConnectionState.COMMUNICATION_ERROR);
				return;
			} catch (IOException e) {
				changeState(ConnectionState.CONNECTION_ERROR);
				return;
			} catch (ClassCastException e) {
				changeState(ConnectionState.COMMUNICATION_ERROR);
				return;
			}
		}

		changeState(ConnectionState.CONNECTED);

		Log.d(getClass().getName(), "Received the fucking array");

		this.initialData = initialData;

		while (true) {
			if (dispose) {
				// The sendingRoutine will handle this
				return;
			}
			RemoteObject o;
			try {
				o = (RemoteObject) in.readObject();
				synchronized (additionalData) {
					List<RemoteObject> l = additionalData.get(o.getId());
					if (l == null) {
						l = new ArrayList<RemoteObject>();
						additionalData.put(o.getId(), l);
					}
					l.add(o);
				}
				notifyReception(o);
			} catch (SocketTimeoutException e) {
			} catch (ClassNotFoundException e) {
				changeState(ConnectionState.COMMUNICATION_ERROR);
				return;
			} catch (IOException e) {
				changeState(ConnectionState.CONNECTION_ERROR);
				return;
			} catch (ClassCastException e) {
				changeState(ConnectionState.COMMUNICATION_ERROR);
				return;
			}
		}
	}

	private void sendingRoutine() {
		Log.d(getClass().getName(), "Sending routine started");
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
			return;
		}

		while (true) {
			if (dispose) {
				Log.d(getClass().getName(), "Disposed, closing socket");
				changeState(ConnectionState.DISPOSED);
				try {
					socket.close();
				} catch (IOException e) {
				}
				return;
			}

			SparseArray<RemoteObject> data;

			synchronized (boarding) {
				data = boarding.clone();
				boarding.clear();
			}

			for (int i = 0; i < data.size(); i++) {
				try {
					out.writeObject(data.valueAt(i));
				} catch (IOException e) {
					dispose = true;
					changeState(ConnectionState.CONNECTION_ERROR);
				}
			}

			synchronized (sendingThread) {
				try {
					sendingThread.wait(SENDING_INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void changeState(final ConnectionState s) {
		if (s == ConnectionState.COMMUNICATION_ERROR || s == ConnectionState.CONNECTION_ERROR) {
			dispose = true;
		}
		this.s = s;
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				Client.this.listener.onStateChange(server, s);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}

	private void notifyReception(final RemoteObject data) {
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				Client.this.listener.onReception(server, data);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}
}