package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.potatoes.cultivation.Cultivation;

public class Tile implements Serializable{
	private static final long serialVersionUID = -6410265910809526066L;
	private LandType myType = LandType.Grass;
	private StructureType structure = StructureType.None;
	public Unit occupant = null;
	public Player owner = null;
	public final int x;
	public final int y;
	
	
	public Tile(int x, int y) {
		structure = StructureType.None;
		this.x = x;
		this.y = y;
	}
	
	public void setUnit(Unit u) {
		this.occupant = u;
	}
	public void addStructure(StructureType structure){
		getVillage().removeWood(structure.getCost());
		this.structure = structure;
	}
	
	public StructureType getStructure(){
		return structure;
	}
	
	public void destroyStructure(){
		this.structure = StructureType.None;
	}
	
	public LandType getLandType(){
		return myType;
	}
	
	public void updateLandType(LandType land){
		myType = land;
	}
	
	public void updateOwner(Player p){
		owner = p;
	}
	
	public Player getPlayer(){
		return owner;
	}
	
	public Unit getUnit(){
		return occupant;
	}

	public boolean canInvade(Unit invader){
		boolean invadable = true;
		if (getRegion()!=null && getVillage()!=null) {
			if (getVillage().getType()==VillageType.Fort && invader.getType()!=UnitType.Knight) {
				invadable = false;
			} else if (invader.getType()==UnitType.Peasant) {
				invadable = false;
			} else if (structure==StructureType.Watchtower && invader.getType()!=UnitType.Knight && invader.getType()!=UnitType.Soldier) {
				invadable = false;
			}
		}		
		if (invadable && occupant !=null) {
			//Check if unit on this tile can be invaded by the invader unit u
			System.out.println("Check if unit on this tile can be invaded");
			invadable = this.occupant.getType().canInvadeBy(invader.getType());
		} else if (invadable && occupant==null) {
			//Check neighbouring tiles to see if an enemy exists
			System.out.println("Checking if neighbouring tiles has higher level enemy");
			Set<Tile> neighbourTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(this);
			Iterator<Tile> it = neighbourTiles.iterator();
			while(it.hasNext()){
				Tile next = it.next();
				if(next.owner!=null && next.owner.equals(invader.myVillage.getOwner())) it.remove();
			}
			for (Tile tile : neighbourTiles) {
				if (tile.getUnit()!=null) {
					System.out.println("Tile "+tile+" has a unit! " + tile.occupant + " " + invader);
					invadable = invadable && invader.getType().canGoNear(tile.getUnit().getType());
				}
				//Check if neighbouring tile has a tower, if so, any invader Unit that is infantry cannot invade
				if (invadable && tile.getStructure() == StructureType.Watchtower && invader.getType() == UnitType.Infantry) {
					invadable = false;
				}
				//Check if neighbouring tile has a castle
				if(invadable && tile.getVillage()!=null && tile.getVillage().getTile().equals(tile) && tile.getVillage().getType()==VillageType.Castle ){
					invadable = false;
				}
			}
		}
		System.out.println("This tile is invadable "+invadable);
		return invadable;
	}

