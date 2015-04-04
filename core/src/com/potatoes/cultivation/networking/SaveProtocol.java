package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.CultivationGame;

public class SaveProtocol implements Protocol {
	private static final long serialVersionUID = 2937108761316608223L;
	private CultivationGame game;
	
	public SaveProtocol(CultivationGame game) {
		this.game = game;
	}
	@Override
	public void execute(Server server) {
		server.save(game);
	}

}
