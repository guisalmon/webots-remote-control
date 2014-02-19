package org.black_mesa.webots_remote_control.test;

import static org.junit.Assert.*;

import org.black_mesa.webots_remote_control.remote_object_state.RemoteCameraState;
import org.junit.Test;

public class CameraTest {

	@Test
	public void test() {
		double epsilon = .00000000001;
		RemoteCameraState expected;

		// We start with the position described in the documentation: we are at
		// the origin and we are looking down the z axis, with (x) on our right
		// and (y) up
		RemoteCameraState camera = new RemoteCameraState(0, 0, 0, 0, 0, 1, 0);

		// We move 1 up, which means we should now be on the y axis
		camera.move(0, 1, 0);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(0, 1, 0, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We move 1 left
		camera.move(-1, 0, 0);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(-1, 1, 0, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We move 1 straight back
		camera.move(0, 0, -1);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(-1, 1, -1, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We now rotate the viewpoint 90 degrees to the right
		camera.turn(Math.PI / 2);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(-1, 1, -1, 0, 1, 0, Math.PI / 2);
		assertTrue(camera.compare(expected, epsilon));

		// We move 1 forward
		camera.move(0, 0, 1);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(0, 1, -1, 0, 1, 0, Math.PI / 2);
		assertTrue(camera.compare(expected, epsilon));
		
		camera = new RemoteCameraState(0, 0, 0, 0, 1, 0, Math.PI / 2);
		camera.turn(Math.PI / 2);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(0, 0, 0, 0, 1, 0, Math.PI);
		assertTrue(camera.compare(expected, epsilon));
		
		camera.pitch(Math.PI);
		System.out.println(camera.toString());
		camera.turn(Math.PI);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(0, 0, 0, -1, 0, 0, Math.PI);
		assertTrue(camera.compare(expected, epsilon));
		
		camera.move(0, 0, 1);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(0, 0, -1, -1, 0, 0, Math.PI);
		assertTrue(camera.compare(expected, epsilon));
		
		camera = new RemoteCameraState(0, 0, 0, 0, 1, 0, 0);
		camera.turn(0);
		System.out.println(camera.toString());
		expected = new RemoteCameraState(0, 0, 0, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));
	}

}
