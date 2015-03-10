package com.potatoes.cultivation.networking;

public class GetWinLossProtocol implements Protocol {
	private static final long serialVersionUID = 266734065221521367L;

	private int wins, losses;
	private String playerName;
	
	public GetWinLossProtocol(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public void execute(Server server) {
		this.wins = server.getWins(playerName);
		this.losses = server.getLosses(playerName);
	}

	public int getWins(){
		return this.wins;
	}
	
	public int getLosses(){
		return this.losses;
	}
	
}