	public boolean tryInvade(Unit u){
		boolean moved = false;
		Tile tileOfUnit = u.getTile();
		Set<Tile> neighbouringTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(tileOfUnit);
		System.out.println("Tiles neighbouring unit "+ neighbouringTiles);
		if (neighbouringTiles.contains(this)) { //We only enter this if Unit u is one tile away from this tile
			System.out.println("Trying to move to neighbouring tile " + this + " , this belongs to "+ this.owner + " whereas unit belongs to "+ u.getVillage().getOwner());
			if ((UnitType.Knight.equals(u.getType()) || UnitType.Cannon.equals(u.getType())) && (structure == StructureType.Tombstone || myType == LandType.Tree)) {
				System.out.println("Knight/Cannon cannot move into "+this.structure.name());
				return false;
			}
			else if(UnitType.Cannon.equals(u.getType()) ){
				System.out.println("Trying to move a cannon");
				if(occupant==null && this.getPlayer().equals(u.getVillage().getOwner())){
					u.currentAction = ActionType.Moved;
					return true;
				}
				return false;
			}
			// If it's not a sea, and (unowned or invadable or ours)
			else if (myType!= LandType.Sea && (owner == null || owner.equals(tileOfUnit.getPlayer()) || this.canInvade(u))) {
				System.out.println("Moving to destination");
				System.out.println("Owner is now "+this.owner+ " village:"+this.getVillage());
				
				// If the target tile is neutral
				if(owner == null || owner.equals(Player.nullPlayer)) {
					u.currentAction = ActionType.Moved;
				}
				
				// Can't move own villager to own village tiles
				if (this.containsVillage() && u.getTile().owner.equals(this.owner)) {
					return false;
				}
				
				// If there's someone
				if (occupant!=null) {
					// If one of our unit is there, we don't crush it
					// We can also push combining unit logic here, before returning false
					if(owner.equals(tileOfUnit.getPlayer())) {
						if(u.canMerge(occupant) || occupant.canMerge(u)) {
							System.out.println("Merging units");
							Region region = this.getRegion();
							Tile t = occupant.myTile;
							int result = occupant.getType().ordinal() + u.getType().ordinal() + 1;
							region.killUnit(occupant);
//							region.killUnit(u);
//							Unit hybridUnit = new Unit(u.myTile);
//							hybridUnit.updateType(UnitType.values()[result]);
//							u.myTile.occupant = hybridUnit;
//							region.getUnits().add(hybridUnit);
//							Cultivation.GAMEMANAGER.getGame().getWorld().createPotatoAt(u.myTile.x, u.myTile.y);
							u.updateType(UnitType.values()[result]);
							u.updateTileLocation(t);
							Cultivation.GAMEMANAGER.getGame().getWorld().upgradePotatoAt(u.myTile.x, u.myTile.y, u.myType);
							return true;
						}
						return false;
					}
					// Else we crush it
					System.out.println("There is an occupant at destination");
					// note:The level of the enemy has already been checked in can invade
					// kill the victim unit 
					Region victimsRegion = this.getRegion();
					victimsRegion.killUnit(this.occupant);
				}
				moved = true;
				Region oldRegion = u.myVillage.getRegion();
				Player oldOwner = this.owner;
				System.out.println("Old region is "+oldRegion+ " old owner:"+oldOwner);
				u.updateTileLocation(this);
				//Handle village invasion, splitting/merging regions in takeoverTile
				if (oldOwner!=null && !oldOwner.equals(Player.nullPlayer) && !oldOwner.equals(u.myVillage.getOwner())) {
					Cultivation.GAMEMANAGER.getGameMap().takeOverTile(this);
					u.updateAction(ActionType.Moved);
				}
				else this.owner = this.occupant.myVillage.getOwner();
				
				//If there is a tree on this tile, cut it down
				if (myType == LandType.Tree) {
					System.out.println("There is a tree at destination");
					myType = LandType.Grass;
					u.myVillage.addWood(1);
					u.updateAction(ActionType.ChoppedTree);
				}
				if (structure == StructureType.Tombstone) {
					System.out.println("There is a tombstone at destination");
					u.updateAction(ActionType.ClearedTombStone);
					structure = StructureType.None;
				}
				if (structure!=StructureType.Road && (u.getType()==UnitType.Knight || u.getType()==UnitType.Soldier || u.getType()==UnitType.Cannon) && myType==LandType.Meadow) {
					System.out.println("Knight/Soldier/Cannon has destroyed a meadow");
					myType = LandType.Grass;
				}
				if (structure==StructureType.Watchtower && (u.getType()==UnitType.Soldier || u.getType()==UnitType.Knight)) {
					structure = StructureType.None;
				}
				System.out.println("Owner is now "+this.owner+ " village:"+this.getVillage());

				Set<Tile> tilesNeighbouringDestination = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(this);
				Set<Village> myNeighbouringVillages = Cultivation.GAMEMANAGER.getGameMap().getMyVillagesOfAdjacentTiles(tilesNeighbouringDestination, this.owner);
				
				//Convert to stack for mergeTo method
				if (myNeighbouringVillages.size()>1) {
					System.out.println("Merging neighbouring "+myNeighbouringVillages.size()+" villages");
					Village biggestVillage = Cultivation.GAMEMANAGER.getGameMap().biggestOf(new HashSet<>(myNeighbouringVillages));
					System.out.println("Biggest village is "+biggestVillage);
					Stack<Village> stackOfVillages = new Stack<Village>();
					for (Village v : myNeighbouringVillages) {
						System.out.println(v+" has gold "+v.getGold());
						stackOfVillages.push(v);
					}
					System.out.println("Biggest before "+ biggestVillage.getGold());
					Cultivation.GAMEMANAGER.getGameMap().mergeTo(biggestVillage, stackOfVillages);
					System.out.println("Biggest after "+ biggestVillage.getGold());
					biggestVillage.getRegion().addTile(this);
				}
				else{
					oldRegion.addTile(this);
				}
				System.out.println("Owner is now "+this.owner+ " village:"+this.getVillage() + " region:"+this.getRegion());
			}
		}
		System.out.println("Unit has moved : "+ moved);
		return moved;
	}
	
