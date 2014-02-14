package org.black_mesa.webots_remote_control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {
	private InetAddress address;
	private int port;
	private Camera camera;
	
	public Client(String address) {
		String[] split = address.split(":");
		// TODO
	}
	
	@Override
	public void run() {
		try {
			Socket socket = new Socket(address, port);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onCameraChange(Camera camera) {
		synchronized (this) {
			this.camera = camera.clone();
		}
	}
}
