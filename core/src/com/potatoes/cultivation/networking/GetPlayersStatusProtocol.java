package com.potatoes.cultivation.networking;

import java.util.List;
import java.util.Map;

import com.potatoes.cultivation.logic.Player;

public class GetPlayersStatusProtocol implements Protocol {
	private static final long serialVersionUID = -869213514872634273L;
	private List<Player> username;
	private Map<Player, Boolean> online;
	
	public GetPlayersStatusProtocol(List<Player> usernames) {
		this.username = usernames;
	}
	
	@Override
	public void execute(Server server) {
		this.online = server.areOnline(username);
	}

	public Map<Player, Boolean> areOnline(){
		return online;
	}
}
