package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.ConnectionState;

public interface ConnectionManagerListener {
	public void onStateChange(Server server, ConnectionState state);
}
