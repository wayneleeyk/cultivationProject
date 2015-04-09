package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.GameMap.MapCoordinates;
import com.potatoes.cultivation.logic.Player;

public class CreateVillageProtocol implements Protocol {
	private static final long serialVersionUID = 5177704818428355072L;

	private MapCoordinates villageLocation;
	private Player sender;

	public CreateVillageProtocol(Player sender, MapCoordinates villageLocation) {
		this.sender = sender;
		this.villageLocation = villageLocation;
	}

	public MapCoordinates getVillageLocation(){
		return this.villageLocation;
	}
	
	@Override
	public void execute(Server server) {
		server.propagate(sender, this);
	}

	public void clearSender() {
		sender = null;
	}

}
