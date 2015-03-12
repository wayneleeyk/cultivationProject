package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.Player;

public class ActionBlockProtocol implements Protocol {
	private static final long serialVersionUID = 2474528820962037060L;
	private GameAction[] actions;
	private Player sender;
	public ActionBlockProtocol(Player sender, GameAction...actions) {
		this.sender = sender;
		this.actions = actions;
	}
	@Override
	public void execute(Server server) {
		server.propagate(sender, this);
	}

	public GameAction[] getActions(){
		return this.actions;
	}
	public void clearSender() {
		sender = null;
	}

}
