package org.black_mesa.webots_remote_control.testing.client;

import java.net.InetAddress;

import org.black_mesa.webots_remote_control.Camera;
import org.black_mesa.webots_remote_control.CameraClient;
import org.black_mesa.webots_remote_control.InvalidClientException;

import android.util.Log;

/**
 * @author Ilja Kroonen
 */
public class ClientTest {

	public static void launch(String host, int port) {
		try {
			InetAddress address = InetAddress.getByName(host);
			CameraClient client = new CameraClient(address, port);
			Thread.sleep(100);
			Camera camera = client.getCamera();
			if (camera != null) {
				Log.i("ClientTest", camera.toString());
			} else {
				Log.i("ClientTest", "Did not receive camera");
			}
			camera = new Camera(0, 0, 0, 0, 0, 0, 0);
			for (int i = 0; i < 10; ++i) {
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
				Log.i("ClientTest", camera.toString());
				client.onCameraChange(camera);
			}
		} catch (InvalidClientException e) {
			Log.e("ClientTest", "Camera instance was invalid");
		} catch (Exception e) {
			Log.e("ClientTest", e.toString());
		}
	}
}
