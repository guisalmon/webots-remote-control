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

		// We now rotate the viewpoint 90 to the right
		camera.changeOrientation(Math.PI / 2, 0);
		System.out.println(camera.toString());
		expected = new Camera(-1, 1, -1, 1, 0, 0, 0);
		assertTrue(camera.compare(expected, epsilon));
		
		// We move 1 straight
		camera.move(0, 0, 1);
		System.out.println(camera.toString());
		expected = new Camera(0, 1, -1, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));
		
		// We reset the camera and we turn 45 degrees to the left
		camera = new Camera(0, 0, 0, 0, 0, 1, 0);
		camera.changeOrientation(- Math.PI / 4, 0);
		System.out.println(camera.toString());
		expected = new Camera(0, 0, 0, -Math.sqrt(2) / 2, 0, Math.sqrt(2) / 2, 0);
		System.out.println(expected.toString());
		assertTrue(camera.compare(expected, epsilon));

		// We reset the camera and we move 1 straight
		camera = new Camera(0, 0, 0, 0, 0, 1, 0);
		camera.move(0, 0, 1);
		System.out.println(camera.toString());
		expected = new Camera(0, 0, 1, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We rotate 90 degrees to the left
		camera.changeOrientation(- Math.PI / 2, 0);
		System.out.println(camera.toString());
		expected = new Camera(0, 0, 1, -1, 0, 0, 0);
		assertTrue(camera.compare(expected, epsilon));
		
		// We move 1 left
		camera.move(-1, 0, 0);
		System.out.println(camera.toString());
		expected = new Camera(0, 0, 0, -1, 0, 0, 0);
		assertTrue(camera.compare(expected, epsilon));
	}

}
