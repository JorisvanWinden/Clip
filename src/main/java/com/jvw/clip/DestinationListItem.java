package com.jvw.clip;

/**
 * Created by Joris on 18-4-14.
 */
public class DestinationListItem {
	private String name;
	private String ip;

	public DestinationListItem(String name, String ip) {
		this.name = name;
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	@Override
	public String toString() {
		return name;
	}
}
