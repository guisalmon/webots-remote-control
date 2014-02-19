package org.black_mesa.webots_remote_control.exceptions;

import android.content.res.Resources;

/**
 * Throws when the client detects that the server is not compatible (typically
 * when deserialization fails)
 * 
 * @author Ilja Kroonen
 * 
 */
public class IncompatibleClientException extends Exception {
	private static final long serialVersionUID = -4023651496164215132L;
	
	public IncompatibleClientException(int stringId) {
		super(Resources.getSystem().getString(stringId));
	}
}
