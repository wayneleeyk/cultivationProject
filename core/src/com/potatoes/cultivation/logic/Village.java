package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Random;

public class Village implements Comparable<Village>, Serializable {
	private static final long serialVersionUID = -8934307394308919420L;
	private VillageType myType;
	private int gold;
	private int wood;
	private Region myRegion;
	private Tile myTile;
	private Player owner;
	
	private int HP;
	
	public Village(Player owner, Region region, Tile tile) {
		this.owner = owner;
		this.myRegion = region;
		this.myTile = tile;
		this.myType = VillageType.Hovel;
		this.gold = 99;
		this.wood = 99;
		this.HP = myType.maxHP();
	}
	
	/**
	 * @return		The village is destroyed
	 */
	public boolean shoot(){
		return --this.HP < 0;
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
		return false;	
	}
	
	public void addWood(int woodToAdd){
		this.wood = this.wood + woodToAdd;  
	}
	
	public void removeWood(int woodToRemove){
		this.wood = this.wood - woodToRemove;
	}
	
	//Merges the smaller village to the larger village
	public Village merge(Village v){
		if(v.equals(this)) return this;
		if (this.compareTo(v)>0) {
			(this.myRegion).merge(v.getRegion());
			v.myRegion = null;
			this.gold += v.gold;
			this.wood += v.wood;
			return this;
		} else {
			(v.getRegion()).merge(this.myRegion);
			this.myRegion = null;
			v.gold += this.gold;
			v.wood += this.wood;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((myTile == null) ? 0 : myTile.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Village other = (Village) obj;
		if (myTile == null) {
			if (other.myTile != null)
				return false;
		} else if (!myTile.equals(other.myTile))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}	
	
	public boolean higherThan(VillageType v){
		return this.myType.ordinal() > v.ordinal();
	}
}
