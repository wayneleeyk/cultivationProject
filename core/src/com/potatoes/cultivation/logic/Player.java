package com.potatoes.cultivation.logic;

import java.io.Serializable;

import cultivationSkeleton.PlayerStatus;

public class Player implements Serializable{
	private static final long serialVersionUID = 6376954568789670281L;
	public static final Player nullPlayer = new Player("");
	private PlayerStatus status;
	private int wins;
	private int losses;
	
	
	
	String username = null;
	
	public Player(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "Username : "+username;
	}
	
	public boolean notNull(){
		return !this.username.equals("");
	}
	
	public void increaseWins(){
		
	}
	
	public void increaseLosses(){
		
	}
	
}
