package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.utils.Predicate;

public class GameMap implements Serializable{
	private static final long serialVersionUID = 1777608645753451446L;
	private Tile[][] map;
	private HashMap<Player, Set<Region>> regions = new HashMap<>();
	
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
		Set<Region> r = regions.get(p);
		Set<Village> villages = new HashSet<>();
		for (Region region : r) {
			villages.add(region.getVillage());
		}
		return villages;
	}

	public Region getRegion(Tile t) {
		Set<Region> r = regions.get(t.getPlayer());
		for (Region region : r) {
			if(region.getTiles().contains(t)) return region;
		}
		return Region.NO_REGION;
	}

	public Set<Tile> getTombstoneTiles(Player p) {
		Set<Region> r = regions.get(p);
		Set<Tile> tiles = new HashSet<>();
		for (Region region : r) {
			for (Tile t : region.getTiles()) {
				if(t.getStructure().equals(StructureType.Tombstone)) tiles.add(t);
			}
		}
		return new HashSet<>();
	}

	public void deleteVillage(Village v) {
		
	}

	public void deleteRegion(Region r) {

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
			r.add( getRegion(tile) );
		}
		return r;
	}

	
	enum MapDirections{
		Up(0,1), Down(0,-1), LeftUp(-1,1), LeftDown(-1,0), RightUp(1,0), RightDown(1,-1);
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
			Tile tile = tileLocation.go(direction).getTile();
			if (tile != null) neighbours.add(tile);
		}
		return neighbours;
	}
	
	public class MapCoordinates {
		int i,j;
		public MapCoordinates(int i, int j) {
			this.i = i;
			this.j = j;
		}
		public MapCoordinates go(MapDirections direction){
			return new MapCoordinates(this.i + direction.iShift, this.j + direction.jShift);
		}
		public Tile getTile(){
			if(i > 0 && i < map.length && j > 0 && j<map[0].length) return map[i][j];
			return null;
		}
	}
	
	private MapCoordinates getCoordinates(Tile t){
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if(map[i][j].equals(t)) return new MapCoordinates(i,j);
			}
		}
		return new MapCoordinates(-1,-1);
	}

	public Set<Village> getMyVillagesOfAdjacentTiles(Queue<Tile> tiles, Player p) {
		Tile t = tiles.poll();
		Set<Village> villages = getMyVillagesOfAdjacentTiles(tiles, p);
		if(t.getPlayer().equals(p)) villages.add(getRegion(t).getVillage());
		return villages;
	}

	public int getRegionCount(Player p) {
		return regions.get(p).size();
	}

	public Set<Region> breakUpRegion(Region r, Tile t) {
		return null;
	}

	public void clearTombstones(Player p) {

	}

	public void produceMeadows(Player p) {

	}

	public void produceRoads(Player p) {

	}

	public void updateTileOwner(Player p) {

	}

	public int playersLeft() {
		return regions.keySet().size();
	}

	public Village biggestOf(Collection<Village> villages) {
		Village v1 = pop(villages);
		Village v2 = biggestOf(villages);
		if(v1.compareTo(v2) > 0) return v1;
		else return v2;
	}

	public void takeOverTile(Tile t) {
		
	}

	public List<Tile> bfsTile(Predicate<Tile> p, Tile t){
		Set<Tile> visited = new HashSet<>();
		Queue<Tile> toVisit = new LinkedList<>();
		List<Tile> result = new LinkedList<>();
		
		Tile current = t;
		toVisit.add(t);
		while(!toVisit.isEmpty()){
			result.add(current);
			visited.add(current);
			Set<Tile> neighbours = getNeighbouringTiles(current);
			Iterator<Tile> it = neighbours.iterator();
			// remove neightbours that are visited or not of interest
			while(it.hasNext()){
				Tile tentative = it.next();
				if(visited.contains(tentative) || !p.evaluate(tentative)) it.remove();
			}
			toVisit.addAll(neighbours);
			current = toVisit.poll();
		}
		return result;
	}
	
	
	public List<Tile> bfsTileOfPlayer(Tile t){
		final Player player = t.getPlayer();
		return bfsTile(new Predicate<Tile>(){
			@Override
			public boolean evaluate(Tile tile) {
				return tile.getPlayer().equals(player);
			}
		}, t);
	}
	
	public List<Tile> bfsTileOfRegion(Tile t) {
		final Region region = t.getRegion(); 
		return bfsTile(new Predicate<Tile>(){
			@Override
			public boolean evaluate(Tile tile) {
				return tile.getRegion().equals(region);
			}
		}, t);
	}

	public void mergeTo(Village v, Stack<Village> villages) {
		mergeTo(villages.pop().merge(v), villages);
	}

	private Set<Tile> getMapTiles(){
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
		int n = players.size();
		int count = 0;
		for (Tile tile : tiles) {
			Player p = players.get(count % n);
			tile.updateOwner(p);
		}
		
	}

	public void generateRegionsFromLandOwnership() {
		Set<Tile> tiles = getMapTiles();
		while(tiles.size() != 0){
			Tile tile = tiles.iterator().next();
			Set<Tile> regionalTiles = new HashSet<>( bfsTileOfPlayer(tile));
			if(regionalTiles.size() >2){
				Region r = new Region(null);
				r.addTiles(regionalTiles);
				Tile aTile = regionalTiles.iterator().next();
				Village v = new Village(tile.getPlayer(), r, aTile);
				r.setVillage(v);
				this.regions.get(tile.getPlayer()).add(r);
			}
			tiles.remove(tile);
		}
	}
	
	private <T> T pop(Collection<T> collection){
		Iterator<T> it = collection.iterator();
		T item = null;
		if(it.hasNext()){
			item = it.next();
			it.remove();
		}
		return item;
	}

}
