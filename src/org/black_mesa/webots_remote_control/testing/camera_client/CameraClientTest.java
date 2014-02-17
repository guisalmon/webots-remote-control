package org.black_mesa.webots_remote_control.testing.camera_client;

import java.net.InetAddress;

import org.black_mesa.webots_remote_control.Camera;
import org.black_mesa.webots_remote_control.CameraClient;
import org.black_mesa.webots_remote_control.Exceptions.InvalidClientException;

import android.util.Log;

/**
 * @author Ilja Kroonen
 */
public class CameraClientTest {

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
			camera = new Camera(0, 0, 0, 0, 0, 1, 0);
			for (int i = 0; i < 10; ++i) {
				Thread.sleep(100);
				switch (i % 3) {
				case 0:
					camera.pitch(1);
					break;
				case 1:
					camera.move(1, 1, 1);
					break;
				case 2:
					camera.yaw(1);
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
