package com.potatoes.cultivation.helpers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.potatoes.cultivation.gameactors.PotatoActor;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.stages.GameWorld;

public class ClickManager {
	
	Actor first = null;
	Actor second = null;

	GameWorld world;
	
	public void setWorld(GameWorld pWorld) {
		world = pWorld;
	}

	public void addClickedActor(Actor actor) {
		if(first instanceof PotatoActor && !(actor instanceof PotatoActor)){
			second = actor;
		}
		else{
			first = actor;
		}
	}
	
	public VillageActor villageToActor(Village village){
		int x = village.getTile().x;
		int y = village.getTile().y;
		for (Actor actor : world.tiles[x][y].getChildren()) {
			if(actor instanceof VillageActor) return (VillageActor) actor;
		}
		return null;
	}
	
	public VillageActor lastClickedVillageActor(){
		return villageToActor(lastClickedVillage());
	}
	
	public Village lastClickedVillage(){
		if(first instanceof PotatoActor){
			return ((PotatoActor) first).getUnit().myVillage;
		}
		else if(first instanceof VillageActor){
			return ((VillageActor) first).getVillage();
		}
		else if(first instanceof TileActor){
			return ((TileActor) first).getTile().getVillage();
		}
		return null;
	}
}
