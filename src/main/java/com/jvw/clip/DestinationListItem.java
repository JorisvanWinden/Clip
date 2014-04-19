package com.jvw.clip;

/**
 * Created by Joris on 18-4-14.
 */
public class DestinationListItem {
	private String name;
	private String ip;
	private int port;

	public DestinationListItem(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return name;
	}
}
