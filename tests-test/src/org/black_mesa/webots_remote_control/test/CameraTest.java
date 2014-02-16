package org.black_mesa.webots_remote_control.test;

import static org.junit.Assert.*;

import org.black_mesa.webots_remote_control.Camera;
import org.junit.Test;

public class CameraTest {

	@Test
	public void test() {
		double epsilon = .00000000001;
		Camera expected;
		
		// We start with the position described in the documentation: we are at
		// the origin and we are looking down the z axis, with (x) on our right
		// and (y) up
		Camera camera = new Camera(0, 0, 0, 0, 0, 1, 0);

		// We move 1 up, which means we should now be on the y axis
		camera.moveSideways(0, 1);
		expected = new Camera(0, 1, 0, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));

		// We now rotate the viewpoint 90 degrees to the right
		camera.changeOrientation(50, 0);
		expected = new Camera(0, 1, 0, 1, 0, 0, 0);
		assertTrue(camera.compare(expected, epsilon));
		
		// We reset the camera and we move 1 straight
		camera = new Camera(0, 0, 0, 0, 0, 1, 0);
		camera.moveStraight(1);
		expected = new Camera(0, 0, 1, 0, 0, 1, 0);
		assertTrue(camera.compare(expected, epsilon));
		
		// We rotate 90 degrees to the left and move 1 straight
		camera.changeOrientation(-50, 0);
		camera.moveStraight(1);
		expected = new Camera(-1, 0, 1, -1, 0, 0, 0);
		assertTrue(camera.compare(expected, epsilon));
		
		// We rotate 45 degrees up and move sqrt(2) straight
		System.out.println(camera.toString());
		camera.changeOrientation(0, 25);
		System.out.println(camera.toString());
		camera.moveStraight(Math.sqrt(2));
		System.out.println(camera.toString());
		expected = new Camera(-2, 1, 1, -1, 0, 0, 0);
		assertTrue(camera.compare(expected, epsilon));
	}

}
