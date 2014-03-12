package org.black_mesa.webots_remote_control.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.Client.State;
import org.black_mesa.webots_remote_control.listeners.ClientListener;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

public class ClientV2 {
	private static int SENDING_INTERVAL = 32;
	private static int SOCKET_TIMEOUT = 5000;

	private State s = State.CREATED;

	private final ClientListener listener;

	private final Socket socket = new Socket();

	private boolean dispose = false;

	private final Thread receivingThread;
	private final Thread sendingThread;

	private final SparseArray<RemoteObject> boarding = new SparseArray<RemoteObject>();

	public ClientV2(Server server, ClientListener listener) {
		this.listener = listener;

		final SocketAddress address = new InetSocketAddress(server.getAdress(), server.getPort());

		sendingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket.connect(address, SOCKET_TIMEOUT);
					changeState(State.CONNECTED);
					socket.setSoTimeout(SOCKET_TIMEOUT);
					sendingThread.start();
					receivingRoutine();
				} catch (IOException e) {
					changeState(State.CONNECTION_FAILED);
				}
			}
		});

		receivingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				sendingRoutine();
			}
		});

		receivingThread.start();
	}

	public void board(RemoteObject data) {
		synchronized (boarding) {
			int id = data.getId();
			boarding.put(id, data.board(boarding.get(id)));
		}
	}

	public void dispose() {
		dispose = true;
	}

	public State getState() {
		return s;
	}

	public enum State {
		CONNECTED, CREATED, INCOMPATIBLE, CONNECTION_FAILED, CONNECTION_CLOSED
	}

	private void receivingRoutine() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

			while (true) {
				if (dispose) {
					// The sendingRoutine will handle this
					return;
				}
				RemoteObject o = (RemoteObject) in.readObject();
				notifyReception(o);
			}

		} catch (EOFException e) {
			// We have a serious problem with the stream
			changeState(State.CONNECTION_CLOSED);
		} catch (ClassNotFoundException e) {
			// The server sent us a class that we don't have, check the server
			// specifications to solve this problem
			changeState(State.INCOMPATIBLE);
		} catch (ClassCastException e) {
			// The server did send an unexpected type, check the server
			// specifications to solve this problem
			changeState(State.INCOMPATIBLE);
		} catch (IOException e) {
			// TODO
		}
	}

	private void sendingRoutine() {
	}

	private void changeState(State s) {
		this.s = s;
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				ClientV2.this.listener.onStateChange();
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}

	private void notifyReception(final RemoteObject data) {
		Runnable notification = new Runnable() {
			@Override
			public void run() {
				ClientV2.this.listener.onReception(data);
			}
		};
		new Handler(Looper.getMainLooper()).post(notification);
	}
}