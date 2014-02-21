package org.black_mesa.webots_remote_control.client;

import java.io.Serializable;

public class InitialConnectionInformation implements Serializable {
	private static final long serialVersionUID = -1614688885637821L;
	
	public int numberOfCameras;
	public int numberOfRobots;
}
