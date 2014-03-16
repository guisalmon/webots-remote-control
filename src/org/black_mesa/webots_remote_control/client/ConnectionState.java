package org.black_mesa.webots_remote_control.client;

/**
 * Enumeration of the possible states of a client.
 * 
 * @author Ilja Kroonen
 * 
 */
public enum ConnectionState {
	/**
	 * Initial state of a Client after instanciation. No event is dispatched for
	 * this state.
	 */
	INIT,
	/**
	 * State of a Client when a connection is active with a valid server.
	 */
	CONNECTED,
	/**
	 * Indicates that there was a problem with the communication protocol
	 * between the Client and the Server. This can be a version problem
	 * (serialization error for example).
	 */
	COMMUNICATION_ERROR,
	/**
	 * Indicates that the connection to the server is broken.
	 */
	CONNECTION_ERROR,
	/**
	 * Indicates that the Client has been disposed using the dispose() method.
	 */
	DISPOSED
}