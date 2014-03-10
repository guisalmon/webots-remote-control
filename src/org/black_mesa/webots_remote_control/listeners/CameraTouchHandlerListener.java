package org.black_mesa.webots_remote_control.listeners;

public interface CameraTouchHandlerListener {
	public void moveForward(float forward);

	public void moveSide(float right, float up, long time);

	public void turnPitch(float turn, float pitch);
}
