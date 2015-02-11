<<<<<<< HEAD
package cultivationSkeleton;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class GameMap {
	
	private Tile[][] map;
	private HashMap<Player,Set<Region>> regions;
	
	public Player getOwner(Tile t){
		return null;
	}
	
public Player getOwner(Region r){
	return null;
	}

public Set<Village> getVillages(Player p){
	return null;
}

public Region getRegion(Tile t){
	return null;
}

public Set<Tile> getTombstoneTiles(Player p){
	return null;
}
	
public void deleteVillage(Village v){

}

public void deleteRegion(Region r){

}

public Set<Region> getRegions(Player p){
	return null;
	
}

public Set<Region> getNeighbouringRegions(Tile t){
	return null;
}

public Set<Tile> getNeighbouringTiles(Tile t){
	return null;
}

public Set<Village> getMyVillagesOfAdjacentTiles(Set<Tile> tiles, Player p){
	return null;
}

public int getRegionCount(Player p){
	return 0;
}

public Set<Region> breakUpRegion(Region r, Tile t){
	return null;
}

public void clearTombstones(Player p){
	
}

public void produceMeadows(Player p){
	
}

public void produceRoads(Player p){
	
}

public void updateTileOwner(Player p){
	
}

public int playersLeft(){
	return 0;
}

public Village biggestOf(Collection<Village> villages){
	return null;
}

=======
package com.potatoes.cultivation.logic;

public class GameMap {
	private Tile[][] tiles;
>>>>>>> origin/master
}
