package com.potatoes.cultivation.networking;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.potatoes.cultivation.logic.Player;

public class GamesManager {
	
	static volatile int gameCount = 0;
	static IntMap<Set<Player>> games = new IntMap<>();
	
	public static void newGame(Set<Player> players){
		int gameID = gameCount++;
		games.put(gameID, players);
	}
	
	public static Collection<Player> opponentsOf(Player p){
		return otherPlayersInRoom(whereIs(p));
	}
	
	public static int whereIs(Player p){
		for (Entry<Set<Player>> entry : games) {
			if(entry.value.contains(p)) return entry.key;
		}
		return -1;
	}
	
	public static Collection<Player> otherPlayersInRoom(int roomNumber){
		if(roomNumber>0){
			return games.get(roomNumber);
		}
		return new HashSet<>();
	}
}
