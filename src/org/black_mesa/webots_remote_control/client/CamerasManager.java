package org.black_mesa.webots_remote_control.client;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.listeners.CameraTouchHandlerListener;
import org.black_mesa.webots_remote_control.remote_object.CameraInstruction;
import org.black_mesa.webots_remote_control.remote_object.InstructionQueue;

/**
 * Factory for CameraTouchHandlerListener instances. When instanciating a touch
 * handler, such an instance will be passed as parameter, linking the touch
 * handler to a remote camera.
 * 
 * @author Ilja Kroonen
 * 
 */
public class CamerasManager {
	/**
	 * Instantiates a CameraTouchHandlerListener linked to a specific remote
	 * camera.
	 * 
	 * @param connectionManager
	 *            TODO Remove this parameter as it can be statically retrieved.
	 * @param server
	 *            Server of the remote camera.
	 * @param cameraId
	 *            Id of the remote camera on the server.
	 * @return The listener.
	 */
	public static CameraTouchHandlerListener makeListener(final ConnectionManager connectionManager,
			final Server server, final int cameraId) {
		return new CameraTouchHandlerListener() {
			private Client client = connectionManager.getClient(server);
			private InstructionQueue camera = (InstructionQueue) client.getInitialData().get(cameraId);

			@Override
			public void moveForward(float forward) {
				CameraInstruction instruction = CameraInstruction.move(0, 0, forward * 16);
				camera.add(instruction);
				client.board(camera);
			}

			@Override
			public void moveSide(float right, float up, long time) {
				CameraInstruction instruction = CameraInstruction.move((right * time) / 128., (-up * time) / 128., 0);
				camera.add(instruction);
				client.board(camera);
			}

			@Override
			public void turnPitch(float turn, float pitch) {
				CameraInstruction instruction = CameraInstruction.turn(turn * Math.PI);
				camera.add(instruction);
				instruction = CameraInstruction.pitch(pitch * Math.PI);
				camera.add(instruction);
				client.board(camera);
			}

		};
	}
}
