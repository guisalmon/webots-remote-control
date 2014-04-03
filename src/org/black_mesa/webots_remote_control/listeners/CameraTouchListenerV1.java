package org.black_mesa.webots_remote_control.listeners;

/**
 * Interface for the listener of the CameraTouchHandler.
 * 
 * @author Ilja Kroonen
 * 
 */
public interface CameraTouchListenerV1 {
	/**
	 * Indicates that the camera must move forward.
	 * 
	 * @param forward
	 *            Distance of the movement (typically between -0.5 and 0.5).
	 */
	void moveForward(float forward);

	/**
	 * Indicates that the camera must move sideways.
	 * 
	 * @param right
	 *            Distance of the movement along the right axis (typically between -0.5 and 0.5).
	 * @param up
	 *            Distance of the movement along the up axis (typically between -0.5 and 0.5).
	 * @param time
	 *            Time, in milliseconds, spent on the event. To get the final distance, distances have typically to be
	 *            multiplied by this value (typically around 32).
	 */
	void moveSide(float right, float up, long time);

	/**
	 * Indicates that the camera must change its orientation.
	 * 
	 * @param turn
	 *            Amplitude of the turn orientation change (typically between -0.5 and 0.5).
	 * @param pitch
	 *            Amplitude of the pitch orientation change (typically between -0.5 and 0.5).
	 */
	void turnPitch(float turn, float pitch);
}