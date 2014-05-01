package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.communication_structures.CommunicationStructure;
import org.black_mesa.webots_remote_control.database.Server;

/**
 * Interface for the listeners of a Client.
 * 
 * @author Ilja Kroonen
 * 
 */
public interface ClientListener {
	/**
	 * Notifies the listener of a state change in the Client.
	 * 
	 * @param server
	 *            Server corresponding to the Client.
	 * @param state
	 *            New state of the Client.
	 */
	void onStateChange(Server server, ConnectionState state);

	/**
	 * Notifies the listener of the reception of an additional object by the
	 * Client.
	 * 
	 * @param server
	 *            Server corresponding to the Client.
	 * @param data
	 *            Object received.
	 */
	void onReception(Server server, CommunicationStructure data);
}