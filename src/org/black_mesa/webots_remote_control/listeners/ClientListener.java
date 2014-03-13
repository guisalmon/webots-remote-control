package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

public interface ClientListener {
	public void onStateChange(Server server, ConnectionState state);
	
	public void onReception(Server server, RemoteObject data);
}