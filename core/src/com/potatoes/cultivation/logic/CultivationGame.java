package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CultivationGame implements Serializable {
	private static final long serialVersionUID = 5863425364436347495L;
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

	public List<Player> getPlayers() {
		List<Player> players = new LinkedList<Player>(this.players);
		return players;
	}

	// Mang0-Tang0
	public void beginTurn(Player p) {
		map.clearTombstones(p);
		map.produceMeadows(p);
		map.produceRoads(p);
		updateGoldEconomy(p);
		increaseRoundCount();

	}

	// return true if the village was successfully upgraded
	// Mang0-Tang0 Strikes again
	public boolean upgradeVillage(Village v) {
		boolean villageUpgraded = false;
		VillageType currentType = v.getType();
		VillageType type;
		if (currentType == VillageType.Hovel) {
			type = VillageType.Town;
		} else if (currentType == VillageType.Town) {
			type = VillageType.Fort;
		} else {
			return false;
		}
		int woodCount = v.getWood();
		int cost = type.getCost();
		if (woodCount >= cost) {
			v.setType(type);
			v.removeWood(cost);
			villageUpgraded = true;
		}
		return villageUpgraded;
	}

	public boolean upgradeUnit(Unit u, UnitType uType) {
		boolean unitUpgraded = false;
		Village v = u.getVillage();
		VillageType vType = v.getType();
		boolean support = false;

		if ((vType == VillageType.Hovel)
				&& (uType != UnitType.Soldier || uType != UnitType.Knight)) {
			support = true;
		} else if ((vType == VillageType.Town) && (uType != UnitType.Knight)) {
			support = true;
		} else if (vType == VillageType.Fort) {
			support = true;
		}

		if (support) {
			UnitType currentType = u.getType();
			int currentRank = currentType.ordinal();
			int newRank = uType.ordinal();

			if (currentRank < newRank) {
				int goldCount = v.getGold();
				int cost = uType.getCostFrom(currentType);

				if (goldCount >= cost) {
					u.updateType(uType);
					v.removeGold(cost);
					unitUpgraded = true;
				}
			}
		}

		return unitUpgraded;
	}

	public void increaseRoundCount() {
		roundsPlayed++;
	}

	public void updateGoldEconomy(Player p) {
		Set<Village> myVillages = map.getVillages(p);

		for (Village v : myVillages) {
			Region myRegion = v.getRegion();
			Set<Tile> myTiles = myRegion.getTiles();

			for (Tile t : myTiles) {
				LandType myLandType = t.getLandType();
				int value = myLandType.getGoldValue(myLandType);
				v.addGold(value);
			}
			Set<Unit> myUnits = myRegion.getUnits();

			for (Unit u : myUnits) {
				UnitType uType = u.getType();
				int salary = uType.getSalary();
				boolean success = v.removeGold(salary);

				if (success == false) {
					Tile myTile = u.getTile();
					myRegion.killUnit(u);
					myTile.addStructure(StructureType.Tombstone);
				}
			}
		}
	}

	// this will show error in tryInvade in Tile isn't changed to return a
	// boolean
	public boolean moveUnit(Unit u, Tile t) {
		return t.tryInvade(u);
	}

	// return true on success
	public boolean buildRoad(Unit u) {
		UnitType uType = u.getType();
		ActionType currAction = u.getCurrentAction();
		boolean success = false;

		if (uType == UnitType.Peasant
				&& currAction == ActionType.ReadyForOrders) {
			u.updateAction(ActionType.BuildingRoad);
			if (u.getCurrentAction() == ActionType.BuildingRoad) {
				success = true;
			}
		}
		return success;
	}

	public GameMap getGameMap() {
		return map;
	}

	public int getRoundsPlayed() {
		return roundsPlayed;
	}

	public Player turnOf() {
		return this.turnOf;
	}

	public void turnOf(Player player) {
		this.turnOf(player);
	}

	public Tile getVillageSpawnPoint(Village village) {
		boolean foundVacantTile = false;
		Tile spawnOn = null;
		//Check if village has enough gold to hire new villager
		if (village.getGold() >= UnitType.Peasant.getCost()) {
			Set<Tile> tiles = village.getRegion().getTiles();
			Iterator<Tile> it = tiles.iterator();
			while (it.hasNext() && foundVacantTile == false) {
				Tile tile = (Tile) it.next();
				if (tile.getUnit() == null) {
					//Place villager on this tile
					foundVacantTile = true;
//					tile.setUnit(new Unit(tile));
					//Subtract gold
//					village.removeGold(10);
					spawnOn = tile;
				}
			}
		}
		return spawnOn;
	}
	
	public Tile getCannonSpawnPoint(Village village){
		Tile spawnPoint = null;
		if(village.getGold() >= UnitType.Cannon.getCost()){
			for (Tile tile : this.map.getNeighbouringTiles(village.getTile())) {
				if(tile.getUnit() == null && tile.getPlayer() != null && tile.getPlayer().equals(village.getOwner())){
					spawnPoint = tile;
					break;
				}
			}
		}
		return spawnPoint;
	}
	
	public void hireCannon(Tile t){
		Unit cannon = new Unit(t);
		cannon.myType = UnitType.Cannon;
		t.setUnit(cannon);
		System.out.println("t " +t+" "+t.getVillage());
		t.getVillage().removeGold(UnitType.Cannon.getCost());
	}
	
	//
	public void hireVillager(Tile t) {
		t.setUnit(new Unit(t));
		//Subtract gold
		t.getVillage().removeGold(10);		
	}
}
