package com.potatoes.cultivation.logic;

public class Tile {
	
	private LandType myType;
	private StructureType structure;
	private Unit occupant;
	private Player owner;
	
	
	
	public void addStructure(StructureType structure){
		
	}
	
	public StructureType getStructure(){
		return structure;
	}
	
	public void destroyStructure(){
		
	}
	
	public LandType getLandType(){
		return myType;
	}
	
	public void updateLandType(LandType land){
		
	}
	
	public void updateOwner(Player p){
		
	}
	
	public Player getPlayer(){
		return owner;
	}
	
	public Unit getUnit(){
		return occupant;
	}
	
	public boolean canInvade(Unit U){
		return false;
	}
	
	public void tryInvade(Unit u){
		
	}
	
	public Region getRegion(){
		return null;
    }
	
	public Village returnVillage(){
		return null;
	}

	
}
