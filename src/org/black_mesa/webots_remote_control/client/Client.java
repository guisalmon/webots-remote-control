package org.black_mesa.webots_remote_control.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.communication_structures.CommunicationStructure;
import org.black_mesa.webots_remote_control.database.Server;
import org.black_mesa.webots_remote_control.listeners.ClientListener;

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

	private final Server mServer;

	private ConnectionState mState = ConnectionState.INIT;

	private final ClientListener mListener;

	private final Socket mSocket = new Socket();

	private boolean mDispose = false;

	private final Thread mReceivingThread;
	private final Thread mSendingThread;

	private final SparseArray<CommunicationStructure> mBoarding = new SparseArray<CommunicationStructure>();
	private SparseArray<CommunicationStructure> mInitialData;
	private final SparseArray<List<CommunicationStructure>> mAdditionalData =
			new SparseArray<List<CommunicationStructure>>();

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
	public Client(final Server server, final ClientListener listener) {
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
	public final void board(final CommunicationStructure data) {
		int id = data.getId();
		synchronized (mBoarding) {
			mBoarding.put(id, data.board(mBoarding.get(id)));
		}
	}

	/**
	 * Liberates the resources allocated to this client (terminates threads and
	 * closes socket).
	 */
	public final void dispose() {
		mDispose = true;
	}

	/**
	 * Getter for the client state.
	 * 
	 * @return Current state of the client.
	 */
	public final ConnectionState getState() {
		return mState;
	}

	/**
	 * When the connection is established, the server sends initial data to the
	 * client. The data is available after OnStateChange has been called with
	 * CONNECTED as the new state.
	 * 
	 * @return Initial data received by the client.
	 */
	public final SparseArray<CommunicationStructure> getInitialData() {
		// This array will never be modified by the Client, we don't need to
		// copy it
		Log.d(getClass().getName(), "Request for received data");
		Log.d(getClass().getName(), "Returning " + mInitialData.get(0));
		return mInitialData;
	}

	/**
	 * Use this function to retrieve additional objects sent by the server.
	 * Availability of objects is notified by an OnReception call.
	 * 
	 * @return Array containing the data.
	 */
	public final SparseArray<List<CommunicationStructure>> getAdditionalData() {
		// We need to make a copy of the data, because the reception of a new
		// object would trigger a modification of the array
		SparseArray<List<CommunicationStructure>> ret;
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

			SparseArray<CommunicationStructure> initialData = new SparseArray<CommunicationStructure>();

			for (int i = 0; i < n; i++) {
				CommunicationStructure r = (CommunicationStructure) in.readObject();
				if (!r.checkIntegrity()) {
					changeState(ConnectionState.COMMUNICATION_ERROR);
					return;
				}
				initialData.put(r.getId(), r);
				Log.d(getClass().getName(), "Received object of id " + r.getId());
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

	private void sendingRoutine(final ObjectOutputStream out) throws IOException {
		SparseArray<CommunicationStructure> data;

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

	private void receivingRoutine(final ObjectInputStream in) throws ClassNotFoundException, IOException {
		CommunicationStructure o;
		try {
			o = (CommunicationStructure) in.readObject();
			synchronized (mAdditionalData) {
				List<CommunicationStructure> l = mAdditionalData.get(o.getId());
				if (l == null) {
					l = new ArrayList<CommunicationStructure>();
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

	private void notifyReception(final CommunicationStructure data) {
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				mListener.onReception(mServer, data);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}
}