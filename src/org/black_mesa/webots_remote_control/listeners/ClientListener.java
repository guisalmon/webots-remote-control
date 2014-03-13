package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.Client;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

public interface ClientListener {
	public void onStateChange(Server server, Client.State state);
	
	public void onReception(Server server, RemoteObject data);
}