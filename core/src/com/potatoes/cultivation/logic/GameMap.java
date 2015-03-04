package com.potatoes.cultivation.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
		return null;
	}

	public Player getOwner(Region r) {
		return null;
	}

	public Set<Village> getVillages(Player p) {
		return null;
	}

	public Region getRegion(Tile t) {
		return null;
	}

	public Set<Tile> getTombstoneTiles(Player p) {
		return null;
	}

	public void deleteVillage(Village v) {

	}

	public void deleteRegion(Region r) {

	}

	public Set<Region> getRegions(Player p) {
		return null;

	}

	public Set<Region> getNeighbouringRegions(Tile t) {
		return null;
	}

	public Set<Tile> getNeighbouringTiles(Tile t) {
		return null;
	}

	public Set<Village> getMyVillagesOfAdjacentTiles(Set<Tile> tiles, Player p) {
		return null;
	}

	public int getRegionCount(Player p) {
		return 0;
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
		return 0;
	}

	public Village biggestOf(Collection<Village> villages) {
		return null;
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

}
