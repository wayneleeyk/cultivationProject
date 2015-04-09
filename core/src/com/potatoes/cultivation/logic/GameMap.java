package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.utils.Predicate;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.Village.VillageStatus;
import com.potatoes.cultivation.stages.GameWorld;

public class GameMap implements Serializable {
	private static final long serialVersionUID = 1777608645753451446L;
	private Tile[][] map;
	private HashMap<Player, Set<Region>> regions = new HashMap<>();


	public void PrintPlayersStuff(Player p) {

		Set<Region> myRegions = regions.get(p);
		System.out.println("Number of regions:" + myRegions.size());
		if (myRegions.size()>0) {
			for (Region r : myRegions) {
				if (r.getVillage()==null) {
					System.out.println("Region has no village.ERROR.");
				} else {
					System.out.println("Region size " + r.getTiles().size() + " has village " + r.getVillage().toString());
				}
			}
		} else {
			System.out.println("Player has no regions");
		}
	}

	public GameMap(int width, int height, List<Player> participants) {
		System.out.println("Generating hex map");
		HexMap mapGenerator = new HexMap(width, height);
		System.out.println("Done hex map");
		map = mapGenerator.getMap();
		System.out.println("Done generator");
		for (Player player : participants) {
			regions.put(player, new HashSet<Region>());
		}
		this.assignRandomLand(participants);
		System.out.println("assigned land");
		this.generateRegionsFromLandOwnership();
		System.out.println("Generated ownership");
	}

	public Tile[][] getMap() {
		return this.map;
	}

	public Tile getTile(int x, int y) {
		return map[x][y];
	}

	public Player getOwner(Tile t) {
		return t.getPlayer();
	}

	public Player getOwner(Region r) {
		return r.getOwner();
	}

	public Set<Village> getVillages(Player p) {
		Set<Village> villages = new HashSet<>();
		for (Region region : regions.get(p)) {
			villages.add(region.getVillage());
		}
		return villages;
	}

	public Region getRegion(Tile t) {
		Set<Region> regionSet = regions.get(t.getPlayer());
		if (regionSet != null) {
			for (Region region : regionSet) {
				if (region.getTiles().contains(t))
					return region;
			}
		}
		return null;
	}

	public Map<Player, Set<Region>> getAllRegions(){
		return new HashMap<>(this.regions);
	}

	public Set<Tile> getTombstoneTiles(Player p) {
		Set<Region> r = regions.get(p);
		Set<Tile> tiles = new HashSet<>();
		for (Region region : r) {
			for (Tile t : region.getTiles()) {
				if (t.getStructure().equals(StructureType.Tombstone))
					tiles.add(t);
			}
		}
		return tiles;
	}

	public void deleteVillage(Village v) {

	}

	public void deleteRegion(Region region) {
		for (Player p : this.regions.keySet()) {
			if(this.regions.get(p).contains(region)) {
				Set<Region> regionSet = this.regions.get(p);
				regionSet.remove(region);
				region.getTiles().clear();
				region.getUnits().clear();
			}
		}
	}

	public Set<Region> getRegions(Player p) {
		Set<Region> r = new HashSet<>();
		r.addAll(regions.get(p));
		return r;
	}

	public Set<Region> getNeighbouringRegions(Tile t) {
		Set<Tile> neighbouringTiles = getNeighbouringTiles(t);
		Set<Region> r = new HashSet<>();
		for (Tile tile : neighbouringTiles) {
			r.add(getRegion(tile));
		}
		return r;
	}

	public enum MapDirections {
		Up(0, 1), Down(0, -1), LeftUp(-1, 1), LeftDown(-1, 0), RightUp(1, 0), RightDown(
				1, -1);
		int iShift, jShift;

		private MapDirections(int iShift, int jShift) {
			this.iShift = iShift;
			this.jShift = jShift;
		}

	}

	public Set<Tile> getNeighbouringTiles(Tile t) {
		MapCoordinates tileLocation = getCoordinates(t);
		Set<Tile> neighbours = new HashSet<>();
		for (MapDirections direction : MapDirections.values()) {
			Tile tile = tileLocation.go(direction).getTile(this);
			if (tile != null) {
				neighbours.add(tile);
				//				System.out.println("Looking at direction "+ direction+" got tile "+tile.x+" "+tile.y);
			}
		}
		return neighbours;
	}

	public static class MapCoordinates implements Serializable{
		private static final long serialVersionUID = -5390246105930686786L;
		int i;
		int j;

		public MapCoordinates(int i, int j) {
			this.i = i;
			this.j = j;
		}

