package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.client.Client;
import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

public interface ClientListener {
	public void onStateChange(Client.State state);
	
	public void onReception(RemoteObject data);
}