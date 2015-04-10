package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gameactors.TileActor;

public class Region implements Serializable{
	private static final long serialVersionUID = -6761074377290852816L;

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
		Cultivation.GAMEMANAGER.getGame().getWorld().removePotatoAt(u.myTile.x, u.myTile.y);
		u.myTile.setUnit(null);
		if (!myUnits.remove(u)) {
			System.out.println("failed to remove from set the unit : " + u);
		}
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
	
	public boolean hasSameTilesAs(Region other){
		return this.myTiles.containsAll(other.myTiles) && other.myTiles.containsAll(this.myTiles);
	}
	
	public void destroy(){
		System.out.println("Destroying region "+ this+ " which has tiles "+this.myTiles);
		myUnits.clear();
		for (Tile tile : myTiles) {
			if(tile.occupant!=null) this.killUnit(tile.occupant);
			if(this.village!=null && tile == this.village.getTile()) {
				this.village = null;
				Cultivation.GAMEMANAGER.getGame().getWorld().removeVillageAt(tile.x,tile.y);
			}
			tile.owner=null;
		}
		myTiles.clear();
		Cultivation.GAMEMANAGER.getGameMap().deleteRegion(this);
	}

	
}
