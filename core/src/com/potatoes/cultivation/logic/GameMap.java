package com.potatoes.cultivation.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class GameMap {

	private Tile[][] map;
	private HashMap<Player, Set<Region>> regions;
	
	public GameMap(int width, int height) {
		HexMap mapGenerator = new HexMap(width, height);
		map = mapGenerator.getMap();
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

	public Set<Tile> getNeighbouringTiles(Tile t) {
		MapCoordinates tileLocation = getCoordinates(t);
		Set<Tile> neighbours = new HashSet<>();
		// TODO add map[i][j] that are neighbours to tileLpcation.i and tileLocation.j
		return null;
	}
	
	private class MapCoordinates {
		int i,j;
		public MapCoordinates(int i, int j) {
			this.i = i;
			this.j = j;
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

	public List<Tile> bfsTileOfRegion(Region r, Tile t) {
		return null;
	}

	public void mergeTo(Village v, Stack<Village> villages) {

	}

	public void assignRandomLand(List<Player> players) {

	}

	public void generateRegionsFromLandOwnership() {

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
