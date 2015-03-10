package com.potatoes.cultivation.logic;

import java.util.Set;

import com.potatoes.cultivation.screens.InGame;

public class Region {
	
	private Set<Tile> myTiles;
	private Village village;
	private Set<Unit> myUnits;
	
	public void addTile(Tile t){
		myTiles.add(t);
	}
	
	public void removeTile(Tile t){
		myTiles.remove(t);
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
		return village.getOwner();
	}
	
	public void  setVillage(Village v){
		this.village = v;
	}
	
	public void killUnit(Unit u){
		myUnits.remove(u);
	}
	
	public int size(){
		return myTiles.size();
	}
	
	public void merge(Region r){
		//Add all units from Region r to our set of units
		for (Unit u : r.getUnits()) {
			this.myUnits.add(u);
		}
		//Add all tiles from Region r to our set of tiles
		for (Tile t : r.getTiles()) {
			this.myTiles.add(t);
		}
		//Delete Region r
		InGame.gameMap.deleteRegion(r);
	}

}
