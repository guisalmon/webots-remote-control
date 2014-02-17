package org.black_mesa.webots_remote_control;

import java.io.Serializable;

/**
 * @author Ilja Kroonen
 */
public class Camera implements Cloneable, Serializable {
	private static final long serialVersionUID = -2000084337375288247L;
	private double positionX, positionY, positionZ;
	private double orientationX, orientationY, orientationZ, orientationAngle;

	public Camera(double positionX, double positionY, double positionZ, double orientationX, double orientationY,
			double orientationZ, double orientationAngle) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.orientationX = orientationX;
		this.orientationY = orientationY;
		this.orientationZ = orientationZ;
		this.orientationAngle = orientationAngle;
	}

	/**
	 * Moves the camera to the side
	 * 
	 * @param horizontal
	 *            Signed value representing the distance of the movement to the
	 *            right
	 * @param vertical
	 *            Signed value representing the distance of the movement to the
	 *            top
	 */
	public void moveSideways(double horizontal, double vertical) {
		double c = Math.cos(orientationAngle);
		double s = Math.sin(orientationAngle);
		positionX = (orientationX * orientationX * (1 - c) + c) * horizontal
				+ (orientationX * orientationY * (1 - c) - orientationZ * s) * vertical
				+ (orientationX * orientationZ * (1 - c) + orientationY * s) * 0 + positionX;
		positionY = (orientationY * orientationX * (1 - c) + orientationZ * s) * horizontal
				+ (orientationY * orientationY * (1 - c) + c) * vertical
				+ (orientationY * orientationZ * (1 - c) - orientationX * s) * 0 + positionY;
		positionZ = (orientationX * orientationZ * (1 - c) - orientationY * s) * horizontal
				+ (orientationY * orientationZ * (1 - c) + orientationX * s) * vertical
				+ (orientationZ * orientationZ * (1 - c) + c) * 0 + positionZ;
	}

	/**
	 * Changes the orientation of the camera
	 * 
	 * @param horizontal
	 *            Signed value representing a percentage of the horizontal field
	 *            of view (typically between -50 and 50)
	 * @param vertical
	 *            Signed value representing a percentage of the vertical field
	 *            of view (typically between -50 and 50)
	 */
	public void changeOrientation(double horizontal, double vertical) {
		// Horizontal rotation
		{
			// TODO Field of view
			double c = Math.cos(horizontal / 100 * Math.PI);
			double s = Math.sin(horizontal / 100 * Math.PI);
			double newX = (0 * 0 * (1 - c) + c) * orientationX + (0 * 1 * (1 - c) - 0 * s) * orientationY
					+ (0 * 0 * (1 - c) + 1 * s) * orientationZ;
			double newY = (1 * 0 * (1 - c) + 0 * s) * orientationX + (1 * 1 * (1 - c) + c) * orientationY
					+ (1 * 0 * (1 - c) - 0 * s) * orientationZ;
			double newZ = (0 * 0 * (1 - c) - 1 * s) * orientationX + (1 * 0 * (1 - c) + 0 * s) * orientationY
					+ (0 * 0 * (1 - c) + c) * orientationZ;
			// The vector must be a unit vector
			double length = vectorLength(newX, newY, newZ);
			orientationX = newX / length;
			orientationY = newY / length;
			orientationZ = newZ / length;
		}

		// Vertical rotation
		{
			// TODO Field of view
			double c = Math.cos(vertical / 100 * Math.PI);
			double s = Math.sin(vertical / 100 * Math.PI);
			double newX = (1 * 1 * (1 - c) + c) * orientationX + (1 * 0 * (1 - c) - 0 * s) * orientationY
					+ (1 * 0 * (1 - c) + 0 * s) * orientationZ;
			double newY = (0 * 1 * (1 - c) + 0 * s) * orientationX + (0 * 0 * (1 - c) + c) * orientationY
					+ (0 * 0 * (1 - c) - 1 * s) * orientationZ;
			double newZ = (1 * 0 * (1 - c) - 0 * s) * orientationX + (0 * 0 * (1 - c) + 1 * s) * orientationY
					+ (0 * 0 * (1 - c) + c) * orientationZ;
			// The vector must be a unit vector
			double length = vectorLength(newX, newY, newZ);
			orientationX = newX / length;
			orientationY = newY / length;
			orientationZ = newZ / length;
		}
	}

	private static final double vectorLength(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Moves the camera on the axis of the viewpoint
	 * 
	 * @param distance
	 *            Signed distance of the movement
	 */
	public void moveStraight(double distance) {
		double c = Math.cos(orientationAngle);
		double s = Math.sin(orientationAngle);
		positionX = (orientationX * orientationX * (1 - c) + c) * 0
				+ (orientationX * orientationY * (1 - c) - orientationZ * s) * 0
				+ (orientationX * orientationZ * (1 - c) + orientationY * s) * distance + positionX;
		positionY = (orientationY * orientationX * (1 - c) + orientationZ * s) * 0
				+ (orientationY * orientationY * (1 - c) + c) * 0
				+ (orientationY * orientationZ * (1 - c) - orientationX * s) * distance + positionY;
		positionZ = (orientationX * orientationZ * (1 - c) - orientationY * s) * 0
				+ (orientationY * orientationZ * (1 - c) + orientationX * s) * 0
				+ (orientationZ * orientationZ * (1 - c) + c) * distance + positionZ;
	}

	@Override
	public Camera clone() {
		return new Camera(positionX, positionY, positionZ, orientationX, orientationY, orientationZ, orientationAngle);
	}

	@Override
	public String toString() {
		return "(" + positionX + "," + positionY + "," + positionZ + ") ; (" + orientationX + "," + orientationY + ","
				+ orientationZ + "," + orientationAngle + ")";
	}
}
