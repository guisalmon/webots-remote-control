package org.black_mesa.webots_remote_control.remote_object;

import java.io.Serializable;

public interface RemoteObject extends Serializable {
	public RemoteObject board();
	public int getId();
}