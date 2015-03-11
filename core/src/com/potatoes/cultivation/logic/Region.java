package com.potatoes.cultivation.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.potatoes.cultivation.Cultivation;

public class Region {
	
	public final static Region NO_REGION = new Region(null);
	
	private Set<Tile> myTiles = new HashSet<>();
	private Village village ;
	private Set<Unit> myUnits = new HashSet<>();
	
	public Region(Village village) {
		this.village = village;
	}
	
	public void addTile(Tile t){
		myTiles.add(t);
	}
	
	public void addTiles(Collection<Tile> tiles){
		myTiles.addAll(tiles);
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
		Cultivation.GAMEMANAGER.getGameMap().deleteRegion(r);
	}

}
