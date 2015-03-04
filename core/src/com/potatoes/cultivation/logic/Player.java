package com.potatoes.cultivation.logic;

import java.io.Serializable;

public class Player implements Serializable{
	
	private static final long serialVersionUID = 6376954568789670281L;
	public static final Player nullPlayer = new Player("");
	
	
	String username = null;
	private PlayerStatus status;
	

	public Player(String username) {
		this.username = username;
		this.status = PlayerStatus.Online;
	}
	@Override
	public String toString() {
		return "Username : "+username;
	}
	
	public boolean notNull(){
		return this.username!=null && !this.username.equals("");
	}
	
	public void increaseWins(){
		//TODO: call to server to update file of player stats
	}
	
	public void increaseLosses(){
		//TODO: call to server to update file of player stats
	}
	public PlayerStatus getStatus() {
		//TODO: call to server to update 
		return null;
	}
	public void setStatus(PlayerStatus s) {
		//TODO: call to server to update 
		this.status = s;
	}
	
}
