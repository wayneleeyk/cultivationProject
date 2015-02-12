package com.potatoes.cultivation.logic;



import java.util.List;

public class Game {

	private int roundsPlayed;
	private Player turnOf;
	private List<Player> players;
	private GameMap map;
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public void beginTurn(Player p){
		
	}
	
	public void upgradeVillage(Village v, VillageType type){
		
	}
	
	public void upgradeUnit(Unit u, UnitType type ){
		
	}
	
	public void increaseRoundCount(){
		
	}
	
	public void updateGoldEconomy(Player p){
		
	}
	
	public void moveUnit(Unit u, Tile t){
		
	}
	
	public GameMap getGameMap(){
		return map;
	}
}
