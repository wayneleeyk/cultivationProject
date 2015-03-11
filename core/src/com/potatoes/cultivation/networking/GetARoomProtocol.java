package com.potatoes.cultivation.networking;

import java.util.HashSet;
import java.util.Set;

import com.potatoes.cultivation.logic.Player;

public class GetARoomProtocol implements Protocol {
	private static final long serialVersionUID = 3280696935670814673L;
	private Set<Player> playerList = null;
	private int number;
	
	public GetARoomProtocol(int roomNumber) {
		number = roomNumber;
	}
	
	@Override
	public void execute(Server server) {
		playerList = new HashSet<>( server.getPlayersForRoom(number) );
	}
	
	public Set<Player> getList() {
		return playerList;
	}
}