package com.potatoes.cultivation.networking;

import java.util.List;
import java.util.Map;

public class GetPlayersStatusProtocol implements Protocol {
	private static final long serialVersionUID = -869213514872634273L;
	private List<String> username;
	private Map<String, Boolean> online;
	
	public GetPlayersStatusProtocol(List<String> usernames) {
		this.username = usernames;
	}
	
	@Override
	public void execute(Server server) {
		this.online = server.areOnline(username);
	}

	public Map<String, Boolean> areOnline(){
		return online;
	}
}
