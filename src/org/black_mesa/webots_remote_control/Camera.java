package org.black_mesa.webots_remote_control;

import java.io.Serializable;

import android.util.Log;

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
		Log.e("Camera", "Not implemented yet");
		positionX += horizontal;
		positionY += vertical;
	}

	/**
	 * Changed the orientation of the camera
	 * 
	 * @param horizontal
	 *            Signed value representing a percentage of the horizontal field
	 *            of view (typically between -50 and 50)
	 * @param vertical
	 *            Signed value representing a percentage of the vertical field
	 *            of view (typically between -50 and 50)
	 */
	public void changeOrientation(double horizontal, double vertical) {
		Log.e("Camera", "Not implemented yet");
		positionX += horizontal;
		positionY += vertical;
	}

	/**
	 * Moves the camera on the axis of the viewpoint
	 * 
	 * @param distance
	 *            Signed distance of the movement
	 */
	public void moveStraight(double distance) {
		Log.e("Camera", "Not implemented yet");
		positionX += distance;
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
