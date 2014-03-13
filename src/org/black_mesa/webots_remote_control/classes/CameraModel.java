package org.black_mesa.webots_remote_control.classes;

public class CameraModel {

	private static CameraModel instance;
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;
	private CameraModel()
	{
		
	}
	public static CameraModel getInstance()
	{
		if(instance==null)
		{
			instance = new CameraModel();
		}
		return instance;
	}
	public float getxMin() {
		return xMin;
	}
	public void setxMin(float xMin) {
		this.xMin = xMin;
	}
	public float getxMax() {
		return xMax;
	}
	public void setxMax(float xMax) {
		this.xMax = xMax;
	}
	public float getyMin() {
		return yMin;
	}
	public void setyMin(float yMin) {
		this.yMin = yMin;
	}
	public float getyMax() {
		return yMax;
	}
	public void setyMax(float yMax) {
		this.yMax = yMax;
	}
}
