package com.potatoes.cultivation.logic;

import java.util.Set;

public class Region {
	
	private Set<Tile> myTiles;
	private Village village;
	private Set<Unit> myUnits;
	
	
	public void addTile(Tile t){
		
	}
	
	public void removeTile(Tile t){
		
	}
	
	public Set<Tile> getTiles(){
	return myTiles;	
	}
	
	public Village getVillage(){
		return village;
	}
	
	public Set<Unit> getUnits(){
	return myUnits;	
	}
	
	public Player getOwner(){
		return null;
	}
	
	
	public void  setVillage(Village v){
		
	}
	
	public void killUnit(Unit u){
		
	}
	
	public int size(){
		return 0;
	}
	
	public Region merge(Region r){
		return null;
	}

}
