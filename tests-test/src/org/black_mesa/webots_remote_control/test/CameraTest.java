package org.black_mesa.webots_remote_control.test;

import static org.junit.Assert.*;

import org.black_mesa.webots_remote_control.Camera;
import org.junit.Test;

public class CameraTest {

	@Test
	public void test() {
		double epsilon = .01;
		Camera expected;

		// We start with the position described in the documentation: we are at
		// the origin and we are looking down the z axis, with (x) on our right
		// and (y) up
		Camera camera = new Camera(0, 0, 0, 0, 0, 1, 0);

		// We move 1 up, which means we should now be on the y axis
		camera.move(0, 1, 0);
		System.out.println(camera.toString());
		expected = new Camera(0, 1, 0, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We move 1 left
		camera.move(-1, 0, 0);
		System.out.println(camera.toString());
		expected = new Camera(-1, 1, 0, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We move 1 straight back
		camera.move(0, 0, -1);
		System.out.println(camera.toString());
		expected = new Camera(-1, 1, -1, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We now rotate the viewpoint 90 degrees to the right
		camera.yaw(Math.PI / 2);
		System.out.println(camera.toString());
		expected = new Camera(-1, 1, -1, 0, 1, 0, Math.PI / 2);
		assertTrue(camera.compare(expected, epsilon));

		// We move 1 forward
		camera.move(0, 0, 1);
		System.out.println(camera.toString());
		expected = new Camera(0, 1, -1, 0, 1, 0, Math.PI / 2);
		assertTrue(camera.compare(expected, epsilon));
	}

}
