package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.potatoes.cultivation.Cultivation;

public class Region implements Serializable{
	private static final long serialVersionUID = -6761074377290852816L;

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
		u.myTile.setUnit(null);
	}
	
	public int size(){
		return myTiles.size();
	}
	
	public void merge(Region r){
		System.out.println("Merging region");
		Iterator<Unit> uit = r.myUnits.iterator();
		Iterator<Tile> tit = r.myTiles.iterator();
		System.out.println(r.myUnits.size() + " tile size:"+ r.myTiles.size());
		while(uit.hasNext()){
			Unit unit = uit.next();
			this.myUnits.add(unit);
			System.out.println("units village in merge region (before)" +unit.myVillage);
			unit.myVillage = this.village;
			System.out.println("units village in merge region (after)" +unit.myVillage);
			uit.remove();
		}
		while(tit.hasNext()){
			Tile tile = tit.next();
			this.myTiles.add(tile);
			tit.remove();
		}
		Cultivation.GAMEMANAGER.getGameMap().deleteRegion(r);
	}

	
}
