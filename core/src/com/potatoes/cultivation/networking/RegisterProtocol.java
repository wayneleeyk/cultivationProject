package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.logic.Player;

public class RegisterProtocol implements Protocol {
	private static final long serialVersionUID = -8658082689064145170L;
	private String username, password;
	private Player player = null;
	
	public RegisterProtocol(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void execute(Server server) {
		this.player(server.register(username, password));
	}

	public void player(Player p){
		this.player = p;
	}
	
	public Player player(){
		return this.player;
	}

}
