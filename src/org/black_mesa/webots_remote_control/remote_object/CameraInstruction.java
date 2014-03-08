package org.black_mesa.webots_remote_control.remote_object;

public class CameraInstruction implements Instruction {
	private static final long serialVersionUID = -1401919642170517372L;
	private final Type type;
	private final double[] args;

	private enum Type {
		MOVE, TURN, PITCH
	}

	private CameraInstruction(Type type, double[] args) {
		this.type = type;
		this.args = args;
	}

	public static CameraInstruction move(double right, double up, double forward) {
		double[] args = { right, up, forward };
		return new CameraInstruction(Type.MOVE, args);
	}

	public static CameraInstruction turn(double angle) {
		double[] args = { angle };
		return new CameraInstruction(Type.TURN, args);
	}

	public static CameraInstruction pitch(double angle) {
		double[] args = { angle };
		return new CameraInstruction(Type.PITCH, args);
	}

	@Override
	public String toString() {
		switch (type) {
		case MOVE:
			return type + "(" + args[0] + "," + args[1] + "," + args[2] + ")";
		case TURN:
			return type + "(" + args[0] + ")";
		case PITCH:
			return type + "(" + args[0] + ")";
		}
		return null;
	}
}