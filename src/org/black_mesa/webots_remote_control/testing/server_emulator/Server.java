package org.black_mesa.webots_remote_control.testing.server_emulator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Ilja Kroonen
 */
public class Server {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(42511);
			System.out.println("Testing server online on port " + serverSocket.getLocalPort());
			Socket socket = serverSocket.accept();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			Camera camera = new Camera(0, 0, 0, 0, 0, 1, 0);
			out.writeObject(camera);
			while (true) {
				camera = (Camera) in.readObject();
				System.out.println(camera.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
