package org.black_mesa.webots_remote_control;

/**
 * Throws when the client detects that the server is not compatible (typically
 * when deserialization fails)
 * 
 * @author Ilja Kroonen
 * 
 */
public class IncompatibleServerException extends Exception {
	private static final long serialVersionUID = -4023651496164215132L;
}
