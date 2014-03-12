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

public class ConnectionManager implements ClientListener {
	private List<ConnectionManagerListener> listeners = new ArrayList<ConnectionManagerListener>();
	private Map<Server, Client> connections = new Hashtable<Server, Client>();

	public void addListener(ConnectionManagerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ConnectionManagerListener listener) {
		listeners.remove(listener);
	}

	public void stop() {
		for (Client c : connections.values()) {
			c.dispose();
		}
		connections.clear();
	}

	public void start() {
	}

	public void addServer(Server server) {
		if (connections.containsKey(server)) {
			throw new IllegalArgumentException(server + " was already present in the manager");
		}
		connections.put(server, new Client(server, this));
	}

	public void removeServer(Server server) {
		connections.remove(server);
	}

	@Override
	public void onStateChange(Client.State state) {
		for (ConnectionManagerListener l : listeners) {
			l.onStateChange();
		}
		Log.d(getClass().getName(), state.toString());
	}

	public Client getClient(Server server) {
		return connections.get(server);
	}

	@Override
	public void onReception(RemoteObject data) {
	}
}
