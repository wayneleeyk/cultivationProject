package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Set;
import java.util.Stack;

import com.potatoes.cultivation.Cultivation;

public class Tile implements Serializable{
	private static final long serialVersionUID = -6410265910809526066L;
	private LandType myType = LandType.Grass;
	private StructureType structure = StructureType.None;
	public Unit occupant = null;
	private Player owner = null;
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

	//Checks if Unit u can invade this tile
	//NOTE: I added a check if the invader unit (u) can defeat the unit on the tile (if any). This seems missing in the sequence diagram..
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
			invadable = this.occupant.getType().canInvadeBy(u.getType());
		} else if (invadable && occupant==null) {
			//Check neighbouring tiles to see if an enemy exists
			Set<Tile> neighbourTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(this);
			for (Tile tile : neighbourTiles) {
				if (tile.getUnit()!=null) {
					invadable = invadable && tile.getUnit().getType().canInvadeBy(u.getType());
					//Check if neighbouring tile has a tower, if so, any invader Unit that is infantry cannot invade
					if (invadable && tile.getStructure() == StructureType.Watchtower && u.getType() == UnitType.Infantry) {
						invadable = false;
					}
				}
			}
		}
		return invadable;
	}
	/*
	 * Questions or mistakes? --> Monica
	 * Unit u is the unit that tries to go on this tile
	 */
	public boolean tryInvade(Unit u){
		boolean moved = false;
		Tile tileOfUnit = u.getTile();
		Set<Tile> neighbouringTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(tileOfUnit);
		if (neighbouringTiles.contains(this)) { //We only enter this if Unit u is one tile away from this tile
			if (UnitType.Knight.equals(u) && (structure==StructureType.Tombstone || myType == LandType.Tree)) {
				//If invader is knight, he cannot invade tile that contains tomb stone nor tree
			}
			else if(UnitType.Cannon.equals(u) && owner == tileOfUnit.getPlayer()){
				moved = true;
				u.updateTileLocation(this);
			}
			else if (myType!= LandType.Sea && (owner == tileOfUnit.getPlayer() || this.canInvade(u))) {
				//Enter here if unit can be moved onto this tile
				moved = true;
				u.updateTileLocation(this);
				//If there is already an occupant on this tile, destroy it
				if (occupant!=null) {
					System.out.println("There is not occupant at destination");
					Region victimsRegion = this.occupant.getTile().getRegion();
					//Remove this tile from victim's region since it got invaded
					victimsRegion.removeTile(this);
					//Remove victim unit from region
					victimsRegion.killUnit(this.occupant);
					//Add this tile to the invader's region
					tileOfUnit.getRegion().addTile(this);
					//Update occupant to the invader unit
					this.occupant = u;
					//Note: we don't change the ownership of the tile yet because we need it in takeoverTile
				}
				//Handle village invasion, splitting/merging regions in takeoverTile
				if (owner!=null) {
					Cultivation.GAMEMANAGER.getGameMap().takeOverTile(this);
				}
				//If there is a tree on this tile, cut it down
				if (myType == LandType.Tree) {
					System.out.println("There is a tree at destination");
					//Change landtype to grass
					myType = LandType.Grass;
					//Update wood stats of village
					this.getVillage().addWood(1);
					//Update unit's action to cutting wood
					u.updateAction(ActionType.ChoppingTree);
				}
				//If there is a tomb stone on tile, clear it
				if (structure == StructureType.Tombstone) {
					//Set unit's action to clearing tomb stone
					u.updateAction(ActionType.ClearingTombStone);
					//Destroy tomb stone
					structure = StructureType.None;
				}
				//If unit is Knight and there is a meadow and no road, he tramples the meadow 
				if (structure!=StructureType.Road && u.getType()==UnitType.Knight && myType==LandType.Meadow) {
					myType = LandType.Grass;
				}
				//Merge part 
				Set<Village> myNeighbouringVillages = Cultivation.GAMEMANAGER.getGameMap().getMyVillagesOfAdjacentTiles(neighbouringTiles, owner);
				//Convert to stack for mergeTo method
				if (myNeighbouringVillages.size()>=1) {
					Village biggestVillage = Cultivation.GAMEMANAGER.getGameMap().biggestOf(myNeighbouringVillages);
					Stack<Village> stackOfVillages = new Stack<Village>();
					for (Village v : myNeighbouringVillages) {
						stackOfVillages.push(v);
					}
					Cultivation.GAMEMANAGER.getGameMap().mergeTo(biggestVillage, stackOfVillages);
				}
			}
		}
		return moved;
	}
	
	public Region getRegion(){
		return Cultivation.GAMEMANAGER.getGameMap().getRegion(this);
    }
	
	public Village getVillage(){
		Region region = Cultivation.GAMEMANAGER.getGameMap().getRegion(this);
		if (region!=Region.NO_REGION) {
			return region.getVillage();
		}
		return null;
	}
	@Override public String toString() {
		return myType.toString();
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
