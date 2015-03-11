package com.potatoes.cultivation.logic;

import java.util.List;

public class GameManager {

	private CultivationGame myGame;
	
	public CultivationGame getGame(){
		return myGame;
	}
	
	public boolean newGame(List<Player> players ){
		this.myGame = new CultivationGame(players);
		return true;
	}
	
	
	public GameMap getGameMap(){
		return myGame.getGameMap();
	}
	
	
	
}
