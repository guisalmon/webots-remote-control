package org.black_mesa.webots_remote_control.listeners;

/**
 * 
 * @author Cassim Ketfi
 * 
 */
public interface CameraJoysticksViewListener {

	/**
	 * Create an area of given radius and initialize a joystick in the center with radius half of the given radius.
	 * 
	 * @param centerXJoystickLeft
	 *            coordinate on axis X for the center of the left joystick
	 * @param centerYJoystickLeft
	 *            coordinate on axis Y for the center of the left joystick
	 * @param joystickRadiusJoystickLeft
	 *            radius of the area of the left joystick
	 */
	public void onJoystickLeftCoordinateChanged(float centerXJoystickLeft, float centerYJoystickLeft,
			float joystickRadiusJoystickLeft);

	/**
	 * Create an area of given radius and initialize a joystick in the center with radius half of the given radius.
	 * 
	 * @param centerXJoystickRight
	 * @param centerYJoystickRight
	 * @param joystickRadiusJoystickRight
	 */
	public void onJoystickRightCoordinateChanged(float centerXJoystickRight, float centerYJoystickRight,
			float joystickRadiusJoystickRight);
}