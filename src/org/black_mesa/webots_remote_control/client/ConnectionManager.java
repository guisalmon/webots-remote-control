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
	private final List<ConnectionManagerListener> listeners = new ArrayList<ConnectionManagerListener>();
	private final Map<Server, Client> connections = new Hashtable<Server, Client>();
	private final ClientListener clientListener;

	/**
	 * Instantiates the ConnectionManager.
	 */
	public ConnectionManager() {
		clientListener = new ClientListener() {

			@Override
			public void onStateChange(Server server, ConnectionState state) {
				Client source = connections.get(server);
				if (source == null) {
					return;
				}
				for (ConnectionManagerListener l : listeners) {
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
		listeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            Listener that will be removed.
	 */
	public void removeListener(ConnectionManagerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Stops the ConnectionManager. This call closes all connections.
	 */
	public void stop() {
		Log.d(getClass().getName(), "Stop");
		for (Client c : connections.values()) {
			c.dispose();
		}
		connections.clear();
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
		if (connections.containsKey(server)) {
			throw new IllegalArgumentException(server + " was already present in the manager");
		}
		connections.put(server, new Client(server, clientListener));
	}

	/**
	 * Removes a server from the ConnectionManager and disconnects its client.
	 * 
	 * @param server
	 *            Server
	 */
	public void removeServer(Server server) {
		connections.get(server).dispose();
		connections.remove(server);
	}

	/**
	 * Returns the client corresponding to a server.
	 * 
	 * @param server
	 *            Server corresponding to the client we need.
	 * @return Client corresponding to the server.
	 */
	public Client getClient(Server server) {
		return connections.get(server);
	}
}