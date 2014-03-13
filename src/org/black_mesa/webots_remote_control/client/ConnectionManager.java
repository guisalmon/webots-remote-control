package org.black_mesa.webots_remote_control.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.listeners.ClientListener;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

import android.util.Log;

/**
 * Manages a collection of clients.
 * 
 * @author Ilja Kroonen
 * 
 */
public class ConnectionManager {
	private final List<ConnectionManagerListener> mListeners = new ArrayList<ConnectionManagerListener>();
	private final Map<Server, Client> mConnections = new Hashtable<Server, Client>();
	private final ClientListener mClientListener;

	/**
	 * Instantiates the ConnectionManager.
	 */
	public ConnectionManager() {
		mClientListener = new ClientListener() {

			@Override
			public void onStateChange(Server server, ConnectionState state) {
				Client source = mConnections.get(server);
				if (source == null) {
					return;
				}
				switch(state) {
				case COMMUNICATION_ERROR:
					mConnections.remove(server);
					break;
				case CONNECTED:
					break;
				case CONNECTION_ERROR:
					mConnections.remove(server);
					break;
				case DISPOSED:
					mConnections.remove(server);
					break;
				case INIT:
					break;
				}
				for (ConnectionManagerListener l : mListeners) {
					l.onStateChange(server, state);
				}
				Log.d(getClass().getName(), state.toString());
			}

			@Override
			public void onReception(Server server, RemoteObject data) {
				// This will be needed to implement further features
			}
		};
	}

	/**
	 * Adds a listener to this ConnectionManager. It will be notified on any
	 * state change in the connections.
	 * 
	 * @param listener
	 *            Listener that will be added.
	 */
	public void addListener(ConnectionManagerListener listener) {
		mListeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            Listener that will be removed.
	 */
	public void removeListener(ConnectionManagerListener listener) {
		mListeners.remove(listener);
	}

	/**
	 * Stops the ConnectionManager. This call closes all connections.
	 */
	public void stop() {
		Log.d(getClass().getName(), "Stop");
		for (Client c : mConnections.values()) {
			c.dispose();
		}
		mConnections.clear();
	}

	/**
	 * Starts the ConnectionManager.
	 */
	public void start() {
		Log.d(getClass().getName(), "Start");
	}

	/**
	 * Connects to a server.
	 * 
	 * @param server
	 *            Server to connect.
	 */
	public void addServer(Server server) {
		Log.d(getClass().getName(), "Adding server");
		if (mConnections.containsKey(server)) {
			throw new IllegalArgumentException(server + " was already present in the manager");
		}
		mConnections.put(server, new Client(server, mClientListener));
	}

	/**
	 * Removes a server from the ConnectionManager and disconnects its client.
	 * 
	 * @param server
	 *            Server
	 */
	public void removeServer(Server server) {
		mConnections.get(server).dispose();
		mConnections.remove(server);
	}

	/**
	 * Returns the client corresponding to a server.
	 * 
	 * @param server
	 *            Server corresponding to the client we need.
	 * @return Client corresponding to the server.
	 */
	public Client getClient(Server server) {
		return mConnections.get(server);
	}
}