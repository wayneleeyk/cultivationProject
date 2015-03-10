package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.Action;
import com.potatoes.cultivation.logic.Player;

public class ActionBlockProtocol implements Protocol {
	private static final long serialVersionUID = 2474528820962037060L;
	private Action[] actions;
	private Player sender;
	public ActionBlockProtocol(Player sender, Action...actions) {
		this.sender = sender;
		this.actions = actions;
	}
	@Override
	public void execute(Server server) {
		server.propagate(sender, this);
	}

	public Action[] getActions(){
		return this.actions;
	}

}
