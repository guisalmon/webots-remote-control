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

	private final Server mServer;

	private ConnectionState mState = ConnectionState.INIT;

	private final ClientListener mListener;

	private final Socket mSocket = new Socket();

	private boolean mDispose = false;

	private final Thread mReceivingThread;
	private final Thread mSendingThread;

	private final SparseArray<RemoteObject> mBoarding = new SparseArray<RemoteObject>();
	private SparseArray<RemoteObject> mInitialData;
	private final SparseArray<List<RemoteObject>> mAdditionalData = new SparseArray<List<RemoteObject>>();

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
		mListener = listener;
		mServer = server;

		mReceivingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				initTask();
			}
		});

		mSendingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				sendingTask();
			}
		});

		mReceivingThread.start();
	}

	/**
	 * Boards data into the client. It will be sent as soon as possible.
	 * 
	 * @param data
	 *            Data that must be sent to the server.
	 */
	public void board(RemoteObject data) {
		int id = data.getId();
		synchronized (mBoarding) {
			mBoarding.put(id, data.board(mBoarding.get(id)));
		}
	}

	/**
	 * Liberates the resources allocated to this client (terminates threads and
	 * closes socket).
	 */
	public void dispose() {
		mDispose = true;
	}

	/**
	 * Getter for the client state.
	 * 
	 * @return Current state of the client.
	 */
	public ConnectionState getState() {
		return mState;
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
		return mInitialData;
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
		synchronized (mAdditionalData) {
			ret = mAdditionalData.clone();
		}
		return ret;
	}

	private void initTask() {
		try {
			InetSocketAddress address = new InetSocketAddress(mServer.getAdress(), mServer.getPort());
			mSocket.connect(address, SOCKET_TIMEOUT);
			mSocket.setSoTimeout(SOCKET_TIMEOUT);
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
			return;
		}
		mSendingThread.start();
		receivingTask();
		try {
			mSocket.close();
		} catch (IOException e) {
		}
	}

	private void sendingTask() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(mSocket.getOutputStream());

			while (!mDispose) {
				sendingRoutine(out);
			}
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
		}
	}

	private void receivingTask() {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(mSocket.getInputStream());
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
			return;
		}

		Integer n;

		try {
			n = (Integer) in.readObject();

			SparseArray<RemoteObject> initialData = new SparseArray<RemoteObject>();

			for (int i = 0; i < n; i++) {
				RemoteObject r = (RemoteObject) in.readObject();
				initialData.put(r.getId(), r);
			}
			mInitialData = initialData;

			changeState(ConnectionState.CONNECTED);

			while (!mDispose) {
				receivingRoutine(in);
			}
		} catch (ClassNotFoundException e) {
			changeState(ConnectionState.COMMUNICATION_ERROR);
		} catch (IOException e) {
			changeState(ConnectionState.CONNECTION_ERROR);
		} catch (ClassCastException e) {
			changeState(ConnectionState.COMMUNICATION_ERROR);
		}
	}

	private void sendingRoutine(ObjectOutputStream out) throws IOException {
		SparseArray<RemoteObject> data;

		synchronized (mBoarding) {
			data = mBoarding.clone();
			mBoarding.clear();
		}

		for (int i = 0; i < data.size(); i++) {
			out.writeObject(data.valueAt(i));
		}

		synchronized (mSendingThread) {
			try {
				mSendingThread.wait(SENDING_INTERVAL);
			} catch (InterruptedException e) {
			}
		}
	}

	private void receivingRoutine(ObjectInputStream in) throws ClassNotFoundException, IOException {
		RemoteObject o;
		try {
			o = (RemoteObject) in.readObject();
			synchronized (mAdditionalData) {
				List<RemoteObject> l = mAdditionalData.get(o.getId());
				if (l == null) {
					l = new ArrayList<RemoteObject>();
					mAdditionalData.put(o.getId(), l);
				}
				l.add(o);
			}
			notifyReception(o);
		} catch (SocketTimeoutException e) {
		}
	}

	private void changeState(final ConnectionState s) {
		if (s != ConnectionState.CONNECTED) {
			mDispose = true;
		}
		mState = s;
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				mListener.onStateChange(mServer, s);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}

	private void notifyReception(final RemoteObject data) {
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				mListener.onReception(mServer, data);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}
}