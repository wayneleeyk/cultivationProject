package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Set;

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.screens.InGame;

public class Tile implements Serializable{
	private static final long serialVersionUID = -6410265910809526066L;
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
	public boolean canInvade(Unit u){
		boolean occupied = occupant!=null || structure!=StructureType.None;
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
		//Check neighbouring tiles to see if an enemy exists
		if (invadable && occupant==null) {
			Set<Tile> neighbourTiles = Cultivation.GAMEMANAGER.getGameMap().getNeighbouringTiles(this);
			for (Tile tile : neighbourTiles) {
				if (tile.getUnit()!=null) {
					invadable = invadable && tile.getUnit().getType().canInvadeBy(u.getType());
				}
			}
		}
		return invadable;
	}
	
	public void tryInvade(Unit u){
		
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
