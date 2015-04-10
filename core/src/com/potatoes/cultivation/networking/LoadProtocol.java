package com.potatoes.cultivation.networking;

import java.util.LinkedList;
import java.util.List;

import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.Player;

public class LoadProtocol implements Protocol {
	private static final long serialVersionUID = 6049005830645189981L;

	private List<Player> players;
	private List<GameDataProtocol> gameData;
	private Player sender;
	public LoadProtocol(Player sender, List<Player> players) {
		this.sender = sender;
		this.players = players;
		this.gameData = new LinkedList<>();
	}
	
	@Override
	public void execute(Server server) {
		server.load(this.sender, this.players, this);
	}
	
	public void setData(List<GameDataProtocol> gameData){
		this.gameData = gameData;
	}
	
	public List<GameDataProtocol> gameData(){
		return this.gameData;
	}
}
