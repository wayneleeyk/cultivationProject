package com.potatoes.cultivation.logic;

import java.io.Serializable;

import com.potatoes.cultivation.Cultivation;

public class Unit implements Serializable{
	private static final long serialVersionUID = -4822623392988569568L;
	public UnitType myType;
	public ActionType currentAction;
	public Tile myTile;
	public Village myVillage;
	public Unit(Tile t) {
		this.myType = UnitType.Peasant;
		this.currentAction = ActionType.ReadyForOrders;
		this.myTile = t;
		this.myVillage = myTile.getRegion().getVillage();
	}
	/***
	 * To be deleted (only here for testing)
	 */
	public Unit() {
		
	}
	
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

	public void updateTileLocation(Tile t){
		myTile.occupant = null;
		myTile = t;
		t.occupant = this;
		t.owner = t.occupant.myVillage.getOwner();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentAction == null) ? 0 : currentAction.hashCode());
		result = prime * result + ((myTile == null) ? 0 : myTile.hashCode());
		result = prime * result + ((myType == null) ? 0 : myType.hashCode());
		result = prime * result
				+ ((myVillage == null) ? 0 : myVillage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Unit other = (Unit) obj;
		if (currentAction != other.currentAction) {
			return false;
		}
		if (myTile == null) {
			if (other.myTile != null) {
				return false;
			}
		} else if (!myTile.equals(other.myTile)) {
			return false;
		}
		if (myType != other.myType) {
			return false;
		}
		if (myVillage == null) {
			if (other.myVillage != null) {
				return false;
			}
		} else if (!myVillage.equals(other.myVillage)) {
			return false;
		}
		return true;
	}
	
}
