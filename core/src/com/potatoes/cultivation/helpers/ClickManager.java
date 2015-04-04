package com.potatoes.cultivation.helpers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	
	/**
	 * This should belong in the GameWorld class
	 */
//	public VillageActor villageToActor(Village village){
//		int x = village.getTile().x;
//		int y = village.getTile().y;
//		for (Actor actor : world.tiles[x][y].getChildren()) {
//			if(actor instanceof VillageActor) return (VillageActor) actor;
//		}
//		return null;
//	}
//	
//	public VillageActor lastClickedVillageActor(){
//		return villageToActor(lastClickedVillage());
//	}
	
	/**
	 * 
	 * @return Returns the last {@link Village} clicked. If the last actor clicked
	 * was not a {@link VillageActor}, then it will try to find the village associated
	 * with that actor.
	 */
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
	
	/**
	 * @return a VillageActor if the last clicked is indeed a VillageActor.
	 * Otherwise, return null.
	 */
	public VillageActor getVillageActor() {
		if(first instanceof VillageActor) return (VillageActor) first;
		return null;
	}
	
	/**
	 * @return a PotatoActor if the last clicked is indeed a PotatoActor.
	 * Otherwise, return null.
	 */
	public PotatoActor getPotatoActor() {
		if(first instanceof PotatoActor) return (PotatoActor) first;
		return null;
	}
	
	/**
	 * @return a PotatoActor if the last clicked is indeed a PotatoActor.
	 * Otherwise, return null.
	 */
	public TileActor getTileActor() {
		if(first instanceof TileActor) return (TileActor) first;
		return null;
	}
	
	
	/**
	 * For any Actor that wants to get tracked by the ClickManager, add this listener to it
	 */
	public class ClickToCMListener extends ClickListener {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if(!event.isStopped()) {
				System.out.println(event.getListenerActor().toString() + " clicked at " + x + " " + y);
				ClickManager.this.addClickedActor(event.getListenerActor());
				event.stop();	
			}
		}
	}
}
