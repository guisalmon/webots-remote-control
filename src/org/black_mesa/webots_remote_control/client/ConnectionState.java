package org.black_mesa.webots_remote_control.client;

/**
 * Enumeration of the possible states of a client.
 * 
 * @author Ilja Kroonen
 * 
 */
public enum ConnectionState {
	INIT, CONNECTED, COMMUNICATION_ERROR, CONNECTION_ERROR, DISPOSED
}