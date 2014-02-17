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
	 * @param right
	 *            Signed value representing the distance of the movement along
	 *            the right vector
	 * @param up
	 *            Signed value representing the distance of the movement along
	 *            the up vector
	 * @param forward
	 *            Signed value representing the distance of the movement along
	 *            the forward vector
	 */
	public void move(double right, double up, double forward) {
		// We use the rotation matrix of the axis / angle representation
		double c = Math.cos(orientationAngle);
		double s = Math.sin(orientationAngle);
		positionX += (orientationX * orientationX * (1 - c) + c) * right
				+ (orientationX * orientationY * (1 - c) - orientationZ * s) * up
				+ (orientationX * orientationZ * (1 - c) + orientationY * s) * forward;
		positionY += (orientationY * orientationX * (1 - c) + orientationZ * s) * right
				+ (orientationY * orientationY * (1 - c) + c) * up
				+ (orientationY * orientationZ * (1 - c) - orientationX * s) * forward;
		positionZ += (orientationX * orientationZ * (1 - c) - orientationY * s) * right
				+ (orientationY * orientationZ * (1 - c) + orientationX * s) * up
				+ (orientationZ * orientationZ * (1 - c) + c) * forward;
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
		{
			// We want to rotate around the local y axis
			// First we rotate the absolute y axis to get the local y axis
			double c = Math.cos(orientationAngle);
			double s = Math.sin(orientationAngle);
			double x = 0;
			double y = 1;
			double z = 0;
			double newX = (orientationX * orientationX * (1 - c) + c) * x
					+ (orientationX * orientationY * (1 - c) - orientationZ * s) * y
					+ (orientationX * orientationZ * (1 - c) + orientationY * s) * z;
			double newY = (orientationY * orientationX * (1 - c) + orientationZ * s) * x
					+ (orientationY * orientationY * (1 - c) + c) * y
					+ (orientationY * orientationZ * (1 - c) - orientationX * s) * z;
			double newZ = (orientationX * orientationZ * (1 - c) - orientationY * s) * x
					+ (orientationY * orientationZ * (1 - c) + orientationX * s) * y
					+ (orientationZ * orientationZ * (1 - c) + c) * z;
			double length = vectorLength(newX, newY, newZ);
			x = newX / length;
			y = newY / length;
			z = newZ / length;
			// We now rotate around the (x, y, z) axis
			c = Math.cos(horizontal);
			s = Math.sin(horizontal);
			newX = (x * x * (1 - c) + c) * orientationX
					+ (x * y * (1 - c) - z * s) * orientationY
					+ (x * z * (1 - c) + y * s) * orientationZ;
			newY = (y * x * (1 - c) + z * s) * orientationX
					+ (y * y * (1 - c) + c) * orientationY
					+ (y * z * (1 - c) - x * s) * orientationZ;
			newZ = (x * z * (1 - c) - y * s) * orientationX
					+ (y * z * (1 - c) + x * s) * orientationY
					+ (z * z * (1 - c) + c) * orientationZ;
			orientationX = newX;
			orientationY = newY;
			orientationZ = newZ;
		}
		
		{
			// We want to rotate around the local x axis
			// First we rotate the absolute x axis to get the local x axis
			double c = Math.cos(orientationAngle);
			double s = Math.sin(orientationAngle);
			double x = 1;
			double y = 0;
			double z = 0;
			double newX = (orientationX * orientationX * (1 - c) + c) * x
					+ (orientationX * orientationY * (1 - c) - orientationZ * s) * y
					+ (orientationX * orientationZ * (1 - c) + orientationY * s) * z;
			double newY = (orientationY * orientationX * (1 - c) + orientationZ * s) * x
					+ (orientationY * orientationY * (1 - c) + c) * y
					+ (orientationY * orientationZ * (1 - c) - orientationX * s) * z;
			double newZ = (orientationX * orientationZ * (1 - c) - orientationY * s) * x
					+ (orientationY * orientationZ * (1 - c) + orientationX * s) * y
					+ (orientationZ * orientationZ * (1 - c) + c) * z;
			double length = vectorLength(newX, newY, newZ);
			x = newX / length;
			y = newY / length;
			z = newZ / length;
			// We now rotate around the (x, y, z) axis
			c = Math.cos(vertical);
			s = Math.sin(vertical);
			newX = (x * x * (1 - c) + c) * orientationX
					+ (x * y * (1 - c) - z * s) * orientationY
					+ (x * z * (1 - c) + y * s) * orientationZ;
			newY = (y * x * (1 - c) + z * s) * orientationX
					+ (y * y * (1 - c) + c) * orientationY
					+ (y * z * (1 - c) - x * s) * orientationZ;
			newZ = (x * z * (1 - c) - y * s) * orientationX
					+ (y * z * (1 - c) + x * s) * orientationY
					+ (z * z * (1 - c) + c) * orientationZ;
			orientationX = newX;
			orientationY = newY;
			orientationZ = newZ;
		}
	}

	private static double vectorLength(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
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

	public boolean compare(Camera camera, double epsilon) {
		return compare(positionX, camera.positionX, epsilon) && compare(positionY, camera.positionY, epsilon)
				&& compare(positionZ, camera.positionZ, epsilon) && compare(orientationX, camera.orientationX, epsilon)
				&& compare(orientationY, camera.orientationY, epsilon)
				&& compare(orientationZ, camera.orientationZ, epsilon)
				&& compare(orientationAngle, camera.orientationAngle, epsilon);
	}

	private static boolean compare(double a, double b, double epsilon) {
		return (a - b) * (a - b) < epsilon;
	}
}
