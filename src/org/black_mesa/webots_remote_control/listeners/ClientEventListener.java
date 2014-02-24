package org.black_mesa.webots_remote_control.listeners;

import java.util.List;

import org.black_mesa.webots_remote_control.remote_object_state.RemoteObjectState;

public interface ClientEventListener {
	public void onReception(List<RemoteObjectState> list);
}
