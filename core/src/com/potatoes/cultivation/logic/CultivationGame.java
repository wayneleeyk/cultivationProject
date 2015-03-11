package com.potatoes.cultivation.logic;

import java.util.List;

public class CultivationGame {

	private int roundsPlayed;
	private Player turnOf;
	private List<Player> players;
	private GameMap map;
	
	public CultivationGame(List<Player> participants) {
		players = participants;
		map = new GameMap(10, 10);
		roundsPlayed = 0;
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public void beginTurn(Player p){
		
	}
	//return true if the village was successfully upgraded
	public boolean upgradeVillage(Village v, VillageType type){
		return false;
	}
	
	public void upgradeUnit(Unit u, UnitType type ){
		
	}
	
	public void increaseRoundCount(){
		roundsPlayed++;
	}
	
	public void updateGoldEconomy(Player p){
		
	}
	
	public void moveUnit(Unit u, Tile t){
		
	}
	
	//return true on success
	public boolean buildRoad(Unit u){
		return false;
	}
	
	public GameMap getGameMap(){
		return map;
	}
}
