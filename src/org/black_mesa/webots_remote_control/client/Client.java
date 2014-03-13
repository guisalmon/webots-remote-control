package org.black_mesa.webots_remote_control.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.listeners.ClientListener;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

public class Client {
	private static int SENDING_INTERVAL = 32;
	private static int SOCKET_TIMEOUT = 5000;

	private State s = State.INIT;

	private final ClientListener listener;

	private final Socket socket = new Socket();

	private boolean dispose = false;

	private final Thread receivingThread;
	private final Thread sendingThread;

	private final SparseArray<RemoteObject> boarding = new SparseArray<RemoteObject>();
	private SparseArray<RemoteObject> initialData = new SparseArray<RemoteObject>();

	public Client(Server server, ClientListener listener) {
		this.listener = listener;

		final InetSocketAddress address = new InetSocketAddress(server.getAdress(), server.getPort());

		receivingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket.connect(address, SOCKET_TIMEOUT);
					socket.setSoTimeout(SOCKET_TIMEOUT);
					receivingRoutine();
				} catch (IOException e) {
					Log.d(getClass().getName(), e.getLocalizedMessage());
					changeState(State.CONNECTION_ERROR);
				}
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

	public void board(RemoteObject data) {
		int id = data.getId();
		synchronized (boarding) {
			boarding.put(id, data.board(boarding.get(id)));
		}
	}

	public void dispose() {
		dispose = true;
	}

	public State getState() {
		return s;
	}

	public SparseArray<RemoteObject> getInitialData() {
		return initialData;
	}

	public enum State {
		INIT, CONNECTED, COMMUNICATION_ERROR, CONNECTION_ERROR, DISPOSED
	}

	private void receivingRoutine() {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			changeState(State.CONNECTION_ERROR);
			return;
		}

		Integer n;

		try {
			n = (Integer) in.readObject();
		} catch (ClassNotFoundException e) {
			changeState(State.COMMUNICATION_ERROR);
			return;
		} catch (IOException e) {
			changeState(State.CONNECTION_ERROR);
			return;
		} catch (ClassCastException e) {
			changeState(State.COMMUNICATION_ERROR);
			return;
		}

		Log.d(getClass().getName(), "Received the fucking integer: " + n);

		SparseArray<RemoteObject> initialData = new SparseArray<RemoteObject>();

		for (int i = 0; i < n; i++) {
			try {
				RemoteObject r = (RemoteObject) in.readObject();
				initialData.put(r.getId(), r);
			} catch (ClassNotFoundException e) {
				changeState(State.COMMUNICATION_ERROR);
				return;
			} catch (IOException e) {
				changeState(State.CONNECTION_ERROR);
				return;
			} catch (ClassCastException e) {
				changeState(State.COMMUNICATION_ERROR);
				return;
			}
		}

		changeState(State.CONNECTED);

		Log.d(getClass().getName(), "Received the fucking array");

		this.initialData = initialData;

		sendingThread.start();

		while (true) {
			if (dispose) {
				// The sendingRoutine will handle this
				return;
			}
			RemoteObject o;
			try {
				o = (RemoteObject) in.readObject();
				notifyReception(o);
			} catch (SocketTimeoutException e) {
			} catch (ClassNotFoundException e) {
				changeState(State.COMMUNICATION_ERROR);
				return;
			} catch (IOException e) {
				changeState(State.CONNECTION_ERROR);
				return;
			} catch (ClassCastException e) {
				changeState(State.COMMUNICATION_ERROR);
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
			changeState(State.CONNECTION_ERROR);
			return;
		}

		while (true) {
			if (dispose) {
				changeState(State.DISPOSED);
				try {
					socket.close();
				} catch (IOException e) {
				}
				return;
			}

			SparseArray<RemoteObject> data;

			synchronized (boarding) {
				data = boarding.clone();
			}

			for (int i = 0; i < data.size(); i++) {
				try {
					out.writeObject(data.valueAt(i));
				} catch (IOException e) {
					dispose = true;
					changeState(State.CONNECTION_ERROR);
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

	private void changeState(final State s) {
		if (s == State.COMMUNICATION_ERROR || s == State.CONNECTION_ERROR) {
			dispose = true;
		}
		this.s = s;
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				Client.this.listener.onStateChange(s);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}

	private void notifyReception(final RemoteObject data) {
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				Client.this.listener.onReception(data);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}
}