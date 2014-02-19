package org.black_mesa.webots_remote_control.exceptions;

import android.content.res.Resources;

/**
 * Throws when the client is not correctly connected to a server (typically
 * thrown if the client could not reach host or has been disposed)
 * 
 * @author Ilja Kroonen
 * 
 */
public class InvalidClientException extends Exception {
	private static final long serialVersionUID = -4777179986968436726L;
	
	public InvalidClientException(int stringId) {
		super(Resources.getSystem().getString(stringId));
	}
}
