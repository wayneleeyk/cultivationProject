package com.potatoes.cultivation.logic;

import java.util.Random;

public class Village implements Comparable<Village> {

	private VillageType myType;
	private int gold;
	private int wood;
	private Region myRegion;
	private Tile myTile;
	private Player owner;
	
	public Village(Player owner, Region region, Tile tile) {
		this.owner = owner;
		this.myRegion = region;
		this.myTile = tile;
	}
	
	public VillageType getType(){
		return myType;
	}
	
	public Region getRegion(){
		return myRegion;
	}
	
	public Tile getTile(){
		return myTile;
	}
	
	public int getGold(){
		return gold;
	}
	
	public int getWood(){
		return wood;
	}
	
	public Player getOwner(){
		return owner;
	}
	
	public void setType(VillageType type){
		this.myType = type;
	}
	
	public void addGold(int goldToAdd){
		this.gold = this.gold + goldToAdd;
	}
	//just added in an if statement incase we go negative.
	public boolean removeGold(int goldToRemove){
		if(this.gold - goldToRemove > 0){
			this.gold = this.gold - goldToRemove;
			return true;		
		}
		else{
			return false;
		}	
	}
	
	public void addWood(int woodToAdd){
		this.wood = this.wood + woodToAdd;  
	}
	
	public void removeWood(int woodToRemove){
		this.wood = this.wood - woodToRemove;
	}
	
	//Merges the smaller village to the larger village
	public Village merge(Village v){
		if (this.compareTo(v)>0) {
			(this.myRegion).merge(v.getRegion());
			return this;
		} else {
			(v.getRegion()).merge(this.myRegion);
			return v;
		}
	}
	
	
	@Override 
	//compares two villages first by type, then my amount gold, then by amount wood. If still equal, will randomly decide
	//which village is higher rank
	public int compareTo(Village v){
		int c = this.getType().ordinal() - v.getType().ordinal();
		if (c == 0){
			c = this.gold - v.gold;
		}
		if(c==0){
			c = this.wood - v.wood;
		}
		if(c==0){
			c = (new Random().nextInt(2) * 2) - 1;
		}
		return c;
	}
}
