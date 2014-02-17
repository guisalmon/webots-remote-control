package org.black_mesa.webots_remote_control;

public class Rotation {
	private double[][] m;
	private double x, y, z, angle;

	private Rotation() {
		m = new double[3][];
		for (int i = 0; i < 3; i++) {
			m[i] = new double[3];
		}
	}

	public Rotation(double x, double y, double z, double angle) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;

		double c = Math.cos(angle);
		double s = Math.sin(angle);
		m[0][0] = c + x * x * (1 - c);
		m[0][1] = x * y * (1 - c) - z * s;
		m[0][2] = x * z * (1 - c) + y * s;
		m[1][0] = y * x * (1 - c) + z * s;
		m[1][1] = c + y * y * (1 - c);
		m[1][2] = y * z * (1 - c) - x * s;
		m[2][0] = z * x * (1 - c) - y * s;
		m[2][1] = z * y * (1 - c) + x * s;
		m[2][2] = c + z * z * (1 - c);
	}
}
