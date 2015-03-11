package com.potatoes.cultivation.logic;

import java.util.Set;

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.screens.InGame;

public class Tile {
	
	private LandType myType;
	private StructureType structure;
	private Unit occupant;
	private Player owner;
	
	public Tile() {
		structure = StructureType.None;
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
	public void tryInvade(Unit u){
		boolean moved = false;
		Tile tileOfUnit = u.getTile();
		Set<Tile> neighbouringTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(tileOfUnit);
		if (neighbouringTiles.contains(this)) { //We only enter this if Unit u is one tile away from this tile
			boolean invadable = true;
			if (u.getType()==UnitType.Knight && (structure==StructureType.Tombstone || myType == LandType.Tree)) {
				//If invader is knight, he cannot invade tile that contains tomb stone nor tree
				invadable = false;
			}
			if (invadable && myType!= LandType.Sea && (owner == tileOfUnit.getPlayer() || this.canInvade(u))) {
				//Enter here if unit can be moved onto this tile
				moved = true;
				u.updateTileLocation(this);
				//If there is already an occupant on this tile, destroy it
				if (occupant!=null) {
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
					//Change landtype to grass
					myType = LandType.Grass;
					//Update wood stats of village
					this.getVillage().addWood(1);
					//Update unit's action to cutting wood
					u.updateAction(ActionType.ChoppingTree);
				}
				//If there is a tomb stone on tile, clear it
				if (structure == StructureType.Tombstone) {
					
					
				}
			}
		}
	}
	
	public Region getRegion(){
		return Cultivation.GAMEMANAGER.getGameMap().getRegion(this);
    }
	
	public Village getVillage(){
		return Cultivation.GAMEMANAGER.getGameMap().getRegion(this).getVillage();
	}
	@Override public String toString() {
		return myType.toString();
	}
	
	
}
