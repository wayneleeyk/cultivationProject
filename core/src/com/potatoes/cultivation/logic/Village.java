package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Random;

import com.potatoes.cultivation.Cultivation;

public class Village implements Comparable<Village>, Serializable {
	private static final long serialVersionUID = -8934307394308919420L;
	private VillageType myType;
	private int gold;
	private int wood;
	private Region myRegion;
	private Tile myTile;
	private Player owner;
	
	private int HP;
	private VillageStatus myStatus;
	
	public Village(Player owner, Region region, Tile tile) {
		this.owner = owner;
		this.myRegion = region;
		this.myTile = tile;
		this.myType = VillageType.Hovel;
		this.gold = 99;
		this.wood = 99;
		this.HP = myType.maxHP();
		this.myStatus = VillageStatus.VillageReady;
	}
	
	/**
	 * @return		The village is destroyed
	 */
	public boolean shoot(){
		return --this.HP <= 0;
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
		if(this.gold - goldToRemove >= 0){
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
	public Village merge(Village village){
		if(village.equals(this)) return this;
		if (this.compareTo(village)>0) {
			(this.myRegion).merge(village.getRegion());
			village.myRegion = null;
			Cultivation.GAMEMANAGER.getGame().getWorld().removeVillageAt(village.getTile().x, village.getTile().y);
			this.gold += village.gold;
			this.wood += village.wood;
			village.myTile.updateLandType(LandType.Meadow);
			return this;
		} else {
			(village.getRegion()).merge(this.myRegion);
			this.myRegion = null;
			Cultivation.GAMEMANAGER.getGame().getWorld().removeVillageAt(this.getTile().x, this.getTile().y);
			village.gold += this.gold;
			village.wood += this.wood;
			this.myTile.updateLandType(LandType.Meadow);
			return village;
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
	
	public VillageStatus getStatus() {
		return myStatus;
	}
	
	public void updateStatus(VillageStatus status) {
		myStatus = status;
	}
	
	public void upgradeVillageType() {
		VillageType type = myType;
		if (myType == VillageType.Hovel) type = VillageType.Town;
		if (myType == VillageType.Town) type = VillageType.Fort;
		if (myType == VillageType.Fort) type = VillageType.Castle;
		if (myType == VillageType.Castle) return;
		this.HP = type.maxHP();
		setType(type);
	}
	
	public enum VillageStatus {
		VillageReady, StartUpgrading, StillUpgrading
	}

	public int HP() {
		return this.HP;
	}
}
