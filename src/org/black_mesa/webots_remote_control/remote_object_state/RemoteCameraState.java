package org.black_mesa.webots_remote_control.remote_object_state;

public class RemoteCameraState implements RemoteObjectState {
	private static final long serialVersionUID = 8377512273594945634L;
	private final int id;
	private double[] t;
	private double[] r;

	public RemoteCameraState(int id, double[] translation, double[] rotation) {
		this.id = id;
		t = translation.clone();
		r = rotation.clone();
	}

	@Override
	public int getId() {
		return id;
	}

	public RemoteCameraState clone() {
		return new RemoteCameraState(id, t, r);
	}

	public void move(double right, double up, double forward) {
		double[] translation = new double[4];
		translation[0] = right;
		translation[1] = up;
		translation[2] = -forward;
		translateLocal(translation);
	}

	public void pitch(double angle) {
		double[] rotation = new double[4];
		rotation[0] = 1;
		rotation[1] = 0;
		rotation[2] = 0;
		rotation[3] = -angle;
		rotateLocal(rotation);
	}

	public void turn(double angle) {
		double[] rotation = new double[4];
		rotation[0] = 0;
		rotation[1] = 1;
		rotation[2] = 0;
		rotation[3] = -angle;
		rotateAbsolute(rotation);
	}

	private void translateLocal(double[] translation) {
		double[] invR = new double[4];
		invR[0] = -r[0];
		invR[1] = -r[1];
		invR[2] = -r[2];
		invR[3] = r[3];
		t = Geometry.add3DVector3DVector(t, Geometry.rotate3DVectorAxisAngle(translation, r));
	}

	private void rotateLocal(double[] rotation) {
		r = Geometry.composeAxisAngleAxisAngle(r, rotation);
	}

	private void rotateAbsolute(double[] rotation) {
		r = Geometry.composeAxisAngleAxisAngle(rotation, r);
	}

	@Override
	public String toString() {
		return "(" + t[0] + "," + t[1] + "," + t[2] + ") ; (" + r[0] + "," + r[1] + "," + r[2] + "," + r[3] + ")";
	}

}
