package com.potatoes.cultivation.logic;

import java.util.List;

public class GameManager {

	private CultivationGame myGame;
	public CultivationGame getGame(){
		return myGame;
	}
	
	public void newGame(List<Player> players ){
		
	}
	
	
	public GameMap getGameMap(){
		return myGame.getGameMap();
	}
	
	
	
}
