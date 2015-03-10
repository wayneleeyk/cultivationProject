package com.potatoes.cultivation.networking;

public class GetOnlineStatusProtocol implements Protocol {

	private String playerName;
	
	public GetOnlineStatusProtocol(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public void execute(Server server) {
		
	}

}
