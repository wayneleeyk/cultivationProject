package com.potatoes.cultivation.networking;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.potatoes.cultivation.logic.Player;

public class GamesManager {
	
	static volatile int gameCount = 0;
	
	public static void newGame(Set<Player> players){
		int gameID = gameCount++;
	}
	

}
