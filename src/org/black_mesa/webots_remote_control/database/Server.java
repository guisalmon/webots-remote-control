package org.black_mesa.webots_remote_control.database;

/**
 * Simple class to represent a server.
 * 
 * @author guisalmon@gmail.com
 * 
 */
public class Server {
	private long mId;
	private String mName;
	private String mAddress;
	private int mPort;

	public Server(long id, String name, String adress, int port) {
		this.mId = id;
		this.mName = name;
		this.mAddress = adress;
		this.mPort = port;
	}

	public long getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getAdress() {
		return mAddress;
	}

	public int getPort() {
		return mPort;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public void setAdress(String adress) {
		this.mAddress = adress;
	}

	public void setPort(int port) {
		this.mPort = port;
	}

	@Override
	public int hashCode() {
		return (int) (mId % (1 << 32));
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		return ((Server) o).getId() == getId();
	}
}
