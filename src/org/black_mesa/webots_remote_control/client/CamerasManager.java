package org.black_mesa.webots_remote_control.client;

import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.communication_structures.CameraInstruction;
import org.black_mesa.webots_remote_control.communication_structures.CameraInstructionQueue;
import org.black_mesa.webots_remote_control.listeners.CameraTouchHandlerListener;

/**
 * Factory for CameraTouchHandlerListener instances. When instanciating a touch
 * handler, such an instance will be passed as parameter, linking the touch
 * handler to a remote camera.
 * 
 * @author Ilja Kroonen
 * 
 */
public class CamerasManager {
	private static final double SCALE_MOVE_FORWARD = 16;
	private static final double SCALE_MOVE_SIDE = .005;
	private static final double SCALE_TURN_PITCH = Math.PI;
	private static final double SCALE_TIMED_MOVE_FORWARD = .00005;
	private final ConnectionManager mConnectionManager;

	/**
	 * Instantiates the CamerasManager.
	 * 
	 * @param connectionManager
	 *            ConnectionManager that will be used to retrieve Client
	 *            instances.
	 */
	public CamerasManager(final ConnectionManager connectionManager) {
		mConnectionManager = connectionManager;
	}

	/**
	 * Instantiates a CameraTouchHandlerListener linked to a specific remote
	 * camera.
	 * 
	 * @param server
	 *            Server of the remote camera.
	 * @param cameraId
	 *            Id of the remote camera on the server.
	 * @return The listener.
	 */
	public final CameraTouchHandlerListener makeListener(final Server server, final int cameraId) {
		return new CameraTouchHandlerListener() {
			private Client mClient;
			private CameraInstructionQueue mCamera;

			@Override
			public void moveForward(final float forward) {
				if (!init()) {
					return;
				}
				CameraInstruction instruction = CameraInstruction.move(0, 0, forward * SCALE_MOVE_FORWARD);
				mCamera.add(instruction);
				mClient.board(mCamera);
			}

			@Override
			public void moveForward(final float forward, final long time) {
				if (!init()) {
					return;
				}
				CameraInstruction instruction =
						CameraInstruction.move(0, 0, forward * SCALE_TIMED_MOVE_FORWARD * time);
				mCamera.add(instruction);
				mClient.board(mCamera);
			}

			@Override
			public void moveSide(final float right, final float up, final long time) {
				if (!init()) {
					return;
				}
				CameraInstruction instruction =
						CameraInstruction.move((right * time) * SCALE_MOVE_SIDE, (-up * time) * SCALE_MOVE_SIDE, 0);
				mCamera.add(instruction);
				mClient.board(mCamera);
			}

			@Override
			public void turnPitch(final float turn, final float pitch) {
				if (!init()) {
					return;
				}
				CameraInstruction instruction = CameraInstruction.turn(turn * SCALE_TURN_PITCH);
				mCamera.add(instruction);
				instruction = CameraInstruction.pitch(pitch * SCALE_TURN_PITCH);
				mCamera.add(instruction);
				mClient.board(mCamera);
			}

			@Override
			public void moveRightForward(final float right, final float forward, final float time) {
				if (!init()) {
					return;
				}
				CameraInstruction instruction =
						CameraInstruction
								.move((right * time) * SCALE_MOVE_SIDE, 0, -(forward * time) * SCALE_MOVE_SIDE);
				mCamera.add(instruction);
				mClient.board(mCamera);
			}

			private boolean init() {
				if (mClient == null) {
					mClient = mConnectionManager.getClient(server);
				}
				if (mClient != null && mCamera == null) {
					mCamera = (CameraInstructionQueue) mClient.getInitialData().get(cameraId);
				}
				return mClient != null && mCamera != null;
			}
		};
	}
}