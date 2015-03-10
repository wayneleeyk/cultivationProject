package com.potatoes.cultivation.networking;

public abstract class AddWinLossProtocol implements Protocol{
	private static final long serialVersionUID = -6722790694574912058L;
	protected String playerName;

	public AddWinLossProtocol(String playerName) {
		this.playerName = playerName;
	}

}