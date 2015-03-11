package com.potatoes.cultivation.logic;

import java.util.List;
import java.util.Set;

public class CultivationGame {

	private int roundsPlayed;
	private Player turnOf;
	private List<Player> players;
	private GameMap map;
	
	public CultivationGame(List<Player> participants) {
		players = participants;
		map = new GameMap(10, 10, participants);
		System.out.println("Made new game map");
		this.roundsPlayed = 0;
		turnOf = participants.get(0);
	}
	
	public List<Player> getPlayers(){
		List<Player> players = new LinkedList<Player>(this.players);
		return players;
	}
	//Mang0-Tang0
	public void beginTurn(Player p){
		map.clearTombstones(p);
		map.produceMeadows(p);
		map.produceRoads(p);
		updateGoldEconomy(p);
		increaseRoundCount();
		
	}
	//return true if the village was successfully upgraded
	//Mang0-Tang0 Strikes again
	public boolean upgradeVillage(Village v, VillageType type){
		boolean villageUpgraded = false;
		VillageType currentType = v.getType();
		int woodCount = v.getWood();
		int cost = type.getCost();
		if((currentType == VillageType.Hovel && type == VillageType.Town) || (currentType == VillageType.Town && type == VillageType.Fort) ){
			if(woodCount >= cost){
				v.setType(type);
				v.removeWood(cost);
				villageUpgraded = true;
			}
		}
		
		return villageUpgraded;
	}
	
	public boolean upgradeUnit(Unit u, UnitType uType ){
		boolean unitUpgraded = false;
		Village v = u.getVillage();
		VillageType vType = v.getType();
		boolean support = false;
		
		if((vType == VillageType.Hovel) && (uType != UnitType.Soldier || uType != UnitType.Knight)){
			support = true;
		}
		else if((vType == VillageType.Town) && (uType != UnitType.Knight)){
			support=true;
		}
		else if(vType == VillageType.Fort){
			support = true;
		}
		
		if(support){
			UnitType currentType = u.getType();
			int currentRank = currentType.ordinal();
			int newRank = uType.ordinal();
			
			if(currentRank < newRank){
				int goldCount = v.getGold();
				int cost = uType.getCostFrom(currentType);
				
				if(goldCount >= cost){
					u.updateType(uType);
					v.removeGold(cost);
					unitUpgraded = true;
				}
			}
		}
		
		return unitUpgraded;
	}
	
	public void increaseRoundCount(){
		roundsPlayed++;
	}
	
	public void updateGoldEconomy(Player p){
		Set<Village> myVillages = map.getVillages(p);
		
		for(Village v : myVillages){
			Region myRegion = v.getRegion();
			Set<Tile> myTiles = myRegion.getTiles();
			
			for(Tile t: myTiles){
				LandType myLandType = t.getLandType();
				int value = myLandType.getGoldValue(myLandType);
				v.addGold(value);
			}
			Set<Unit> myUnits = myRegion.getUnits();
			
			for(Unit u : myUnits){
				UnitType uType = u.getType();
				int salary = uType.getSalary(uType);
				boolean success = v.removeGold(salary);
				
				if(success == false){
					Tile myTile = u.getTile();
					myRegion.killUnit(u);
					myTile.addStructure(StructureType.Tombstone);
				}
			}
		}
	}
	//this will show error in tryInvade in Tile isn't changed to return a boolean
	public boolean moveUnit(Unit u, Tile t){
		boolean moved = t.tryInvade(u);
		return moved;
	}
	
	//return true on success
	public boolean buildRoad(Unit u){
		UnitType uType = u.getType();
		ActionType currAction = u.getCurrentAction();
		boolean success = false;
		
		if(uType == UnitType.Peasant && currAction == ActionType.ReadyForOrders){
			u.updateAction(ActionType.BuildingRoad);
			if(u.getCurrentAction() == ActionType.BuildingRoad){
				success = true;
			}
		}
		return success;
	}
	
	public GameMap getGameMap(){
		return map;
	}
}
