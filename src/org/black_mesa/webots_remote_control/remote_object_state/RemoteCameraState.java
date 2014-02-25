package org.black_mesa.webots_remote_control.remote_object_state;

/**
 * @author Ilja Kroonen
 */
public class RemoteCameraState implements RemoteObjectState {
	private static final long serialVersionUID = -2000084337375288247L;
	private int id;
	private double positionX, positionY, positionZ;
	private double orientationX, orientationY, orientationZ, orientationAngle;

	public RemoteCameraState(int id, double positionX, double positionY, double positionZ, double orientationX,
			double orientationY, double orientationZ, double orientationAngle) {
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
	 *            the local right vector
	 * @param up
	 *            Signed value representing the distance of the movement along
	 *            the local up vector
	 * @param forward
	 *            Signed value representing the distance of the movement along
	 *            the local forward vector
	 */
	public void move(double right, double up, double forward) {
		// right = local x axis, up = local y axis, forward = local z axis
		// We multiply our translation with the rotation matrix in order to get
		// the translation in the local system
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
	 * Rotates around the local right axis
	 * 
	 * @param angle
	 *            Angle of the rotation in radians
	 */
	public void pitch(double angle) {
		rotateLocal(1, 0, 0, angle);
	}

	/**
	 * Rotates around the absolute up axis
	 * 
	 * @param angle
	 *            Angle of the rotation in radians
	 * 
	 */
	public void turn(double angle) {
		rotateAbsolute(0, 1, 0, angle);
	}

	private void rotateLocal(double x, double y, double z, double angle) {
		// We want to rotate around the local axis
		// First we rotate the absolute axis to get the local axis
		double c = Math.cos(orientationAngle);
		double s = Math.sin(orientationAngle);
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
		// We now compute the new axis angle representation
		rotateAbsolute(x, y, z, angle);
	}

	private void rotateAbsolute(double x, double y, double z, double angle) {
		// We want to rotate around the absolute axis
		// We can do this directly
		AxisAngleComposition r = new AxisAngleComposition(orientationX, orientationY, orientationZ, orientationAngle,
				x, y, z, angle);
		orientationX = r.x;
		orientationY = r.y;
		orientationZ = r.z;
		orientationAngle = r.angle;
	}

	private static double vectorLength(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}

	@Override
	public String toString() {
		return "(" + positionX + "," + positionY + "," + positionZ + ") ; (" + orientationX + "," + orientationY + ","
				+ orientationZ + "," + orientationAngle + ")";
	}

	public boolean compare(RemoteCameraState camera, double epsilon) {
		return compare(positionX, camera.positionX, epsilon) && compare(positionY, camera.positionY, epsilon)
				&& compare(positionZ, camera.positionZ, epsilon) && compare(orientationX, camera.orientationX, epsilon)
				&& compare(orientationY, camera.orientationY, epsilon)
				&& compare(orientationZ, camera.orientationZ, epsilon)
				&& compare(orientationAngle, camera.orientationAngle, epsilon);
	}

	private static boolean compare(double a, double b, double epsilon) {
		return (a - b) * (a - b) < epsilon;
	}

	@Override
	public RemoteObjectState clone() {
		return new RemoteCameraState(id, positionX, positionY, positionZ, orientationX, orientationY, orientationZ,
				orientationAngle);
	}

	@Override
	public int getId() {
		return id;
	}
}
