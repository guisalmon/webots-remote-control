package org.black_mesa.webots_remote_control.client;

import org.black_mesa.webots_remote_control.remote_object_state.RemoteObjectState;

public interface ClientEventListener {
	public void onObjectReceived(RemoteObjectState state);
}
