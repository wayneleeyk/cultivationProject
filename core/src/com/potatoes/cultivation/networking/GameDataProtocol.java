package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.Player;

public class GameDataProtocol implements Protocol {
	private static final long serialVersionUID = -7864602523506260134L;
	
	private CultivationGame game;
	private Player sender;
	
	public GameDataProtocol(Player sender, CultivationGame game) {
		this.game = game;
		this.sender = sender;
	}
	
	@Override
	public void execute(Server server) {
		server.propagate(sender, this);
	}
	
	public CultivationGame getGame(){
		return this.game;
	}

	public void clearSender() {
		sender = null;
	}

}
