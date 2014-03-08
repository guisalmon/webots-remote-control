package org.black_mesa.webots_remote_control.remote_object;

import java.io.Serializable;

/**
 * Represents a RemoteObject. This can for example be a camera or a robot. The
 * implementation can hold the state of the object (position for example), or be
 * a simple instruction queue.
 * 
 * @author Ilja Kroonen
 * 
 */
public abstract class RemoteObject implements Serializable {
	private static final long serialVersionUID = -6066263437899074202L;
	private final int id;

	protected RemoteObject(int id) {
		this.id = id;
	}

	/**
	 * Identifies the RemoteObject in a unique way. Normally, this id is given
	 * by the server at connection, and never changes.
	 * 
	 * @return Id of this RemoteObject.
	 */
	public int getId() {
		return id;
	}

	/**
	 * This method is used to board the new informations about the remote object
	 * into a client.
	 * 
	 * @param previous
	 *            Previous RemoteObject version for the same id in the boarding
	 *            table of the client.
	 * @return Reference to a new version that will replace the old one. The
	 *         object referenced should never be modified afterwards.
	 */
	public abstract RemoteObject board(RemoteObject previous);
}