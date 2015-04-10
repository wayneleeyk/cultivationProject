package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.Village.VillageStatus;
import com.potatoes.cultivation.stages.GameWorld;

public class CultivationGame implements Serializable {
	private static final long serialVersionUID = -4015259598754971722L;
	private int roundsPlayed;
	private List<Player> players;
	private GameMap map;
	transient private GameWorld world;

	public CultivationGame(List<Player> participants) {
		players = participants;
		map = new GameMap(10, 10, participants);
		System.out.println("Made new game map");
		this.roundsPlayed = 0;
		beginTurn(players.get(0));
	}

	public void setWorld(GameWorld pWorld) {
		world = pWorld;
	}
	
	public GameWorld getWorld() {
		return world;
	}
	
	public List<Player> getPlayers() {
		List<Player> players = new LinkedList<Player>(this.players);
		return players;
	}
	
	/*
	 * Get a potato color from a player
	 */
	public String playerToPotatoColor(Player p) {
		int i = players.indexOf(p);
		if(i == 0) {
			return "red";
		}
		if(i == 1) {
			return "purple";
		}
		if(i == 2) {
			return "yellow";
		}
		return "yellow";
	}

	public void beginTurn(Player p) {
		System.out.println("Beginning turn");
		map.clearTombstones(p);
		// Produce Meadow, Roads and ALSO updates unit ActionType (even those that are chopping trees)
		map.produceMeadowsRoads(p);
		map.updateVillageStatus(p);
		updateGoldEconomy(p);
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
		} else if (currentType == VillageType.Fort) {
			type = VillageType.Castle;
		} else {
			return false;
		}
		int woodCount = v.getWood();
		int cost = type.getCost();
		if (woodCount >= cost) {
			v.removeWood(cost);
			villageUpgraded = true;
			v.updateStatus(VillageStatus.StartUpgrading);
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
		} else if (vType == VillageType.Fort || vType == VillageType.Castle) {
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

		//Loop through each village belonging to player p
		for (Village v : myVillages) {
			Region myRegion = v.getRegion();
			Set<Tile> myTiles = myRegion.getTiles();
			
			//If village v is a castle, pay for upkeep
			if (v.getType().equals(VillageType.Castle) && v.getStatus()!=VillageStatus.StartUpgrading && v.getStatus()!=VillageStatus.StillUpgrading) {
				boolean success = v.removeGold(80);//80 gold? what a rip off!
				if (!success) {
					//If can't afford to pay for castle,
					//downgrade castle to fort
					v.setType(VillageType.Fort);
				}
			}
			//For each tile, generate gold depending on its landtype
			//and pay unit if a unit is on tile
			for (Tile t : myTiles) {
				LandType myLandType = t.getLandType();
				int value = myLandType.getGoldValue(myLandType);
				v.addGold(value);
			}
			//Pay villagers their wage
			for (Tile t : myTiles) {
				if (t.getUnit()!=null) {
					UnitType uType = t.getUnit().getType();
					int salary = uType.getSalary();
					boolean success = v.removeGold(salary);
					//If fail to pay villager, it dies and gets buried
					if (success == false) {
						System.out.println("CAN'T PAY UNIT. MAKE A TOMBSTONE!!");
						t.getRegion().killUnit(t.getUnit());
						t.addStructure(StructureType.Tombstone);
					}
				}
			}
			
			


		}
	}

	public boolean moveUnit(Unit u, Tile t) {
		return t.tryInvade(u);
	}

	// return true on success
	public boolean buildRoad(Unit u) {
		UnitType uType = u.getType();
		ActionType currAction = u.getCurrentAction();
		boolean success = false;

		if (uType == UnitType.Peasant && currAction == ActionType.ReadyForOrders) {
			u.updateAction(ActionType.BuildingRoad);
			success = true;
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
		return this.players.get(roundsPlayed%this.players.size());
	}

	public void turnOf(Player player) {
		this.turnOf(player);
	}

	public Tile getVillagerSpawnPoint(Village village) {
		boolean foundVacantTile = false;
		Tile spawnOn = null;
		//Check if village has enough gold to hire new villager
		if (village.getGold() >= UnitType.Peasant.getCost()) {
			Set<Tile> tiles = new HashSet<>(village.getRegion().getTiles());
			tiles.remove(village.getTile());
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
		if(village.getType().compareTo(VillageType.Fort) <0) {
			return null;
		}
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
		Unit unit = new Unit(t);
		t.setUnit(unit);
		//Subtract gold
		t.getVillage().removeGold(10);	
		t.getVillage().getRegion().getUnits().add(unit);
		if(t.getLandType()==LandType.Tree){
			t.updateLandType(LandType.Grass);
			t.getVillage().addWood(1);
			unit.updateAction(ActionType.ChoppedTree);
		}
	}

	
	public void fireAt(Tile tile) {
		if(tile.getUnit()!=null){
			tile.getRegion().killUnit(tile.getUnit());
			tile.addStructure(StructureType.Tombstone);
		}
	}
	public boolean isMyTurn(Player me) {
		return turnOf().equals(me);
	}

	public boolean cultivateMeadow(Unit u) {
		UnitType uType = u.getType();
		ActionType currAction = u.getCurrentAction();
		boolean success = false;

		if (uType == UnitType.Peasant && currAction == ActionType.ReadyForOrders) {
			u.updateAction(ActionType.StartCultivating);
			success = true;
		}
		return success;
	}
	
	public void growTrees(List<Tile> tilesToGrow) {
		for(Tile t : tilesToGrow) {
			getGameMap().getMap()[t.x][t.y].updateLandType(LandType.Tree);
		}
	}
}
