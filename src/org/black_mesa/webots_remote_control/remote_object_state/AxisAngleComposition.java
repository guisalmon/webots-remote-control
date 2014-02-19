package org.black_mesa.webots_remote_control.remote_object_state;

/**
 * The purpose of this class is to combine two rotation represented by axis and
 * angle into a signe axis-angle representation. This is done by translation the
 * two rotation into matrix, by multiplying the two matrixes, and then by
 * calculating the corresponding axis-angle representation.
 * 
 * @author Ilja Kroonen
 * 
 */
public class AxisAngleComposition {
	// TODO EPSILON should probably not be here
	private static final double EPSILON = 0.0000000001;
	public final double x, y, z, angle;

	/**
	 * Initializes the fields of this class with the composition of two rotation
	 * 
	 * @param x1
	 *            x component of the vector of the first rotation
	 * @param y1
	 *            y component of the vector of the first rotation
	 * @param z1
	 *            z component of the vector of the first rotation
	 * @param angle1
	 *            angle of the first rotation
	 * @param x2
	 *            x component of the vector of the second rotation
	 * @param y2
	 *            y component of the vector of the second rotation
	 * @param z2
	 *            z component of the vector of the second rotation
	 * @param angle2
	 *            angle1 angle of the second rotation
	 */
	public AxisAngleComposition(double x1, double y1, double z1, double angle1, double x2, double y2, double z2,
			double angle2) {
		double[][] m1 = axisAngleToMatrix(x1, y1, z1, angle1);
		double[][] m2 = axisAngleToMatrix(x2, y2, z2, angle2);

		double[][] m = multiplyMatrix(m1, m2);

		double cos = (m[0][0] + m[1][1] + m[2][2] - 1) / 2;

		if (!(cos > -1 - EPSILON && cos < 1 + EPSILON)) {
			// Case angle = pi
			angle = Math.PI;
		} else {
			angle = Math.acos((m[0][0] + m[1][1] + m[2][2] - 1) / 2);
		}

		if (Math.abs(angle) < EPSILON) {
			// Case angle = 0, axis is irrelevant
			x = 0;
			y = 0;
			z = 1;
			return;
		}

		double denom = Math.sqrt((m[2][1] - m[1][2]) * (m[2][1] - m[1][2]) + (m[0][2] - m[2][0]) * (m[0][2] - m[2][0])
				+ (m[1][0] - m[0][1]) * (m[1][0] - m[0][1]));
		x = (m[2][1] - m[1][2]) / denom;
		y = (m[0][2] - m[2][0]) / denom;
		z = (m[1][0] - m[0][1]) / denom;
	}

	private static double[][] axisAngleToMatrix(double x, double y, double z, double angle) {
		double[][] ret = new double[3][];
		for (int i = 0; i < 3; i++) {
			ret[i] = new double[3];
		}

		double c = Math.cos(angle);
		double s = Math.sin(angle);
		ret[0][0] = c + x * x * (1 - c);
		ret[0][1] = x * y * (1 - c) - z * s;
		ret[0][2] = x * z * (1 - c) + y * s;
		ret[1][0] = y * x * (1 - c) + z * s;
		ret[1][1] = c + y * y * (1 - c);
		ret[1][2] = y * z * (1 - c) - x * s;
		ret[2][0] = z * x * (1 - c) - y * s;
		ret[2][1] = z * y * (1 - c) + x * s;
		ret[2][2] = c + z * z * (1 - c);

		return ret;
	}

	private static double[][] multiplyMatrix(double[][] m1, double[][] m2) {
		double[][] ret = new double[3][];
		for (int i = 0; i < 3; i++) {
			ret[i] = new double[3];
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ret[i][j] = 0;
				for (int k = 0; k < 3; k++) {
					ret[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}

		return ret;
	}

}
