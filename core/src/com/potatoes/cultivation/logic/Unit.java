package com.potatoes.cultivation.logic;
//Author: Shivani Sharma
public class Unit {

	private UnitType myType;
	private ActionType currentAction;
	private Tile myTile;
	private Village myVillage;
	
	public ActionType getCurrentAction(){
		return currentAction;
	}
	
	public void updateAction (ActionType action){
		currentAction = action;
	}
	
	public UnitType getType(){
		return myType;
	}
	
	public Tile getTile(){
		return myTile;
	}
	
	public void updateType(UnitType type){
		myType = type;
	}
	
	public Village getVillage(){
		return myVillage;
	}
	
	public boolean tryInvadeTile(Tile t){
		
		return false;
	}
	//a little unsure about this, but I'm assuming we set the Unit's tile as the tile passed as a parameter.
	public void updateTileLocation(Tile t){
		myTile = t;
	}
}
