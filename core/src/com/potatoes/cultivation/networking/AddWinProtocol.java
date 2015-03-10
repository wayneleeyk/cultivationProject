package com.potatoes.cultivation.networking;

public class AddWinProtocol extends AddWinLossProtocol {
	private static final long serialVersionUID = 5492982135232963488L;
	public AddWinProtocol(String playerName) {
		super(playerName);
	}

	@Override
	public void execute(Server server) {
		server.addWin(playerName);
	}

}
