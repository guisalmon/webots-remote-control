package org.black_mesa.webots_remote_control.testing.client;

import java.net.InetAddress;

import org.black_mesa.webots_remote_control.Camera;
import org.black_mesa.webots_remote_control.Client;

public class ClientTest {

	public static void launch(String host, int port) {
		try {
			InetAddress address = InetAddress.getByName(host);
			Client client = new Client(address, port);
			Camera camera = client.getCamera();
			System.out.println(camera.toString());
			for (int i = 0; i < 1000; ++i) {
				switch (i % 3) {
				case 0:
					camera.changeOrientation(1, 1);
					break;
				case 1:
					camera.moveSideways(1, 1);
					break;
				case 2:
					camera.moveStraight(1);
					break;
				}
				client.onCameraChange(camera);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
