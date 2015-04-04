package com.potatoes.cultivation.networking;

import java.util.List;

import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.Player;

public class LoadProtocol implements Protocol {
	private static final long serialVersionUID = 6049005830645189981L;

	private List<Player> players;
	
	public LoadProtocol(List<Player> players) {
		this.players = players;
	}
	
	@Override
	public void execute(Server server) {
		server.load(this.players);
	}
}
