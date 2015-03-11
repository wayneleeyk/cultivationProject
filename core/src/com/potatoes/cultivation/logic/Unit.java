package com.potatoes.cultivation.logic;

import java.io.Serializable;

//Author: Shivani Sharma
public class Unit implements Serializable{
	private static final long serialVersionUID = -4822623392988569568L;
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
	
	//I can't find this in the sequence diagrams. Might be an old method that we don't use anymore
	public boolean tryInvadeTile(Tile t){
		return false;
	}
	//a little unsure about this, but I'm assuming we set the Unit's tile as the tile passed as a parameter.
	public void updateTileLocation(Tile t){
		myTile = t;
	}
}
