package org.black_mesa.webots_remote_control.test;

import static org.junit.Assert.*;

import org.black_mesa.webots_remote_control.Camera;
import org.junit.Test;

public class CameraTest {

	@Test
	public void test() {
		Camera camera = new Camera(0, 0, 0, 0, 0, 1, 0);
		assertTrue(camera.toString().equals("(0.0,0.0,0.0) ; (0.0,0.0,1.0,0.0)"));
		
		camera.moveSideways(0, 1);
		System.out.println(camera.toString());
	}

}
