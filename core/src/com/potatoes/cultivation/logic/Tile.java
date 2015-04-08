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

	public boolean canInvade(Unit u){
		boolean invadable = true;
		if (getRegion()!=null && getVillage()!=null) {
			if (getVillage().getType()==VillageType.Fort && u.getType()!=UnitType.Knight) {
				invadable = false;
			} else if (u.getType()==UnitType.Peasant) {
				invadable = false;
			} else if (structure==StructureType.Watchtower && u.getType()!=UnitType.Knight && u.getType()!=UnitType.Soldier) {
				invadable = false;
			}
		}		
		if (invadable && occupant !=null) {
			//Check if unit on this tile can be invaded by the invader unit u
			System.out.println("Check if unit on this tile can be invaded");
			invadable = this.occupant.getType().canInvadeBy(u.getType());
		} else if (invadable && occupant==null) {
			//Check neighbouring tiles to see if an enemy exists
			System.out.println("Checking if neighbouring tiles has higher level enemy");
			Set<Tile> neighbourTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(this);
			Iterator<Tile> it = neighbourTiles.iterator();
			while(it.hasNext()){
				Tile next = it.next();
				if(next.owner!=null && next.owner.equals(u.myVillage.getOwner())) it.remove();
			}
			for (Tile tile : neighbourTiles) {
				if (tile.getUnit()!=null) {
					System.out.println("Tile "+tile+" has a unit! " + tile.occupant + " " + u);
					invadable = invadable && tile.getUnit().getType().canInvadeBy(u.getType());
					//Check if neighbouring tile has a tower, if so, any invader Unit that is infantry cannot invade
					if (invadable && tile.getStructure() == StructureType.Watchtower && u.getType() == UnitType.Infantry) {
						invadable = false;
					}
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
			}
			else if (myType!= LandType.Sea && (owner == tileOfUnit.getPlayer() || this.canInvade(u))) {
				System.out.println("Moving to destination");
				System.out.println("Owner is now "+this.owner+ " village:"+this.getVillage());
				if (occupant!=null) {
					System.out.println("There is an occupant at destination");
//					Region victimsRegion = this.occupant.getTile().getRegion();
//					victimsRegion.removeTile(this);
//					victimsRegion.killUnit(this.occupant);
//					tileOfUnit.getRegion().addTile(this);
//					this.occupant = u;
				}
				moved = true;
				Region oldRegion = u.myVillage.getRegion();
				System.out.println("Old region is "+oldRegion);
				u.updateTileLocation(this);
				
				//Handle village invasion, splitting/merging regions in takeoverTile
//				if (owner!=null) {
//					Cultivation.GAMEMANAGER.getGameMap().takeOverTile(this);
//				}
				//If there is a tree on this tile, cut it down
				if (myType == LandType.Tree) {
					System.out.println("There is a tree at destination");
					myType = LandType.Grass;
					u.myVillage.addWood(1);
					u.updateAction(ActionType.ChoppingTree);
				}
				if (structure == StructureType.Tombstone) {
					System.out.println("There is a tombstone at destination");
					u.updateAction(ActionType.ClearingTombStone);
					structure = StructureType.None;
				}
				if (structure!=StructureType.Road && (u.getType()==UnitType.Knight || u.getType()==UnitType.Soldier || u.getType()==UnitType.Cannon) && myType==LandType.Meadow) {
					System.out.println("Knight/Soldier/Cannon has destroyed a meadow");
					myType = LandType.Grass;
				}
				System.out.println("Owner is now "+this.owner+ " village:"+this.getVillage());

				Set<Tile> tilesNeighbouringDestination = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(this);
				System.out.println("Neighbouring tiles "+tilesNeighbouringDestination);
				Set<Village> myNeighbouringVillages = Cultivation.GAMEMANAGER.getGameMap().getMyVillagesOfAdjacentTiles(tilesNeighbouringDestination, this.owner);
				System.out.println("Neighbouring villages " +myNeighbouringVillages);
				
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
	/**
	 * 
	 * @return true if this tile contains its village building, false otherwise
	 */
	public boolean containsVillage() {
		Village myVillage = getVillage();
		if (myVillage!=null && myVillage.getTile()==(this)) {
//			System.out.println("Found village in containsVillage method");
			return true;
		}
//		System.out.println("Cannot find village in containsVillage");
		return false;
	}

}
