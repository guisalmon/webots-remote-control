package org.black_mesa.webots_remote_control;

import java.io.Serializable;

public class Camera implements Cloneable, Serializable {
	private static final long serialVersionUID = -2000084337375288247L;
	private double positionX, positionY, positionZ;
	private double orientationX, orientationY, orientationZ, orientationAngle;

	public Camera(double positionX, double positionY, double positionZ, double orientationX, double orientationY,
			double orientationZ, double orientationAngle) {
		super();
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.orientationX = orientationX;
		this.orientationY = orientationY;
		this.orientationZ = orientationZ;
		this.orientationAngle = orientationAngle;
	}

	public void moveSideways(int horizontal, int vertical) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public void changeOrientation(int horizontal, int vertical) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public void moveStraight(int distance) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public double getPositionX() {
		return positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public double getPositionZ() {
		return positionZ;
	}

	public double getOrientationX() {
		return orientationX;
	}

	public double getOrientationY() {
		return orientationY;
	}

	public double getOrientationZ() {
		return orientationZ;
	}

	public double getOrientationAngle() {
		return orientationAngle;
	}

	@Override
	public Camera clone() {
		return new Camera(positionX, positionY, positionZ, orientationX, orientationY, orientationZ, orientationAngle);
	}
}
