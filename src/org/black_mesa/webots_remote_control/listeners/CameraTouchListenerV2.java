package org.black_mesa.webots_remote_control.listeners;

public interface CameraTouchListenerV2 extends CameraJoysticksViewListener {
	/**
	 * Indicates that the camera must change its orientation.
	 * 
	 * @param turn
	 *            Amplitude of the turn orientation change (typically between -0.5 and 0.5).
	 * @param pitch
	 *            Amplitude of the pitch orientation change (typically between -0.5 and 0.5).
	 */
	void turnPitch(float turn, float pitch);

	/**
	 * Indicates that the camera must move along the right and forward axis.
	 * 
	 * @param right
	 *            Distance of the movement along the right axis (typically between -0.5 and 0.5).
	 * @param forward
	 *            Distance of the movement along the forward axis (typically between -0.5 and 0.5).
	 * @param time
	 *            Time, in milliseconds, spent on the event. To get the final distance, distances have typically to be
	 *            multiplied by this value (typically around 32).
	 */
	void moveRightForward(float right, float forward, float time);
}