	/**
	 * Same thing as tryInvade, but without the side effect
	 */
	public boolean tryInvadeCheck(Unit u) {
		boolean moved = false;
		Tile tileOfUnit = u.getTile();
		Set<Tile> neighbouringTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(tileOfUnit);
		if (neighbouringTiles.contains(this)) { //We only enter this if Unit u is one tile away from this tile
			if ((UnitType.Knight.equals(u.getType()) || UnitType.Cannon.equals(u.getType())) && (structure == StructureType.Tombstone || myType == LandType.Tree)) {
				//If tile to invade has tree or tombstone and invader is a knight or cannon, can't invade
				return false;
			}
			else if(UnitType.Cannon.equals(u.getType()) ){
				if (this.getPlayer()==null) return myType!=LandType.Sea; //If neutral land, return true if it's not a sea tile
				return occupant==null && this.getPlayer().equals(u.getVillage().getOwner());
			}
			// If it's not a sea, and (unowned or invadable or ours)
			else if (myType!= LandType.Sea && (owner == null || owner.equals(tileOfUnit.getPlayer()) || this.canInvade(u))) {
				if (occupant!=null && owner.equals(tileOfUnit.getPlayer())) {
					return u.canMerge(occupant) || occupant.canMerge(u);
				}
				if (this.containsVillage() && u.getTile().owner.equals(this.owner)) {
					return false;
				}
				moved = true;
			}
		}
		return moved;
	}
	
	public Region getRegion(){
		return Cultivation.GAMEMANAGER.getGameMap().getRegion(this);
    }
	
	public Village getVillage(){
		Region region = Cultivation.GAMEMANAGER.getGameMap().getRegion(this);
		if (region!=null) {
			return region.getVillage();
		}
		return null;
	}
	@Override 
	public String toString() {
		return myType.toString() + "("+this.x+","+this.y+")";
	}

	public boolean containsVillage() {
		Village myVillage = getVillage();
		return myVillage!=null && myVillage.getTile()==(this);
	}

	public boolean inShootingDistance(GameMap map, Tile target) {
		if(this.equals(target)) return false;
		Set<Tile> stepOne = map.getNeighbouringTiles(this);
		Set<Tile> stepTwo = new HashSet<>();
		for (Tile tile : stepOne) {
			stepTwo.addAll( map.getNeighbouringTiles(tile));
		}
		return stepTwo.contains(target);
	}

}
