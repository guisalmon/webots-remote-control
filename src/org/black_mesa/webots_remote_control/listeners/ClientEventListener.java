package org.black_mesa.webots_remote_control.listeners;

import java.util.List;

import org.black_mesa.webots_remote_control.remote_object.RemoteObject;

public interface ClientEventListener {
	public void onReception(List<RemoteObject> list);
}