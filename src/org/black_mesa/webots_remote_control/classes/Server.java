package org.black_mesa.webots_remote_control.classes;

/**
 * Simple class to represent a server
 * @author guisalmon@gmail.com
 *
 */
public class Server {
	private long id;
	private String name;
	private String adress;
	private int port;

	public Server(long id, String name, String adress, int port) {
		this.id = id;
		this.name = name;
		this.adress = adress;
		this.port = port;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAdress() {
		return adress;
	}

	public int getPort() {
		return port;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public int hashCode() {
		return (int) (id % (1 << 32));
	}
	
	public boolean equals(Object o) {
		if(o.getClass() != getClass()) {
			return false;
		}
		return ((Server) o).getId() == getId();
	}
}
