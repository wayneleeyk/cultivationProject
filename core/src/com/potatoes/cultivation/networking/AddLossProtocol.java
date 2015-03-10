package com.potatoes.cultivation.networking;

public class AddLossProtocol extends AddWinLossProtocol {
	private static final long serialVersionUID = 3736779973157489919L;

	public AddLossProtocol(String playerName) {
		super(playerName);
	}
	
	public void execute(Server server) {
		server.addLoss(playerName);
	}

}
