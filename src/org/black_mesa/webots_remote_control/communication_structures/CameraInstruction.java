package org.black_mesa.webots_remote_control.communication_structures;

import java.io.Serializable;

/**
 * Instruction class for a Webots camera.
 * 
 * @author Ilja Kroonen
 * 
 */
public final class CameraInstruction implements Serializable {
	private static final long serialVersionUID = -1401919642170517372L;
	private final Type mType;
	private final double[] mArgs;

	private enum Type {
		MOVE, TURN, PITCH
	}

	private CameraInstruction(final Type type, final double[] args) {
		mType = type;
		mArgs = args;
	}

	/**
	 * Creates a move CameraInstruction.
	 * 
	 * @param right
	 *            Distance of the movement along the right vector.
	 * @param up
	 *            Distance of the movement along the up vector.
	 * @param forward
	 *            Distance of the movement along the forward vector.
	 * @return Resulting instance.
	 */
	public static CameraInstruction move(final double right, final double up, final double forward) {
		double[] args = { right, up, forward };
		return new CameraInstruction(Type.MOVE, args);
	}

	/**
	 * Creates a turn CameraInstruction.
	 * 
	 * @param angle
	 *            Angle of the turn (rad).
	 * @return Resulting instance.
	 */
	public static CameraInstruction turn(final double angle) {
		double[] args = { angle };
		return new CameraInstruction(Type.TURN, args);
	}

	/**
	 * Creates a pitch CameraInstruction.
	 * 
	 * @param angle
	 *            Angle of the turn (rad).
	 * @return Resulting instance.
	 */
	public static CameraInstruction pitch(final double angle) {
		double[] args = { angle };
		return new CameraInstruction(Type.PITCH, args);
	}

	@Override
	public String toString() {
		switch (mType) {
		case MOVE:
			return mType + "(" + mArgs[0] + "," + mArgs[1] + "," + mArgs[2] + ")";
		case TURN:
			return mType + "(" + mArgs[0] + ")";
		case PITCH:
			return mType + "(" + mArgs[0] + ")";
		}
		return null;
	}
}