		public MapCoordinates go(MapDirections direction) {
			return new MapCoordinates(this.i + direction.iShift, this.j
					+ direction.jShift);
		}

		public Tile getTile(GameMap gameMap){
			if(i() >= 0 && i() < gameMap.map.length && j >= 0 && j<gameMap.map[0].length) return gameMap.map[i][j];
			return null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			result = prime * result + j;
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
			MapCoordinates other = (MapCoordinates) obj;
			if (i != other.i())
				return false;
			if (j != other.j)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MapCoordinate("+this.i+","+this.j+")";
		}

		public int i() {
			return i;
		}

		public int j(){
			return j;
		}
	}

	private MapCoordinates getCoordinates(Tile t) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j]==t)
					return new MapCoordinates(i, j);
			}
		}
		return new MapCoordinates(-1, -1);
	}

	public Set<Village> getMyVillagesOfAdjacentTiles(Set<Tile> tiles, Player p) {
		if(tiles.size() > 0){
			Tile tile = pop(tiles);
			Set<Village> villages = getMyVillagesOfAdjacentTiles(tiles, p);
			if (tile.getPlayer()!=null && tile.getPlayer().equals(p)) {
				villages.add(getRegion(tile).getVillage());
			}

			return villages;
		}
		return new HashSet<Village>();
	}

	public int getRegionCount(Player p) {
		return regions.get(p).size();
	}

	public Set<Region> breakUpRegion(Region r, Tile t) {
		Set<Tile> tiles = getNeighbouringTiles(t);
		Set<Region> brokenRegions = new HashSet<>();
		System.out.println("Region before breaking up has size "+ r.size());
		
		for (Tile tile : tiles) {
			if(tile.owner != null && tile.owner.equals(r.getOwner())){
				System.out.println("Victim is "+r.getOwner()+ " tile owner is "+tile.owner);
				List<Tile> regionTiles = bfsTileOfRegion(tile);
				System.out.println("subregion has size "+regionTiles.size());
				Region region = new Region(null);
				region.addTiles(regionTiles);
				boolean repeat = false;
				for (Region established : brokenRegions) {
					if(established.hasSameTilesAs(region)) repeat = true;
				}
				if(!repeat) {
					
					Village village;
					if(Cultivation.GAMEMANAGER.getGame().isMyTurn(Cultivation.CLIENT.player)){
						village = new Village(r.getOwner(), region, regionTiles.get(new Random().nextInt(regionTiles.size())));
						Cultivation.CLIENT.sendVillageLocation(new MapCoordinates(village.getTile().x, village.getTile().y));
						System.out.println("Sent village for subregion at "+village.getTile().x+" "+village.getTile().y);
					}
					else{
						GameWorld world = Cultivation.GAMEMANAGER.getGame().getWorld();
						MapCoordinates constructionSite = world.getNextVillageConstructionSite(region);
						while(constructionSite==null) {
							constructionSite = world.getNextVillageConstructionSite(region);
						}
						System.out.println("Received village for subregion at "+constructionSite);
						village = new Village(r.getOwner(), region, this.map[constructionSite.i()][constructionSite.j]);
						System.out.println("Made village "+village);
					}
					region.setVillage(village);
					// transfer units from old region to new separated region
					for (Tile regionalTile : region.getTiles()) {
						
						if(regionalTile.occupant!=null){
							System.out.println("Transfering "+regionalTile.occupant+ " at "+regionalTile+" to new region "+region);
							regionalTile.occupant.myVillage = village;
						}
					}
					brokenRegions.add(region);
					Cultivation.GAMEMANAGER.getGame().getWorld().createVillageAt(village.getTile().x, village.getTile().y);
				}
			}
		}
		Cultivation.GAMEMANAGER.getGame().getWorld().villageConstructionSites.clear();
		Player owner = r.getOwner();
		regions.get(owner).remove(r);
		regions.get(owner).addAll(brokenRegions);

		return brokenRegions;
	}

	public void clearTombstones(Player p) {
		//Get all the tiles belonging to p that has a tombstone
		Set<Tile> tombstoneTiles = getTombstoneTiles(p);
		for (Tile t : tombstoneTiles) {
			//Destroy tombstone and update land type to grass
			t.destroyStructure();
			t.updateLandType(LandType.Grass);
		}
	}

	public void produceMeadowsRoads(Player p) {
		Set<Village> villages = getVillages(p);
		for (Village v : villages) {
			Set<Unit> units = v.getRegion().getUnits();
			System.out.println("Updating turn for units of village "+ v+ " "+units);
			for (Unit u : units) {
				if (u.getCurrentAction()==ActionType.StartCultivating) {
					u.updateAction(ActionType.FinishCultivating);
				} else if (u.getCurrentAction()==ActionType.FinishCultivating) {
					u.updateAction(ActionType.ReadyForOrders);
					Tile tile = u.getTile();
					tile.updateLandType(LandType.Meadow);
				} else if (u.getCurrentAction()==ActionType.BuildingRoad) {
					u.updateAction(ActionType.ReadyForOrders);
					Tile tile = u.getTile();
					tile.addStructure(StructureType.Road);
				}
				else {
					u.updateAction(ActionType.ReadyForOrders);
				}
			}
		}
	}

	public int playersLeft() {
		return regions.keySet().size();
	}

	public Village biggestOf(Collection<Village> villages) {
		if(villages.size() == 1) return villages.iterator().next();
		System.out.println("before :" + villages.size());
		Village v1 = pop(villages);
		System.out.println("after :" + villages.size());
		Village v2 = biggestOf(villages);
		System.out.println("Merging "+v1+" "+v2);
		if (v1.compareTo(v2) > 0)
			return v1;
		else
			return v2;
	}

	public void takeOverTile(Tile target) {
		Region victimRegion = target.getRegion();
		if (victimRegion!=null)
			System.out.println("Victim "+victimRegion.getOwner()+" region has "+victimRegion.getTiles());
		Player victim = target.getPlayer();
		System.out.println("Taking over enemy region "+ victimRegion+" of "+victim);
		Unit invadingUnit = target.getUnit();
		Village invaderVillage = invadingUnit.getVillage();
		Player invadingOwner = invaderVillage.getOwner();
		Region invaderRegion = invaderVillage.getRegion();
		target.owner = target.occupant.myVillage.getOwner();

		target.updateOwner(invadingOwner);
		victimRegion.removeTile(target);
		invaderRegion.addTile(target);
		Set<Village> villages = getVillages(victim);

		// check if the tile is a village tile
		for (Village village : villages) {
			if(target.equals(village.getTile())){	// the tile under the invaded village
				invaderVillage.addGold(village.getGold());
				invaderVillage.addWood(village.getWood());
				// generate a new village
				System.out.println("Generating new village after hostile takeover of "+village);
				Region region = village.getRegion();
				List<Tile> regionTiles = new LinkedList<>(region.getTiles());
				if(Cultivation.GAMEMANAGER.getGame().isMyTurn(Cultivation.CLIENT.player)){
					village = new Village(victim, region, regionTiles.get(new Random().nextInt(regionTiles.size())));
					Cultivation.CLIENT.sendVillageLocation(new MapCoordinates(village.getTile().x, village.getTile().y));
					region.setVillage(village);
				}
				else{
					MapCoordinates coordinates = Cultivation.GAMEMANAGER.getGame().getWorld().getNextVillageConstructionSite(region);
					while(coordinates==null) {
						System.out.println("Polling for coordinates");
						coordinates = Cultivation.GAMEMANAGER.getGame().getWorld().getNextVillageConstructionSite(region);
					}
					village = new Village(victim, region, this.map[coordinates.i()][coordinates.j]);
					region.setVillage(village);
				}

				break;
			}
		}
		Cultivation.GAMEMANAGER.getGame().getWorld().villageConstructionSites.clear();

		// break apart the region if at a branch point
		System.out.println("Victim "+victimRegion.getOwner()+" region has "+victimRegion.getTiles()+ " target tile is "+ target);
		Set<Region> regions = breakUpRegion(victimRegion,target);
		if(regions.size() > 1) System.out.println("Regions are broken up into "+regions.size());
		for (Region region : regions) {
			System.out.println("Region "+region+" has size "+ region.size());
			// destroy regions, along with their units, which have tiles less than 3
			if(region.size() <3){
				System.out.println("Region "+region+" has been destroyed");
				region.destroy();
			}
		}

	}

	public List<Tile> bfsTile(Predicate<Tile> p, Tile t) {
		System.out.println("BFSing tile "+ t);
		Set<Tile> visited = new HashSet<>();
		Queue<Tile> toVisit = new LinkedList<>();
		List<Tile> result = new LinkedList<>();

		Tile current = t;
		while(current != null){
			result.add(current);
			visited.add(current);
			Set<Tile> neighbours = getNeighbouringTiles(current);
			Iterator<Tile> it = neighbours.iterator();
			// remove neightbours that are visited or not of interest
			while (it.hasNext()) {
				Tile tentative = it.next();
				if (visited.contains(tentative) || !p.evaluate(tentative))
					it.remove();
			}
			visited.addAll(neighbours);
			toVisit.addAll(neighbours);
			current = toVisit.poll();
		}
		return result;
	}

	public List<Tile> bfsTileOfPlayer(Tile t) {
		//		System.out.println("tile's player" + t.getPlayer());
		if (t.getPlayer()==null || t.getPlayer()==Player.nullPlayer) {
			return new LinkedList<Tile>();
		}
		final Player player = t.getPlayer();
		return bfsTile(new Predicate<Tile>() {
			@Override
			public boolean evaluate(Tile tile) {
				if (tile!=null && tile.getPlayer()!=null && tile.getPlayer()!=Player.nullPlayer) {
					return tile.getPlayer().username.equals(player.username);
				} else {
					return false;
				}
			}
		}, t);
	}

	public List<Tile> bfsTileOfRegion(Tile t) {
		final Region region = t.getRegion();
		return bfsTile(new Predicate<Tile>() {
			@Override
			public boolean evaluate(Tile tile) {
				return (tile.getRegion()!=null)? tile.getRegion().equals(region): region == null;
			}
		}, t);
	}

	public void mergeTo(Village v, Stack<Village> villages) {
		if(!villages.empty()) mergeTo(villages.pop().merge(v), villages);
	}

	private Set<Tile> getMapTiles() {
		Set<Tile> tiles = new HashSet<>();
		for (Tile[] rows : map) {
			for (Tile tile : rows) {
				tiles.add(tile);
			}
		}
		return tiles;
	}

	public void assignRandomLand(List<Player> players) {
		Set<Tile> tiles = getMapTiles();
		int n = players.size() + 1;
		int count = 0;
		for (Tile tile : tiles) {
			int randomIndex = count % n;
			if (randomIndex < players.size() && tile.getLandType()!=LandType.Sea) {
				Player p = players.get(randomIndex);
				tile.updateOwner(p);
			}
			count++;
		}
	}

	public void generateRegionsFromLandOwnership() {
		//		System.out.println("Generating regions ....");
		Set<Tile> tiles = getMapTiles();
		while (tiles.size() > 0) {
			Tile tile = tiles.iterator().next();
			Set<Tile> regionalTiles = new HashSet<>(bfsTileOfPlayer(tile));
			//			System.out.println("BFS grabbed tiles:" + regionalTiles.size());
			if (regionalTiles.size() > 2) {
				System.out.println("Region found, size:" + regionalTiles.size());
				Region r = new Region(null);
				r.addTiles(regionalTiles);
				Tile aTile = regionalTiles.iterator().next();
				Village v = new Village(tile.getPlayer(), r, aTile);
				r.setVillage(v);
				// Change the village tile to a grass one
				aTile.updateLandType(LandType.Grass);
				this.regions.get(tile.getPlayer()).add(r);
				//Remove these regional tiles from tiles list
				for (Tile rTile : regionalTiles) {
					tiles.remove(rTile);
				}
			} else {
				//Remove ownership of these regionalTiles since it contains less than 3 tiles
				//and remove these tiles from tiles list 
				for (Tile rTile : regionalTiles) {
					rTile.updateOwner(Player.nullPlayer);
					tiles.remove(rTile);
				}
			}
			tiles.remove(tile);
		}
	}

	private <T> T pop(Collection<T> collection) {
		Iterator<T> it = collection.iterator();
		T item = null;
		if (it.hasNext()) {
			item = it.next();
			it.remove();
		}
		return item;
	}
	/**
	 * Not used because produce meadow takes care of it
	 */
	public void updateUnitActions(Player p) {
		for(Region r : getRegions(p)) {
			for(Unit u : r.getUnits()) {
				if(!u.currentAction.equals(ActionType.StartCultivating)) {
					u.currentAction = ActionType.ReadyForOrders;
				}
				else {
					u.currentAction = ActionType.FinishCultivating;
				}
			}
		}
	}
	/**
	 * Updates the status of every village of Player p. Called at the begining of the turn
	 */
	public void updateVillageStatus(Player p) {
		for(Village v : getVillages(p)) {
			if(v.getStatus().equals(VillageStatus.StartUpgrading)) {
				v.upgradeVillageType();
				v.updateStatus(VillageStatus.StillUpgrading);
			}
			else {
				v.updateStatus(VillageStatus.VillageReady);
			}
		}
	}

}
