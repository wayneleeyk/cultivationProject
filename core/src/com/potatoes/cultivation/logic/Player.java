package com.potatoes.cultivation.logic;

import java.io.Serializable;

public class Player implements Serializable{
	
	private static final long serialVersionUID = 6376954568789670281L;
	public static final Player nullPlayer = new Player("");
	
	
	String username = null;
	String password = null;
	private PlayerStatus status;
	private int wins;
	private int losses;
	

	public Player(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "Username : "+username;
	}
	
	public boolean notNull(){
		return this.username!=null && !this.username.equals("");
	}
	
	public void increaseWins(){
		wins++;
	}
	
	public void increaseLosses(){
		losses++;
	}
	
}
