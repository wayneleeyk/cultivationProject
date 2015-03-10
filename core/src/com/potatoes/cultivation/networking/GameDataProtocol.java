package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.Game;
import com.potatoes.cultivation.logic.Player;

public class GameDataProtocol implements Protocol {
	private static final long serialVersionUID = -7864602523506260134L;
	
	private Game game;
	private Player sender;
	
	public GameDataProtocol(Player sender, Game game) {
		this.game = game;
		this.sender = sender;
	}
	
	@Override
	public void execute(Server server) {
		server.propagate(sender, this);
	}
	
	public Game getGame(){
		return this.game;
	}

}
