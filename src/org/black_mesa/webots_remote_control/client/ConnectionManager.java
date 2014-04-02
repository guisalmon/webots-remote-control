package org.black_mesa.webots_remote_control.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.communication_structures.CommunicationStructure;
import org.black_mesa.webots_remote_control.listeners.ClientListener;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;

/**
 * Manages a collection of connections represented by Client objects.
 * 
 * @author Ilja Kroonen
 * 
 */
public class ConnectionManager {
	private final List<ConnectionManagerListener> mListeners = new ArrayList<ConnectionManagerListener>();
	private final Map<Server, Client> mConnections = new Hashtable<Server, Client>();
	private final ClientListener mClientListener;
	private List<Server> mSavedServers = new ArrayList<Server>();

	/**
	 * Instantiates the ConnectionManager.
	 */
	public ConnectionManager() {
		mClientListener = new ClientListener() {

			@Override
			public void onStateChange(final Server server, final ConnectionState state) {
				Client source = mConnections.get(server);
				if (source == null) {
					// The client could have queued a stateChange event in the
					// UI thread queue between the call to dispose and the
					// actual disposing. We just ignore the event.
					return;
				}
				if (state != ConnectionState.CONNECTED) {
					mConnections.remove(server);
				}

				for (ConnectionManagerListener l : mListeners) {
					l.onStateChange(server, state);
				}
			}

			@Override
			public void onReception(final Server server, final CommunicationStructure data) {
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
	public final void addListener(final ConnectionManagerListener listener) {
		mListeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            Listener that will be removed.
	 */
	public final void removeListener(final ConnectionManagerListener listener) {
		mListeners.remove(listener);
	}

	/**
	 * Connects to a server.
	 * 
	 * @param server
	 *            Server to connect.
	 */
	public final void addServer(final Server server) {
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
	public final void removeServer(final Server server) {
		Client client = mConnections.get(server);
		if (client == null) {
			return;
		}
		client.dispose();
		mConnections.remove(server);
	}

	/**
	 * Returns the client corresponding to a server.
	 * 
	 * @param server
	 *            Server corresponding to the client we need.
	 * @return Client corresponding to the server.
	 */
	public final Client getClient(final Server server) {
		return mConnections.get(server);
	}

	/**
	 * Closes all connections.
	 */
	public final void dispose() {
		for (Client c : mConnections.values()) {
			c.dispose();
		}
		mConnections.clear();
	}

	/**
	 * Getter for the server list.
	 * 
	 * @return List of connected servers.
	 * @deprecated Server list should be held somewhere else.
	 */
	@Deprecated
	public final List<Server> getServerList() {
		return new ArrayList<Server>(mConnections.keySet());
	}

	/**
	 * Saves the current servers to be restored later with the restore() method.
	 * 
	 * @deprecated Server list should be held somewhere else.
	 */
	@Deprecated
	public final void save() {
		mSavedServers = new ArrayList<Server>(mConnections.keySet());
	}

	/**
	 * Restores the connections previously saved with the save() method.
	 * 
	 * @deprecated Server list should be held somewhere else.
	 */
	@Deprecated
	public final void restore() {
		stop();
		start();
		for (Server s : mSavedServers) {
			addServer(s);
		}
	}

	/**
	 * Stops the ConnectionManager. This call closes all connections.
	 * 
	 * @deprecated Use dispose() instead.
	 */
	@Deprecated
	public final void stop() {
		dispose();
	}

	/**
	 * Starts the ConnectionManager.
	 * 
	 * @deprecated Calling this method is useless.
	 */
	@Deprecated
	public final void start() {
	}
}