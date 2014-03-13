package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.ConnectionState;

/**
 * Interface for the listeners of a ConnectionManager.
 * 
 * @author Ilja Kroonen
 * 
 */
public interface ConnectionManagerListener {
	/**
	 * Notifies the listener that the state of one of the connections has
	 * changed. This will not be called after the server has been removed from
	 * the manager.
	 * 
	 * @param server
	 *            Server corresponding to the connection of which the state has
	 *            changed.
	 * @param state
	 *            New state of that connection.
	 */
	public void onStateChange(Server server, ConnectionState state);
}