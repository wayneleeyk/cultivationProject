package com.potatoes.cultivation.networking;

public class HeartbeatProtocol implements Protocol {
	private static final long serialVersionUID = -7281495517237641521L;

	private String username;
	
	public HeartbeatProtocol(String username) {
		this.username = username;
	}
	
	@Override
	public void execute(Server server) {
		server.heartbeat(username);
	}

}
