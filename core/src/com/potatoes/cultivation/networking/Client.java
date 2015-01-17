package com.potatoes.cultivation.networking;

import java.util.LinkedList;
import java.util.List;

import com.potatoes.cultivation.logic.Command;
import com.potatoes.cultivation.logic.Player;

public class Client {
	public void login(String username, String password){}
	public void createAccount(String username, String password){}
	public List<Player> updateLobby(){return new LinkedList<Player>();}
	public void endTurn(List<Command> actions){}
}
