package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.Player;


public class LoginProtocol implements Protocol{
	private static final long serialVersionUID = 4688377699824719114L;
	private String name, password;
	private Player player = null;
	public LoginProtocol(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	@Override
	public void execute(Server server) {
		this.player(server.login(name, password));
	}

	public void player(Player p){
		this.player = p;
	}
	
	public Player player(){
		return this.player;
	}
}
