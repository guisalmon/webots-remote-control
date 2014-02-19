package org.black_mesa.webots_remote_control.remote_object_state;

/**
 * @author Ilja Kroonen
 */
public class RemoteRobotState implements RemoteObjectState {
	private static final long serialVersionUID = -3590254358706938917L;
	private int leftSpeed;
	private int rightSpeed;

	public RemoteRobotState(int leftSpeed, int rightSpeed) {
		this.leftSpeed = leftSpeed;
		this.rightSpeed = rightSpeed;
	}

	public int getLeftSpeed() {
		return leftSpeed;
	}

	public int getRightSpeed() {
		return rightSpeed;
	}

	public RemoteRobotState clone() {
		return this;
	}

	public String toString() {
		return "Left speed: " + leftSpeed + " ; RIght speed: " + rightSpeed;
	}
}
