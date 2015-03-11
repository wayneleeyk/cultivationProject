package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.Player;

public class JoinRoomProtocol implements Protocol {
	private static final long serialVersionUID = 3828140352552921120L;
	private boolean success = false;
	private Player player;
	private int number;
	
	public JoinRoomProtocol(int roomNumber, Player p) {
		player = p;
		number = roomNumber;
	}
	
	@Override
	public void execute(Server server) {
		success = server.addPlayerToRoom(number, player);
	}
	
	public boolean getResult() {
		return success;
	}
	
}